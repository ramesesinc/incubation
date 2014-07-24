/*
 * QueueChannel.java
 *
 * Created on March 3, 2013, 10:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.util.Map;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class QueueChannel extends Channel {
    
    private QueueChannelGroup defaultgroup = new QueueChannelGroup("default");
    
    public QueueChannel(String name) {
        this(name, null); 
    }
    
    public QueueChannel(String name, Map conf) {
        super( name, conf );
    }    
    
    public ChannelGroup addGroup(String name) {
        return defaultgroup;
    }
    
    public ChannelGroup getGroup(String name) {
        return defaultgroup;
    }        
        
    public void removeSocket(WebSocket.Connection conn) {
        defaultgroup.removeSocket(conn); 
    }
    
    public void send(String data) {
        defaultgroup.send( data );
    }
    
    public void send(byte[] b, int offset, int len) {
        defaultgroup.send(b, offset, len); 
    }
    
    //closes all connections
    public void close(int status, String msg ) {
        defaultgroup.close(status, msg); 
    }
}
