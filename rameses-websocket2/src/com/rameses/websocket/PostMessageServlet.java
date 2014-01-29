/*
 * PostMessageServlet.java
 *
 * Created on January 26, 2014, 14:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.SealedMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wflores
 */
public class PostMessageServlet extends HttpServlet 
{    
    private SocketConnections sockets;
    
    public PostMessageServlet(SocketConnections s) {
        this.sockets = s;
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            in = new ObjectInputStream(req.getInputStream());
            
            Object o = in.readObject();
            if (o instanceof SealedMessage) { 
                o = ((SealedMessage)o).getMessage(); 
            } 
            Collection collection = null;
            if (o instanceof Collection) { 
                collection = (Collection) o; 
            } else if (o instanceof Object[]) { 
                collection = Arrays.asList((Object[]) o);
            } 
            if (collection == null) return;

            Iterator itr = collection.iterator(); 
            while (itr.hasNext()) {
                Map map = (Map) itr.next();
                String action = map.remove("action")+"";
                if ("addchannel".equals(action)) { 
                    processAddChannelAction(map); 
                } else if ("removechannel".equals(action)) {
                    processRemoveChannelAction(map);
                } else {
                    processSendAction(map); 
                }
            }
            
            out = new ObjectOutputStream(resp.getOutputStream());
            out.writeObject("OK"); 
            
        } catch(IOException ioe) { 
            throw ioe; 
        } catch(ServletException se) { 
            throw se; 
        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage(), e);
        } finally {
            try { in.close(); } catch(Exception ign){;}
            try { out.close(); } catch(Exception ign){;}
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map params = buildParams(req);
            String action = params.remove("action")+"";
            if ("addchannel".equals(action)) {
                processAddChannelAction(params);
            } else if ("removechannel".equals(action)) {
                processRemoveChannelAction(params);
            } else { 
                processSendAction(params); 
            } 
        } catch(IOException ioe) { 
            throw ioe; 
        } catch(ServletException se) { 
            throw se; 
        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage(), e);
        } 
    }
    
    private void processSendAction(Map params) throws Exception {
        String channel = (String) params.get("channel");
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject( params );
            bos.flush();
            
            byte[] bytes = bos.toByteArray();
            sockets.getChannel(channel).send( bytes, 0, bytes.length );
        } catch(Exception e) {
            throw e;
        } finally {
            try { bos.close(); } catch(Exception ign){;}
            try { oos.close(); } catch(Exception ign){;}
        }        
    }
    
    private void processAddChannelAction(Map params) throws Exception {
        String name  = (String) params.get("channel");
        if (name == null || name.length() == 0) return;
        if (sockets.isChannelExist(name)) return;
        
        String type = (String) params.get("type");
        Channel channel = null;
        if (type != null && type.equalsIgnoreCase("queue")) 
            channel = new QueueChannel(name);
        else 
            channel = new TopicChannel(name);

        sockets.addChannel(channel);
        System.out.println("channel "+name +" added"); 
    }
    
    private void processRemoveChannelAction(Map params) throws Exception {
        String name = (String) params.get("channel");
        sockets.removeChannel(name); 
        System.out.println("channel "+ name +" removed");
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
}
