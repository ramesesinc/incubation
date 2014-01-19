/*
 * ManagedScriptExecutor.java
 *
 * Created on January 29, 2013, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.Async;
import com.rameses.annotations.LogEvent;

import com.rameses.annotations.ProxyMethod;
import com.rameses.common.AsyncRequest;
import com.rameses.osiris3.cache.CacheConnection;

import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.xconnection.XConnection;
import java.lang.reflect.Method;
import java.rmi.server.UID;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Elmo
 * This can only be created from the TransactionContext
 */
public class ManagedScriptExecutor {
    
    public static final String ASYNC_ID = "_async_";
    
    private static String GET_STRING_INTERFACE = "getStringInterface";
    
    private ScriptExecutor scriptExecutor;
    
    /** Creates a new instance of ManagedScriptExecutor */
    ManagedScriptExecutor(ScriptExecutor s) {
        this.scriptExecutor = s;
    }
    
    public Object execute( final String method, final Object[] args ) throws Exception {
        return execute(method, args, true );
    }
    
    public Object execute( final String method, final Object[] args, boolean fireInterceptors  ) throws Exception {
        try {
            ScriptInfo scriptInfo = scriptExecutor.getScriptInfo();

            if( method.equals( GET_STRING_INTERFACE )) {
                return scriptInfo.getStringInterface();
            }
            
            //get first the necessary resources
            ExecutionInfo e = new ExecutionInfo(scriptInfo.getName(),method, args);
            Method m = scriptInfo.getClassDef().findMethodByName( method );
            if (m == null) throw new NoSuchMethodException("'"+method+"' method does not exist");

            TransactionContext txn = TransactionContext.getCurrentContext();
            OsirisServer svr = txn.getServer();
            MainContext ct = txn.getContext();
            
            ProxyMethod pma = m.getAnnotation(ProxyMethod.class);
            boolean isProxyMethod = (pma!=null);
            if (isProxyMethod) e.setTag(pma.tag());
            
            //this is to support old methods. if proxy method marked as local, do not fire interceptors
            if(isProxyMethod && pma.local())fireInterceptors = false;
            
            //check async
            Async async = m.getAnnotation(Async.class);
            Map _env = txn.getEnv();
            
            //we need to do this to avoid recursion.
            if(isProxyMethod && async!=null && !_env.containsKey(ASYNC_ID)) {
                AsyncRequest result = null;
                //determine if we need to respond to this request through the provider
                String provider = async.provider();
                if(provider==null || provider.trim().length()==0) provider = "default";
                
                //test if connection in async exists! if not throw an error bec. it will be pointless to continue
                final CacheConnection cache = (CacheConnection) ct.getResource(XConnection.class, CacheConnection.CACHE_KEY);
                
                //add the async info in the env. This is to avoid recursion.
                final String channelId = "ASYNC" + new UID();
                result = new AsyncRequest();
                result.setChannel( channelId );
                result.setProvider( provider );
                _env.put(ASYNC_ID, result );
                
                //prepare the bulk entry.
                int timeout = (async.timeout() == 0)? 30 : async.timeout();
                cache.createBulk( channelId, timeout, 0 );
                
                ScriptRunnable.Listener listener = new ScriptRunnable.AbstractListener(){
                    public void onComplete(Object obj){
                        try {
                            cache.appendToBulk( channelId, null, obj );
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                
                ScriptRunnable sr = new ScriptRunnable(ct);
                sr.setServiceName( scriptInfo.getName() );
                sr.setMethodName( method );
                sr.setArgs(args);
                sr.setEnv( _env );
                sr.setFireInterceptors( fireInterceptors );
                sr.setListener(listener);
                ct.submitAsync( sr );
                return result;
            }
            
            
            
            
            //we need to check validation in @Params
            CheckedParameter[] checkParams = scriptInfo.getCheckedParameters( method );
            for(CheckedParameter p: checkParams ) {
                if(p.isRequired() && args[p.getIndex()]==null )
                    throw new Exception( "argument " + p.getIndex() + " for method " + method + " must not be null" );
                String schemaName = p.getSchema();
                if(schemaName!=null && schemaName.trim().length()>0) {
                    DataService dataSvc = ct.getService( DataService.class );
                    dataSvc.validate( schemaName, args[p.getIndex()] );
                }
            }
            
            //inject the dependencies
            

            ScriptService scriptSvc = ct.getService( ScriptService.class );
            DependencyInjector di = scriptSvc.getDependencyInjector();
            di.injectDependencies( scriptExecutor, e );
            
            //fire interceptors
            Object result = null;
            if(fireInterceptors) {
                InterceptorSet s = scriptSvc.findInterceptors( ct, e.toString() );
                InterceptorChain ic = new InterceptorChain(s);
                result = ic.fireChain( new Callable(){
                    public Object call() throws Exception {
                        return scriptExecutor.invokeMethod( method, args );
                    }
                }, e);
            } else {
                result = scriptExecutor.invokeMethod( method, args );
            }
            
            
            //If method is evented, we publish it in the esb connector
            LogEvent logEvent = m.getAnnotation(LogEvent.class);
            if(logEvent!=null) {
                String eventConnection = logEvent.value();
                if(eventConnection!=null && eventConnection.trim().length()>0) {
                    XConnection conn = ct.getResource( XConnection.class, eventConnection  );
                        
                }
            }
            
            return result;
        } catch(Exception e) {
            throw e;
        }
    }
    
    public void close() {
        this.scriptExecutor.close();
        this.scriptExecutor = null;
    }
    
    public ScriptInfo getScriptInfo() {
        return this.scriptExecutor.getScriptInfo();
    }
    
}
