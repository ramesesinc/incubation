/*
 * Channel.java
 *
 * Created on January 3, 2013, 7:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public abstract class Channel {
    
    private String name;
     
    public Channel(String name) {
        this.name = name;
    }
    
    public abstract void addSocket(WebSocket.Connection conn);
    public abstract void removeSocket(WebSocket.Connection conn);
    public abstract void send(String data);
    public abstract void send(byte[] b, int offset, int len);
    public abstract void close(int status, String msg );

    public String getName() {
        return name;
    }
    
}
