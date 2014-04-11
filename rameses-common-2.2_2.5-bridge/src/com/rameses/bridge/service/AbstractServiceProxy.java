/*
 * AbstractServiceProxy.java
 * Created on April 7, 2014, 1:01 PM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.bridge.service;

import com.rameses.bridge.http.HttpClient;
import java.util.Map;

/**
 *
 * @author jzamss
 */
public abstract class AbstractServiceProxy 
{
    protected HttpClient client;
    protected String serviceName;
    protected Map conf;
    
    public AbstractServiceProxy(String serviceName, Map conf){
        String appContext = (String)conf.get("app.context");
        String host = (String)conf.get("app.host");
        if(host==null) host = "localhost:8080";
        client = new HttpClient(host, true);
        
        //the application context here would be the context path of servlet 
        this.serviceName = serviceName;
        this.conf = conf;
        //settings
        String _readTimeout = (String)conf.get("readTimeout");
        if(_readTimeout!=null) {
            int rt = 0;
            try {
                rt = Integer.parseInt(_readTimeout+"");
            }catch(Exception e){;}
            client.setReadTimeout( rt );
        }
        
        String _connectionTimeout = (String)conf.get("connectionTimeout");
        if(_connectionTimeout!=null) {
            int ct = 0;
            try {
                ct = Integer.parseInt(_connectionTimeout);
            }catch(Exception e){;}
            client.setConnectionTimeout(ct);
        }        
        
        String _protocol = (String)conf.get("protocol");
        if(_protocol != null && _protocol.length() > 0) {
            client.setProtocol(_protocol);
        } 
    }
    
    public Map getConf() {
        return conf;
    }
}
