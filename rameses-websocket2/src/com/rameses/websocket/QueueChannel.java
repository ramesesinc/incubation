/*
 * QueueChannel.java
 *
 * Created on March 3, 2013, 10:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class QueueChannel extends Channel {
    
    private LinkedBlockingQueue<WebSocket.Connection> queue = new LinkedBlockingQueue();
    
    public QueueChannel(String name) {
        this(name, null); 
    }
    
    public QueueChannel(String name, Map conf) {
        super( name, conf );
    }    
    
    public void addSocket(WebSocket.Connection conn) {
        queue.add( conn );
    }
    
    public void removeSocket(WebSocket.Connection conn) {
        queue.remove( conn );
    }
    
    public void send(String data) {
        WebSocket.Connection conn = queue.poll();
        if(conn!=null) {
            try {
                conn.sendMessage( data );
                //send back to pool for reuse
                queue.add( conn );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    public void send(byte[] b, int offset, int len) {
        WebSocket.Connection conn = queue.poll();
        if(conn!=null) {
            try {
                conn.sendMessage( b, offset, len );
                queue.add( conn );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //closes all connections
    public void close(int status, String msg ) {
        WebSocket.Connection conn = null;
        while( (conn=queue.poll())!=null ) {
            conn.close(status, msg);
        }
    }
    
}
