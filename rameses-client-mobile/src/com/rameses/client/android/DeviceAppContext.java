/*
 * DeviceAppContext.java
 *
 * Created on January 22, 2014, 11:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import java.util.Map;

/**
 *
 * @author wflores
 */
class DeviceAppContext extends AppContext 
{
    private Map env;
    
    public DeviceAppContext(Map env) {
        this.env = env;
    }

    public Map getEnv() { return env; }

    protected void close() {
        super.close();
        if (env != null) env.clear(); 
    }
}
