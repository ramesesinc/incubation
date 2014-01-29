/*
 * AppContext.java
 *
 * Created on January 22, 2014, 10:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class AppContext 
{
    private static Object LOCK = new Object();
    private static AppContext instance;
    
    static void setInstance(AppContext newContext) {
        synchronized (LOCK) {
            AppContext oldContext = instance; 
            if (oldContext != null) oldContext.close(); 
            
            instance = newContext; 
            instance.load(); 
            instance.afterLoad();
        } 
    }
    
    public static SessionContext getSession() {
        return (instance == null? null: instance.sessionContext); 
    }
    
    public static SecurityManager getSecurityManager() {
        return (instance == null? null: instance.securityManager); 
    }
    
    private SessionContext sessionContext;
    private SecurityManager securityManager;
    
    public AppContext() {
    }

    public abstract Map getEnv();
    
    protected void load() {
    }
    
    protected void close() { 
        if (sessionContext != null) sessionContext.close(); 
        if (securityManager != null) securityManager.close(); 
        
        sessionContext = null;
        securityManager = null;
        SessionContext.setCurrent(null); 
        SecurityManager.setCurrent(null); 
    }
    
    protected SessionContext createSession() {
        return new SessionContext(this); 
    } 
    
    protected SecurityManager createSecurityProvider(SessionContext sess) {
        return new SecurityManager(sess); 
    } 
    
    private void afterLoad() {
        sessionContext = createSession();
        securityManager = createSecurityProvider(sessionContext); 
        SessionContext.setCurrent(sessionContext); 
        SecurityManager.setCurrent(securityManager); 
    }
}
