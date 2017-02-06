/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq.connector;

import com.rabbitmq.client.Channel;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.util.Base64Cipher;
import java.util.Map;

/**
 *
 * @author dell
 */
public class RemoteServiceSenderConnection extends MessageConnection {
    
    private AbstractContext context;     
    private String name;
    private Map conf; 
    
    private Base64Cipher base64;
    private boolean enabled;
    private Channel channel;
    private String exchange;
    private String queue;
    
    public RemoteServiceSenderConnection(String name, AbstractContext context, Map conf ) {
        this.base64 = new Base64Cipher(); 
        this.context = context; 
        this.name = name; 
        this.conf = conf; 
        
        if ("false".equals( getProperty("enabled")+"")) { 
            enabled = false; 
        } else {
            enabled = true; 
        }
        
        this.exchange = (String) conf.get("exchange");
        this.queue = (String) conf.get("queue"); 
    }
    
    void setChannel(Channel channel) {
        this.channel = channel; 
    }    

    public Map getConf() { 
        return conf; 
    }

    public void start() { 
        System.out.println("starting remote service");
    } 

    public void stop() {
        super.stop();
    }
    
    private String getProperty( String name ) {
        Object o = (conf == null? null: conf.get(name)); 
        return ( o == null ? null: o.toString()); 
    }

    public void send( Object data ) { 
        send( data, this.queue );  
    }

    public void send( Object data, String routekey ) { 
        sendImpl( base64.encode( data ).getBytes(), routekey );  
    }

    
    public void sendText( String data ) {
        sendText( data, this.queue ); 
    }
    public void sendText( String data, String routekey ) {
        if ( base64.isEncoded( data )) {
            sendImpl( data.getBytes(), routekey ); 
        } else {
            sendImpl( base64.encode( data ).getBytes(), routekey );
        }
    }
    
    private void sendImpl( byte[] message, String routekey ) { 
        try { 
            if ( exchange == null) { 
                channel.basicPublish("", this.queue, null, message); 
            } else { 
                channel.basicPublish( exchange, routekey, null, message);
            } 
        } catch (RuntimeException re) { 
            throw re; 
        } catch (Exception e) { 
            throw new RuntimeException( e.getMessage(), e ); 
        }                
    }
}
