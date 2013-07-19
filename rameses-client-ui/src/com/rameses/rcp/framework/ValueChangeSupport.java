/*
 * ValueChangeSupport.java
 *
 * Created on July 8, 2013, 11:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.rcp.common.CallbackHandlerProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author wflores
 */
public class ValueChangeSupport 
{
    private Map<String,List> handlers = new HashMap();  
    private Map extended;
            
    ValueChangeSupport() {}

    public void setExtendedHandler(Map extended) { 
        this.extended = extended; 
    }
    
    public void add(String property, Object callbackListener) 
    {
        if (property == null || callbackListener == null) return;
        
        List list = handlers.get(property); 
        if (list == null) 
        {
            list = new ArrayList();
            handlers.put(property, list); 
        }
        
        list.add(callbackListener); 
    }
    
    public void remove(String property, Object callbackListener) 
    {
        if (property == null || callbackListener == null) return;
        
        List list = handlers.get(property); 
        if (list != null) 
        {
            list.remove(callbackListener);
            if (list.isEmpty()) handlers.remove(property);
        }         
    }
    
    public void removeAll() 
    {
        for (List list : handlers.values()) list.clear(); 
        
        handlers.clear(); 
    }
    
    public void notify(String property, Object value) 
    {
        if (property == null) return;
        
        Set<Map.Entry<String,List>> entries = handlers.entrySet(); 
        for (Map.Entry<String,List> entry: entries) 
        {
            String key = entry.getKey();
            if (!property.matches(key)) continue;
            
            List list = entry.getValue(); 
            if (list == null) continue;
            
            for (Object callback: list) {
                invokeCallback(callback, value); 
            }
        }
        
        if (extended == null) return;
        
        Set<Map.Entry> sets = extended.entrySet(); 
        for (Map.Entry entry: sets) 
        {
            String key = (entry.getKey() == null? null: entry.getKey().toString()); 
            if (key == null || key.length() == 0) continue;
            
            invokeCallback(entry.getValue(), value);
        }
    }
    
    private void invokeCallback(Object callback, Object value) 
    {
        try 
        {
            if (callback == null) return;
            
            new CallbackHandlerProxy(callback).call(value);
        } 
        catch(Exception ex) 
        {
            System.out.println("[ValueChangeSupport_notify] failed caused by " + ex.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) ex.printStackTrace(); 
        } 
    }
}
