/*
 * AsyncResponseServlet.java
 *
 * Created on May 29, 2014, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.common.AsyncBatchResult;
import com.rameses.common.AsyncToken;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.MessageQueue;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author Elmo
 */
public class AsyncResponseServlet extends AbstractServlet 
{
    private static ExecutorService taskPool;
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }            
    
    public String getMapping() {
        return "/async/poll";
    }

    public long getBlockingTimeout() {
        return 30000;
    }

    public int getTaskPoolSize() {
        return 100; 
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        String reqid = AsyncResponseServlet.class.getName();
        PollTask atask = (PollTask) req.getAttribute(reqid);
        if (atask == null) {
            Object[] args = readRequest(req); 
            Map params = (args.length > 0? (Map)args[0]: new HashMap()); 
            
            require(params, "id", "Please specify id");
            require(params, "connection", "Please specify connection");
            require(params, "context", "Please specify context");

            Continuation cont = ContinuationSupport.getContinuation(req);
            if( cont.isInitial() ) {
                cont.setTimeout( getBlockingTimeout()  );
                cont.suspend();
            }
            
            atask = new PollTask(cont, params); 
            req.setAttribute(reqid, atask);            
            Future future = taskPool.submit(atask); 
            atask.setFuture(future); 
        } else {
            Object result = null; 
            if (atask.isExpired()) {
                atask.cancel();
                result = new Exception("Timeout exception. Transaction was not processed");
            } else {
                result = atask.getResult();
            }
            writeResponse(result, resp); 
        }
    } 
    
    private void require(Map params, String name, String msg) throws ServletException {
        Object value = params.get(name); 
        if (value == null) throw new ServletException(msg);
    }

    private class PollTask implements Runnable 
    {
        private Continuation cont;
        private Future future; 
        private Map params;
        private Object result;

        private String id;
        private String context; 
        private String connection;
        
        PollTask(Continuation cont, Map params) {
            this.cont = cont; 
            this.params = params; 
            this.context = params.get("context").toString();
            this.connection = params.get("connection").toString();
            this.id = params.get("id").toString();
        }
        
        Continuation getContinuation() { 
            return cont;
        }
        
        void setFuture(Future future) {
            this.future = future; 
        }
        
        Object getResult() { return result; }
        
        boolean isExpired() {
            return cont.isExpired(); 
        }
        
        void cancel() {
            if (future != null) future.cancel(true); 
        }
        
        public void run() {
            try {
                System.out.println("run poll task in server");
                AppContext ctx = OsirisServer.getInstance().getContext( AppContext.class, context );
                XAsyncConnection ac = (XAsyncConnection) ctx.getResource(XConnection.class, connection );
                MessageQueue queue = ac.getQueue( id );
                System.out.println("async connection is "+ac);
                if (ac == null) throw new Exception("async connection '"+ connection +"' not found");
                result = queue.poll(); 
                if (result instanceof AsyncBatchResult) {
                    AsyncBatchResult batch = (AsyncBatchResult)result; 
                    if (batch.hasEOF()) ac.unregister(id); 
                } else if (result instanceof AsyncToken) {
                    AsyncToken at = (AsyncToken)result; 
                    if (at.isClosed()) ac.unregister(id); 
                } 
            } catch (Exception ex) {
                result = ex; 
            } finally {
                cont.resume(); 
            }
        } 
    }
    
} 
