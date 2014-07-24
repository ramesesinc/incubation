/*
 * TopicChannel.java
 *
 * Created on March 3, 2013, 10:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class TopicChannel extends Channel 
{    
    private Hashtable<String,ChannelGroup> groups = new Hashtable();
        
    public TopicChannel(String name) {
        this(name, null);
    }
    
    public TopicChannel(String name, Map conf) {
        super(name, conf);
    }    
    
    public synchronized ChannelGroup addGroup(String name) {
        if (name == null) name = "default";
        
        String keyname = name.toLowerCase();
        ChannelGroup grp = groups.get(keyname); 
        if (grp != null) return grp;
        
        grp = new TopicChannelGroup(keyname);
        groups.put(keyname, grp);
        return grp; 
    }
    
    public ChannelGroup getGroup(String name) {
        if (name == null) name = "default";
        
        return groups.get(name.toLowerCase());
    }    
    
    public void send(String data) {
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg != null) cg.send( data ); 
        }
    }
    
    public void send(byte[] b, int offset, int len) {
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg != null) cg.send(b, offset, len); 
        }
    }
    
    //closes all connections
    public void close(int status, String msg ) {
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg != null) cg.close(status, msg); 
        }
    }

    public void removeSocket(WebSocket.Connection conn) {
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg != null) cg.removeSocket(conn); 
        }        
    }
}
