/*
 * NotificationManager.java
 *
 * Created on January 14, 2014, 9:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.util.Service;
import java.util.Iterator;

/**
 *
 * @author wflores
 */
public final class NotificationManager 
{
    private static Object LOCK = new Object();
    private NotificationProvider provider;
    private ClassLoader classLoader;

    NotificationManager(ClassLoader classLoader) {
        Iterator itr = Service.providers(NotificationProvider.class, classLoader); 
        while (itr.hasNext()) {
            provider = (NotificationProvider) itr.next(); 
            break; 
        }
    }
    
    public void close() {
        synchronized (LOCK) {
            if (provider == null) return;
            
            provider.close(); 
        }
    }
    
    public void add(NotificationHandler handler) {
        synchronized (LOCK) {
            if (provider == null) return; 
            
            if (handler != null) {
                provider.add(handler);
            } 
        }
    }
    
    public boolean remove(NotificationHandler handler) {
        synchronized (LOCK) {
            if (handler == null || provider == null) return false; 
            
            return provider.remove(handler); 
        } 
    } 
    
    public void publish(Object data) { 
        synchronized (LOCK) {
            if (provider == null) { 
                System.out.println("No available notification provider"); 
                return; 
            } 
            
            provider.publish(data);
        } 
    } 
    
    public void markAsRead(Object data) {
        synchronized (LOCK) {
            if (provider == null) return; 
            
            provider.markAsRead(data);
        }         
    }
}