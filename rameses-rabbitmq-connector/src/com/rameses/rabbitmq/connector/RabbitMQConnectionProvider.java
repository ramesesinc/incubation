/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq.connector;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    
    public String getProviderName() {
        return PROVIDER_NAME; 
    }

    public XConnection createConnection(String name, Map conf) { 
        
        try {
            RabbitMQConnection mqc = new RabbitMQConnection(name, context, conf ); 
            Channel channel = getChannel( conf ); 
            channel.basicConsume( queue, true, new Receiver(channel, mqc));  
            return mqc; 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    Channel getChannel( Map conf ) { 
        try { 
            if ( factory == null ) { 
                factory = new ConnectionFactory(); 
                factory.setHost( (String) conf.get("host") ); 
                factory.setUsername( (String) conf.get("user") ); 
                factory.setPassword( (String) conf.get("pwd") ); 

                int heartbeat = 60; 
                try { 
                    heartbeat = Integer.parseInt(conf.get("heartbeat").toString()); 
                } catch(Throwable t) {;} 

                factory.setRequestedHeartbeat( heartbeat ); 
            }  
            
            if ( connection == null ) { 
                connection = factory.newConnection(); 
            } 
            
            if ( channel == null ) { 
                channel = connection.createChannel(); 
                
                queue = (String) conf.get("queue"); 
                if ( queue != null ) {
                    channel.queueDeclarePassive( queue ); 
                }
                
                String channelgroup = (String) conf.get("channelgroup"); 
                String exchange = (String) conf.get("exchange");
                
                if ( exchange != null ) {
                    channel.exchangeDeclare( exchange, "direct", true );
                    channel.queueBind( queue, exchange, channelgroup);
                }
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
        
        Receiver( Channel channel, MessageConnection messageConn ) { 
            super( channel ); 
            
            this.messageConn = messageConn; 
        }
        
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException { 
            String message = new String(body, "UTF-8");
            System.out.println(" [x] Received: '" + message + ";");
            
            if ( messageConn != null ) { 
                messageConn.notifyHandlers( message ); 
            } 
        } 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Sender ">
    
    public class Sender {
        
        private String queue; 
        private String exchange;
        private String channelgroup;
        
        Sender( String queue, String exchange, String channelgroup ) {
            this.queue = queue;
            this.exchange = exchange;
            this.channelgroup = channelgroup;
        }
        
        public void send( Object message ) { 
            ByteArrayOutputStream baos = null; 
            ObjectOutputStream oos = null; 
            byte[] bytes = null; 
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream( baos ); 
                oos.writeObject( message ); 
                oos.flush(); 
                bytes = baos.toByteArray(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            } finally {
                try { oos.close(); }catch(Throwable t){;}
                try { baos.close(); }catch(Throwable t){;}
            }
            
            send( bytes ); 
        }
        
        public void send( String message ) { 
            send( message.getBytes() ); 
        }
        
        public void send( byte[] message ) { 
            try { 
                if ( exchange == null) { 
                    channel.basicPublish("", queue, null, message); 
                } else { 
                    channel.basicPublish( exchange, channelgroup, null, message);
                } 
            } catch (RuntimeException re) { 
                throw re; 
            } catch (Exception e) { 
                throw new RuntimeException( e.getMessage(), e ); 
            }                
        }
    }
    
    // </editor-fold> 
    
}
