/*
 * XAsyncLocalConnectionProvider.java
 *
 * Created on May 29, 2014, 2:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public class XAsyncLocalConnectionProvider extends XConnectionProvider {
    
    /** Creates a new instance of XAsyncLocalConnectionProvider */
    public XAsyncLocalConnectionProvider() {
    }

    public String getProviderName() {
        return "async";
    }

    public XConnection createConnection(String name, Map conf) {
        return new XAsyncLocalConnection(name,conf);
    }
    
}
