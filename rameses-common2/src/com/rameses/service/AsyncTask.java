package com.rameses.service;

import com.rameses.common.AsyncHandler;
import com.rameses.common.AsyncRequest;
import com.rameses.common.AsyncResponse;


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
                public void onMessage(AsyncResponse ar) {
                    System.out.println("unhandled message. No handler passed " + ar.getNextValue());
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
            if (result!=null && (result instanceof AsyncRequest)) {
                //get the location of queue
                AsyncRequest arequest = (AsyncRequest)result;
                String provider = null;
                String channel = null;
                //String provider = arequest.getProvider();
                //String channel = arequest.getChannel();
                AsyncPoller poller = new AsyncPoller(proxy.getConf(), provider, channel);
                boolean completed = false;
                
                while (!completed) {
                    completed = true;
                    //this is blocking until message arrives
                    AsyncResponse response = poller.poll();
                    if (response.getStatus()  != AsyncResponse.COMPLETED) {
                        completed = false;
                    }
                    handler.onMessage(response);
                }
                
            }  else {
                handler.onMessage( new AsyncResponse(result) );
            }
        }  catch (Exception e) {
            handler.onError( e );
        }
    }
}