/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public class DataUtil {
    
    public static Object getData( Map data, String name ) throws Exception {
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
    
    public static Object putData( Map data, String name, Object value ) throws Exception {
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
        Object l =  getData(data, name);
        if(l!=null && !(l instanceof List)) {
            throw new Exception("DataUtil.getDataList error. List "+ name + " does not exist");
        }
        if(l==null) l = new ArrayList();
        return (List)l;
    }
}
