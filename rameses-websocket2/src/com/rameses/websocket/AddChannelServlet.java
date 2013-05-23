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
public class AddChannelServlet extends HttpServlet {
    
    private SocketConnections sockets;
    
    public AddChannelServlet(SocketConnections s) {
        this.sockets = s;
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String channelName = req.getParameter("channel");
        String type = req.getParameter("type");
        Channel channel = null;
        if(type!=null && type.equalsIgnoreCase("queue")) {
            channel = new QueueChannel(channelName);
        }
        else {
            channel = new TopicChannel(channelName);
        }
        sockets.addChannel(channel);
        System.out.println("channel "+channelName +" added");
    }
}
