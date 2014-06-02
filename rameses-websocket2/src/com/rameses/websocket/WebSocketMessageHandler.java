/*
 * WebSocketMessageHandler.java
 *
 * Created on January 21, 2013, 3:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.http.HttpClient;
import com.rameses.util.AccessDeniedException;
import com.rameses.util.ExceptionManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class WebSocketMessageHandler implements WebSocket, WebSocket.OnTextMessage, WebSocket.OnBinaryMessage 
{
    private SocketConnections sockets;    
    private Map conf;
    private Properties headers;
    private Map cache;

    private String channelName;
    private String connectionid;
    
    private WebSocket.Connection connection;
    private Channel channel;
    
    public WebSocketMessageHandler(SocketConnections conn, Map conf, Properties headers, Map cache) {
        this.sockets = conn; 
        this.conf = conf; 
        this.cache = cache;         
        this.headers = headers;         
        this.channelName = headers.getProperty("channel"); 
        this.connectionid = headers.getProperty("connectionid");
    }
    
    public void onOpen(WebSocket.Connection connection) {
        //on open add the channel if not yet exis
        //System.out.println("onOpen: connectionid="+connectionid + ", channel="+channelName);
        boolean authenticated = (cache.get(connectionid) != null); 
        boolean authEnabled = "true".equals(conf.get("auth.enabled")+"");        
        if (authEnabled && !authenticated) {
            try { 
                authenticate(); 
                cache.put(connectionid, connectionid); 
            } catch(Exception e) { 
                Exception x = ExceptionManager.getOriginal(e);
                if (x instanceof AccessDeniedException) {
                    System.out.println("[WebSocketMessageHandler, "+channelName+"] " + x.getMessage());
                    connection.close(1002, "AUTH_FAILED");
                } else {
                    connection.close(1000, "Failed caused by " + x.getMessage());
                } 
            } 
        } 
        
        try {
            this.connection = connection; 
            this.channel = sockets.getChannel( channelName );
            this.channel.addSocket( this.connection );
        } catch(ChannelNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * if closing was due to a timeout, do not remove the channel yet as
     * it will still attempt to reconnect otherwise some messages might be missed.
     *
     */
    public void onClose(int i, String msg) {
        //System.out.println("onClose: connectionid="+connectionid + ", channel="+channelName);
        if(this.connection!=null) {
            channel.removeSocket( this.connection );
            this.connection = null;
        }
    }
    
    public void onMessage(String data) {
        channel.send( data );
    }
    
    public void onMessage(byte[] b, int i, int i0) {
        channel.send( b, i, i0 );
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private void authenticate() throws Exception {
        String authCluster = (String)conf.get("auth.cluster");        
        String authContext = (String)conf.get("auth.context");
        String authPort = (String)conf.get("auth.port");
        if (authCluster == null) authCluster = "osiris3";
        if (authContext == null) authContext = "default";
        if (authPort == null) authPort = "8070"; 
        
        String authPath = authCluster +"/services/"+ authContext  +"/WebsocketService.authenticate";
        HttpClient httpc = new HttpClient("localhost:"+authPort, true);
        httpc.post(authPath, new Object[]{ new Object[]{headers}, new HashMap()}); 
    }    
    
    // </editor-fold>
    
}
