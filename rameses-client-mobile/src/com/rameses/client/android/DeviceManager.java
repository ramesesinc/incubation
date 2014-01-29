/*
 * DeviceManager.java
 *
 * Created on January 28, 2014, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class DeviceManager 
{
    private static DeviceManager instance;
    
    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager(); 
        }
        return instance;
    }
    
    
    private Map<String,UIActivity> windows;
    
    private DeviceManager() {
        windows = new HashMap();
    }
    
    public synchronized void register(UIActivity ui) {
        if (ui == null) return;
        String name = ui.getClass().getName();
        if (windows.containsKey(name)) return;
        
        windows.put(name, ui); 
    }
    
    public synchronized void unregister(UIActivity ui) {
        if (ui == null) return;
        
        windows.remove(ui.getClass().getName()); 
    }
    
    public synchronized UIActivity find(String name) {
        if (name == null || name.length() == 0) return null;
        
        return windows.get(name); 
    }

    public synchronized void closeAll() {
        closeAll(null);
    }
    
    public synchronized void closeAll(UIActivity except) {
        Iterator<UIActivity> itr = windows.values().iterator(); 
        while (itr.hasNext()) {
            UIActivity ui = itr.next();
            if (except != null && except.equals(ui)) continue;
            
            try {                 
                ui.finish(); 
            } catch(Throwable t) {
                t.printStackTrace();
            }
        } 
        windows.clear(); 
    }    
}

