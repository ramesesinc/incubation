/*
 * InvokerProxy.java
 *
 * Created on June 28, 2009, 6:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;


import com.rameses.rcp.framework.ClientContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.service.ServiceProxyInvocationHandler;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class InvokerProxy  {
    
    private GroovyClassLoader classLoader;
        
    private static InvokerProxy instance;
    private Map env;
    
    public InvokerProxy() {
    }
    
    public synchronized static InvokerProxy getInstance() {
        if ( instance == null ) {
            instance = new InvokerProxy();
        }
        return instance;
    }
    
    public Map getAppEnv() {
        return ClientContext.getCurrentContext().getAppEnv();
    }
    
    public synchronized void reset() {
        classLoader = new GroovyClassLoader(ClientContext.getCurrentContext().getClassLoader());
        instance = null;
    }
    
    public synchronized Object create(String name) {
        return create(name, null);
    }
    
    private interface ScriptInfoInf  {
        String getStringInterface();
    }
    
    private Map<String,Class> services = new HashMap();
    
    public synchronized Object create(String name, Class localInterface) {
        try {
            if (classLoader == null) 
                classLoader = new GroovyClassLoader(ClientContext.getCurrentContext().getClassLoader());
            
            ScriptServiceContext ect = new ScriptServiceContext(getAppEnv());
            Map _env = OsirisContext.getSessionEnv();
            
            if(localInterface != null) {
                return ect.create( name, _env, localInterface );
            } 
            else {
                if( !services.containsKey( name )) {
                    ScriptInfoInf si = ect.create( name,  ScriptInfoInf.class  );
                    Class clz = classLoader.parseClass( si.getStringInterface() );
                    services.put( name, clz  );
                }
                ServiceProxy sp = ect.create( name, _env );
                
                Class clz = services.get(name);
                return Proxy.newProxyInstance( classLoader, new Class[]{clz}, new ServiceProxyInvocationHandler(sp)  );
            }
        } 
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
