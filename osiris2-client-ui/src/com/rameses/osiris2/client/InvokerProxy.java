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
import groovy.lang.GroovyObject;
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
                //we cannot at this time use the dynamic invocation from groovy because we have methods
                //that may not match example, async.
                if( !services.containsKey( name )) {
                    StringBuilder builder = new StringBuilder();
                    builder.append( "public class MyMetaClass  { \n" );
                    builder.append( "    def invoker; \n");
                    builder.append( "    public Object invokeMethod(String string, Object args) { \n");
                    builder.append( "        return invoker.invokeMethod(string, args); \n" );
                    builder.append( "    } \n");
                    builder.append(" } ");
                    Class metaClass = classLoader.parseClass( builder.toString() );                    
                    services.put( name, metaClass  );
                    /*
                    ScriptInfoInf si = ect.create( name,  ScriptInfoInf.class  );
                    Class clz = classLoader.parseClass( si.getStringInterface() );
                    services.put( name, clz  );
                     */
                }
                
                
                ServiceProxy sp = ect.create( name, _env );
                ServiceProxyInvocationHandler si = new ServiceProxyInvocationHandler(sp);
                
                Object obj = services.get(name).newInstance();
                ((GroovyObject)obj).setProperty( "invoker", si );
                return obj;                
                
                /*
                ServiceProxy sp = ect.create( name, _env );
                Class clz = services.get(name);
                return Proxy.newProxyInstance( classLoader, new Class[]{clz}, new ServiceProxyInvocationHandler(sp)  );
                 */
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
