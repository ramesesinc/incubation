package com.rameses.service;

import com.rameses.common.AbstractAsyncHandler;
import com.rameses.common.AsyncBatchResult;
import com.rameses.common.AsyncException;
import com.rameses.common.AsyncHandler;
import com.rameses.common.AsyncToken;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncTask implements Runnable 
{   
    private ServiceProxy proxy;
    private String methodName;
    private Object[] args;
    private AsyncHandlerProxy handlerProxy;
    
    private AsyncPoller poller; 
    private int retrycount;
 
    private final LinkedBlockingQueue WAIT_LOCKER = new LinkedBlockingQueue();
    
    public AsyncTask(ServiceProxy proxy, String methodName, Object[] args, AsyncHandler handler) {
        this.proxy = proxy;
        this.methodName = methodName;
        this.args = args;
        
        handlerProxy = new AsyncHandlerProxy(handler); 
    }
    
    public void run() {
        try {
            retrycount = 0;
            
            Object result = invoke();
            if (result instanceof AsyncToken) {
                AsyncToken token = (AsyncToken)result; 
                if (token.isClosed()) {
                    fireOnMessage( AsyncHandler.EOF ); 
                    return;
                }
                
                AsyncPoller poller = new AsyncPoller(proxy.getConf(), token); 
                handle( poller ); 
                return;
            } 
            notify( result );
            
        } catch ( Exception e ) { 
            e.printStackTrace(); 
            handlerProxy.onError( e ); 
        } 
    } 
    
    private Object invoke() { 
        Exception err = null; 
        int retrycount = 0;
        while ( retrycount < 3 ) { 
            try { 
                retrycount += 1;
                return proxy.invoke( methodName, args ); 
            } catch( Exception e ) {
                System.err.println(e.getClass().getName() +": "+ e.getMessage()); 
                err = e; 
            } catch( Throwable t ) {
                System.err.println(t.getClass().getName() +": "+ t.getMessage()); 
                err = new Exception(t.getMessage(), t); 
            }
        }         
        if ( err != null ) { 
            handlerProxy.onError( err ); 
        } 
        return null; 
    }
    
    private Object fetchResult( AsyncPoller poller, int retryLimit ) {
        int retrycount = 0; 
        int maxRetryLimit = ( retryLimit > 0 ? retryLimit : 1 );
        while ( retrycount < maxRetryLimit ) { 
            try { 
                retrycount += 1;
                return poller.poll(); 
            } catch(Exception e) {
                System.err.println(e.getClass().getName() +": "+ e.getMessage()); 
            } catch( Throwable t ) {
                System.err.println(t.getClass().getName() +": "+ t.getMessage()); 
            } 
            
            try {
                WAIT_LOCKER.poll(1000, TimeUnit.MILLISECONDS); 
            } catch(Throwable t) {
                //do nothing 
            } 
        } 
        return new RetryFailedException(); 
    }
    
    private boolean fireOnMessage( Object data ) {
        if (handlerProxy.isCancelRequested()) {
            try {
                handlerProxy.onCancel(); 
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } finally {
                return false; 
            }
        } else {
            handlerProxy.onMessage( data ); 
            return true; 
        }
    }
    
    private void handle( AsyncPoller poller ) throws Exception {
        if (handlerProxy.isCancelRequested()) {
            try {
                handlerProxy.onCancel(); 
            } catch(Throwable t) {
                t.printStackTrace(); 
            } finally {
                return; 
            }
        }
        
        Object result = fetchResult( poller, 3 ); 
        if (result == null || result instanceof RetryFailedException) {
            handlerProxy.onTimeout("poll failed after 3 retries");
            if (handlerProxy.isRetryRequested()) { 
                handle( poller ); 
            } 
            //
            // exit fetching data 
            // 
            return; 
        } 
        
        if (result instanceof AsyncToken) {
            AsyncToken at = (AsyncToken)result;
            if (at.isClosed()) {
                poller.close(); 
                fireOnMessage(AsyncHandler.EOF); 
                return;
            }
            
        } else {
            if (notify( result )) { 
                handle( poller ); 
            } else { 
                poller.close(); 
                handlerProxy.onMessage(AsyncHandler.EOF); 
            } 
        }
    } 
    
    private boolean notify(Object o) {
        if (o instanceof AsyncException) {
            handlerProxy.onError((AsyncException) o);
            return false;
            
        } else if (o instanceof AsyncBatchResult) {
            boolean is_closed = false;
            AsyncBatchResult batch = (AsyncBatchResult)o;
            for (Object item : batch) {
                if (item instanceof AsyncToken) {
                    is_closed = ((AsyncToken)item).isClosed(); 
                    
                } else if (item instanceof AsyncException) {
                    handlerProxy.onError((AsyncException) item);
                    return false; 
                    
                } else {
                    fireOnMessage( item ); 
                } 
            } 
            return !is_closed; 
            
        } else {
            return fireOnMessage( o ); 
        } 
    }
    
    private class RetryFailedException extends Exception {
        //do nothing 
    }
        
    private class AsyncHandlerProxy extends AbstractAsyncHandler {
        private AsyncHandler source;
        
        AsyncHandlerProxy(AsyncHandler source) {
            this.source = source; 
            if (source == null) {
                this.source = new AsyncHandler() {
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

        public void onError(Exception e) {
            if (source != null) {
                source.onError(e);
            } 
        }

        public void onMessage(Object o) {
            if (source != null) {
                source.onMessage(o);
            } 
        }

        public void call(Object o) {
            if (source != null) {
                source.call(o);
            } 
        }

        public void onTimeout(String msg) {
            if (source instanceof AbstractAsyncHandler) {
                ((AbstractAsyncHandler) source).onTimeout(); 
            } else if (source != null) {
                source.onError(new TimeoutException(msg)); 
            }
        }
        
        public void onCancel() {
            if (source instanceof AbstractAsyncHandler) {
                ((AbstractAsyncHandler) source).onCancel(); 
            } 
        }
        
        public boolean isRetryRequested() {
            if (source instanceof AbstractAsyncHandler) {
                return ((AbstractAsyncHandler) source).hasRequestRetry();
            } else {
                return false; 
            }
        }
        
        public boolean isCancelRequested() {
            if (source instanceof AbstractAsyncHandler) {
                return ((AbstractAsyncHandler) source).hasRequestCancel();
            } else {
                return false; 
            }
        }        
    }
}