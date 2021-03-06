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
    
    synchronized static void setInstance(AppContext newContext) {
        AppContext oldContext = instance; 
        if (oldContext != null) oldContext.close(); 

        instance = newContext; 
        if (instance != null) { 
            instance.init();
            instance.load(); 
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
    
    private void init() {
        sessionContext = createSessionContext();
        securityManager = createSecurityProvider(sessionContext); 
        SessionContext.setCurrent(sessionContext); 
        SecurityManager.setCurrent(securityManager); 
    }
    
    protected void load() {
    }
    
    void close() { 
        SessionContext.setCurrent(null); 
        SecurityManager.setCurrent(null); 
        
        if (sessionContext != null) sessionContext.close(); 
        if (securityManager != null) securityManager.close(); 
        
        sessionContext = null;
        securityManager = null;
        afterClose();
    }
    
    protected void afterClose() {
    }
    
    protected SessionContext createSessionContext() {
        return new SessionContext(this); 
    } 
    
    protected SecurityManager createSecurityProvider(SessionContext sess) {
        return new SecurityManager(sess); 
    } 
}
