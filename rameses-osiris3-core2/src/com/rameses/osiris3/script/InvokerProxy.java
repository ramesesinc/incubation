/*
 * InvokerProxy.java
 *
 * Created on February 24, 2013, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.NullIntf;
import com.rameses.osiris3.core.AppContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.service.ServiceProxyInvocationHandler;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public final class InvokerProxy {
    
    private AppContext context;
    private Map conf;
    private Map<String, Class> scripts = Collections.synchronizedMap(  new HashMap() );
    private GroovyClassLoader classLoader;
    
    /** Creates a new instance of InvokerProxy */
    public InvokerProxy(AppContext ctx, Map conf) {
        this.conf = conf;
        this.context = ctx;
        this.classLoader = new GroovyClassLoader(ctx.getClassLoader());
    }
    
    private interface ScriptInfoInf  {
        String getStringInterface();
    }
    public Object create(String serviceName, Map env) throws Exception{
        return create(serviceName, env, null);
    }
    public Object create(String serviceName, Map env, Class localInterface) throws Exception{
        ScriptServiceContext ect = new ScriptServiceContext(conf);
        //context.get
        if(localInterface!=NullIntf.class && localInterface!=null) {
            return ect.create( serviceName, env, localInterface );
        }
        if( !scripts.containsKey(serviceName) ) {
            ScriptInfoInf si = ect.create( serviceName,  ScriptInfoInf.class  );
            Class clz = classLoader.parseClass( si.getStringInterface() );
            scripts.put( serviceName, clz );
        }
        Class clz  = scripts.get(serviceName);
        ServiceProxy sp = ect.create( serviceName, env );
        return Proxy.newProxyInstance( classLoader, new Class[]{clz}, new ServiceProxyInvocationHandler(sp) );
    }
    
}
