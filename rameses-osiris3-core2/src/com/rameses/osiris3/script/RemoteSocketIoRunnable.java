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
import com.rameses.util.ExceptionManager;
import java.util.HashMap;
import java.util.Map;


public class RemoteSocketIoRunnable extends ScriptRunnable implements MessageHandler {
    
    private String channel;
    
    public RemoteSocketIoRunnable(MainContext ctx) {
        super(ctx);
    }
    
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    @Override
    public boolean accept(Object data) {
        return true;
    }
    
    @Override
    public void run() {
        if (getChannel() == null) return;
        
        try {
            if( getMethodName().matches("stringInterface|metaInfo")) {
                CacheConnection cache = (CacheConnection)context.getResource(XConnection.class, "remote-script-cache");
                if(cache==null) {
                    throw new Exception("remote-script-cache not properly defined in connections" );
                }
                Map hostMap = (Map)cache.get("remote-script:"+channel);
                if(hostMap==null) {
                    throw new Exception("services for " + channel + " not found " );
                }
                result = hostMap.get(getServiceName());
                if( result == null ) {
                    throw new Exception("service name " + channel + ":" + getServiceName() + " not found!");
                }
                return;
            }
            else {
                String _connName = (String)super.getContext().getConf().get("gdx-socketio");
                if( _connName == null ) _connName = "gdx-socketio";
                System.out.println("Remote script connection is " + _connName );
                MessageConnection xconn = (MessageConnection)context.getResource(XConnection.class, _connName);
                if(xconn==null) {
                    throw new Exception(_connName + " not found or properly defined in connections" );
                }
                
                //attach immediate response handler
                //xconn.addResponseHandler( tokenid, this ); 
                //xconn.start();                
                
                Map param = new HashMap();
                param.put("channel", getChannel());
                param.put("name", getServiceName());
                param.put("method", getMethodName());
                param.put("args", getArgs() );
                xconn.send( param, getChannel());
            }
        }
        catch(Exception ex) {
            err = ex;
        } finally {
            listener.onClose();
        }
    }

    public void onMessage(Object data) {
        try {
            Base64Cipher encoder = new Base64Cipher();
            if (encoder.isEncoded(data.toString())){
                result = encoder.decode(data.toString());
            }
            else{
                result = data;
            }

            if( result instanceof Exception ) {
                throw ExceptionManager.getOriginal((Exception)result);
            }
        }
        catch(Exception ex) {
            err = ex;
        }
        finally {
            listener.onClose();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
    }

}
