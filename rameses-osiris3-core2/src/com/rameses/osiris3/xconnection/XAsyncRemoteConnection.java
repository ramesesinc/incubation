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
public class XAsyncRemoteConnection extends XConnection implements XAsyncConnection  {
    
    private Map conf;
    private String name;
    private HttpClient client;
    
    /** Creates a new instance of XAsyncLocalConnection */
    public XAsyncRemoteConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
    }
    
    public void start() {
        String host = (String)conf.get("host");
        client = new HttpClient(host,true);
    }
    
    public void stop() {
        client = null;
    }
    
    public Map getConf() {
        return conf;
    }
    
    public void register(String id) throws Exception {
        Map params = new HashMap();
        params.put("id", id);
        client.post( "register", new Object[]{params} );
    }
    
    public void unregister(String id) throws Exception {
        Map params = new HashMap();
        params.put("id", id);
        client.post( "unregister", new Object[]{params} );
    }
    
    public Object poll(String id) throws Exception {
        Map params = new HashMap();
        params.put("id", id);
        return client.post( "poll", new Object[]{params} );
    }
    
    public void push(String id, Object data) throws Exception {
        Map params = new HashMap();
        params.put("id", id);
        params.put("data", data);
        client.post( "push", new Object[]{params} );
    }
    
    public void submitAsync(AsyncRequest ar) {
    }     
}
