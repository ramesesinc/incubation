/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
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
public class XAsyncLocalConnection extends XConnection implements XAsyncConnection  {
    
    private AsyncQueue queue;
    private Map conf;
    private String name;
    
    /** Creates a new instance of XAsyncLocalConnection */
    public XAsyncLocalConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
    }

    public void start() {
        queue = new AsyncQueue();
    }

    public void stop() {
        queue.clear();
        queue = null;
    }

    public Map getConf() {
        return conf;
    }

    public void register(String id) throws Exception {
        queue.register(id,conf);
    }

    public void unregister(String id) throws Exception {
        queue.unregister(id);
    }

    public Object poll(String id) throws Exception {
        return queue.poll(id);        
    }

    public void push(String id, Object data) throws Exception {
        queue.push( id, data );
    }
    
}
