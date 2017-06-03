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
import com.rameses.anubis.ServiceAdapter;
import com.rameses.anubis.ServiceInvoker;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptServiceHandler  implements ServiceAdapter {
    
    public String getName() {
        return "script";
    }
    
    private boolean isRemoteService(String name) {
        return ( name.indexOf(":") >0 ); 
    }
    
    @Override
    public Object create(String name, Map conf) {
        if (conf.get("app.cluster") == null) 
            throw new RuntimeException("cluster is not defined");        
        if (conf.get("app.host") == null) 
            throw new RuntimeException("app.host is not defined");        
        if (conf.get("app.context") == null)
            throw new RuntimeException("app.context is not defined");
        
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
        
        if( isRemoteService(name) ) {
            return new RemoteScriptInvoker(name, conf, env);
        }
        else {
            return new LocalScriptInvoker(name, conf, env);
        }
    }
    
    public Map getClassInfo(String name, Map conf) { 
        if (conf.get("app.cluster") == null) 
            throw new RuntimeException("cluster is not defined");        
        if (conf.get("app.host") == null) 
            throw new RuntimeException("app.host is not defined");        
        if (conf.get("app.context") == null)
            throw new RuntimeException("app.context is not defined");
        if(isRemoteService(name)) {
            return getRemoteServiceClassInfo(name, conf);
        }
        else {
            return getLocalServiceClassInfo( name, conf );
        }
    }

    /***************************************************************************
     * The following code is to get the class info for local and remote
     **************************************************************************/ 
    private interface IScriptService  {
        String stringInterface();
        Map metaInfo();
    }
    
    private interface IRemoteScriptCacheService {
         Map getInfo(String name);
    }

    private Map getLocalServiceClassInfo( String name, Map conf ) {
        ScriptServiceContext ctx = new ScriptServiceContext(conf);
        IScriptService svc = ctx.create(name, IScriptService.class );
        Map metainfo = svc.metaInfo();
        Map methods = (Map) metainfo.get("methods"); 
        Map results = new HashMap();
        results.put("methods", methods.values()); 
        results.put("name", metainfo.get("serviceName")); 
        return results; 
    }
    
    private Map getRemoteServiceClassInfo( String name, Map conf ) {
        ScriptServiceContext ctx = new ScriptServiceContext(conf);
        IRemoteScriptCacheService svc = ctx.create("RemoteScriptCacheService", IRemoteScriptCacheService.class );
        return svc.getInfo(name);
    }
    
    private class LocalScriptInvoker implements ServiceInvoker {
        
        private ServiceProxy serviceProxy;
        
        public LocalScriptInvoker(String name, Map conf, Map env) {
            ScriptServiceContext ctx = new ScriptServiceContext(conf);
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
    
    /***************************************************************************
     * REMOTE SERVICE
     **************************************************************************/ 
    private class RemoteScriptInvoker implements ServiceInvoker {
        
        private String serviceName;
        private ServiceProxy invokerProxy;
        
        public RemoteScriptInvoker(String name, Map conf, Map env) {
            this.serviceName = name;
            ScriptServiceContext ctx = new ScriptServiceContext(conf);
            invokerProxy = ctx.create( "RemoteScriptInvokerService", env );
        }

        public Object invokeMethod(String methodName, Object[] args) {
            System.out.println("Invoking remote method");
            Map map = new HashMap();
            map.put("serviceName", serviceName);
            map.put("methodName", methodName);
            map.put("args", args);
            try {
               return invokerProxy.invoke("invoke", new Object[]{map} );
           } catch(Exception ex) {
               throw new RuntimeException(ex.getMessage(), ex);
               //return "<font color=red>error service:" + ex.getMessage()+"</font>";
           }
        }
    }
    
}
