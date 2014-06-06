package com.rameses.service;

import com.rameses.common.AsyncBatchResult;
import com.rameses.common.AsyncHandler;
import com.rameses.common.AsyncToken;

public class AsyncTask implements Runnable {
    
    private String methodName;
    private Object[] args;
    private AsyncHandler handler;
    private ServiceProxy proxy;
    
    private AsyncPoller poller; 
    
    public AsyncTask(ServiceProxy proxy, String methodName, Object[] args, AsyncHandler handler) {
        this.proxy = proxy;
        this.methodName = methodName;
        this.args = args;
        this.handler = handler;
        if (this.handler == null) {
            this.handler = new AsyncHandler() {
                public void onMessage(Object value) {
                    System.out.println("unhandled message. No handler passed ");
                }
                public void onError(Exception e) {
                    e.printStackTrace();
                }
                public void call(Object o) {
                    
                }
            };
        }
    }
    
    public void run() {
        try {
            Object result = proxy.invoke( methodName, args );
            if (result instanceof AsyncToken) {
                AsyncToken token = (AsyncToken)result; 
                if (token.isClosed()) return;
                
                AsyncPoller poller = new AsyncPoller(proxy.getConf(), token); 
                handle(poller, poller.poll()); 
                return;
            }             
            notify( result );
            
        }  catch (Exception e) {
            e.printStackTrace();
            handler.onError( e );
        }
    } 
    
    private void handle(AsyncPoller poller, Object result) throws Exception {
        if (result instanceof AsyncToken) {
            AsyncToken at = (AsyncToken)result;
            if (at.isClosed()) return; 
        } 
        
        if (!notify( result )) return; 
        
        Object o = poller.poll(); 
        if (o == null) {
            handler.onMessage( o ); 
        } else {
            handle(poller, o); 
        } 
    } 
    
    private boolean notify(Object o) {
        if (o instanceof AsyncBatchResult) {
            boolean is_closed = false;
            AsyncBatchResult batch = (AsyncBatchResult)o;
            for (Object item : batch) {
                if (item instanceof AsyncToken) {
                    is_closed = ((AsyncToken)item).isClosed(); 
                } else {
                    handler.onMessage(item); 
                } 
            } 
            return !is_closed; 
        } else {
            handler.onMessage(o); 
            return (o == null? false: true); 
        } 
    }
}