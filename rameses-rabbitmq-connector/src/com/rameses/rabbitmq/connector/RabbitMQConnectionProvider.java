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
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import com.rameses.util.Base64Cipher;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class RabbitMQConnectionProvider extends XConnectionProvider {

    private final static String PROVIDER_NAME = "rabbitmq";
    private final static String DEFAULT_QUEUE_NAME = "default-queue";
            
    private ConnectionFactory factory; 
    private Connection connection;
    private Channel channel;
    
    private String queue;
    private Sender sender; 
    
    public String getProviderName() {
        return PROVIDER_NAME; 
    }

    public XConnection createConnection(String name, Map params ) { 
        try { 
            Config conf = new Config( params ); 
            return new RabbitMQClient( name, context, conf );
//            Channel channel = getChannel( conf );             
//            RabbitMQConnection mqc = new RabbitMQConnection(name, context, conf, sender ); 
//
//            if ( conf.isAllowReceive() ) {
//                String routingkey = conf.getRoutingKey();
//                channel.basicConsume( routingkey, true, new Receiver(channel, mqc));  
//            }
//            return mqc; 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    Channel getChannel( Config conf ) { 
        try { 
            if ( factory == null ) { 
                factory = new ConnectionFactory(); 
                factory.setHost( conf.getHost() ); 
                factory.setUsername( conf.getUserName() ); 
                factory.setPassword( conf.getPassword() ); 
                factory.setRequestedHeartbeat( conf.getHearbeat() ); 
            }  
            
            if ( connection == null ) { 
                connection = factory.newConnection(); 
            } 
            
            if ( channel == null ) { 
                channel = connection.createChannel(); 
                
                queue = conf.getQueueName(); 
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
                sender = new Sender( queue, exchange, routingkey );                 
            } 
            return channel; 
        } catch (RuntimeException re) { 
            throw re; 
        } catch (Exception e) { 
            throw new RuntimeException( e.getMessage(), e ); 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Receiver ">
    
    public class Receiver extends DefaultConsumer {
        
        private MessageConnection messageConn; 
        private Base64Cipher base64; 
        
        Receiver( Channel channel, MessageConnection messageConn ) { 
            super( channel ); 
            
            this.messageConn = messageConn; 
            this.base64 = new Base64Cipher();
        }
        
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException { 
            if ( body == null || body.length==0 ) return; 
            
            if ( messageConn != null ) { 
                String message = new String(body, "UTF-8");
                if ( base64.isEncoded( message ) ) {
                    Object o = base64.decode( message ); 
                    messageConn.notifyHandlers( o ); 
                } else { 
                    messageConn.notifyHandlers( message ); 
                } 
            } 
        } 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Sender ">
    
    public class Sender {
        
        private String queue; 
        private String exchange;
        private String routingkey;
        
        private Base64Cipher base64; 
        
        Sender( String queue, String exchange, String routingkey ) {
            this.queue = queue;
            this.exchange = exchange;
            this.routingkey = routingkey;
            this.base64 = new Base64Cipher(); 
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

    // <editor-fold defaultstate="collapsed" desc=" DisabledMode ">

    private class DisabledMode extends MessageConnection {

        private Map conf; 
        
        DisabledMode( Map conf ) {
            this.conf = conf; 
        }
        
        public void send(Object data) {
        }
        public void sendText(String data) {
        }
        public void start() {
        }
        public Map getConf() { 
            return conf; 
        }

        public synchronized void addHandler(MessageHandler mh) {
        }
        public synchronized void notifyHandlers(Object data) {
        }
    }
    
    // </editor-fold> 
    
}
