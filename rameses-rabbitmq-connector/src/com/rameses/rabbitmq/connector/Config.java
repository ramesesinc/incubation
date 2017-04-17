/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rabbitmq.connector;

import java.util.Map;

/**
 *
 * @author wflores 
 */
class Config {
    
    private Map conf; 

    Config( Map conf ) {
        this.conf = conf; 
    }

    private Object get( Object key ) {
        return (conf == null? null: conf.get(key)); 
    }
    private String getString( Object key ) {
        Object value = get( key );
        return (value == null? null: value.toString()); 
    }
    private Boolean getBool( Object key ) {
        try { 
            return new Boolean( getString(key).toString() ); 
        } catch(Throwable t) {
            return null; 
        }
    }

    public Map getSource() {
        return conf; 
    }
    public String getHost() {
        return getString( "host" ); 
    }
    public String getUserName() {
        return getString( "user" ); 
    }
    public String getPassword() {
        return getString( "pwd" ); 
    }
    public String getQueueName() {
        return getString("queue");
    }
    public String getExchangeName() {
        return getString("exchange");
    }
    public String getRoutingKey() {
        String key = getString("routingkey"); 
        if ( key==null || key.trim().length()==0 ) {
            return getString("channelgroup"); 
        } else {
            return key; 
        }
    }
    public String getType() {
        String type = getString("type"); 
        if ( type == null || type.trim().length()==0 ) {
            return "direct"; 
        } else {
            return type; 
        }
    }        
    public int getHearbeat() {
        try { 
            return new Integer(getString("heartbeat")).intValue(); 
        } catch(Throwable t) {
            return 60; 
        }
    }
    public boolean isDurable() {
        Boolean bool = getBool("durable"); 
        if ( bool != null && bool.booleanValue()==false ) {
            return false; 
        } else {
            return true; 
        } 
    }     
    public boolean isEnabled() {
        Boolean bool = getBool("enabled"); 
        if ( bool != null && bool.booleanValue()==false ) {
            return false; 
        } else {
            return true; 
        } 
    }    
    public boolean isAllowSend() {
        Boolean bool = getBool("allowsend"); 
        if ( bool != null && bool.booleanValue()==false ) {
            return false; 
        } else {
            return true; 
        } 
    }  
    public boolean isAllowReceive() {
        Boolean bool = getBool("allowreceive"); 
        if ( bool != null && bool.booleanValue()==true ) {
            return true; 
        } else {
            return false; 
        } 
    }      
}
