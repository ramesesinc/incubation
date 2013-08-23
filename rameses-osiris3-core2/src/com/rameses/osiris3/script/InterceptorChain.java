/*
 * InterceptorChain.java
 *
 * Created on January 28, 2013, 9:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.common.AsyncRequest;
import com.rameses.common.ExpressionResolver;
import com.rameses.osiris3.cache.CacheConnection;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.util.BreakException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Elmo
 */
public class InterceptorChain {
    
    private InterceptorSet interceptorSet;
    
    /** Creates a new instance of InterceptorChain */
    public InterceptorChain(InterceptorSet i) {
        this.interceptorSet = i;
    }
    
    private void fireInterceptorList(List<InterceptorInfo> interceptors,  ExecutionInfo einfo, final AsyncRequest arequest, final CacheConnection cache) throws Exception {
        TransactionContext tc = TransactionContext.getCurrentContext();
        ScriptTransactionManager smr = tc.getManager( ScriptTransactionManager.class );
        MainContext context = (MainContext)tc.getContext();
        
        Map map = new HashMap();
        map.put("args", einfo.getArgs());
        map.put("env", tc.getEnv());
        map.put("tag", einfo.getTag()); 
        if(einfo.getResult()!=null) {
            map.put("result", einfo.getResult());
        }
        
        for( InterceptorInfo info: interceptors ) {
            //check eval
            
            if(info.getEval()!=null && info.getEval().trim().length()>0) {
                boolean b = ExpressionResolver.getInstance().evalBoolean( info.getEval(), map );
                if( b == false ) continue;
            }
            
            if( info.isAsync()) {
                ScriptRunnable sr = new ScriptRunnable(context);
                sr.setArgs( new Object[]{einfo} );
                sr.setMethodName( info.getMethodName() );
                sr.setFireInterceptors( false );
                sr.setServiceName( info.getServiceName() );
                sr.setEnv( tc.getEnv() );
                sr.setListener( new ScriptRunnable.AbstractListener(){
                    public void onComplete(Object obj){
                        try {
                            cache.appendToBulk( arequest.getChannel(), null, obj );
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                context.submitAsync( sr );
            } else {
                try {
                    ManagedScriptExecutor me = smr.create(info.getServiceName() );
                    Object vresult = me.execute( info.getMethodName(), new Object[]{einfo}, false );
                } catch(BreakException be) {
                    System.out.println("Interceptor error " + info.getServiceName()+"."+info.getMethodName() );
                    be.printStackTrace();
                } catch(Exception e) {
                    throw e;
                }
            }
        }
    }
    
    public Object fireChain(Callable callable, ExecutionInfo einfo) throws Exception {
        TransactionContext tc = TransactionContext.getCurrentContext();
        OsirisServer server  = tc.getServer();
        AbstractContext context = tc.getContext();
        ScriptTransactionManager smr = tc.getManager( ScriptTransactionManager.class );
        
        CacheConnection cache = null;
        AsyncRequest arequest = (AsyncRequest) tc.getEnv().get( ManagedScriptExecutor.ASYNC_ID );
        if(arequest!=null && (context instanceof MainContext)) {
            cache = (CacheConnection) context.getResource(XConnection.class, CacheConnection.CACHE_KEY);
        }
        
        
        fireInterceptorList( interceptorSet.getBeforeInterceptors(), einfo, arequest, cache );
        Object result = callable.call();
        einfo.setResult( result );
        fireInterceptorList( interceptorSet.getAfterInterceptors(), einfo, arequest, cache );
        return result;
    }
    
    
    
    
}
