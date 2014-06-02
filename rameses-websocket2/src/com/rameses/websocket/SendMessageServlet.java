/*
 * AddChannelServlet.java
 *
 * Created on March 2, 2013, 12:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.MessageObject;
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
public class SendMessageServlet extends HttpServlet 
{    
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
            if (o instanceof Collection)
                collection = (Collection) o; 
            else if (o instanceof Object[]) 
                collection = Arrays.asList((Object[]) o);
            
            if (collection == null) return;

            Iterator itr = collection.iterator(); 
            while (itr.hasNext()) 
            {
                Map map = (Map) itr.next();
                String channel = (String) map.get("channel");
                String group = (String) map.get("group");
                send(channel, group, map); 
            }
            
            out = new ObjectOutputStream(resp.getOutputStream());
            out.writeObject("OK"); 
        } 
        catch(IOException ioe) { throw ioe; }
        catch(ServletException se) { throw se; }
        catch(Exception e) 
        {
            e.printStackTrace();
            throw new ServletException(e.getMessage(), e);
        }
        finally {
            try {in.close();} catch(Exception ign){;}
            try {out.close();} catch(Exception ign){;}
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try 
        {
            String channel = req.getParameter("channel") ;
            String group = req.getParameter("group");
            
            Map params = buildParams( req );
            send(channel, group, params);
        } 
        catch(IOException ioe) { throw ioe; }
        catch(ServletException se) { throw se; }
        catch(Exception e) 
        {
            e.printStackTrace();
            throw new ServletException(e.getMessage(), e);
        } 
    }
    
    private void send(String channel, String group, Object params) throws Exception { 
        MessageObject mo = new MessageObject();
        mo.setConnectionId("send-message-servlet");
        mo.setGroupId(group == null? channel: group);
        mo.setData(params); 
        byte[] bytes = mo.encrypt();
        sockets.getChannel(channel).send( bytes, 0, bytes.length );                
    }    
}
