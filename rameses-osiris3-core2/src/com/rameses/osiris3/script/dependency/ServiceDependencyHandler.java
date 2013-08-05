/*
 * DefaultServiceDependencyHandler.java
 *
 * Created on January 10, 2013, 8:53 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.osiris3.core.AbstractContext;

import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.script.ManagedScriptExecutor;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.script.ScriptInvocation;
import com.rameses.osiris3.script.ScriptTransactionManager;
import com.rameses.osiris3.script.messaging.ScriptConnection;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.util.ExceptionManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 *
 * @author Elmo
 */
public class ServiceDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return com.rameses.annotations.Service.class;
    }
    
    public Object getResource(Annotation c, ExecutionInfo einfo) {
       
        TransactionContext txn = TransactionContext.getCurrentContext();
        AbstractContext ac = txn.getContext();
        try {
            com.rameses.annotations.Service s  = (com.rameses.annotations.Service)c;
            
            //we should not use the transaction service because transactions are only created once. use direct script execution.
            String serviceName = s.value();
            if(serviceName==null || serviceName.trim().length()==0) {
                serviceName = einfo.getServiceName();
            }
            
            //check connection
            String conn = s.connection();
            
            if( conn==null || conn.trim().length()==0) {
                ScriptTransactionManager smr = txn.getManager(ScriptTransactionManager.class);
                final ManagedScriptExecutor executor = smr.create( serviceName );
                final boolean managed = s.managed();
                ScriptInfo sinfo = executor.getScriptInfo();
                return Proxy.newProxyInstance( sinfo.getInterfaceClass().getClassLoader(), new Class[]{sinfo.getInterfaceClass()}, new ScriptInvocation(executor,managed));
            }
            else {
                Class localIntf = s.localInterface();
                ScriptConnection sc = (ScriptConnection)ac.getResource(XConnection.class, conn);
                if(localIntf!=null)
                    return sc.create(serviceName, txn.getEnv(),localIntf);
                else
                    return sc.create( serviceName, txn.getEnv() );
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("error injecting resource @Service."+ ExceptionManager.getOriginal(e).getMessage());
            return null;
        } finally {
            
        }
    }
}
