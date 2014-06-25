/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class XAsyncLocalConnection extends XConnection implements XAsyncConnection  
{
    private final Map<String, MessageQueue> map = new Hashtable();
    private final Object REGISTRY_LOCK = new Object();
    
    private Map conf;
    private String name;
    private boolean debug;
    private int poolSize = 100;
    
    public XAsyncLocalConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
        
        if (conf != null) {
            debug = "true".equals(conf.get("debug")+"");
        }
    }
    
    public MessageQueue register( String id ) throws Exception {
        synchronized (REGISTRY_LOCK) {
            MessageQueue mq = new LocalMessageQueue(id, conf);
            if (map.containsKey(id)) {
                return map.get(id); 
            } else {
                if (debug) {
                    System.out.println("[" + getClass().getSimpleName() + "_register] " + id);
                }
                map.put( id, mq );
                return mq;
            }
        }
    }
    
    public void unregister( String id ) {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_unregister] " + id);
        }        
        map.remove( id );
    }
    
    public MessageQueue getQueue( String id ) throws Exception {
        MessageQueue q = map.get(id);
        if(q==null) throw new Exception("MessageQueue " + id + " does not exist");
        return q;
    }
    
    public Map getQueueMap() {
        return map;
    }
    
    public void clear() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_clear] " + name);
        }         
        map.clear();
    }
    
    public void start() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_start] " + name);
        } 
    }
    
    public void stop() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_stop] " + name);
        } 
        map.clear();
    }
    
    public Map getConf() {
        return conf;
    }
}
