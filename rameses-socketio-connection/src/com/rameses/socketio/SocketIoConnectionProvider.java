package com.rameses.socketio;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;


public class SocketIoConnectionProvider extends XConnectionProvider {

    private final static String PROVIDER_NAME = "socketio";
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME; 
    }

    @Override
    public XConnection createConnection(String name, Map conf) { 
        return new SocketIoConnectionPool(conf, context, name);
    }

}
