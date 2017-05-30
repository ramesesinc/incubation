/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.remote;


import com.rameses.osiris3.cache.CacheProvider;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.script.InvokerProxy;
import com.rameses.osiris3.xconnection.XConnection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elmo
 */
public class RemoteScriptConnection extends XConnection {

    private AbstractContext context;
    private String name;
    private Map conf = new HashMap();
    private InvokerProxy proxy;
    
    private CacheProvider registryCache;
    private CacheProvider dataCache;
    
    public RemoteScriptConnection(String name, AbstractContext ctx, Map conf ) {
        this.name = name;
        this.conf = conf;
        this.context = ctx;
        this.proxy = new InvokerProxy((AppContext) ctx, conf);
    }
    
    public void start() {
        System.out.println("conf is " + conf );
        System.out.println("STARTING REMOTE SCRIPT CONNECTION");
    }

    public void stop() {
        System.out.println("STOP REMOTE SCRIPT CONNECTION");
    }

    public Map getConf() {
        return conf;
    }
    
    public void register(Map defs) throws Exception {
        Object s = proxy.create("RemoteScriptRegistryService", new HashMap(), RemoteScriptRegistryServiceInterface.class);
        RemoteScriptRegistryServiceInterface rs = (RemoteScriptRegistryServiceInterface)s;
        String id = (String)conf.get("id");
        if(id == null ) 
            throw new Exception("RemoteScriptRegistryService error. id must be provided in connection settings");
        rs.register(id, defs);
    }
    
    public CacheProvider getRegistryCache() {
        if( registryCache != null) return registryCache;
        
        
        return registryCache;
    }
    
    
    
}
