/*
 * AddChannelServlet.java
 *
 * Created on May 18, 2013, 3:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.SealedMessage;
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
 * @author Elmo
 */
public class AddChannelServlet extends HttpServlet 
{
    
    private SocketConnections sockets;
    
    public AddChannelServlet(SocketConnections s) {
        this.sockets = s;
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            in = new ObjectInputStream(req.getInputStream());
            
            Object o = in.readObject();
            if (o instanceof SealedMessage) 
                o = ((SealedMessage)o).getMessage(); 

            Collection collection = null;
            if (o instanceof Collection) { 
                collection = (Collection) o; 
            } else if (o instanceof Object[]) { 
                collection = Arrays.asList((Object[]) o); 
            } 
            
            if (collection != null) { 
                Iterator itr = collection.iterator(); 
                while (itr.hasNext()) {
                    Map conf = (Map) itr.next();
                    addChannel(conf); 
                }
                collection.clear(); 
            }
            out = new ObjectOutputStream(resp.getOutputStream());
            out.writeObject("OK"); 
        } catch(IOException ioe) { 
            throw ioe; 
        } catch(Exception e) { 
            e.printStackTrace(); 
            throw new ServletException(e.getMessage(), e); 
        } finally { 
            try {in.close();} catch(Exception ign){;}
            try {out.close();} catch(Exception ign){;}
        }        
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addChannel(buildParams(req)); 
    } 
    
    private void addChannel(Map conf) {
        String name = (String) conf.remove("channel");
        if (name == null || name.length() == 0) return;
        if (sockets.isChannelExist(name)) return;
        
        Channel channel = null;
        String type = (String) conf.remove("type");        
        if (type != null && type.equalsIgnoreCase("queue")) 
            channel = new QueueChannel(name, conf);
        else 
            channel = new TopicChannel(name, conf);

        sockets.addChannel(channel);
        System.out.println("channel "+name +" added");        
    }
    
    private Map buildParams(HttpServletRequest req) {
        Map params = new HashMap(); 
        Enumeration names = req.getParameterNames(); 
        while (names.hasMoreElements()) { 
            Object oname = names.nextElement(); 
            if(oname == null) continue; 
            
            String sval = req.getParameter(oname.toString()); 
            params.put(oname.toString(), sval); 
        } 
        return params; 
    }
}
