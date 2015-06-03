package com.rameses.osiris3.script;

import com.rameses.annotations.Async;
import com.rameses.common.AsyncRequest;
import com.rameses.osiris3.core.MainContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptInvocation implements InvocationHandler { 
    
    private final static ExecutorService asyncPool = Executors.newCachedThreadPool();
    
    private MainContext mainCtx;
    private String serviceName; 
    private ManagedScriptExecutor executor;
    
    public ScriptInvocation(MainContext mainCtx, String name, ManagedScriptExecutor executor ) {
        this.mainCtx = mainCtx; 
        this.serviceName = name;         
        this.executor = executor;  
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return executor.toString();
        }
        
        Object result = executor.execute(method.getName(), args, false); 
        if( result instanceof AsyncRequest ) {
            System.out.println("submit to poll");
            ScriptRunnable sr = new ScriptRunnable( mainCtx ); 
            sr.setServiceName( serviceName );
            sr.setMethodName( method.getName() );
            sr.setArgs( args );
            sr.setBypassAsync( true );
            mainCtx.submitAsync( sr ); 
            return null; 
        } else {
            System.out.println("submit to process");
            return result; 
        }
    } 
}