/*
 * CallbackHandlerProxy.java
 *
 * Created on June 26, 2013, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.lang.reflect.Method;

/**
 *
 * @author wflores
 */
public class CallbackHandlerProxy implements CallbackHandler 
{
    private Object source;
    
    public CallbackHandlerProxy(Object source) {
        this.source = source; 
    }

    public Object call() {
        return invoke(source); 
    }

    public Object call(Object value) { 
        return invoke(source, value); 
    } 

    public Object call(Object[] values) {
        return invoke(source, values); 
    }
    
    public Object invoke(Object source) {
        if (source instanceof CallbackHandler) {
            return ((CallbackHandler) source).call(); 
        } else { 
            return invokeMethod(source, new Object[]{});
        } 
    } 
    
    public Object invoke(Object source, Object value) {
        if (source instanceof CallbackHandler) {
            return ((CallbackHandler) source).call(value); 
        } else { 
            return invokeMethod(source, new Object[]{ value }); 
        } 
    }

    public Object invoke(Object source, Object[] values) {
        if (source instanceof CallbackHandler) {
            return ((CallbackHandler) source).call(values); 
        } else { 
            return invokeMethod(source, new Object[]{ values });
        } 
    } 
       
    private Object invokeMethod(Object source, Object[] args) 
    {
        try 
        {
            if (source == null) 
                throw new NullPointerException("failed to invoke method call caused by source object null");

            Object[] params = (args == null? new Object[]{}: args); 
            Class[] classes = new Class[params.length];
            if (params.length > 0) classes[0] = Object.class; 
                
            Class sourceClass = source.getClass();
            Method m = sourceClass.getMethod("call", classes); 
            return m.invoke(source, params); 
        }
        catch(Throwable t) 
        {
            //System.out.println("[CallbackHandlerProxy.invokeMethod] error: " + t.getMessage());  
            if (t instanceof RuntimeException) 
                throw (RuntimeException)t;
            
            throw new RuntimeException(t.getMessage(), t);
        } 
    }
}
