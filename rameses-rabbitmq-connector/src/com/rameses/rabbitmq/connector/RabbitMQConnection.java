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
 * @author wflores 
 */
public class RabbitMQConnection extends MessageConnection {
    
    private AbstractContext context;     
    private String name;
    private Map conf; 
    
    private boolean enabled;
    
    public RabbitMQConnection(String name, AbstractContext context, Map conf ) {
        this.context = context;
        this.name = name;
        this.conf = conf; 
        
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

    public void send(Object data) {
    }

    public void sendText(String data) {
    }
}
