/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public final class FileConf { 
    
    private final static Config config = new Config(); 
    
    public static synchronized FileConf add( String name, boolean defaulted ) {
        FileConf conf = config.confs.get( name ); 
        if ( conf == null ) {
            conf = new FileConf( name ); 
            config.confs.put( name, conf ); 
        } 
        if ( defaulted ) { 
            config.defaultConfName = conf.getName(); 
        } 
        return conf; 
    }
    
    public static synchronized FileConf add( String name, String type, String readPath, String writePath, String user, String pwd, boolean defaulted ) {
        FileConf conf = config.confs.get( name ); 
        if ( conf == null ) {
            conf = new FileConf( name ); 
            config.confs.put( name, conf ); 
        } 
        conf.setType( type ); 
        conf.setReadPath(readPath);
        conf.setWritePath(writePath);
        conf.setUser( user ); 
        conf.setPassword( pwd ); 
        if ( defaulted ) { 
            config.defaultConfName = conf.getName(); 
        } 
        return conf; 
    } 
    public static synchronized FileConf getDefault() {
        FileConf conf = config.confs.get( config.defaultConfName );
        if ( conf == null && !config.confs.isEmpty()) {
            Iterator keys = config.confs.keySet().iterator(); 
            return config.confs.get( keys.next()); 
        } 
        return conf; 
    } 
    public static synchronized FileConf get( String name ) {
        return config.confs.get( name ); 
    } 
    public static synchronized void remove( String name ) {
        config.confs.remove( name); 
    }
    public static synchronized void clear() {
        config.confs.clear(); 
    }
    
    
    
    private String name; 
    private String type;
    private String readPath;
    private String writePath;
    private String username;
    private String userpwd;
    
    public FileConf( String name ) {
        this.name = name; 
    }

    public String getName() { return name; } 

    public String getType() { return type; } 
    public void setType( String type ) {
        this.type = type; 
    }
    
    public String getReadPath() { return readPath; } 
    public void setReadPath( String readPath ) {
        this.readPath = readPath; 
    }
    
    public String getWritePath() { return writePath; } 
    public void setWritePath( String writePath ) {
        this.writePath = writePath; 
    }    
    public String getUser() { return username; }
    public void setUser( String username ) {
        this.username = username; 
    }
    
    public String getPassword() { return userpwd; } 
    public void setPassword( String userpwd ) {
        this.userpwd = userpwd; 
    }
        
    private static class Config {
        Map <String, FileConf> confs = new HashMap(); 
        String defaultConfName; 
    }    
    
}
