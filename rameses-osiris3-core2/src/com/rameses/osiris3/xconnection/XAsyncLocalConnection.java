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
public class XAsyncLocalConnection extends XConnection implements XAsyncConnection  {
    
    private final Map<String, MessageQueue> map = new Hashtable();
    
    private Map conf;
    private String name;
    private int poolSize = 100;
    
    /** Creates a new instance of XAsyncLocalConnection */
    public XAsyncLocalConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
    }
    
    public MessageQueue register( String id ) throws Exception {
        MessageQueue mq = new LocalMessageQueue(id, conf);
        map.put( id, mq );
        return mq;
    }
    
    public void unregister( String id ) {
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
        map.clear();
    }
    
    public void start() {
       
    }
    
    public void stop() {
        map.clear();
    }
    
    public Map getConf() {
        return conf;
    }
    
    
    

}
