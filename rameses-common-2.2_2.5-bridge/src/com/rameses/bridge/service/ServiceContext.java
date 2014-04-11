/*
 * ServiceContext.java
 * Created on April 7, 2014, 1:01 PM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.bridge.service;

import java.util.Map;

/**
 * This class used is to lookup a service proxy
 */
public abstract class ServiceContext  {
    
    public static String USE_DEFAULT = "use.default";
    
    private Map map;
    protected boolean secured = true;
    
    public ServiceContext(Map conf) {
        this.map = conf;
        String host = (String)getMap().get("app.host");
        if(host==null || host.trim().length() == 0 ) {
            getMap().put("app.host", "localhost:8080");
        }
        if(this.getMap().containsKey("secured")) {
            try {
                secured  = Boolean.parseBoolean(this.getMap().get("secured")+"");
            }
            catch(Exception e){;}
        }
        getMap().put("secured", secured);
    }

    public Map getMap() {
        return map;
    }
}
