/*
 * FunctionProvider.java
 *
 * Created on June 18, 2013, 1:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.custom.impl;

import com.rameses.common.FunctionResolver;
import com.rameses.io.StreamUtil;
import com.rameses.util.URLDirectory;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class BasicFunctionResolver extends FunctionResolver {
    
    private Map<String,Map> functions;
    
    private class CustomFilter implements URLDirectory.URLFilter {
        private Map map = new LinkedHashMap();
        public boolean accept(URL u, String filter) {
            try {
                String name = filter.substring(filter.lastIndexOf("/")+1);
                String s = StreamUtil.toString(u.openStream());
                map.put(  name, JsonUtil.toMap(s));
            } catch(Throwable e) {
                e.printStackTrace();
            }
            return false;
        }
        
        public Map getMap() {
            return map;
        }
    }
    
    private void buildMap() {
        if( functions == null ) {
            try {
                CustomFilter cf = new CustomFilter();
                ClassLoader loader = FunctionResolver.class.getClassLoader();
                Enumeration<URL> e = loader.getResources( "META-INF/functions" );
                while(e.hasMoreElements()) {
                    URL u = e.nextElement();
                    URLDirectory ud = new URLDirectory(u);
                    ud.list( cf, loader );
                }
                functions = cf.getMap();
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
    
    public String findStringFunction(String key) {
        buildMap();
        Map func = functions.get(key);
        String f = (String) func.get("code");
        if(f==null)
            throw new RuntimeException("Function " + key + " not found");
        return f;
    }

    public Map getFunctionInfo(String key) {
        buildMap();
        Map func = functions.get(key);
        if(func==null)
            throw new RuntimeException("Function " + key + " not found");
        return func;
    }

    public List<Map> getAllFunctions(String n) {
        return getFunctionsByGroup(null);
    }

    public List<Map> getFunctionsByGroup(String n) {
        buildMap();
        List<Map> list = new LinkedList();
        Iterator<Map> iter = functions.values().iterator();
        while(iter.hasNext()) {
            Map map =iter.next();
            if(n==null || n.equalsIgnoreCase( map.get("group")+"")) {
                list.add(map);
            }
        }
        return list;
    }
    
    
}
