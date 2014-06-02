/*
 * TopicChannel.java
 *
 * Created on March 3, 2013, 10:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class TopicChannel extends Channel 
{    
    private Set<WebSocket.Connection> connections = new CopyOnWriteArraySet();
    
    public TopicChannel(String name) {
        super(name);
    }
    
    public void addSocket(WebSocket.Connection conn) {
        connections.add( conn );
    }
    
    public void removeSocket(WebSocket.Connection conn) {
        connections.remove( conn );
    }
    
    public void send(String data) {
        for(WebSocket.Connection conn: connections) {
            try {
                conn.sendMessage( data );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void send(byte[] b, int offset, int len) {
        for(WebSocket.Connection conn: connections) {
            try {
                conn.sendMessage( b, offset, len );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //closes all connections
    public void close(int status, String msg ) {
        for(WebSocket.Connection conn: connections) {
            conn.close(status, msg);
        }
    }
}
