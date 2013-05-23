/*
 * AddChannelServlet.java
 *
 * Created on March 2, 2013, 12:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class SendMessageServlet extends HttpServlet {
    
    private SocketConnections sockets;
    
    public SendMessageServlet(SocketConnections s) {
        this.sockets = s;
    }
    
    private Map buildParams(HttpServletRequest hreq) {
        Map params = new HashMap();
        Enumeration e = hreq.getParameterNames();
        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            params.put( name, hreq.getParameter(name) );
        }
        return params;
    }
    
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            String channel = req.getParameter("channel") ;
            String msg = req.getParameter("msg");
            Map params = buildParams( req );
            
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject( params );
            bos.flush();
            byte[] bytes = bos.toByteArray();
            sockets.getChannel(channel).send( bytes, 0, bytes.length );
        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            try {bos.close();} catch(Exception ign){;}
            try {oos.close();} catch(Exception ign){;}
        }
    }
    
}
