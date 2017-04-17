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
    private Config conf; 
    
    private Sender sender; 
    private boolean enabled;
    
    public RabbitMQConnection(String name, AbstractContext context, Config conf, Sender sender ) {
        this.context = context;
        this.name = name;
        this.conf = conf; 
        this.sender = sender; 
        this.enabled = (conf == null ? true: conf.isEnabled()); 
    }

    public Map getConf() { 
        return ( conf == null ? null: conf.getSource()); 
    }

    public void start() { 
        System.out.println("start rabbitmqconnection " + hashCode());
    } 

    public void stop() {
        System.out.println("stop rabbitmqconnection " + hashCode());
        super.stop();
    }

    public void send( Object data ) { 
        sender.send( data ); 
    }

    public void sendText( String data ) {
        sender.send( data ); 
    }
}
