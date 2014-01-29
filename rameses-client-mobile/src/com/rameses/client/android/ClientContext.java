/*
 * ClientContext.java
 *
 * Created on January 22, 2014, 12:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import com.rameses.client.interfaces.DeviceContext;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class ClientContext 
{
    private static Object LOCK = new Object();
    private static ClientContext current; 
    
    public static ClientContext getCurrentContext() {
        return current; 
    }
    
    static synchronized void setCurrentContext(ClientContext newContext) {
        ClientContext old = current;
        if (old != null) old.close();
        
        current = newContext; 
        if (current != null) current.init();
    }
    
    
    private AppContext appContext;
    private DeviceContext deviceContext;
    private TaskManager taskManager;
    
    ClientContext(AppContext appContext, DeviceContext deviceContext) {
        this.appContext = appContext; 
        this.deviceContext = deviceContext;
    }
    
    public Map getAppEnv() { 
        return appContext.getEnv();  
    } 
    
    public Map getEnv() {
        SessionContext sess = appContext.getSession(); 
        return (sess == null? null: sess.getHeaders()); 
    }
    
    public DeviceContext getDeviceContext() {
        return deviceContext; 
    } 
    
    public TaskManager getTaskManager() {
        return taskManager; 
    }
    
    protected void init() {
        taskManager = new TaskManager(); 
        TaskManager.setCurrent(taskManager); 
    } 
    
    protected void close() {
    }
}
