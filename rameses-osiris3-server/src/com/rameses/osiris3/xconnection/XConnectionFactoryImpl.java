/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.script.messaging.ScriptConnection;
import com.rameses.osiris3.server.wsclient.WebsocketConnection;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
class XConnectionFactoryImpl extends XConnectionFactory {

    private AbstractContext ctx;
    private String name;
    private Map conf;
    
    public XConnectionFactoryImpl(String name, AbstractContext ctx, Map conf) {
        this.name = name; 
        this.ctx = ctx;
        this.conf = conf; 
    }
    
    @Override
    public Map getConf() {
        return conf; 
    }
    
    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void stop() {
        //do nothing
    }
    

    
    private Map<String,XConnection> conns = new HashMap(); 
    
    @Override
    public XConnection getConnection(Annotation anno) {
        if (anno == null) { return null; } 

        String providerName = null; 
        if (anno instanceof com.rameses.annotations.Service) {
            providerName = "script"; 
        } else if (anno instanceof com.rameses.annotations.OnMessage) {
            providerName = "websocket";
        } else {
            return this; 
        } 
        
        XConnection xc = conns.get(providerName); 
        if (xc == null) {
            XConnectionContextResource xcr = (XConnectionContextResource) ctx.getContextResource(XConnection.class);
            xc = xcr.getProvider(providerName).createConnection(name, getGroupConf(providerName));
            conns.put(providerName, xc); 
            xc.start();
        }
        return xc; 
    }
    
    @Override
    public XConnection getConnection(String category) {
        if (category == null) { return null; } 

        XConnection xc = conns.get(category); 
        if (xc == null) {
            Map catconf = getGroupConf(category);
            if (catconf == null || catconf.isEmpty()) { return null; }
            
            String providerName = (String) catconf.get("provider");
            if (providerName == null || providerName.trim().length() == 0) {
                providerName = category; 
            }
            
            XConnectionContextResource xcr = (XConnectionContextResource) ctx.getContextResource(XConnection.class);
            XConnectionProvider xp = xcr.getProvider(providerName); 
            if (xp == null) {
                throw new NullPointerException("XConnectionProvider '"+ providerName + "' not found");
            }
            xc = xp.createConnection(category, catconf);
            conns.put(category, xc); 
            xc.start();
        }
        return xc; 
    }
    
    private Map getGroupConf(String name) { 
        try { 
            Map grpconf = (Map) getConf().get(name); 
            if (grpconf == null) {
                return new HashMap(); 
            } else { 
                return grpconf; 
            }
        } catch(Throwable t) {
            return new HashMap();
        }
    }
}
