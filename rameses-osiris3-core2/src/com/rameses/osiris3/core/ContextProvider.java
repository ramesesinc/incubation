/*
 * ContextProvider.java
 *
 * Created on January 28, 2013, 7:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import groovy.lang.GroovyClassLoader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Elmo
 */
public abstract class ContextProvider {
    
    protected abstract AbstractContext findContext(String name);
    protected abstract String getClassLoaderPath(String name);
    protected abstract String getConfUrl(String name);
    protected abstract void initContext(AbstractContext ac);
    public abstract String getRootUrl();
    
    protected OsirisServer server;
    
    private Map<String, AbstractContext> contexts = Collections.synchronizedMap( new HashMap() );
    
    public ContextProvider(OsirisServer server) {
        this.server = server;
    }
    
    public final AbstractContext getContext(String name) {
        if(!contexts.containsKey(name)) {
            AbstractContext ac = findContext(name);
            ac.setName( name );
            ac.setConf( getConf(name) );
            ac.setClassLoader(getClassLoader(name) );
            ac.setRootUrl(getRootUrl()+"/"+name);
            initContext(ac);
            contexts.put(name, ac);
            ac.start();
        }
        return contexts.get(name);
    }
    
    protected OsirisServer getServer() {
        return server;
    }
    
    //INFORMATION OF THE CONTEXT
    protected Map getConf(String name) {
        InputStream is = null;
        try {
            String path = getConfUrl(name);
            if(path==null) return new HashMap();
            URL u = new URL( path );
            is = u.openStream();
            Properties props = new Properties();
            if(is!=null) props.load( is );
            return props;
        } catch(FileNotFoundException fnfe) {
            throw new RuntimeException("'"+name+"' conf not found"); 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {is.close();} catch(Exception ign){;}
        }
    }
    
    //CLASSLOADER OF THE CONTEXT
    protected  ClassLoader getClassLoader(String name) {
        URLClassLoader urc = null;
        try {
            String path = getClassLoaderPath(name);
            URLDirectory ud = new URLDirectory(new URL(path));
            URL[] urls = ud.list(new URLFilter(){
                public boolean accept(URL u, String filter) {
                    return (filter.endsWith(".jar") || filter.endsWith(".jar/"));
                }
            });
            urc = new URLClassLoader(urls);
            return new GroovyClassLoader(urc);
        } catch(Exception ign){
            throw new RuntimeException("ERROR init classloader for "+name+" "+ign.getMessage());
        }
    }

    public void stop() {
        for(AbstractContext ac: this.contexts.values()) {
            try {
                System.out.println("stopping context provider " + ac.getName());
                ac.stop(); 
            } catch(Exception e) {
                System.out.println("failed to stop context provider " + ac.getName() + ", caused by " + e.getMessage());
            }
        }
    }
    
}
