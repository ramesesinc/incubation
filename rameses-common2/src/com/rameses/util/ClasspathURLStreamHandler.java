/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.net.URL;

/**
 *
 * @author wflores
 */
public class ClasspathURLStreamHandler extends URLStreamHandler {

    private final static String KEY_NAME = "classpath"; 
    
    public String getProtocol() {
        return KEY_NAME; 
    }

    public URL getResource(String spath) {
        ClassLoader[] loaders = new ClassLoader[]{ getClass().getClassLoader(), getClassLoader() }; 
        for ( ClassLoader loader : loaders ) {
            URL result = (loader == null ? null : loader.getResource(spath)); 
            if ( result != null ) return result; 
        }
        return null; 
    } 
}
