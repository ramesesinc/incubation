/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import com.sun.jmx.remote.util.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author wflores 
 */
public final class NotificationManager {
    
    private final static NotificationManager instance = new NotificationManager(); 
    private final static Object LOCKED = new Object(); 
    
    public static NotificationProvider getDefaultProvider() { 
        try { 
            return instance.getProviders().get(0); 
        } catch(ArrayIndexOutOfBoundsException aie) { 
            //do nothing 
        } catch(Throwable t) {
            t.printStackTrace(); 
        } 
        return instance.getEmptyProvider();  
    } 
    
    public static void addHandler( NotificationHandler handler ) { 
        synchronized ( LOCKED ) {
            if ( handler == null ) return; 
            
            instance.handlers.remove( handler ); 
            instance.handlers.add( handler ); 
        }
    }
    public static void removeHandler( NotificationHandler handler ) { 
        synchronized ( LOCKED ) {
            if ( handler != null ) {
                instance.handlers.remove( handler ); 
            }
        } 
    }
    public static List<NotificationHandler> getHandlers() {
        return instance.handlers; 
    }
    
    
    private final List<NotificationProvider> providers = new ArrayList();
    private final List<NotificationHandler> handlers = new ArrayList();
    
    private NotificationProvider emptyProvider;
    private boolean allow_fetch_providers = true; 
    
    private synchronized List<NotificationProvider> getProviders() { 
        if ( allow_fetch_providers ) {  
            Iterator itr = Service.providers(NotificationProvider.class, ClientContext.getCurrentContext().getClassLoader()); 
            allow_fetch_providers = false; 
            providers.clear(); 

            while ( itr.hasNext() ) { 
                Object o = itr.next(); 
                if ( o instanceof NotificationProvider ) {
                    providers.add((NotificationProvider) o); 
                } 
            } 
        } 
        return providers; 
    }
    
    private NotificationProvider getEmptyProvider() {
        if ( emptyProvider == null ) { 
            emptyProvider = new EmptyNotificationProvider(); 
        } 
        return emptyProvider; 
    }
}
