package com.rameses.osiris3.script;

import com.rameses.annotations.Async;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptInvocation implements InvocationHandler { 
    
    private final static ExecutorService asyncPool = Executors.newCachedThreadPool();
    
    private ManagedScriptExecutor executor;
    private boolean managed;
    
    public ScriptInvocation(ManagedScriptExecutor executor, boolean managed) {
        this.executor = executor;
        this.managed = managed;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return executor.toString();
        }
        
        ScriptHandler sh = new ScriptHandler();
        sh.method = method;
        sh.args = args; 
        
        Async async = method.getAnnotation(Async.class);
        if ( async == null ) { 
            sh.run();
            return sh.result; 
            
        } else { 
            asyncPool.submit( sh );
            return null; 
        } 
    } 
    
    
    
    private class ScriptHandler implements Runnable {
        
        Method method;
        Object[] args;        
        Object result;
        
        public void run() { 
            try { 
                result = null; 
                result = executor.execute( method.getName(), args, managed ); 
            } catch( RuntimeException re ) {
                throw re; 
            } catch( Exception e ) {
                throw new RuntimeException(e.getMessage(), e); 
            }
        }
    }
}