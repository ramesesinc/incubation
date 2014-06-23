/*
 * ObjectProxy.java
 *
 * Created on June 10, 2014, 5:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.util.AppException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author wflores 
 */
public class ObjectProxy 
{
    
    public ObjectProxy() {
    }
    
    
    public <T> T create(Object source, Class<T> interface0) {
        return create(source, interface0, ObjectProxy.class.getClassLoader()); 
    } 
    
    public <T> T create(Object source, Class<T> interface0, ClassLoader classLoader) {
        InvocationHandlerImpl handler = new InvocationHandlerImpl(source); 
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{interface0}, handler); 
    }    
    
    
    private class InvocationHandlerImpl implements InvocationHandler 
    {
        private Object source;
        private Class sourceClass;
        
        InvocationHandlerImpl(Object source) {
            this.source = source; 
            this.sourceClass = source.getClass(); 
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("toString")) {
                return sourceClass.toString(); 
            }
            
            try { 
                if (args == null) args = new Object[]{};
                
                Method sourceMethod = getCallbackMethod(methodName); 
                if (sourceMethod == null) {
                    Class[] argtypes = method.getParameterTypes();
                    sourceMethod = sourceClass.getMethod(methodName, argtypes); 
                    return sourceMethod.invoke(source, args); 
                } else {
                    Class[] argtypes = sourceMethod.getParameterTypes(); 
                    Class argtype0 = argtypes[0];
                    if (Object[].class == argtype0) {
                        return sourceMethod.invoke(source, new Object[]{ args });
                    } else {
                        return sourceMethod.invoke(source, args); 
                    }
                } 
            } catch(Throwable t) { 
                if (t instanceof AppException) {
                    throw t;
                } else if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else { 
                    throw new RuntimeException(t.getMessage(), t); 
                }
            }  
        }
        
        private Method getCallbackMethod(String name) {
            if (name == null) return null;

            try {
                Method m = sourceClass.getMethod(name, new Class[]{Object[].class});
                if (m != null) return m; 
            } catch(Throwable t) {;} 

            try {
                Method m = sourceClass.getMethod(name, new Class[]{Object.class});
                if (m != null) return m; 
            } catch(Throwable t) {;} 
            
            return null; 
        } 
    }
}
