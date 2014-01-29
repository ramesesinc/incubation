/*
 * DefaultAppLoader.java
 *
 * Created on January 22, 2014, 1:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android.impl;

import com.rameses.client.interfaces.AppLoader;
import com.rameses.client.interfaces.AppLoaderCaller;

/**
 *
 * @author compaq
 */
public class DefaultAppLoader implements AppLoader 
{
    private AppLoaderCaller caller;
    
    public DefaultAppLoader() {
    }

    public int getIndex() { 
        return 0; 
    }

    public void load() {
        System.out.println("DefaultAppLoader loaded...");
    }

    public void setCaller(AppLoaderCaller caller) {
        this.caller = caller;
    }
}
