/*
 * Channel.java
 *
 * Created on January 3, 2013, 7:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.io.IOException;
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
        this.id = "WSCHANNEL" + new UID();        
        this.conf = (conf == null? new HashMap(): conf); 
    }    
    
    //public abstract void addSocket(WebSocket.Connection conn);
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
    
    
    public abstract ChannelGroup addGroup(String name); 
    public abstract ChannelGroup getGroup(String name); 
    
    
    public static class Connection 
    {
        private WebSocket.Connection conn;
        private String connid;
        
        public Connection(WebSocket.Connection conn, String connid) {
            this.conn = conn;
            this.connid = connid;
        }
        
        //public WebSocket.Connection getConnection() { return conn; } 
        public String getConnectionId() { return connid; } 
        public final boolean isClosed() { 
            return (conn == null); 
        } 
        
        public boolean accept(WebSocket.Connection source) {
            if (conn == null && source == null) {
                return true;
            } else if (conn != null) {
                return conn.equals(source); 
            } else if (source != null) {
                return source.equals(conn); 
            } else {
                return false; 
            }
        }
        
        public void send(String data) throws IOException {
            if (conn != null) conn.sendMessage( data ); 
        }
        
        public void send(byte[] b, int offset, int len) throws IOException {
            if (conn != null) conn.sendMessage(b, offset, len); 
        }
        
        public void close(int status, String msg ) {
            if (conn == null) return;
            
            try { 
                conn.close(status, msg);
            } catch(Throwable t) {
                //do nothing 
            } finally {
                conn = null; 
            }
        }

//        public boolean equals(Object obj) {
//            if (obj == null) return false; 
//            if (obj instanceof WebSocket.Connection) {
//                return obj.equals( getConnection() ); 
//            } else if (obj instanceof Channel.ConnectionInfo) {
//                Channel.ConnectionInfo info = (Channel.ConnectionInfo)obj;
//                if (info.getConnection() == null && getConnection() == null) {
//                    return true; 
//                } else if (getConnection() != null) {
//                    return getConnection().equals(info.getConnection()); 
//                } else if (info.getConnection() != null) {
//                    return info.getConnection().equals(getConnection()); 
//                } else {
//                    return false; 
//                }
//            } else {
//                return super.equals(obj); 
//            }
//        }
    }
}
