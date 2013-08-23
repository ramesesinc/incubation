/*
 * HttpServiceHandler.java
 *
 * Created on June 24, 2012, 12:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis.service;

import com.rameses.anubis.AnubisContext;
import com.rameses.anubis.ServiceInvoker;
import com.rameses.classutils.ClassDefMap;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import groovy.lang.GroovyClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptServiceHandler  extends AbstractServiceHandler {
    
    public String getName() {
        return "script";
    }
    
    protected ServiceInvoker getServiceInvoker(String name, Map conf) {
        return new MyScriptInvoker(name, conf);
    }
    
    public Map getClassInfo(String name, Map conf) { 
        if (conf.get("app.cluster") == null) 
            throw new RuntimeException("cluster is not defined");        
        if (conf.get("app.host") == null) 
            throw new RuntimeException("app.host is not defined");        
        if (conf.get("app.context") == null)
            throw new RuntimeException("app.context is not defined");
        
        ScriptServiceContext ctx = new ScriptServiceContext(conf);
        IScriptService svc = ctx.create(name, IScriptService.class );
        GroovyClassLoader loader = new GroovyClassLoader();
        
        Class clazz = loader.parseClass(svc.getStringInterface(name));
        return  ClassDefMap.toMap(clazz);
    }
    
    private interface IScriptService  {
        String getStringInterface(String name);
    }
    
    private class MyScriptInvoker implements ServiceInvoker {
        
        private ServiceProxy serviceProxy;
        
        public MyScriptInvoker(String name, Map conf) {
            if (conf.get("app.cluster") == null) 
                throw new RuntimeException("cluster is not defined");        
            if (conf.get("app.host") == null) 
                throw new RuntimeException("app.host is not defined");        
            if (conf.get("app.context") == null)
                throw new RuntimeException("app.context is not defined");

            ScriptServiceContext ctx = new ScriptServiceContext(conf);
            
            //build the env. include special parameters
            Map env = new HashMap();
            for (Object es : conf.entrySet()) {
                Map.Entry me = (Map.Entry)es;
                if( me.getKey().toString().startsWith("ds.")) {
                    env.put( me.getKey(), me.getValue() );
                }
            }
            AnubisContext actx = AnubisContext.getCurrentContext();
            env.putAll( actx.getEnv() );
            
            serviceProxy = ctx.create( name, env );
        }
        public Object invokeMethod(String methodName, Object[] args) {
            try {
                return serviceProxy.invoke( methodName, args );
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
                //return "<font color=red>error service:" + ex.getMessage()+"</font>";
            }
        }
    }
    
    
}
