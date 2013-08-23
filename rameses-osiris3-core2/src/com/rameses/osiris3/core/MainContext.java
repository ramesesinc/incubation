/*
 * MainContext.java
 *
 * Created on January 30, 2013, 10:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.util.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Elmo
 * MainContext is the front facing context. Applicable for AppContext
 * and PluginContext. Not applicable to SharedContext
 *
 */
public class MainContext extends AbstractContext {
    
    //this is used for running asynchronous tasks
    private Map<String, ContextService> services = Collections.synchronizedMap(new HashMap());
    protected ExecutorService asyncExecutor = Executors.newCachedThreadPool();
    
    public MainContext(OsirisServer s) {
        super(s);
    }
    
    public void start() {
        asyncExecutor = Executors.newCachedThreadPool();
        //load the services
        List<ContextService> list = new ArrayList();
        Iterator<ContextService> iter = Service.providers(ContextService.class, getClass().getClassLoader());
        while(iter.hasNext()) {
            ContextService cs = iter.next();
            cs.setContext( this );
            //System.out.println("add service " + cs);
            addService( cs.getProviderClass(), cs );
            list.add( cs );
        }
        
        Collections.sort( list );
        for(ContextService c: list) {
            try {
               c.start();
            } catch(Exception e) {
                System.out.println("error starting service " + super.getName()+":"+c.getName() +" " +e.getMessage());
            }
        }
        list.clear();
    }
    
    public void stop() {
        for(ContextService c: services.values()) {
            try {
                c.stop();
            } catch(Exception e) {
                System.out.println("error stopping service " + super.getName()+":"+c.getName() +" " +e.getMessage());
            }
        }
        asyncExecutor.shutdownNow();
    }
    
    
    public final void addService(String name, ContextService svc) {
        this.services.put( name, svc );
    }
    
    public final void addService(Class serviceClass, ContextService svc) {
        this.services.put( serviceClass.getSimpleName(), svc );
    }
    
    public final ContextService getService(String serviceName) {
        return services.get(serviceName);
    }
    
    public final <T> T getService(Class<T> serviceClass ) {
        return (T)services.get( serviceClass.getSimpleName() );
    }
    
    public void submitAsync(Runnable runnable) {
        asyncExecutor.submit( runnable );
    }
    
}
