/*
 * XConnectionContextResource.java
 *
 * Created on February 24, 2013, 7:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.ContextResource;
import com.rameses.util.Service;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Elmo
 */
public class XConnectionContextResource extends ContextResource {
    
    private Map<String, XConnectionProvider> providers = Collections.synchronizedMap( new HashMap());
    
    public void init() {
        Iterator<XConnectionProvider> iter = Service.providers( XConnectionProvider.class, getClass().getClassLoader() );
        while(iter.hasNext()) {
            XConnectionProvider xp = iter.next();
            xp.setContext( context );
            System.out.println("added provider " + xp.getProviderName());
            providers.put( xp.getProviderName(), xp );
        }
    }
    
    public Class getResourceClass() {
        return XConnection.class;
    }
    
    protected XConnection findResource(String key) {
        try {
            if(key.startsWith("default-")) {
                XConnection xc = providers.get(key).createConnection(key, null);
                if(xc==null)
                    throw new Exception("connection key "+key+ " not found!");
                return xc; 
            }
            
            URL u = new URL(context.getRootUrl() +  "/connections/" + key );
            if(u==null) {
                throw new Exception("Connection " + key + " not found");
            }
            InputStream is  = u.openStream();
            Properties props = new Properties();
            props.load(is);
            is.close();
            
            //load the connection
            String providerType = (String) props.get("provider");
            if( providerType == null || providerType.trim().length()==0)
                throw new Exception("Provider must be specified for connection " + key);
            
            XConnectionProvider cp = providers.get( providerType );
            XConnection conn = cp.createConnection(  key, props );
            conn.start();
            return conn;
            
        } catch(FileNotFoundException nfe) {
            //attempt to find the default. we do this by appending default to the key
            String newKey = "default-"+key;
            XConnection xc = providers.get(newKey).createConnection(newKey, null);
            if(xc==null)
                throw new RuntimeException("connection key "+key+ " not found!");
            return xc;
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    
    
    
    
}