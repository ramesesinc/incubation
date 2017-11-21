/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class URLStreamHandlers { 
        
    private final static URLStreamHandlers instance = new URLStreamHandlers(); 
    private final static Object LOCKED = new Object(); 
    
    public static void setClassLoader( ClassLoader loader ) {
        synchronized( LOCKED ) { 
            instance.setClassLoaderImpl( loader );
        }
    }
    
    public static void load() { 
        synchronized( LOCKED ) { 
            instance.loadImpl();
        }
    }
    
    public static java.net.URLStreamHandlerFactory getFactory() {
        synchronized( LOCKED ) { 
            return instance.getFactoryImpl(); 
        }
    }
    
    public static Object getProperty( String name ) {
        return instance.properties.get( name ); 
    }
    public static void setProperty( String name, Object value ) { 
        if ( name != null && name.trim().length() > 0 ) {
            instance.properties.put( name, value ); 
        }
    }
    
    
    private Map<String, Object> properties; 
    private Map<String, URLStreamHandlerProxy> handlers; 
    private URLStreamHandlerFactoryImpl factory;
    private ClassLoader cloader; 
    
    private URLStreamHandlers(  ) { 
        setClassLoaderImpl( null ); 
        this.handlers = new HashMap();  
        this.properties = new HashMap();
        this.factory = new URLStreamHandlerFactoryImpl();
    } 
    
    private void setClassLoaderImpl( ClassLoader cloader ) {
        this.cloader = cloader; 
    }
    
    private void loadImpl() { 
        loadImpl( getClass().getClassLoader() ); 
        loadImpl( this.cloader );         
    }
    
    private void loadImpl( ClassLoader loader ) {
        if ( loader == null ) return; 
        
        Iterator itr = Service.providers( URLStreamHandler.class, loader ); 
        while (itr.hasNext()) { 
            URLStreamHandler handler = (URLStreamHandler) itr.next(); 
            String protocol = handler.getProtocol(); 
            if ( protocol != null && protocol.trim().length() > 0 ) {
                String sname = protocol.toLowerCase();
                URLStreamHandlerProxy proxy = handlers.get( sname ); 
                if (proxy == null) {
                    proxy = new URLStreamHandlerProxy( sname );
                    proxy.setClassLoader( this.cloader ); 
                    handlers.put( sname, proxy ); 
                }
                proxy.add( handler ); 
            } 
        } 
    }
    
    private java.net.URLStreamHandlerFactory getFactoryImpl() { 
        return factory; 
    }
    
    public URLStreamHandler get( String sname ) {
        if ( sname != null ) {
            return handlers.get( sname.toLowerCase()); 
        } else {
            return null; 
        } 
    } 
 
    
    private class URLStreamHandlerProxy extends URLStreamHandler {

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
                Collections.sort( handlers, new Comparator<URLStreamHandler>() {
                    public int compare(URLStreamHandler o1, URLStreamHandler o2) {
                        if ( o1 == null || o2 == null ) return 0;                         
                        if ( o1.getIndex() > o2.getIndex() ) return 1; 
                        if ( o1.getIndex() < o2.getIndex() ) return -1; 
                        return 0; 
                    } 
                });
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
    
    
    private class URLStreamHandlerFactoryImpl implements java.net.URLStreamHandlerFactory {

        private URLStreamHandlers root = URLStreamHandlers.this; 
        
        public URLStreamHandler createURLStreamHandler(String protocol) { 
            URLStreamHandlerProxy proxy = (URLStreamHandlerProxy) root.get( protocol ); 
            if ( proxy != null ) { 
                proxy.setClassLoader( root.cloader );
            } 
            return proxy; 
        } 
    }
}
