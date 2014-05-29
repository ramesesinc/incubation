/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.common.AsyncRequest;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.script.ScriptRunnable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Elmo
 */
public class XAsyncLocalConnection extends XConnection implements XAsyncConnection  {
    
    private static ExecutorService taskPool;
    private AsyncQueue queue;
    private Map conf;
    private String name;
    private int poolSize = 100;
    
    /** Creates a new instance of XAsyncLocalConnection */
    public XAsyncLocalConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
    }
    
    public void start() {
        queue = new AsyncQueue();
        taskPool = Executors.newFixedThreadPool(poolSize);
    }
    
    public void stop() {
        queue.clear();
        queue = null;
        taskPool.shutdownNow();
    }
    
    public Map getConf() {
        return conf;
    }
    
    public void register(String id) throws Exception {
        queue.register(id,conf);
    }
    
    public void unregister(String id) throws Exception {
        queue.unregister(id);
    }
    
    public Object poll(String id) throws Exception {
        return queue.poll(id);
    }
    
    public void push(String id, Object data) throws Exception {
        queue.push( id, data );
    }
    
    public void submitAsync(AsyncRequest ar) {
        AppContext ct = OsirisServer.getInstance().getContext( AppContext.class, ar.getContextName() );
        ScriptRunnable sr = new ScriptRunnable(ct);
        sr.setServiceName(ar.getServiceName() );
        sr.setMethodName(ar.getMethodName());
        sr.setArgs(ar.getArgs());
        sr.setEnv( ar.getEnv() );
        if(ar.getConnection()!=null) {
            sr.setListener( new MyListener(ar) );
        }    
        taskPool.submit( sr );
    } 
    
    
    
    public class MyListener implements ScriptRunnable.Listener {
        private AsyncRequest ar;
        
        public MyListener(AsyncRequest ar) {
            this.ar = ar; 
        }
        public void onBegin() {
        }

        public void onComplete(Object result) {
            try {
                System.out.println("env-> " + ar.getEnv());                
                XAsyncLocalConnection.this.push(ar.getId(), result );
                if ("true".equals(ar.getEnv().get(ar.getVarStatus())+"")) {
                    ar.getEnv().put(ar.getVarStatus(), null); 
                    XAsyncLocalConnection.this.submitAsync(ar); 
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void onRollback(Exception e) {
        }

        public void onClose() {
        }

        public void onCancel() {
        }
        
    }
    
}
