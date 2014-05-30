/*
 * AsyncQueueMap.java
 *
 * Created on May 30, 2014, 4:43 PM
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
public final class MessageQueueMap {
    
    private final Map<String, MessageQueue> map = new Hashtable();
    
    public MessageQueue register( String id, Map opt ) throws Exception {
        MessageQueue mq = new MessageQueue(id, opt);
        map.put( id, mq );
        return mq;
    }
    
    public void unregister( String id ) {
        map.remove( id );
    }
    
    public MessageQueue getQueue( String id ) {
        return map.get(id);
    }
    
    public void clear() {
        map.clear();
    }
    
}
