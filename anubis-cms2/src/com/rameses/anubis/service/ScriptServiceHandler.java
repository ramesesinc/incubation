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
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
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
        Map metainfo = svc.metaInfo();
        Map methods = (Map) metainfo.get("methods"); 
        Map results = new HashMap();
        results.put("methods", methods.values()); 
        results.put("name", metainfo.get("serviceName")); 
        return results; 
    }
    
    private interface IScriptService  {
        String stringInterface();
        Map metaInfo();
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
