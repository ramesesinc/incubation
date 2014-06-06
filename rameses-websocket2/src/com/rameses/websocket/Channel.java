/*
 * Channel.java
 *
 * Created on January 3, 2013, 7:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public abstract class Channel 
{    
    private String name;
    private String id;
    private Map conf;
    
    public Channel(String name) {
        this(name, null);
    }
    
    public Channel(String name, Map conf) {
        this.name = name;
        this.id = "WSCHANNEL"+ new UID();        
        this.conf = (conf == null? new HashMap(): conf); 
    }    
    
    public abstract void addSocket(WebSocket.Connection conn);
    public abstract void removeSocket(WebSocket.Connection conn);
    public abstract void send(String data);
    public abstract void send(byte[] b, int offset, int len);
    public abstract void close(int status, String msg );

    public String getName() {
        return name;
    }
    
    public String getId() {
        return id; 
    }
    
    public Map getConf() {
        return conf; 
    }
    
    public String getGroup() {
        return getProperty("group"); 
    }
    
    public String getProperty(String name) {
        Map conf = getConf();
        Object value = (conf == null? null: conf.get(name));
        return (value == null? null: value.toString()); 
    }
}
