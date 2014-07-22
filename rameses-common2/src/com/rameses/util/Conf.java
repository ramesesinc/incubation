/*
 * Conf.java
 *
 * Created on July 18, 2014, 11:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author wflores
 */
public class Conf 
{
    public final static ConfResolver Resolver = new ConfResolver(); 
    public final static ConfLoader Loader = new ConfLoader(); 
    
    private DataProvider provider;
    
    public Conf() { 
    }
    
    public Conf(DataProvider provider) { 
        this.provider = provider; 
    }    
    
    public void setDataProvider(DataProvider provider) {
        this.provider = provider; 
    }

    public Map load(InputStream inp) {
        return new ConfReader().read(inp); 
    }
    
    public static interface DataProvider { 
        Object getValue(String name); 
    } 
    
    public static class ConfLoader {
        public Map load(InputStream inp) {
            return new Conf().load(inp); 
        }
    }

    public static class ConfResolver {
        
        private DataProvider source;
        
        public ConfResolver() {
        }
        
        public ConfResolver(Map source) {
            this.source = new MapDataProvider(source); 
        }
        
        public Object parse(Object value) {
            return parse(value, source); 
        }
        
        public Object parse(Object value, Map source) {
            return parse(value, new MapDataProvider(source)); 
        }
        
        public Object parse(Object value, DataProvider source) {
            if (value == null) return value;

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

                Object objval = (source==null? null: source.getValue(skey));
                if (objval == null) objval = System.getProperty(skey);             

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
    }
    
    public static class MapDataProvider implements DataProvider {
        
        private Map data;
        
        public MapDataProvider(Map data) {
            this.data = data; 
        }
        
        public Object getValue(String name) {
            return (data == null? null: data.get(name)); 
        } 
    } 
        
    
    private static class ConfReader extends Properties 
    {
        private Map conf;
        private DataProvider provider;
        
        ConfReader() {
            conf = new LinkedHashMap();
            provider = new MapDataProvider(conf); 
        }
        
        public Map read(InputStream inp) {
            try { 
                conf.clear(); 
                super.load(inp);
                return conf; 
            } catch(RuntimeException re) { 
                throw re;
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            }
        }
        
        public Object put(Object key, Object value) {
            Object newvalue = new ConfResolver().parse(value, provider);
            return conf.put(key, newvalue);             
        } 
    }  
        
}
