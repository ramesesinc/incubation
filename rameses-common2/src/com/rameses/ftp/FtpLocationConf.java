/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.ftp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public final class FtpLocationConf { 
    
    private final static Config config = new Config(); 
    
    public static synchronized FtpLocationConf add( String name ) {
        FtpLocationConf conf = config.confs.get( name ); 
        if ( conf == null ) {
            conf = new FtpLocationConf( name ); 
            config.confs.put( name, conf ); 
        } 
        return conf; 
    }
    
    public static synchronized void add( String name, String host, String user, String pwd ) {
        FtpLocationConf conf = config.confs.get( name ); 
        if ( conf == null ) { 
            conf = new FtpLocationConf( name ); 
            config.confs.put( name, conf ); 
        } 

        conf.setHost( host ); 
        conf.setUser( user ); 
        conf.setPassword( pwd ); 
    } 

    public static synchronized FtpLocationConf get( String name ) {
        return config.confs.get( name ); 
    } 
    public static synchronized void remove( String name ) {
        config.confs.remove( name); 
    }
    public static synchronized void clear() {
        config.confs.clear(); 
    }
    
    
    
    private String name; 
    private String host; 
    private String username;
    private String userpwd;
    private String rootdir; 
    private int port; 
    
    public FtpLocationConf( String name ) {
        this.name = name; 
    }

    public String getName() { return name; } 

    public String getHost() { return host; } 
    public void setHost( String host ) {  
        String[] arr = (host==null? "" : host).split(":"); 
        this.host = arr[0]; 
        
        try {
            this.port = new Integer(arr[1]).intValue();  
        } catch(Throwable t) { 
            this.port = 21; 
        } 
    }
    
    public String getUser() { return username; }
    public void setUser( String username ) {
        this.username = username; 
    }
    
    public String getPassword() { return userpwd; } 
    public void setPassword( String userpwd ) {
        this.userpwd = userpwd; 
    }
    
    public int getPort() { return port; } 
    public void setPort( int port ) {
        this.port = port; 
    }
    
    public String getRootDir() { return rootdir; } 
    public void setRootDir( String rootdir ) {
        this.rootdir = rootdir; 
    }
    
    
    private static class Config {
        Map <String, FtpLocationConf> confs = new HashMap(); 
        String defaultConfName; 
    }
}
