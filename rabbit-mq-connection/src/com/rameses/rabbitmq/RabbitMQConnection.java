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
import com.rameses.util.Base64Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private ChannelSet channelSet;
    private Channel defaultChannel;
    private boolean started;
    
    public RabbitMQConnection(String name, AbstractContext context, Map conf) {
        this.name = name;
        this.context = context;
        this.conf = conf; 
        if ("false".equals( getProperty("enabled")+"")) { 
            enabled = false; 
        } else {
            enabled = true; 
        }
    }

    private String getProperty( String name ) {
        Object o = (conf == null? null: conf.get(name)); 
        return ( o == null ? null: o.toString()); 
    }
     
    public Map getConf() { 
        return conf; 
    }

    public void start() { 
        if(started) return;
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
            channelSet = new ChannelSet(connection);
            
            //check if there is a channel specified. If there is, then you must listen.
            String queueName = getProperty("queue");
            if(queueName!=null) {
                defaultChannel = channelSet.getChannel(queueName);
                defaultChannel.queueDeclarePassive( queueName );
                String type = getProperty("type"); 
                if ( type == null ) throw new Exception("type is required");  
                String exchange = getProperty("exchange");                                
                if ( exchange != null ) {
                    defaultChannel.exchangeDeclare( exchange, type, true );
                    defaultChannel.queueBind( queueName, exchange, queueName);
                } 
                MessageConsumer mc = new MessageConsumer(defaultChannel);
                defaultChannel.basicConsume( queueName, true, mc);  
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("RabbitMQ Connection not started. "+ex.getMessage());
        }
    } 

    private class MessageConsumer extends DefaultConsumer {
        private Base64Cipher base64 = new Base64Cipher(); 
        public MessageConsumer(Channel channel) {
            super(channel);
        }
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException { 
            if ( body == null || body.length==0 ) return; 
            String message = new String(body, "UTF-8");
            if ( base64.isEncoded( message ) ) {
                Object o = base64.decode( message ); 
                notifyHandlers( o ); 
            } else { 
                notifyHandlers( message ); 
            } 
        } 
    }
    
    
    public void stop() {
        channelSet.close();
        try {
            connection.close();
        }
        catch(Exception ign){;}
        super.stop();
        System.out.println("Stopping RabbitMQ Connection" + name );
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
        try {
            Channel channel = defaultChannel;
            if(queueName!=null) {
                channel = channelSet.getChannel(queueName);
            }
            try { 
                String exchange = getProperty("exchange");
                if ( exchange == null) { 
                    channel.basicPublish("", queueName, null, bytes); 
                } else { 
                    channel.queueDeclare(exchange, true, true, true, null);
                    channel.basicPublish( exchange, queueName, null, bytes);
                } 
            } catch (RuntimeException re) { 
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            }             
        }
        catch(Exception ex) {
            throw new RuntimeException("Channel not created! " + queueName);
        }
    }
     
    public void send( Object data ) { 
        sendBytes(  convertBytes(data, true), null ); 
    }

    public void send( Object data, String queueName ) {
        sendBytes(  convertBytes(data, true), queueName ); 
    }
     
    public void send( Object data, String queueName, boolean encoded ) {
        sendBytes(  convertBytes(data, encoded), queueName ); 
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
        Channel channel = channelSet.getChannel(queueName);
        if(exchange==null) {
            exchange = getProperty("exchange");
        }
        channel.queueDeclare( queueName, true, false, true, null );
        channel.exchangeDeclare(exchange, "direct", true);
        channel.queueBind( queueName, exchange, queueName);
    }
    
    public void removeQueue(String queueName) throws Exception {
         removeQueue(queueName, null);
    }
    
    public void removeQueue(String queueName, String exchange) throws Exception {
        Channel channel = channelSet.getChannel(queueName);
        if(exchange==null) {
            exchange = getProperty("exchange");
        }
        channel.exchangeDeclare(exchange, "direct", true);
        channel.queueUnbind(queueName, exchange, queueName);
        channel.queueDelete(queueName);
    }
}
