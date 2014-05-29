package com.rameses.service;

import com.rameses.common.AsyncBatchResult;
import com.rameses.common.AsyncHandler;
import com.rameses.common.AsyncToken;
import java.util.HashMap;
import java.util.Map;


public class AsyncTask implements Runnable {
    
    private String methodName;
    private Object[] args;
    private AsyncHandler handler;
    private ServiceProxy proxy;
    
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
            if (result!=null && (result instanceof AsyncToken)) {
                AsyncToken token = (AsyncToken)result;                
                result = new AsyncPoller(proxy.getConf(), token).poll();
            } 
            if (result instanceof AsyncBatchResult) {
                for (Object o : (AsyncBatchResult)result) {
                    handler.onMessage(o); 
                }
            } else {
                handler.onMessage( result );
            }
        }  catch (Exception e) {
            handler.onError( e );
        }
    }
    
    
    private class AsyncPoller extends AbstractServiceProxy 
    {
        private AsyncToken token;
        
        AsyncPoller(Map appenv, AsyncToken token) {
            super(null, appenv);
            this.token = token;
        }      
        
        public Object poll() throws Exception {
            String appcontext = (String) super.conf.get("app.context");
            String path = "async/poll";
            String cluster = (String) super.conf.get("app.cluster");
            if( cluster !=null ) path = cluster + "/" + path;

            Map params = new HashMap();
            params.put("id", token.getId()); 
            params.put("connection", token.getConnection());
            params.put("context", appcontext); 
            return client.post(path, new Object[]{params});
        }        
    }
}