/*
 * MemcacheCached.java
 *
 * Created on February 9, 2013, 10:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.memcached;

import com.rameses.osiris3.cache.BlockingCache;
import com.rameses.osiris3.cache.CacheConnection;
import com.rameses.osiris3.cache.ChannelNotFoundException;

import java.net.InetSocketAddress;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Elmo
 */
public class MemcacheCached extends BlockingCache implements CacheConnection  {
    
    private String name;
    private Map conf;
    private MemcachedClient client;
    
    /**
     * Creates a new instance of MemcacheCached
     */
    public MemcacheCached(String name, Map props) {
        this.name = name;
        this.conf = props;
    }
    
    public void start() {
        try {
            String host = (String)conf.get("memcached.host");
            String sport = (String)conf.get("memcached.port");
            int port = 0;
            if(sport!=null) {
                port = Integer.parseInt(sport);
            }
            client = new MemcachedClient(new InetSocketAddress(host, port));
            super.start();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void stop() {
        super.stop();
        client = null;
    }
    
    public Object get(String name) {
        return client.get( name );
    }
    
    public Object put(String name, Object data, int timeout) {
        return client.add( name, timeout, data);
    }
    
    public Object put(String name, Object data) {
        return client.add( name, 30, data);
    }
    
    public void remove(String name) {
        client.delete( name );
    }
    
    //first portion of the string should be the expiry date.
    public void createBulk(String id, int timeout, int options) {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.SECOND, timeout );
        long expiry = cal.getTimeInMillis();
        client.add(id, 5, expiry+"|");
    }
    
    
    public void appendToBulk(String bulkid, String newKeyId, Object data) {
        if(client.get(bulkid)==null) return;
        if(newKeyId==null) newKeyId = "SUBKEY"+new UID();
        client.append(0, bulkid, ","+newKeyId );
        client.add( newKeyId, 60, data );
    }
    
    public Map<String, Object> getBulk(String bulkid, int timeout) {
        List list = new ArrayList();
        String s = (String)client.get( bulkid );
        if(s==null)
            throw new ChannelNotFoundException("channel not found");
        
        long expiry = Long.parseLong( s.substring(0, s.indexOf("|")) );
        long now = (new Date()).getTime();
        
        if(now>expiry)
            throw new ChannelNotFoundException("channel not found");
        
        s = s.substring(s.indexOf("|")+1);
        //stop if bulkid has timed out
        
        for(String s1: s.split(",")) {
            if(s1!=null && s1.trim().length()>0) {
                list.add( s1.trim() );
            }
        }
        Map data = client.getBulk( list );
        if(data.size()==0) {
            try {Thread.sleep(timeout*1000);} catch(InterruptedException ie){;}
            data = client.getBulk( list );
        }
        
        //remove the key so it will not be fetched again
        for(Object d: data.keySet()) {
            String skey = (String)d;
            client.delete( skey );
        }
        return data;
    }

    public Map getConf() {
        return conf;
    }
    
    
}
