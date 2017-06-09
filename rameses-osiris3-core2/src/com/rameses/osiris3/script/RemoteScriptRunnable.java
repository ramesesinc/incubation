/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.script;

import com.rameses.osiris3.cache.CacheConnection;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.util.Base64Cipher;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 * This is mainly used for 
 */
public class RemoteScriptRunnable extends ScriptRunnable implements MessageHandler {
    
    private String hostName;
    
    public RemoteScriptRunnable(MainContext ctx) {
        super(ctx);
    }
    
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    @Override
    public boolean accept(Object data) {
        return true;
    }

    private String generateKey() {
        StringBuilder sb = new StringBuilder();
        sb.append( getHostName() +":" + getServiceName() +"." +getMethodName() );
        if(args!=null) {
            for(Object arg: args) {
                sb.append( arg );
            }
        }
        Base64Cipher base64 = new Base64Cipher();
        return base64.encode( sb.toString() ); 
    }
    
    public Object getCacheData( String key ) throws Exception {
        CacheConnection cache = (CacheConnection)context.getResource(XConnection.class, "remote-script-data-cache");
        if(cache==null) {
            throw new Exception("remote-script-data-cache not properly defined in connections" );
        }
        return cache.get(key);
    }
    
    //listener on close will resume the process in the web
    @Override
    public void run() {
        try {
            if( getMethodName().matches("stringInterface|metaInfo")) {
                CacheConnection cache = (CacheConnection)context.getResource(XConnection.class, "remote-script-cache");
                if(cache==null) {
                    throw new Exception("remote-script-cache not properly defined in connections" );
                }
                Map hostMap = (Map)cache.get("remote-script:"+hostName);
                if(hostMap==null) {
                    throw new Exception("services for " + hostName + " not found " );
                }
                result = hostMap.get(getServiceName());
                if( result == null ) {
                    throw new Exception("service name " + getHostName() + ":" + getServiceName() + " not found!");
                }
                listener.onClose();
                return;
            }
            else {
                String key = generateKey();
                //test first if data exists in cache
                result = getCacheData(key);
                if( result !=null ) {
                    listener.onClose();
                    return;
                }
                String tokenid = "TOKEN"+new UID();
                MessageConnection xconn = (MessageConnection)context.getResource(XConnection.class, "remote-script-mq");
                if(xconn==null) {
                    throw new Exception("remote-script-mq not properly defined in connections" );
                }
                //attach immediate response handler
                xconn.addResponseHandler( tokenid, this ); 
                //send the header to the destination
                Map map = new HashMap();
                map.put("tokenid", tokenid);
                map.put("txnid", tokenid);  //remove this after we test successfully.
                map.put("key", key );
                map.put("exchange", getHostName() );
                
                map.put("serviceName", getServiceName() );
                map.put("name", getServiceName() );
               
                map.put("methodName", getMethodName() );
                map.put("args", getArgs() );
                //send to the host listener
                xconn.send( map, getHostName() );
            }
        }
        catch(Exception ex) {
            err = ex;
            listener.onClose();
        }
    }

    public void onMessage(Object data) {
        try {
            Map map = (Map)data;
            result = getCacheData(map.get("key").toString());
            MessageConnection xconn = (MessageConnection)context.getResource(XConnection.class, "remote-script-mq");
            if(xconn==null) {
                throw new Exception("remote-script-mq not properly defined in connections" );
            } 
            xconn.removeQueue(map.get("tokenid").toString());
        }
        catch(Exception ex) {
            err = ex;
        }
        finally {
            listener.onClose();
        }
    }
    
}
