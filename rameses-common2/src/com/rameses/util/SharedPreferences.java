/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import com.rameses.io.IOStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author wflores 
 */
public final class SharedPreferences { 
    
    private final static ConfImpl defaultConf = new ConfImpl("default-conf-impl"); 
    
    public static Conf getDefault() {
        return defaultConf; 
    }
    
    
    public abstract static class Conf { 
        
        private Properties props = new Properties();
        private boolean has_loaded = false; 
        
        public abstract String getName();
        
        synchronized void load() { 
            if ( !has_loaded ) {
                props.clear();
                props = read();
            }
            has_loaded = true; 
        }
        
        File getFile() {
            String sharedkey = SharedPreferences.class.getName() +"#"+ getName(); 
            String enckey = "prefs" + Encoder.MD5.encode( sharedkey ); 
            File tmpdir = new File(System.getProperty("java.io.tmpdir")); 
            File basedir = new File( tmpdir, "rameses/preferences" ); 
            if ( !basedir.exists() ) { 
                basedir.mkdirs(); 
            }
            return new File( basedir, enckey ); 
        }
        
        Properties read() {
            Properties newprops = new Properties();
            FileInputStream fis = null; 
            try { 
                byte[] bytes = IOStream.toByteArray( getFile() ); 
                String encstr = new String( bytes, "UTF-8" );
                Object obj = new Base64Cipher().decode( encstr ); 
                if ( obj instanceof Map ) {
                    newprops.putAll((Map) obj); 
                } 
                return newprops;
            } catch(Throwable t) {
                return newprops;
            } finally {
                try { fis.close(); }catch(Throwable ign){;} 
            } 
        }
        void update( Map setprops, Map delprops ) {
            Properties oldprops = read();
            oldprops.putAll( setprops );
            Iterator itr = delprops.keySet().iterator(); 
            while ( itr.hasNext() ) {
                oldprops.remove( itr.next() ); 
            } 
            
            if ( write( oldprops ) ) {
                props.clear();
                props = oldprops; 
            } 
        } 
        boolean write( Object obj ) {
            String encstr = new Base64Cipher().encode( obj ); 
            boolean success = true;
            FileWriter writer = null; 
            try {
                writer = new FileWriter( getFile() ); 
                writer.write(encstr);
                writer.flush(); 
            } catch(Throwable t) {
                success = false; 
            } finally {
                try { writer.close(); }catch(Throwable ign){;} 
            } 
            return success; 
        }
        
        public Editor getEditor() {
            return new Editor( this ); 
        }
        public Object get( String name ) { 
            load(); 
            return props.get( name ); 
        } 
        public String getString( String name ) { 
            Object value = get( name ); 
            return ( value == null ? null: value.toString() ); 
        }
        public Number getNumber( String name ) {
            Object value = get( name ); 
            if ( value instanceof Number ) {
                return (Number)value; 
            } 
            return null; 
        }          
        public Integer getInteger( String name ) {
            Number num = getNumber( name ); 
            if ( num == null ) return null; 
            
            return new Integer( num.intValue());  
        } 
        public Boolean getBoolean( String name ) {
            Object value = get( name ); 
            if ( value instanceof Boolean ) {
                return (Boolean)value; 
            } 
            return null; 
        } 
        public synchronized void clear() {
            if ( write(new HashMap()) ) {
                props.clear(); 
            }
        }         
    }
    
    public static class Editor {
        
        private Conf conf; 
        private Map setprops;
        private Map delprops;
        
        Editor( Conf conf ) {
            this.conf = conf; 
            this.setprops = new HashMap();
            this.delprops = new HashMap();
        }
        
        public Editor put( String key, Object value ) { 
            if ( key != null ) { 
                if ( value == null ) {
                    delprops.put( key, null ); 
                } else {
                    setprops.put( key, value ); 
                }
            }
            return this; 
        }
        public Editor remove( String key ) {
            if ( key != null ) {
                delprops.put( key, null ); 
            }
            return this; 
        }        
        public void save() {
            conf.update( setprops, delprops ); 
            setprops.clear(); 
            delprops.clear(); 
        }
    }
    
    private static class ConfImpl extends Conf {
        
        private String prefname; 
        
        ConfImpl( String prefname ) {
            this.prefname = prefname; 
        }
        
        public String getName() { 
            return prefname; 
        }
    }
}
