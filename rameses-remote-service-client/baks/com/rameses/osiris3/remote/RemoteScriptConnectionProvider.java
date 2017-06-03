/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.remote;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;



/**
 * @author elmo
 * This is the connection used for loading 
 */
public class RemoteScriptConnectionProvider extends XConnectionProvider {
    
    public String getProviderName() {
        return "remote-script";
    }

    public XConnection createConnection(String name,Map conf) {
        return new RemoteScriptConnection(name, context, conf);
    }
    
}
