/*
 * TransactionScopeDependencyHandler.java
 *
 * Created on January 15, 2013, 6:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.XConnection;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class XConnectionDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return XConnection.class;
    }
    public Object getResource(Annotation c, ExecutionInfo e) {
        XConnection mc = (XConnection)c;
        MainContext ctx = TransactionContext.getCurrentContext().getContext();
        String connName = mc.value();
        if(connName==null || connName.trim().length()==0) connName = "default";        
        return ctx.getResource( XConnection.class, connName );
    }
    
}
