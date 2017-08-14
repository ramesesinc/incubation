/*
 * MemcachedCache.java
 *
 * Created on February 9, 2013, 10:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.redis;

import com.rameses.osiris3.cache.BlockingCache;
import com.rameses.osiris3.cache.CacheConnection;
import com.rameses.util.Base64Cipher;

import java.util.Map;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author Elmo
 */
public class RedisCache extends BlockingCache implements CacheConnection  {
    
    private JedisPool pool;
    private Map conf;
    private JedisPoolConfig config;
    private String host = "localhost";
    private int port = -1;
    /**
     * Creates a new instance of MemcachedCache
     */
    public RedisCache(String name, Map props) {
        this.conf = props;
        config = new JedisPoolConfig();
        if(props.containsKey("redis.host")) {
            host = props.get("redis.host").toString();
        }
        if(props.containsKey("redis.port")) {
            try { port = Integer.parseInt(props.get("redis.port").toString() ); } catch(Exception ign){;}
        }
    }
    
    public Map getConf() {
        return conf;
    }

    public void start() {
        try {
            super.start();
            if( port <= 0 ) {
                pool = new JedisPool(config, host);              
            }
            else {
                pool = new JedisPool(config, host, port);                              
            }
        }
        catch(Exception ex) {
            System.out.println("Error loading jedis pool. " + ex.getMessage());
        }
    }
    
    public void stop() { 
        try { 
            super.stop(); 
        } catch(Throwable t){;}  
        
        try { 
            pool.destroy(); 
        } catch(Throwable t) {
            //do nothing 
        } finally {
            pool = null; 
        }
    }
    
    private Object convertToObject(String data) {
        Base64Cipher cipher = new Base64Cipher(); 
        return cipher.decode(data);
    }
    
    private String convertToString( Object data ) {
        Base64Cipher cipher = new Base64Cipher(); 
        return cipher.encode(data);
    }
    
    public Object get(String name) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return convertToObject(jedis.get(name));
        }
        catch(Exception ex) {
            ex.printStackTrace();
            try {pool.returnBrokenResource(jedis); } catch (Exception e){;}            
            throw new RuntimeException(ex);
        }
        finally {
            try {pool.returnResource(jedis); } catch (Exception e){;}
        }
    }
    
    public Object put(String name, Object data) {
        return put(name,data,-1);        
    }

    public Object put(String name, Object data, int timeout) { 
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            if(timeout>0) jedis.expire(name, timeout);
            jedis.set(name, convertToString(data));
            return data;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            try {pool.returnBrokenResource(jedis); } catch (Exception e){;}
            throw new RuntimeException(ex);
        }
        finally {
            try {pool.returnResource(jedis); } catch (Exception e){;}
        }
    }    
    
    public void remove(String name) { 
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(name);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            try {pool.returnBrokenResource(jedis); } catch (Exception e){;}
            throw new RuntimeException(ex);
        }
        finally {
            try {pool.returnResource(jedis); } catch (Exception e){;}
        }
    }
    
    public void createBulk(String id, int timeout, int options) {
    }
    public void appendToBulk(String bulkid, String newKeyId, Object data) {
    }

    public Map<String, Object> getBulk(String bulkid, int timeout) { 
        return null; 
    }

    private String getProperty( String name ) {
        Object value = (conf == null? null: conf.get(name)); 
        return (value == null ? null: value.toString()); 
    }
    private Number convertInt( String value ) {
        try {
            return new Integer( value ); 
        } catch(Throwable t) {
            return null; 
        }
    }
}
