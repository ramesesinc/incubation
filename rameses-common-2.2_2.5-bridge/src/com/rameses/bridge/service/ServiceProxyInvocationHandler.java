/*
 * ServiceProxyInvocationHandler.java
 * Created on September 23, 2011, 11:29 AM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */
package com.rameses.bridge.service;

import com.rameses.service.ServiceProxy;
import com.rameses.util.AppException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author jzamss
 */
public class ServiceProxyInvocationHandler implements InvocationHandler{
    
    private static final ExecutorService thread = Executors.newFixedThreadPool(10);
    
    private ServiceProxy proxy;
    
    public ServiceProxyInvocationHandler(ServiceProxy p) {
        this.proxy = p;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        if (method.getName().equals("toString")) return proxy.getClass().getName();
                
        try {
            if( args == null ) {
                return this.proxy.invoke(method.getName());
            } else {
                return this.proxy.invoke( method.getName(), args );
            }
        } catch(Throwable t) {
            t.printStackTrace();
            if (t instanceof AppException) { 
                throw t; 
            } else if (t instanceof RuntimeException) { 
                throw (RuntimeException) t; 
            } else { 
                throw new RuntimeException(t.getMessage(), t); 
            } 
        }
    }
}
