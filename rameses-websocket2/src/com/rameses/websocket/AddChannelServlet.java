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
        try 
        {
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
                while (itr.hasNext()) 
                {
                    Map map = (Map) itr.next();
                    String name  = (String) map.get("channel");
                    String type  = (String) map.get("type");
                    addChannel(name, type); 
                }
                collection.clear(); 
            }
            out = new ObjectOutputStream(resp.getOutputStream());
            out.writeObject("OK"); 
        } 
        catch(IOException ioe) { throw ioe; }
        catch(Exception e) { 
            e.printStackTrace(); 
            throw new ServletException(e.getMessage(), e); 
        } 
        finally { 
            try {in.close();} catch(Exception ign){;}
            try {out.close();} catch(Exception ign){;}
        }        
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        String name = req.getParameter("channel");
        String type = req.getParameter("type");
        addChannel(name, type); 
    } 
    
    private void addChannel(String name, String type) 
    {
        if (name == null) return;
        if (sockets.isChannelExist(name)) return;
                
        Channel channel = null;
        if (type != null && type.equalsIgnoreCase("queue")) 
            channel = new QueueChannel(name);
        else 
            channel = new TopicChannel(name);

        sockets.addChannel(channel);
        System.out.println("channel "+name +" added");        
    }
}
