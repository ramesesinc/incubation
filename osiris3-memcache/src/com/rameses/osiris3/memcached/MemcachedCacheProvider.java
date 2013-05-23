/*
 * MemcachedCacheProvider.java
 *
 * Created on February 9, 2013, 10:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.memcached;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class MemcachedCacheProvider extends XConnectionProvider {
    
    public String getProviderName() {
        return "memcached";
    }

    public XConnection createConnection(String name, Map conf) {
        return new MemcacheCached(name, conf);
    }
    
}
