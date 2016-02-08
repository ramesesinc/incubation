/*
 * AbstractServiceProxy.java
 * Created on September 22, 2011, 5:39 PM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.service;

import com.rameses.http.HttpClient;
import java.util.Map;

/**
 *
 * @author jzamss
 */
public abstract class AbstractServiceProxy{
    
     
    protected HttpClient client;
    protected String serviceName;
    protected Map conf;
    
    /** Creates a new instance of AbstractServiceProxy */
    public AbstractServiceProxy(String serviceName, Map conf){
        String appContext = (String)conf.get("app.context");
        String host = (String)conf.get("app.host");
        if ( host == null ) {
            host = "localhost:8080"; 
        }
        
        client = new HttpClient(host, true);
        
        //the application context here would be the context path of servlet 
        this.serviceName = serviceName;
        this.conf = conf;
        
        //settings
        Number num = getNumber( conf, "readTimeout" ); 
        if ( num != null ) { 
            client.setReadTimeout( num.intValue() ); 
        }
        
        num = getNumber( conf, "connectionTimeout" ); 
        if ( num != null ) { 
            client.setConnectionTimeout( num.intValue() ); 
        }
        
        try {
            client.setProtocol(conf.get("protocol").toString());
        } catch(Throwable t){;} 

        try {
            client.setDebug( "true".equalsIgnoreCase(conf.get("debug").toString()) ); 
        } catch(Throwable t){;} 
        
        try {
            boolean bool = "true".equals(conf.get("encrypted").toString());  
            client.setEncrypted( bool ); 
        } catch(Throwable t){;} 
    }
    
    public Map getConf() {
        return conf;
    }
    
    private Number getNumber( Map map, Object key ) {
        Object value = (map == null? null : map.get(key)); 
        if ( value == null ) { return null; }
        
        if ( value instanceof Number ) {
            return (Number)value; 
        } else {
            try {
                return new Integer( value.toString() ); 
            } catch(Throwable t) {
                return null; 
            } 
        } 
    }
}
