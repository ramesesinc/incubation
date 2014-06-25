/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.common.AsyncRequest;
import com.rameses.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class XAsyncRemoteConnection extends XConnection implements XAsyncConnection  
{
    private Map conf;
    private String name;
    private String host;    
    private String cluster;
    private String context;
    private String connection;
    private boolean debug;
    
    public XAsyncRemoteConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
        
        if (conf != null) {
            debug = "true".equals(conf.get("debug")+"");
        } 
    } 
    
    public void start() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_start] " + name);
        } 
        
        host = (String)conf.get("host");
        if (host == null) host = (String) conf.get("app.host");
        
        cluster = (String) conf.get("cluster"); 
        if (cluster == null) cluster = (String)conf.get("app.cluster"); 
        if (cluster == null) cluster = "osiris3"; 
        
        context = (String) conf.get("context"); 
        if (context == null) context = (String)conf.get("app.context"); 
        
        connection = (String) conf.get("connection"); 
    }
    
    public void stop() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_stop] " + name);
        }        
    }
    
    public Map getConf() {
        return conf;
    }
    
    public MessageQueue getQueue(String id) throws Exception {
        return new RemoteMessageQueue(id, host, cluster, context);
    }
    
    public MessageQueue register(String id) throws Exception {
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        mq.register(); 
        return mq; 
    }
        
    public void unregister(String id) throws Exception {        
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        mq.unregister(); 
    }
    
    public Object poll(String id) throws Exception {
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        return mq.poll(); 
    }
    
    public void push(String id, Object data) throws Exception {
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        mq.push(data); 
    }
    
    public void submitAsync(AsyncRequest ar) {
    }     
    
    
    // <editor-fold defaultstate="collapsed" desc=" RemoteMessageQueue ">
    
    public class RemoteMessageQueue implements MessageQueue
    {
        XAsyncRemoteConnection root = XAsyncRemoteConnection.this;
        
        private HttpClient client;
        private String cluster;
        private String context;
        private String id;
        
        public RemoteMessageQueue(String id, String host, String cluster, String context) {
            this.id = id;             
            this.cluster = cluster;
            this.context = context;
            this.client = new HttpClient(host, true);
        }
        
        public void register() throws Exception {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_register] " + id);
            } 
            
            String path = cluster + "/async/register";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            client.post(path, new Object[]{ params }); 
        } 
        
        public void unregister() throws Exception {
            if (debug) { 
                System.out.println("[" + getClass().getSimpleName() + "_unregister] " + id);
            } 
            
            String path = cluster + "/async/unregister";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            client.post(path, new Object[]{ params });
        }        
        
        public void push(Object obj) throws Exception {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_push] id="+ id +", obj"+ obj);
            }
            String path = cluster + "/async/push";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            params.put("data", obj);
            client.post(path, new Object[]{ params });
        }

        public Object poll() throws Exception {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_poll] id="+ id);
            }
            String path = cluster + "/async/poll";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            return client.post(path, new Object[]{ params });            
        } 
    } 
    
    // </editor-fold>
}
