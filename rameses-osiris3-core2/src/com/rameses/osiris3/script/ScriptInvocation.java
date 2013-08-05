package com.rameses.osiris3.script;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;



public class ScriptInvocation implements InvocationHandler {
    private ManagedScriptExecutor executor;
    private boolean managed;
    public ScriptInvocation(ManagedScriptExecutor executor, boolean managed) {
        this.executor = executor;
        this.managed = managed;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if( method.getName().equals("toString")) return executor.toString();
        return executor.execute( method.getName(), args, managed );
    }
}