/*
 * EntityUtil.java
 *
 * Created on May 17, 2013, 2:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 * This will transfer field names starting with :
 * separated with underscore _
 * into embedded map objects. For example:
 * :permitee_name, :permitee_address
 *
 */
public final class EntityUtil {
    
    public static Map fieldToMap( Map source  ) {
        return fieldToMap(source, false);
    }
    
    public static Map fieldToMap( Map source, boolean prefixed  ) {
        Map target = new LinkedHashMap();
        Map<String, Map> embeddedFields = new LinkedHashMap();
        for(Object o: source.entrySet() ) {
            Map.Entry me = (Map.Entry)o;
            String key = me.getKey().toString();
            boolean _process = false;
            if( prefixed && key.startsWith(":")) {
                _process = true;
            } else if( key.indexOf("_") > 0 && !key.endsWith("_") ) {
                _process = true;
            }
            if(_process) {
                if(key.startsWith(":")) key = key.substring(1);
                
                //int pos = key.indexOf("_");
                String arr[] = key.split("_");
                Map c = embeddedFields;
                for(int i=0; i<(arr.length-1);i++) {
                    String kf = arr[i];
                    Map inner = (Map)c.get(kf);
                    if(inner == null ) {
                        inner = new LinkedHashMap();
                        c.put( kf, inner );
                    }
                    c = inner;
                }
                String lastField = arr[arr.length-1];
                c.put( lastField, me.getValue() );
            } else {
                target.put( me.getKey(), me.getValue() );
            }
        }
        //apply the embedded fields
        for(Object m: embeddedFields.entrySet()) {
            Map.Entry me = (Map.Entry)m;
            target.put( me.getKey(), me.getValue() );
        }
        return target;
    }
    
    /*************************************************************************
     * UPDATE OR INSERT TO DATABASE
     *************************************************************************/
    public static Map mapToField( Map source) {
        return mapToField(source,null,false);
    }
    public static Map mapToField( Map source, boolean includePrefix) {
        return mapToField(source,null, includePrefix);
    }
     public static Map mapToField( Map source, String excludeFields) {
        return mapToField(source,excludeFields, false);
    }
    private static void collectData(String parentField, String fieldName, Object d, Map target, boolean includePrefix ) {
        if(d instanceof Map) {
            if( parentField == null )
                parentField = fieldName;
            else
                parentField = parentField +"_" + fieldName;
            for(Object o: ((Map)d).entrySet() ) {
                Map.Entry me = (Map.Entry)o;
                collectData( parentField , me.getKey().toString(), me.getValue(), target, includePrefix );
            }
        } else {
            if(parentField!=null)
                fieldName = parentField+"_"+fieldName;
            if(includePrefix)
                fieldName = ":"+fieldName;
            target.put(fieldName, d );
        }
    }
    
    public static Map mapToField( Map source, String excludeFields, boolean includePrefix ) {
        Map target = new LinkedHashMap();
        for(Object o: source.entrySet() ) {
            Map.Entry me = (Map.Entry)o;
            if( excludeFields!=null && me.getKey().toString().matches(excludeFields) ) {
                target.put(me.getKey(), me.getValue());
            } else if( me.getValue() instanceof Map) {
                collectData( null, me.getKey()+"", me.getValue(), target, includePrefix );
            } else {
                target.put(me.getKey(), me.getValue());
            }
        }
        return target;
    }
    
    public static boolean checkNestedValueExist( Map data, String name ) throws Exception { 
        if( name.indexOf("_")>0) {
            //try first if there is an actual field with underscores.
            if( data.containsKey(name)) {
                return true; 
            }
            Map odata = data;
            String[] arr = name.split("_");
            for(int i=0; i<(arr.length-1); i++) {
                Object z = odata.get(arr[i]);
                if ( z == null ) return false; 
                
                if( !(z instanceof Map )) {
                    return false;
                }
                
                odata = (Map)z;
            }
            return true; 
        } 
        else {
            return (data.containsKey(name)? true: false); 
        }
    }
    
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
                if(!(z instanceof Map)) 
                    throw new Exception("Cannot cast to map ->"+name);
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
    
    public static Map clone( Map source, String fieldExpr ) {
        return new CloneHelper().clone(source, fieldExpr);
    }
}
