/*
 * BasicWebsocketHandler.java
 *
 * Created on January 21, 2013, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.Base64CoderImpl;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

/**
 *
 * @author Elmo
 */
public class BasicWebsocketHandler extends WebSocketHandler 
{
    private SocketConnections sockets;
    private Map conf; 
    
    private CacheMap cacheMap;    
    private Timer timer;
    
    public BasicWebsocketHandler(SocketConnections conn, Map conf) {
        this.sockets = conn;
        this.conf = conf;
        
        cacheMap = new CacheMap();         
    }
    
    /***
     * check the id here
     */
    public WebSocket doWebSocketConnect(HttpServletRequest hreq, String protocol) {
        //protocol format: <channel>;<encrypted_additional_headers>       
        String channel = null;
        String extended = null;
        int idx = protocol.indexOf(';'); 
        if (idx > 0) {
            channel = protocol.substring(0, idx); 
            extended = protocol.substring(idx+1);
        } else {
            channel = protocol; 
        } 
        
        Properties headers = toProperties(extended); 
        headers.setProperty("channel", channel); 
        
        //test first if channel exists before creating the websocket
        if(sockets.isChannelExist( channel )) {
            return new WebSocketMessageHandler(sockets, conf, headers, cacheMap);
        }
        else {
            throw new RuntimeException("Channel " + channel + " does not exist!");
        }
    }
    
    public void close() {
        cacheMap.close();
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private Properties toProperties(String text) {
        Properties props = new Properties();
        try { 
            byte[] bytes = new Base64CoderImpl().decode(text.toCharArray()); 
            Object obj = toObject(bytes); 
            if (obj instanceof Map) {
                props.putAll((Map) obj); 
            }
            return props;
        } catch(Throwable t) {
            return props;
        } 
    } 
    
    private Object toObject(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null; 
        
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes); 
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { ois.close(); }catch(Throwable t){;} 
            try { bais.close(); }catch(Throwable t){;} 
        }        
    }    
    
    // </editor-fold>
        
}
