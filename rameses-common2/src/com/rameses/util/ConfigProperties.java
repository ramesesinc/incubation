/*
 * ConfigProperties.java
 *
 * Created on March 1, 2013, 2:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ConfigProperties {
    
    private Map map = new LinkedHashMap();
    //private List<String,  gMap> groups = new ArrayList();
    private Map<String,Map> groups = new LinkedHashMap();
    private File file;
    private boolean updatable = true;
    
    private ConfigProperties() {}
    
    public ConfigProperties(String filename) {
        this( new File(filename));
    }
    
    public ConfigProperties(File sfile ) {
        try {
            this.file = sfile;
            if( file.getParentFile()!=null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if(!file.exists()) { 
                file.createNewFile();
            }
            parse(new FileInputStream(file), null); 
            
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } 
    }
    
    public ConfigProperties(InputStream inp) {
        try {
            updatable = false;
            parse(inp, null); 
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }
    
    public ConfigProperties(URL u) {
        try {
            updatable = false;
            parse(u.openStream(), null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void clear() {
        for (Map m: groups.values()) {
            m.clear();
        }
        groups.clear(); 
        map.clear();        
    }
    
    private void parse(InputStream inp, Map ref) {
        try {
            updatable = false;
            load( new BufferedReader(new InputStreamReader(inp)), ref );
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {inp.close();} catch(Exception ign){;}
        }        
    }
    
    private void load( BufferedReader reader, Map ref) throws Exception {
        map = new LinkedHashMap();
        Map[] refs = {map, ref};
        String s = null;
        Map groupMap = null;
        while( (s=reader.readLine())!=null ) {
            s = s.trim();
            if(s.length()==0) continue;
            if( s.startsWith("#")) continue;
            if( s.startsWith("[") ) {
                String groupName = s.substring(1, s.lastIndexOf("]"));
                groupMap = new Group(refs);
                groups.put( groupName.trim(), groupMap );
                continue;
            }
            String name = s.substring(0, s.indexOf("=")).trim();
            String value = s.substring( s.indexOf("=")+1 ).trim();
            if( groupMap!=null ) {
                groupMap.put(name, value );
            } else {
                map.put(name, resolveValue(value, refs));
            }
        }
    }
    
    public Object getProperty(String name) {
        if( name.indexOf(":")>0) {
            String groupName = name.substring( 0, name.indexOf(":") );
            String propName = name.substring( name.indexOf(":")+1 );
            return groups.get( groupName ).get(propName);
        } else {
            return map.get(name);
        }
    }
    
    /**
     *specify a group name and return properties for that group
     */
    public Map getProperties(String groupName) {
        return groups.get(groupName);
    }
    
    public Map getProperties()  {
        return map;
    }
    
    public void put(String name, String data) {
        setProperty(name.trim(), data.trim());
    }
    
    public String get(String name) {
        return (String) getProperty(name);
    }
    
    public void setProperty(String name, String data) {
        if( name.indexOf(":")>0) {
            String groupName = name.substring( 0, name.indexOf(":") );
            String propName = name.substring( name.indexOf(":")+1 );
            groups.get( groupName ).put(propName, data);
        } else {
            map.put(name.trim(), data.trim());
        }
    }
    
    public void update(){
        StringBuilder sb = new StringBuilder();
        for(Object o: map.keySet() ) {
            String skey = (String)o;
            sb.append( skey +"="+map.get(skey) +"\n" );
        }
        sb.append("\n\n");
        for( Object g: groups.keySet() ) {
            sb.append( "[" + g + "]\n");
            Map gm = groups.get(g);
            for( Object k: gm.keySet()) {
                String gkey = (String)k;
                sb.append( gkey + "=" + gm.get(gkey) + "\n" );
            }
            sb.append("\n\n");
        }
        
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream( file );
            fos.write( sb.toString().getBytes() );
        } 
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } 
        finally {
            try { fos.close(); } catch(Exception e) {;}
        }
    }
    
    public Map<String, Map> getGroups() {
        return groups;
    }
    
    public void putAll( Map map ) {
        for(Object o: map.entrySet()) {
            Map.Entry me = (Map.Entry)o;
            put( me.getKey()+"", me.getValue()+"");
        }
    }
    
    public boolean isUpdatable() {
        return updatable;
    }
    
    class Group extends LinkedHashMap {
        private Map[] refs;
        
        Group(Map[] deps) {
            if (deps == null) {
                deps = new Map[0]; 
            }
            
            refs = new Map[deps.length+1];
            refs[0] = this; 
            for (int i=0; i<deps.length; i++) {
                refs[i+1] = deps[i]; 
            } 
        }

        @Override
        public Object put(Object key, Object value) {
            value = resolveValue(value, refs); 
            return super.put(key, value);
        }
    }
    
    private Object resolveValue(Object value, Map[] refs) { 
        if (value == null) { 
            return null; 
        } else if (!(value instanceof String)) {
            return value; 
        }
        
        int startidx = 0; 
        boolean has_expression = false; 
        String str = value.toString();         
        StringBuilder builder = new StringBuilder(); 
        while (true) {
            int idx0 = str.indexOf("${", startidx);
            if (idx0 < 0) break;
            
            int idx1 = str.indexOf("}", idx0); 
            if (idx1 < 0) break;
            
            has_expression = true; 
            String skey = str.substring(idx0+2, idx1); 
            builder.append(str.substring(startidx, idx0)); 
            
            Object objval = null; 
            for (Map mref : refs) {
                if (mref == null) { continue; }
                
                objval = mref.get(skey); 
                if (objval != null) { break; }
            }
            
            if (objval == null) {
                objval = System.getProperty(skey);
            } 
            if (objval == null) {
                objval = System.getenv(skey); 
            }
            
            if (objval == null) { 
                builder.append(str.substring(idx0, idx1+1)); 
            } else { 
                builder.append(objval); 
            } 
            startidx = idx1+1; 
        } 
        
        if (has_expression) {
            builder.append(str.substring(startidx));  
            return builder.toString(); 
        } else {
            return value; 
        }
    }     
    
    
    public static synchronized Parser newParser() {
        return new Parser();
    }
    
    public static class Parser {
        
        public Map parse(File file, Map ref) {
            try { 
                return parse(new FileInputStream(file), ref);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e); 
            }
        }
        
        public Map parse(URL url, Map ref) {
            try {
                InputStream inp = url.openStream();
                return parse(inp, ref); 
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e); 
            } 
        }        
        
        public Map parse(InputStream inp, Map ref) {
            ConfigProperties conf = new ConfigProperties();
            conf.parse(inp, ref); 

            Map result = new LinkedHashMap(); 
            result.putAll(conf.map); 
            for (Map.Entry<String,Map> me : conf.groups.entrySet()) {
                Map grp = new LinkedHashMap(); 
                grp.putAll(me.getValue()); 
                result.put(me.getKey(), grp); 
            } 
            return result; 
        }
    }
}
