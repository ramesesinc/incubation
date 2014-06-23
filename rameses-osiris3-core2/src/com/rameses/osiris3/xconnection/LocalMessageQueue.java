/*
 * AsyncQueue.java
 *
 * Created on May 29, 2014, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.common.AsyncBatchResult;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Elmo
 */
public final class LocalMessageQueue implements MessageQueue {
    
    private LinkedBlockingQueue queue = new LinkedBlockingQueue();
    private Map options;
    private String id;
    private long timeout = 20000;
    
    public LocalMessageQueue(String id, Map options ) {
        this.id = id;
        this.options = options;
        if(options.containsKey("timeout")) {
            timeout = Long.parseLong(options.get("timeout").toString());
        }
    }
    
    public void push(Object obj) throws Exception {
        System.out.println("push: id="+id + ", obj=" + obj);
        queue.add(obj);
    }
    
    public Object poll() throws Exception {
        System.out.println("poll: id="+id + ", empty=" + queue.isEmpty());
        Object o = queue.poll( timeout, TimeUnit.MILLISECONDS );
        if (!queue.isEmpty()) {
            List list = new AsyncBatchResult();
            list.add(o);
            while (!queue.isEmpty()) {
                list.add(queue.poll());
            }
            return list;
        } else {
            return o;
        }
    }
    
    public String toString() {
        return "MESSAGE-QUEUE " + this.id;
    }
    
}
