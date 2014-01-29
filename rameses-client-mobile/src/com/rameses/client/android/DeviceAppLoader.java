/*
 * DevicePlatformLoader.java
 *
 * Created on January 22, 2014, 11:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import com.rameses.client.interfaces.DeviceContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class DeviceAppLoader 
{
    private static ClassLoader mainClassLoader = Thread.currentThread().getContextClassLoader();
    
    public static ClassLoader getDefaultClassLoader() {
        return mainClassLoader; 
    }
    
    public static void load(Map env, DeviceContext deviceContext) {
        load(env, deviceContext, mainClassLoader);
    }
    
    public static void load(Map env, DeviceContext deviceContext, ClassLoader classLoader) {
        new DeviceAppLoader().loadImpl(env, deviceContext, classLoader);
    }    
        
    private DeviceAppLoader() {
    }
    
    private void loadImpl(Map env, DeviceContext deviceContext, ClassLoader classLoader) {
        if (env == null) env = new HashMap(); 
        
        DeviceAppContext dac = new DeviceAppContext(env);
        if (env.get("readTimeout") == null) {
            env.put("readTimeout", "20000");
        } 
        AppContext.setInstance(dac); 
        ClientContext cctx = new ClientContext(dac, deviceContext); 
        ClientContext.setCurrentContext(cctx); 
        Loaders.load(classLoader);
    }
    
}
