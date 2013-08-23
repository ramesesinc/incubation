/*
 * ConfigProperties.java
 *
 * Created on March 1, 2013, 2:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ConfigProperties {
    
    private Map map = new LinkedHashMap();
    //private List<String, Map> groups = new ArrayList();
    private Map<String,Map> groups = new LinkedHashMap(); 
    private File file;
    
    /** Creates a new instance of ConfigProperties */
    public ConfigProperties(String filename) {
        BufferedReader reader = null;
        try {
            file = new File(filename);
            reader = new BufferedReader(new FileReader(file));
            load( reader );
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            try { reader.close(); } catch(Exception e){;}
        }
    }
    
    private void load( BufferedReader reader ) throws Exception {
        map = new LinkedHashMap();
        String s = null;
        Map groupMap = null;
        while( (s=reader.readLine())!=null ) {
            s = s.trim();
            if(s.length()==0) continue;
            if( s.startsWith("#")) continue;
            if( s.startsWith("[") ) {
                String groupName = s.substring(1, s.lastIndexOf("]"));
                groupMap = new LinkedHashMap();
                groups.put( groupName, groupMap );
                continue;
            }
            String name = s.substring(0, s.indexOf("="));
            String value = s.substring( s.indexOf("=")+1 );
            if( groupMap!=null ) {
                groupMap.put(name, value );
            }
            else {
                map.put( name, value );
            }
        }
    }
    
    public Object getProperty(String name) {
        if( name.indexOf(":")>0) {
            String groupName = name.substring( 0, name.indexOf(":") );
            String propName = name.substring( name.indexOf(":")+1 );
            return groups.get( groupName ).get(propName);
        }
        else {
            return map.get(name);
        }
    }
    
    /**
     *specify a group name and return properties for that group
     */
    public Map getProperties(String groupName) {
        return groups.get(groupName);
    }
    
    public Map getProperties() throws Exception {
        return map;
    }
    
    public void put(String name, String data) {
        setProperty(name, data);
    }    
    
    public String get(String name) {
        return (String) getProperty(name);
    }    

    public void setProperty(String name, String data) {
        if( name.indexOf(":")>0) {
            String groupName = name.substring( 0, name.indexOf(":") );
            String propName = name.substring( name.indexOf(":")+1 );
            groups.get( groupName ).put(propName, data);
        }
        else {
            map.put(name, data);
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
        catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try { fos.close(); } catch(Exception e) {;}
        }
    }
    
    
    
    
}
