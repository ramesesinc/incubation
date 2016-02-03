/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public final class URLStreamHandlerFactory { 
    
    private final static Map<String,URLStreamHandlerProxy> URL_HANDLERS = new HashMap(); 
    
    static {
        Iterator itr = Service.providers(URLStreamHandler.class, URLStreamHandlerFactory.class.getClassLoader()); 
        while (itr.hasNext()) { 
            URLStreamHandler handler = (URLStreamHandler) itr.next(); 
            String protocol = handler.getProtocol(); 
            if ( protocol != null && protocol.trim().length() > 0 ) {
                String sname = protocol.toLowerCase();
                URLStreamHandlerProxy proxy = URL_HANDLERS.get( sname ); 
                if (proxy == null) {
                    proxy = new URLStreamHandlerProxy( sname );
                    URL_HANDLERS.put( sname, proxy ); 
                }
                proxy.add( handler ); 
            } 
        } 
    } 
    
    
    public static URLStreamHandlerFactory getInstance() {
        return getInstance( null ); 
    }

    public static URLStreamHandlerFactory getInstance( ClassLoader loader ) { 
        return new URLStreamHandlerFactory( loader );  
    } 
    
    
    private ClassLoader loader;
    
    private URLStreamHandlerFactory( ClassLoader loader ) {
        if ( loader == null ) { 
            loader = URLStreamHandlerFactory.class.getClassLoader(); 
        } 
        this.loader = loader;  
    }
    
    public URLStreamHandler getHandler( String protocol ) {
        if ( protocol == null || protocol.trim().length()==0 ) {
            return null; 
        } else {
            return URL_HANDLERS.get( protocol.toLowerCase() ); 
        } 
    } 
    
    public URL getResource( String spath ) { 
        if ( spath == null || spath.trim().length() == 0 ) {
            return null; 
        }
        int idx = spath.indexOf("://"); 
        if ( idx <= 0 ) {
            URL result = this.loader.getResource( spath ); 
            if ( result == null ) {
                result = this.loader.getSystemClassLoader().getResource( spath ); 
            }
            return result; 
        } else {
            String protocol = spath.toLowerCase().substring(0, idx); 
            URLStreamHandlerProxy proxy = URL_HANDLERS.get( protocol ); 
            if ( proxy != null ) { 
                proxy.setClassLoader( this.loader );
                return proxy.getResource(spath.substring(idx+3)); 
            }
        }
        return null; 
    }
 
    private static class URLStreamHandlerProxy extends URLStreamHandler {

        private String protocol; 
        private List<URLStreamHandler> handlers; 
        
        URLStreamHandlerProxy( String protocol ) {
            this.protocol = protocol; 
            this.handlers = new ArrayList(); 
        }
        
        public String getProtocol() { 
            return protocol; 
        }
        
        public void add( URLStreamHandler o ) {
            if ( o != null && !handlers.contains(o)) {
                handlers.add( o ); 
            } 
        }
        
        public void removeAll() {
            handlers.clear(); 
        }

        public URL getResource( String spath ) {
            for ( URLStreamHandler handler : handlers ) { 
                handler.setClassLoader( getClassLoader() ); 
                URL result = handler.getResource( spath ); 
                if ( result != null ) { 
                    return result; 
                } 
            }
            return null; 
        } 
    }
    
}
