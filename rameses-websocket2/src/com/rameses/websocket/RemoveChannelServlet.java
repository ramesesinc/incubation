/*
 * AddChannelServlet.java
 *
 * Created on May 18, 2013, 3:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class RemoveChannelServlet extends HttpServlet {
    
    private SocketConnections sockets;
    
    public RemoveChannelServlet(SocketConnections s) {
        this.sockets = s;
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        String channelName = req.getParameter("channel");
        sockets.removeChannel(channelName);
        System.out.println("channel "+channelName +" removed");
    }
}
