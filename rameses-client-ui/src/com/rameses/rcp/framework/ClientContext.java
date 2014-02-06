/*
 * ClientContext.java
 *
 * Created on June 18, 2010, 1:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.impl.ClientContextImpl;
import com.rameses.rcp.impl.ControllerProviderImpl;
import com.rameses.rcp.impl.NavigationHandlerImpl;

import com.rameses.common.ValueResolver;
import com.rameses.util.Service;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

/**
 * @author jaycverg
 */
public abstract class ClientContext 
{
    private static ClientContext currentContext;
    
    private TaskManager taskManager;
    private NotificationManager notificationMgr;
    private NavigationHandler navigationHandler;
    private ControllerProvider controllerProvider;
    private ActionProvider actionProvider;
    private OpenerProvider openerProvider;
    private Services services;
    
    private Map appEnv = new HashMap();
    private Map headers = new HashMap();
    private Map properties = new HashMap();
    
    private boolean debugMode;
    
    private List<WeakReference<ExecutorService>> executors = new Vector();
    
    
    //<editor-fold defaultstate="collapsed" desc="  abstract properties  ">
    public abstract ValueResolver getValueResolver();
    public abstract Locale getLocale();
    public abstract void setLocale(Locale locale);
    public abstract Platform getPlatform();
    public abstract void setPlatform(Platform platform);
    public abstract ClassLoader getClassLoader();
    public abstract void setClassLoader(ClassLoader cl);
    public abstract ResourceProvider getResourceProvider();
    public abstract void setResourceProvider(ResourceProvider resProvider);
    public abstract ClientSecurityProvider getSecurityProvider();
    public abstract void setSecurityProvider(ClientSecurityProvider csp);
    //</editor-fold>
    
    public final ControllerProvider getControllerProvider() {
        if ( controllerProvider == null ) {
            Iterator itr = Service.providers(ControllerProvider.class, getClassLoader());
            if ( itr.hasNext() ) {
                controllerProvider = (ControllerProvider) itr.next();
            } else {
                controllerProvider = new ControllerProviderImpl();
            }
        }
        return controllerProvider;
    }
    
    public final NavigationHandler getNavigationHandler() {
        if ( navigationHandler == null ) {
            Iterator itr = Service.providers(NavigationHandler.class, getClassLoader());
            if ( itr.hasNext() ) {
                navigationHandler = (NavigationHandler) itr.next();
            } else {
                navigationHandler = new NavigationHandlerImpl();
            }
        }
        return navigationHandler;
    }
    
    public final ActionProvider getActionProvider() {
        if ( actionProvider == null ) {
            Iterator itr = Service.providers(ActionProvider.class, getClassLoader());
            if ( itr.hasNext() ) {
                actionProvider = (ActionProvider) itr.next();
            }
        }
        return actionProvider;
    }
    
    public final OpenerProvider getOpenerProvider() {
        if ( openerProvider == null ) {
            Iterator itr = Service.providers(OpenerProvider.class, getClassLoader());
            if ( itr.hasNext() ) {
                openerProvider = (OpenerProvider) itr.next();
            }
        }
        return openerProvider;
    }
    
    public static final ClientContext getCurrentContext() {
        if ( currentContext == null ) {
            setCurrentContext( new ClientContextImpl() );
        }
        return currentContext;
    }
    
    public static final void setCurrentContext(ClientContext context) {
        ClientContext old = currentContext;
        if (old != null) {
            try { old.taskManager.stop(); }catch(Throwable t){;} 
            try { old.notificationMgr.close(); }catch(Throwable t){;} 
            try { old.services.stop(); }catch(Throwable t){;}             
        }
        
        currentContext = context;        
        currentContext.notificationMgr = new NotificationManager(context.getClassLoader());         
        currentContext.taskManager = new TaskManager();
        currentContext.services = new Services();        
    }
    
    public final TaskManager getTaskManager() {
        return taskManager;
    }
    
    public final NotificationManager getNotificationManager() {
        return notificationMgr; 
    }
    
    public final Services getServices() {
        return services; 
    }
    
    public Map getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map headers) {
        this.headers = headers;
    }
    
    public Map getProperties() {
        return properties;
    }
    
    public Map getAppEnv() {
        return appEnv;
    }
    
    public void setAppEnv(Map appEnv) {
        this.appEnv = appEnv;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public void registerExecutor(ExecutorService svc) {
        if( !executors.contains(svc) ) {
            executors.add(new WeakReference(svc));
        }
    }
    
    public void unregisterExecutor(ExecutorService svc) {
        executors.remove(svc);
    }
    
    public void shutdown() {
        getTaskManager().stop();
        getNotificationManager().close();
        getServices().stop();
        for(WeakReference wr : executors) {
            try {
                ExecutorService svc = (ExecutorService) wr.get();
                if( svc == null ) continue;
                
                if( isDebugMode() ) 
                    System.out.println("shutting down executor service: " + svc);
                                
                svc.shutdownNow();
            }
            catch(Exception e) {
                if( isDebugMode() ) e.printStackTrace();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" DesktopService "> 
    
    public static interface DesktopService 
    { 
        void start(); 
        void stop(); 
    } 
    
    // </editor-fold>
}
