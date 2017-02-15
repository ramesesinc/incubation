/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.io;

import com.rameses.util.Service;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public final class FileLocType {
    
    private final static FileLocType instance = new FileLocType(); 
    private final static Object LOCKED = new Object(); 
    
    public static FileLocTypeProvider getProvider( String type ) { 
        synchronized (LOCKED) {
            return instance.getProviders().get( type );  
        }
    }

    public static void reload() {
        synchronized (LOCKED) {
            instance.providers = null; 
            instance.getProviders(); 
        }
    }
    
    private Map<String,FileLocTypeProvider> providers = null; 
    
    private Map<String,FileLocTypeProvider> getProviders() { 
        if ( providers == null ) { 
            Iterator itr = Service.providers( FileLocTypeProvider.class, FileLocType.class.getClassLoader() ); 
            providers = new HashMap(); 
            while (itr.hasNext()) { 
                FileLocTypeProvider o = (FileLocTypeProvider) itr.next(); 
                providers.put( o.getName(), o ); 
            } 
        } 
        return providers; 
    } 
}
