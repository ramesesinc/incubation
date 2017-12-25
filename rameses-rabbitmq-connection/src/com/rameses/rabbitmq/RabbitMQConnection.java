/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq;

import com.rabbitmq.client.AMQP;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.util.Base64Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class RabbitMQConnection extends MessageConnection {
    
    private Connection connection;
    private AbstractContext context;     
    private Map conf; 
    private String name;
    private boolean enabled;
    private boolean started;
    private Channel defaultChannel;
    private List<Channel> channels = new LinkedList<Channel>();
    
    private boolean allowSend; 
    private boolean allowReceive; 
    
    public RabbitMQConnection(String name, AbstractContext context, Map conf) {
        this.name = name;
        this.context = context;
        this.conf = conf; 
        
        enabled = ("false".equals(getProperty("enabled")+"") ? false : true);
        allowSend = ("false".equals(getProperty("allowSend")+"") ? false : true);
        allowReceive = ("false".equals(getProperty("allowReceive")+"") ? false : true);
    }

    private String getProperty( String name ) {
        Object o = (conf == null? null: conf.get(name)); 
        return ( o == null ? null: o.toString()); 
    }
     
    public final boolean isEnabled() {
        return this.enabled; 
    }
    
    public Map getConf() { 
        return conf; 
    }

    public void start() { 
        if ( started ) return; 
        
        started = true;
        
        System.out.println("Starting RabbitMQConnection Version 2.0 " + name );
        try {
            ConnectionFactory factory = new ConnectionFactory(); 
            factory.setHost( getProperty("host") ); 
            factory.setUsername( getProperty("user") ); 
            factory.setPassword( getProperty("pwd") ); 
            
            int heartbeat = 60; 
            try { 
                heartbeat = Integer.parseInt(getProperty("heartbeat")); 
            } 
            catch(Throwable t) {;} 
            factory.setRequestedHeartbeat( heartbeat );
            connection = factory.newConnection(); 
            
            //check if there is a channel specified. If there is, then you must listen.
            String queueName = getProperty("queue");
            if(queueName!=null) {
                defaultChannel = connection.createChannel();
                defaultChannel.queueDeclarePassive( queueName );
                String type = getProperty("type"); 
                if ( type == null ) type = "direct";  
                String exchange = getProperty("exchange");                                
                if ( exchange != null ) {
                    defaultChannel.exchangeDeclare( exchange, type, true );
                    defaultChannel.queueBind( queueName, exchange, queueName);
                } 
                
                if ( allowReceive ) { 
                    MessageConsumer mc = new MessageConsumer(defaultChannel, null);
                    defaultChannel.basicConsume( queueName, true, mc);  
                } 
            } 
        } 
        catch(Throwable ex) {
            System.out.println("RabbitMQ Connection not started. "+ex.getMessage());
            ex.printStackTrace();            
        }
    } 

    private class MessageConsumer extends DefaultConsumer {
        private MessageHandler handler;
        private Base64Cipher base64 = new Base64Cipher(); 
        private String exchange;
        private String queueName;
        private boolean autoDelete = false;
        
        public MessageConsumer(Channel channel, MessageHandler handler) {
            super(channel);
            this.handler = handler;
        }

        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException { 
            if ( body == null || body.length==0 ) return; 
            String message = new String(body, "UTF-8");
            if ( base64.isEncoded( message ) ) {
                Object o = base64.decode( message ); 
                if (handler == null)
                    notifyHandlers(o);
                else 
                    handler.onMessage(o);
                
            } else { 
                if (handler == null)
                    notifyHandlers(message);
                else 
                    handler.onMessage(message);
            } 
            if (autoDelete){
                getChannel().queueUnbind(queueName, exchange, queueName);
                getChannel().queueDelete(queueName);
            }
        } 
        
        public void setAutoDeleteQueue(String exchange, String queueName){
            this.exchange = exchange;
            this.queueName = queueName;
            this.autoDelete = true;
        }
    }
    
    public void stop() {
        System.out.println("Stopping RabbitMQ Connection" + name );
        
        for(Channel channel: channels) {
            try {channel.close();}catch(Throwable ign){;}
        }
        
        try {
            if ( defaultChannel != null ) { 
                defaultChannel.close();
            } 
        } catch(Throwable ex) {;} 
        
        try { 
            connection.close();
        } catch(Throwable ign){;}
        
        super.stop();
    }

    private byte[] convertBytes( Object data, boolean encoded ) {
        if( encoded == true ) {
            String ret = null;
            Base64Cipher base64 = new Base64Cipher();
            if( data instanceof String ) {
                //check if already encoded
                if( !base64.isEncoded( data.toString() ) ) {
                    ret = base64.encode(data);
                }
                else {
                    ret = data.toString();
                }
            }
            else {
                ret = base64.encode(data);
            }
            return ret.getBytes();
        }
        else if( data instanceof String ) {
            return data.toString().getBytes();
        }
        else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {
              out = new ObjectOutputStream(bos);   
              out.writeObject(data);
              out.flush();
              return bos.toByteArray();
            } 
            catch(Exception e){
                throw new RuntimeException(e);
            }
            finally {
              try {
                bos.close();
              } catch (IOException ex) {
                // ignore close exception
              }
            }
            
        }
    }
    
    public void sendBytes( byte[] bytes, String queueName ) { 
        if ( !allowSend ) return; 
         
        Channel channel = null; 
        try {
            channel = connection.createChannel();
            try {                 
                String exchange = getProperty("exchange");
                if ( exchange == null) { 
                    channel.basicPublish("", queueName, null, bytes); 
                } else {
                    channel.queueDeclare( exchange, true, false, true, null);
                    channel.basicPublish( exchange, queueName, null, bytes);
                } 
            } catch (RuntimeException re) { 
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            }             
        }
        catch(Exception ex) { 
            ex.printStackTrace();
            throw new RuntimeException("Channel not created! " + queueName);
        }
        finally {
            try { channel.close(); } catch(Throwable ign){;}
        }
    }
     
    public void send( Object data ) { 
        sendBytes(  convertBytes(data, true), null ); 
    }

    public void send( Object data, String queueName ) {
        sendBytes(  convertBytes(data, true), queueName ); 
    }
     
    public void sendText(String data) {
        sendBytes( convertBytes(data,false), null );
    }

    public void sendText(String data, String queueName) {
        sendBytes(convertBytes(data,false) , queueName );
    }

    public void addQueue(String queueName) throws Exception {
        addQueue(queueName, null);
    }
    
    public void addQueue(String queueName, String exchange) throws Exception {
        Channel channel = connection.createChannel();
        if(exchange==null) {
            exchange = getProperty("exchange");
        }
        channel.queueDeclare( queueName, true, false, false, null );
        channel.exchangeDeclare(exchange, "direct", true);
        channel.queueBind( queueName, exchange, queueName);
        //add in queue so we can close it properly
        channels.add(channel);
    }
    
    public void removeQueue(String queueName){
         removeQueue(queueName, null);
    }
    
    public void removeQueue(String queueName, String exchange)  {
        Channel channel = null;
        try {
            channel = connection.createChannel();
            if(exchange==null) {
                exchange = getProperty("exchange");
            }
            channel.exchangeDeclare(exchange, "direct", true);
            channel.queueUnbind(queueName, exchange, queueName);
            channel.queueDelete(queueName);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try { channel.close(); } catch(Exception ign){;}
        }
    }
    
    /**************************************************************************
    * This is used for handling direct or P2P responses. The queue to create
    * will be a temporary queue.
    ***************************************************************************/ 
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception{
       String exchange = getProperty("exchange"); 
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, "direct", true );
        
        Map args = new HashMap();
        args.put("x-expires", 60000); 
        channel.queueDeclare( tokenid, false, false, false, args);
        channel.queueBind( tokenid, exchange, tokenid);
        MessageConsumer mc = new MessageConsumer(channel, handler);
        mc.setAutoDeleteQueue(exchange, tokenid);
        channel.basicConsume( tokenid, true, mc);              
    }

    public void send( Object data, String queueName, boolean encoded ) {
        sendBytes(  convertBytes(data, encoded), queueName ); 
    }
    
    
}
