/*
 * WebSocketMessageHandler.java
 *
 * Created on January 21, 2013, 3:16 PM
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
public class WebSocketMessageHandler implements WebSocket, WebSocket.OnTextMessage, WebSocket.OnBinaryMessage {
    
    private String protocol;
    private SocketConnections sockets;
    private WebSocket.Connection connection;
    private Channel channel;
    
    public WebSocketMessageHandler(String protocol, SocketConnections conn) {
        this.sockets = conn;
        this.protocol = protocol;
    }
    
    public void onOpen(WebSocket.Connection connection) {
        try {
            //on open add the channel if not yet exis
            this.connection = connection;
            this.channel = sockets.getChannel( connection.getProtocol() );
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
    
    
}
