/*
 * ContextProvider.java
 *
 * Created on January 28, 2013, 7:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.util.ConfigProperties;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import groovy.lang.GroovyClassLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Map conf = getConf(name);
            if ("false".equals( conf.get("enabled") )) {
                //this app has been disabled, exit right away 
                System.out.println("apps: "+ name +" has been disabled. please check the conf setting.");
                return null; 
            } 
            
            AbstractContext ac = findContext(name);
            ac.setName( name );
            ac.setConf( conf );
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
        try {
            String path = getConfUrl(name);
            if(path == null) return new HashMap(); 
            
            URL url = new URL( path );
            return ConfigProperties.newParser().parse(url, null); 
            
        } catch(Exception e) {
            if (e instanceof FileNotFoundException) {
                throw new RuntimeException("'"+name+"' conf not found"); 
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException)e; 
            } else {
                throw new RuntimeException(e.getMessage(), e);
            }
        } 
    }
    
    //CLASSLOADER OF THE CONTEXT
    protected  ClassLoader getClassLoader(String name) {
        URLClassLoader urc = null;
        try {
            String path = getClassLoaderPath(name);
            final List<URL> urlList = new ArrayList();
            URLDirectory ud = new URLDirectory(new URL(path));
            ud.list(new URLFilter(){
                public boolean accept(URL u, String filter) {
                    if( (filter.endsWith(".jar") || filter.endsWith(".jar/"))) {
                        urlList.add(u);
                    }
                    return false;
                }
            });
            
            //we'll also add the bootstrap file.
            System.out.println(" reading bootstrap file " + getRootUrl() + "/"+name + "/bootstrap.conf");
            URL uf = new URL(getRootUrl() + "/"+name + "/bootstrap.conf");
            File f = new File(uf.getFile());
            if( f.exists() ) {
                System.out.println("exists: yes");
                InputStream is = null;
                InputStreamReader isr = null;
                BufferedReader br = null;
                try {
                    is = new FileInputStream(f);
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String s = null;
                    while( (s=br.readLine())!=null) {
                        try {
                            System.out.println("loading url->"+s);
                            URL u1 = new URL(s);
                            System.out.println(u1);
                            urlList.add(u1);
                        }
                        catch(Exception ign){
                            System.out.println("Error loading bootstrap file. " + ign.getMessage());
                        }
                    }
                }
                catch(Exception ign) {;}
                finally {
                    try {br.close();} catch(Exception ex){;}
                    try {isr.close();} catch(Exception ex){;}
                    try {is.close();} catch(Exception ex){;}
                }
            }
            
            URL[] urls = urlList.toArray(new URL[]{});
            System.out.println("************************************");
            System.out.println("loading name ->"+name);
            System.out.println("************************************");
            for(URL ul: urls) {
                System.out.println(ul.getFile());
            }
            urc = new URLClassLoader(urls);           
            GroovyClassLoader gc = new GroovyClassLoader(urc);
            //add url so it can scan classes
            for( URL u : urls) {
                gc.addURL( u );    
            }
            return gc;
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
