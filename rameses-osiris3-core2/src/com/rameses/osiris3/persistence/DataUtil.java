/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SimpleField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 * Utilities for accessing data in nested format. It also contains other related facilities
 */
public class DataUtil {
    
    public static Object getNestedValue( Map data, String name ) throws Exception {
        if( name.indexOf("_")>0) {
            //try first if there is an actual field with underscores.
            if( data.containsKey(name)) {
                return data.get(name);
            }
            Map odata = data;
            String[] arr = name.split("_");
            for(int i=0; i<(arr.length-1); i++) {
                Object z = odata.get(arr[i]);
                if( z ==null ) return null;
                if( !(z instanceof Map )) {
                    return z;
                }
                odata = (Map)z;
            }
            return odata.get(arr[arr.length-1]);
        }
        else {
            return data.get(name);
        }
    }
    
    public static Object putNestedValue( Map data, String name, Object value ) throws Exception {
        if( name.indexOf("_")>0) {
            //try first if there is an actual field with underscores.
            if( data.containsKey(name)) {
                return data.put(name, value);
            }
            Map odata = data;
            String[] arr = name.split("_");
            for(int i=0; i<(arr.length-1); i++) {
                Object z = odata.get(arr[i]);
                if( z == null ) {
                    //auto create a new entry immediately if it is nested and is not a map
                    z = new HashMap();
                    odata.put(arr[i], z);
                }
                odata = (Map)z;
            }
            return odata.put(arr[arr.length-1], value);
        }
        else {
            return data.put(name, value);
        }
    }
    
    
    public static List getDataList( Map data, String name ) throws Exception {
        Object l =  getNestedValue(data, name);
        if(l!=null && !(l instanceof List)) {
            throw new Exception("DataUtil.getDataList error. List "+ name + " does not exist");
        }
        if(l==null) l = new ArrayList();
        return (List)l;
    }
    
    
    public static String stringifyMapKeys(Map map) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for( Object s: map.keySet() ) {
            if(i++>0) sb.append("|");
            sb.append(s.toString());
        }
        return sb.toString();
    }
    
    
    public static Map buildFinderFromPrimaryKeys( SchemaElement elem, Map data ){
        if(data==null) return null;
        Map mapKey = new HashMap();
        for(SimpleField sf: elem.getPrimaryKeys()) {
            Object val = data.get(sf.getName());
            if( val!=null) {
                mapKey.put( sf.getName(), val );
            }
        }
        if( mapKey.size() == 0 )
            return null;
        return mapKey;
    }
    
    public static void printMap( Map baseData ) {
        for( Object o: baseData.entrySet()) {
            Map.Entry m= (Map.Entry)o;
            System.out.println(m.getKey()+"="+m.getValue());
        }    
    }
    
}
