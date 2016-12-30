/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq.connector;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import java.util.Map;

/**
 *
 * @author dell
 */
public class RemoteServiceConnection extends MessageConnection {
    
    private AbstractContext context;     
    private String name;
    private Map conf; 
    
    private RemoteServiceConnectionProvider.Sender sender; 
    private boolean enabled;
    
    public RemoteServiceConnection(String name, AbstractContext context, Map conf, RemoteServiceConnectionProvider.Sender sender ) {
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
        sender.send( data ); 
    }

    public void sendText( String data ) {
        sender.send( data ); 
    }
}
