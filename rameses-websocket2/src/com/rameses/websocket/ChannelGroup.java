/*
 * ChannelGroup.java
 *
 * Created on July 24, 2014, 12:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author wflores
 */
public abstract class ChannelGroup 
{    
    private String name;
    
    public ChannelGroup(String name) {
        this.name = name;
    }    
    
    public String getName() { 
        return name; 
    }
    
    public abstract void addSocket(WebSocket.Connection conn);
    public abstract void addSocket(WebSocket.Connection conn, String connid);
    public abstract void removeSocket(WebSocket.Connection conn);
    public abstract void send(String data);
    public abstract void send(byte[] b, int offset, int len);
    public abstract void close(int status, String msg );
}
