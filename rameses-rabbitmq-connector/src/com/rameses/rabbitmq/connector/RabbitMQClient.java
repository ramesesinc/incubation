/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq.connector;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.util.Base64Cipher;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class RabbitMQClient extends MessageConnection {

    private String name;
    private Config conf;
    private AbstractContext context; 
    private ConnectionObject connObj;
    
    private Sender sender;
    private Receiver receiver; 
    
    public RabbitMQClient( String name, AbstractContext context, Config conf ) {
        this.name = name;
        this.conf = conf; 
        this.context = context;
    }
    
    public Map getConf() {
        return conf.getSource(); 
    }    

    public void start() { 
        System.out.println("start RabbitMQClient " + hashCode());
        reset(); 
    }
    
    public void stop() {
        System.out.println("stop RabbitMQClient " + hashCode()); 
        try { connObj.close(); } catch(Throwable t){;} 
        try { super.stop(); } catch(Throwable t){;}         
    } 
    
    private void reset() {
        try { connObj.close(); } catch(Throwable t){;} 
        
        connObj = new ConnectionObject(); 
        boolean enabled = conf.isEnabled();         
        if ( enabled && conf.isAllowSend() ) {
            sender = new Sender( connObj.getChannel(), conf); 
        } 
        if ( enabled && conf.isAllowReceive() ) {
            receiver = new Receiver( connObj.getChannel()); 
        }
    }
    
    public void send(Object data) {
        if ( sender != null ) {
            sender.send( data ); 
        }
    }

    public void sendText(String data) {
        if ( sender != null ) {
            sender.send( data );  
        }
    }

    
    // <editor-fold defaultstate="collapsed" desc=" ConnectionObject ">    
    
    private ConnectionFactory factory;     
    private ConnectionFactory getConnectionFactory() {
        if ( factory == null ) { 
            factory = new ConnectionFactory(); 
            factory.setHost( conf.getHost() ); 
            factory.setUsername( conf.getUserName() ); 
            factory.setPassword( conf.getPassword() ); 
            factory.setRequestedHeartbeat( conf.getHearbeat() ); 
        } 
        return factory; 
    }
    
    private class ConnectionObject {
        
        RabbitMQClient root = RabbitMQClient.this; 
        
        private Channel channel;
        private Connection connection;
        
        void close() { 
            try { channel.close(); } catch(Throwable t) {;} 
            try { connection.close(); } catch(Throwable t) {;} 
            
            channel = null; 
            connection = null; 
        }

        Channel getChannel() {
            try { 
                if ( connection == null ) {
                    connection = root.getConnectionFactory().newConnection(); 
                }
                
                if ( channel == null ) { 
                    channel = connection.createChannel(); 

                    Config conf = root.conf; 
                    String queue = conf.getQueueName(); 
                    if ( queue != null ) {
                        channel.queueDeclarePassive( queue ); 
                    }

                    String routingkey = conf.getRoutingKey();
                    String exchange = conf.getExchangeName();
                    if ( exchange != null && exchange.trim().length() > 0 ) {
                        String type = conf.getType();
                        channel.exchangeDeclare( exchange, type, true );
                        //channel.queueBind( queue, exchange, channelgroup);
                    } 
                } 
                return channel; 
            } catch (RuntimeException re) { 
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            } 
        }
    }
    
    // </editor-fold>         
    
    // <editor-fold defaultstate="collapsed" desc=" Sender ">
    
    public class Sender {
        
        RabbitMQClient root = RabbitMQClient.this; 
        
        private Config config;
        private Channel channel;
        private Base64Cipher base64; 
        
        private String queue; 
        private String exchange;
        private String routingkey;
        
        
        Sender( Channel channel, Config config ) { 
            this.channel = channel;
            this.config = config; 
            this.base64 = new Base64Cipher(); 
            this.queue = config.getQueueName(); 
            this.exchange = config.getExchangeName();
            this.routingkey = config.getRoutingKey();
        }
        
        public void send( Object message ) { 
            send( base64.encode( message ).getBytes() );  
        } 
        
        public void send( String message ) { 
            if ( base64.isEncoded( message )) {
                send( message.getBytes() ); 
            } else {
                send( base64.encode( message ).getBytes() );
            }
        } 
        
        public void send( byte[] message ) { 
            try { 
                if ( exchange == null) { 
                    channel.basicPublish("", queue, null, message); 
                } else { 
                    channel.basicPublish( exchange, routingkey, null, message);
                } 
            } catch (RuntimeException re) { 
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            } 
        }
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" Receiver ">
    
    public class Receiver extends DefaultConsumer {
        
        RabbitMQClient root = RabbitMQClient.this; 
        
        private Base64Cipher base64; 
        
        Receiver( Channel channel ) { 
            super( channel ); 

            this.base64 = new Base64Cipher();
        }
        
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException { 
            if ( body == null || body.length==0 ) return; 
            
            String message = new String(body, "UTF-8");
            if ( base64.isEncoded( message ) ) {
                Object o = base64.decode( message ); 
                root.notifyHandlers( o ); 
            } else { 
                root.notifyHandlers( message ); 
            } 
        } 

        public void handleShutdownSignal(String tag, ShutdownSignalException sig) {
            System.out.println("handleShutdownSignal: tag=" + tag);
            System.out.println("   message-> "+ sig.getMessage());
            System.out.println("   reason-> "+ sig.getReason());
            //root.reset(); 
        }
    } 
    
    // </editor-fold> 
    
}
