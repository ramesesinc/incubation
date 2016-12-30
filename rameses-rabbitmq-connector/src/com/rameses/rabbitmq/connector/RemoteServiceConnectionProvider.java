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
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.util.Base64Cipher;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class RemoteServiceConnectionProvider extends XConnectionProvider {

    private final static String PROVIDER_NAME = "remote-service";
    private final static String DEFAULT_QUEUE_NAME = "default-queue";
            
    private ConnectionFactory factory; 
    private Connection connection;
    private Channel channel;
    
    private String queue;
    private Sender sender; 
    
    public String getProviderName() {
        return PROVIDER_NAME; 
    }

    public XConnection createConnection(String name, Map conf) { 
        try { 
            System.out.println("remote service connection create...");
            Channel channel = getChannel( conf ); 
            RemoteServiceConnection mqc = new RemoteServiceConnection(name, context, conf, sender ); 
            channel.basicConsume( queue, true, new Receiver(channel, mqc, conf));  
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
                    //channel.queueBind( queue, exchange, channelgroup);
                } 
                sender = new Sender( queue, exchange, channelgroup );                 
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
        private Map conf;
        
        Receiver( Channel channel, MessageConnection messageConn, Map conf ) { 
            super( channel ); 
            
            this.messageConn = messageConn; 
            this.base64 = new Base64Cipher();
            this.conf = conf;
        }
        
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException { 
            if ( body == null || body.length==0 ) return; 

            System.out.println("remote service connection receive...");            
            String message = new String(body, "UTF-8");
            System.out.println("Message string is " + message); 
            if ( base64.isEncoded( message ) ) {
                Map mo = (Map) base64.decode( message ); 
                Map appenv = new HashMap();
                
                Object cluster = conf.get("app.cluster"); 
                appenv.put("app.cluster", (cluster==null? "osiris3": cluster));
                appenv.put("app.context", conf.get("app.context"));
                appenv.put("app.host", conf.get("app.host"));
                ScriptServiceContext ssc = new ScriptServiceContext(appenv);
                
                String requestId = (String)mo.get("requestId"); 
                String serviceName = (String) mo.get("serviceName");
                String methodName = (String) mo.get("methodName"); 
                Object arg = mo.get("args"); 
                ServiceProxy proxy = ssc.create("remote/"+ serviceName);
                try { 
                    Object result = proxy.invoke( methodName, (arg instanceof Object[] ? (Object[])arg : new Object[]{arg}));
                    System.out.println( "result is " + result );
                    
                    Map resmap = new HashMap(); 
                    resmap.put("requestId", requestId); 
                    resmap.put("queue", conf.get("queue"));
                    resmap.put("exchange", conf.get("exchange"));
                    resmap.put("result", result); 
                    
                    cluster = conf.get("response.cluster"); 
                    appenv.put("app.cluster", (cluster==null? "osiris3": cluster));
                    appenv.put("app.context", conf.get("response.context"));
                    appenv.put("app.host", conf.get("response.host"));
                    ssc = new ScriptServiceContext(appenv); 
                    proxy = ssc.create("RemoteServiceResponse"); 
                    proxy.invoke("post", new Object[]{ resmap });
                } catch (Exception ex) {
                    ex.printStackTrace(); 
                }
            } 
            
        } 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Sender ">
    
    public class Sender {
        
        private String queue; 
        private String exchange;
        private String channelgroup;
        
        private Base64Cipher base64; 
        
        Sender( String queue, String exchange, String channelgroup ) {
            this.queue = queue;
            this.exchange = exchange;
            this.channelgroup = channelgroup;
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
