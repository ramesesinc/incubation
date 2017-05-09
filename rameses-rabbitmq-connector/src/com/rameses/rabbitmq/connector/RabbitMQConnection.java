/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq.connector;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.rabbitmq.connector.RabbitMQConnectionProvider.Sender;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class RabbitMQConnection extends MessageConnection {
    
    private AbstractContext context;     
    private String name;
    private Map conf; 
    
    private Sender sender; 
    private boolean enabled;
    
    public RabbitMQConnection(String name, AbstractContext context, Map conf, Sender sender ) {
        this.context = context;
        this.name = name;
        this.conf = conf; 
        this.sender = sender; 
        
        if ("false".equals( getProperty("enabled")+"")) { 
            enabled = false; 
        } else {
            enabled = true; 
        }
    }

    public Map getConf() { 
        return conf; 
    }

    public void start() { 
    } 

    public void stop() {
        super.stop();
    }
    
    private String getProperty( String name ) {
        Object o = (conf == null? null: conf.get(name)); 
        return ( o == null ? null: o.toString()); 
    }

    public void send( Object data ) { 
        send( data, null ); 
    }

    public void send( Object data, String channelgroup ) { 
        sender.send( data, channelgroup ); 
    }
    
    public void sendText( String data ) {
        sendText( data, null ); 
    }
    public void sendText( String data, String channelgroup ) {
        sender.send( data, channelgroup ); 
    }    
}
