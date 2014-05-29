/*
 * AsyncQueue.java
 *
 * Created on May 29, 2014, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Elmo
 */
public final class AsyncQueue {
    
    private Map<String, AsyncQueueHolder> map = new Hashtable();
    
    public void register( String id, Map opt ) throws Exception {
        map.put( id, new AsyncQueueHolder(id, opt) );
    }
    
    public void unregister( String id ) {
        map.remove( id );
    }
    
    public void push(String id, Object data ) throws Exception {
        AsyncQueueHolder h = map.get(id);
        if(h==null) throw new Exception("Async id " + id + " not registered" );
        h.push(data);
    }
    
    public Object poll(String id ) throws Exception {
        AsyncQueueHolder h = map.get(id);
        if(h==null) throw new Exception("Async id " + id + " not registered" );
        return h.poll();
    }

    public void clear() {
        map.clear();
    }
    
    public static class AsyncQueueHolder {
        private LinkedBlockingQueue queue = new LinkedBlockingQueue();
        private Map options;
        private String id;
        private long timeout = 10000;
        
        public AsyncQueueHolder(String id, Map options ) {
            this.id = id;
            this.options = options;
            if(options.containsKey("timeout")) {
                timeout = Long.parseLong(options.get("timeout").toString());
            }
        }
        
        public void push(Object obj) {
            queue.add(obj); 
        }
        
        public Object poll() throws Exception {
            return queue.poll( timeout, TimeUnit.MILLISECONDS );
        }
    }
    
    
}
