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
        if (list != null) list.remove(callbackListener); 
    }
    
    public void removeAll() 
    {
        for (List list : handlers.values()) list.clear(); 
        
        handlers.clear(); 
    }
    
    public void notify(String property, Object value) 
    {
        if (property == null) return;
        
        List list = handlers.get(property); 
        if (list != null) 
        { 
            for (Object callback : list) { 
                invokeCallback(callback, value); 
            } 
        }        
        if (extended != null) 
            invokeCallback(extended.get(property), value); 
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
            if (ClientContext.getCurrentContext().isDebugMode()) 
                ex.printStackTrace(); 
        } 
    }
}
