/*
 * AsyncUnregisterServlet.java
 *
 * Created on May 29, 2014, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class AsyncUnregisterServlet extends AbstractServlet 
{
    private static ExecutorService taskPool;
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }            
    
    public String getMapping() {
        return "/async/unregister";
    }

    public long getBlockingTimeout() {
        return 30000;
    }

    public int getTaskPoolSize() {
        return 100; 
    } 
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        Object[] args = readRequest(req); 
        Map params = (args.length > 0? (Map)args[0]: new HashMap()); 

        require(params, "id", "Please specify id");
        //require(params, "context", "Please specify context");

        String context = (String) params.get("context");
        if (context == null) context = "default"; 
        
        String connection = (String) params.get("connection"); 
        if (connection == null) connection = "async"; 
        
        AppContext ctx = OsirisServer.getInstance().getContext(AppContext.class, context); 
        XAsyncConnection ac = (XAsyncConnection) ctx.getResource(XConnection.class, connection);
        
        String id = params.get("id").toString();
        try {
            ac.unregister(id); 
        } catch (Exception ex) {
            throw new ServletException(ex.getMessage(), ex); 
        } 
        
        Map result = new HashMap();
        result.put("id", id);
        result.put("status", "SUCCESS");
        writeResponse(result, resp); 
    } 
    
    private void require(Map params, String name, String msg) throws ServletException {
        Object value = params.get(name); 
        if (value == null) throw new ServletException(msg);
    }
} 
