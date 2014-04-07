/*
 * InvokerProxy.java
 *
 * Created on April 7, 2014, 1:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.bridge.service;

import com.rameses.service.ServiceProxy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class InvokerProxy 
{
    private static Object LOCK = new Object();
    
    public static synchronized ServiceProxy create(String serviceName, String connectionName) {
        return create(serviceName, connectionName, new HashMap()); 
    }
    
    public static synchronized ServiceProxy create(String serviceName, String connectionName, Map env) {
        Map appenv = getAppEnv(connectionName);
        ScriptServiceContext ssc = new ScriptServiceContext(appenv); 
        return ssc.create(serviceName, new HashMap()); 
    }

    private static Map getAppEnv(String connectionName) {
        String appcluster = System.getProperty(connectionName + ".cluster");
        if (appcluster == null) appcluster = "osiris3";

        String apphost = System.getProperty(connectionName + ".host");
        String appcontext = System.getProperty(connectionName + ".context");

        Map appenv = new HashMap();
        appenv.put("app.host", apphost); 
        appenv.put("app.cluster", appcluster); 
        appenv.put("app.context", appcontext); 
        
        try { 
            int readTimeout = Integer.parseInt(System.getProperty(connectionName + ".readTimeout")); 
            appenv.put("readTimeout", readTimeout);
        } catch(Throwable t) {;} 
        
        try { 
            int connectionTimeout = Integer.parseInt(System.getProperty(connectionName + ".connectionTimeout")); 
            appenv.put("connectionTimeout", connectionTimeout);
        } catch(Throwable t) {;} 
        
        return appenv;
    }
    
    private InvokerProxy() {
    }
}
