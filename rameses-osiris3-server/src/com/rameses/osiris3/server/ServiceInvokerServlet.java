/*
 * ServiceInvokerServlet.java
 *
 * Created on January 10, 2013, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;


import com.rameses.common.AsyncRequest;
import com.rameses.common.AsyncToken;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.script.ScriptRunnable;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.MessageQueue;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.io.IOException;
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
 *
 * Pattern for invoking is :
 * http://<server>:<port>/osiris3/<app.context>/<service>.<method>
 *
 * for asking info:
 * http://<server>:<port>/osiris3-info/<app.context>/<service>
 */

public class ServiceInvokerServlet extends AbstractServlet {
    
    private static ExecutorService taskPool;
    
    public String getMapping() {
        return "/services/*";
    }
    
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }
    
    private class ContinuationListener extends ScriptRunnable.AbstractListener {
        private Continuation continuation;
        private Future future;
        private HttpServletRequest req;
        public ContinuationListener(HttpServletRequest req) {
            this.req = req;
        }
        public void start() {
            continuation = ContinuationSupport.getContinuation(req);
            if( continuation.isInitial() ) {
                continuation.setTimeout( getBlockingTimeout()  );
                continuation.suspend();
            }
        }
        public void onClose() {
            if(continuation!=null) {
                continuation.resume();
                continuation = null;
            }
        }
        
        public void onCancel() {
            future.cancel(true);
        }
        
        public boolean isExpired() {
            return (continuation!=null && continuation.isExpired());
        }
    }
    
    protected void service(final HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ScriptRunnable tr = (ScriptRunnable) req.getAttribute( ScriptRunnable.class.getName() );
        RequestParser p = new RequestParser(req);
        
        if(tr==null) {
            ContinuationListener listener = new ContinuationListener(req);
            //replace the values
            Object[] params = readRequest(req);
            
            AppContext ct = OsirisServer.getInstance().getContext( AppContext.class, p.getContextName() );
            
            tr = new ScriptRunnable( (MainContext)ct );
            tr.setServiceName(p.getServiceName() );
            tr.setMethodName(p.getMethodName());
            tr.setArgs((Object[])params[0] );
            tr.setEnv( (Map)params[1] );
            tr.setListener( listener );
            tr.setBypassAsync(false);
            listener.start();
            
            req.setAttribute(  ScriptRunnable.class.getName(), tr );
            listener.future = taskPool.submit(tr);
            
        } else {
            ContinuationListener listener = (ContinuationListener)tr.getListener();
            Object response= null;
            if( listener.isExpired() ) {
                tr.cancel();
                response = new Exception("Timeout exception. Transaction was not processed");
            } else {
                if( tr.hasErrs()) {
                    response = tr.getErr();
                    System.out.println("error "+tr.getErr().getClass() + " " + tr.getErr().getMessage());
                } else {
                    response = tr.getResult();
                    if(response instanceof AsyncRequest) {
                        AsyncRequest ar = (AsyncRequest)response;
                        ar.setContextName( p.getContextName());
                        if( ar.getConnection()!=null) {
                            try {
                                XAsyncConnection ac = (XAsyncConnection) tr.getContext().getResource( XConnection.class, ar.getConnection() );
                                ac.register( ar.getId() );
                                tr.setBypassAsync(true);
                                tr.setAsyncRequest(ar);
                                tr.setListener(new AsyncListener(tr, ac));
                                taskPool.submit( tr );
                            } catch(Exception e) {
                                response = e;
                            }
                        }
                        response = new AsyncToken(ar.getId(), ar.getConnection());
                    }
                }
            }
            writeResponse( response, res );
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" AsyncListener ">
    
    private class AsyncListener implements ScriptRunnable.Listener {
        private ScriptRunnable sr;
        private XAsyncConnection conn;
        
        public AsyncListener(ScriptRunnable sr, XAsyncConnection conn) {
            this.sr = sr; 
            this.conn = conn;
        }
        
        public void onBegin() {}

        public void onComplete(Object result) {
            try {
                AsyncRequest ar = sr.getAsyncRequest();
                boolean hasmore = "true".equals(sr.getEnv().get(ar.getVarStatus())+""); 
                MessageQueue queue = conn.getQueue( ar.getId() );
                queue.push( result ); 
                if (hasmore) { 
                    ar.getEnv().put(ar.getVarStatus(), null); 
                    taskPool.submit( sr ); 
                } else { 
                    AsyncToken at = new AsyncToken();
                    at.setClosed(true);
                    queue.push( at);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void onRollback(Exception e) {}
        public void onClose() {}
        public void onCancel() {}
    }
    
    // </editor-fold>
    
}
