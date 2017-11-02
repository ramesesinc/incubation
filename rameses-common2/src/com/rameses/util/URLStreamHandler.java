/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author wflores 
 */
public abstract class URLStreamHandler extends java.net.URLStreamHandler {

    private ClassLoader classLoader; 
    private int index;
    
    public abstract String getProtocol();
    public abstract URL getResource( String spath );
    
    public int getIndex() { return index; } 
    
    public ClassLoader getClassLoader() { return classLoader; } 
    public void setClassLoader(ClassLoader classLoader ) {
        this.classLoader = classLoader; 
    }
    
    protected URLConnection openConnection( URL url ) throws IOException {
        String[] values = new String[]{ url.getHost(), url.getPath() }; 
        String respath = join(values); 
        if (respath.startsWith("/")) { 
            respath = respath.substring(1); 
        } 
        if (respath == null || respath.trim().length() == 0) { 
            return null; 
        }      
        
        URL result = getResource( respath ); 
        if ( result == null ) { 
            return new EmptyURLConnection( url );
        } else { 
            return result.openConnection(); 
        } 
    } 
    
    private String join(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (String str : values) {
            if (str == null || str.trim().length() == 0) {
                continue;
            } 
            sb.append(str);
        }
        return sb.toString();
    }  
    
    private class EmptyURLConnection extends URLConnection {
        
        protected EmptyURLConnection( URL url ) {
            super( url ); 
        }
        
        public void connect() throws IOException { 
            connected = true; 
        } 

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream( new byte[0] );
        }
    }    
}
