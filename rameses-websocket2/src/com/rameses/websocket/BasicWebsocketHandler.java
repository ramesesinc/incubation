/*
 * BasicWebsocketHandler.java
 *
 * Created on January 21, 2013, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

/**
 *
 * @author Elmo
 */
public class BasicWebsocketHandler extends WebSocketHandler {
    
    private SocketConnections sockets;
    
    /** Creates a new instance of BasicWebsocketHandler */
    public BasicWebsocketHandler(SocketConnections conn) {
        this.sockets = conn;
    }

    /***
     * check the id here
     */
    public WebSocket doWebSocketConnect(HttpServletRequest hreq, String protocol) {
        //tes first if channel exists before creating the websocket
        if(sockets.isChannelExist( protocol )) {
            return new WebSocketMessageHandler(protocol, sockets);
        }
        else {
            throw new RuntimeException("Channel " + protocol + " does not exist!");
        }
    }
    
}
