/*
 * ScriptRunnable.java
 *
 * Created on February 8, 2013, 8:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.util.ExceptionManager;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptRunnable implements Runnable {
    
    private MainContext context;
    private String serviceName;
    private String methodName;
    private Object[] args;
    private Map env;
    private boolean fireInterceptors = true;
    
    private Object result;
    private Exception err;
    private Listener listener;
    private boolean cancelled = false;
    
    /**
     * Creates a new instance of ScriptRunnable
     */
    public ScriptRunnable(MainContext ctx) {
        this.context = ctx;
    }
    
    public ScriptRunnable(MainContext context, String serviceName, String methodName, Object[] args, Map env) {
        this(context,serviceName,methodName,args,env,true);
    }
    
    public ScriptRunnable(MainContext context, String serviceName, String methodName, Object[] args, Map env, boolean fireInterceptors) {
        this.setContext(context);
        this.setServiceName(serviceName);
        this.setMethodName(methodName);
        this.setArgs(args);
        this.setEnv(env);
        this.setFireInterceptors(fireInterceptors);
    }
    
    public void run() {
        //System.out.println("run service method ->" + this.serviceName+"."+this.methodName);
        if(cancelled ) return;
        TransactionContext txn = new TransactionContext(context.getServer(), context, env);
        try {
            if(listener!=null) listener.onBegin();
            //call the service here.
            ScriptTransactionManager t = txn.getManager( ScriptTransactionManager.class );
            ManagedScriptExecutor mse = t.create( getServiceName());
            result = mse.execute( getMethodName(), getArgs());
            if (result == null) {
                result = "#NULL";
            }
            txn.commit();
            if(listener!=null) listener.onComplete( result );
        } catch(Exception ex) {
            txn.rollback();
            err = ExceptionManager.getOriginal(ex);
            if(listener!=null) listener.onRollback(ex);
        } finally {
            txn.close();
            if(listener!=null) listener.onClose();
        }
    }
    
    public void cancel() {
        cancelled = true;
        if(listener!=null) listener.onCancel();
    }
    
    public Object getResult() {
        return result;
    }
    
    public Exception getErr() {
        return err;
    }
    
    public Listener getListener() {
        return listener;
    }
    
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    //the interface for listening on the transactions
    public static interface Listener {
        void onBegin();
        void onComplete(Object result);
        void onRollback(Exception e);
        void onClose();
        void onCancel();
    }
    
    public static abstract class AbstractListener implements Listener {
        public void onBegin(){;}
        public void onComplete(Object result){;}
        public void onRollback(Exception e){;}
        public void onClose(){;}
        public void onCancel(){;}
    }
    
    public MainContext getContext() {
        return context;
    }
    
    public void setContext(MainContext context) {
        this.context = context;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void setArgs(Object[] args) {
        this.args = args;
    }
    
    public Map getEnv() {
        return env;
    }
    
    public void setEnv(Map env) {
        this.env = env;
    }
    
    public boolean isFireInterceptors() {
        return fireInterceptors;
    }
    
    public void setFireInterceptors(boolean fireInterceptors) {
        this.fireInterceptors = fireInterceptors;
    }
    
    public boolean hasErrs() {
        return err!=null;
    }


    
}