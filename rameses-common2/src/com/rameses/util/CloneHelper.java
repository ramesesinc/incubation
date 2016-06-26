/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public final class CloneHelper {
    
    public Map clone( Map source, String fieldExpr ) {
        if ( fieldExpr == null || fieldExpr.trim().length()==0 ) {
            fieldExpr = ".*"; 
        } 

        List<KeyVal> keyvals = new ArrayList();
        extract( keyvals, null, source );

        Map target = new HashMap();
        String[] fields = fieldExpr.split(","); 
        for ( String text : fields ) { 
            String str = text.trim(); 
            if ( str.length()==0 ) continue; 
            for ( KeyVal kv : keyvals ) {
                if ( kv.accept( str )) {
                    kv.copyTo( target );
                }
            }
        } 
        return target; 
    } 

    private void extract( List<KeyVal> keyvals, String name, Map source ) { 
        Iterator keys = source.keySet().iterator(); 
        while (keys.hasNext()) {
            Object key = keys.next(); 
            Object val = source.get( key ); 
            if ( val instanceof Map ) { 
                Map map = (Map)val; 
                if ( name == null ) { 
                    keyvals.add( new KeyVal( key+"", key, map)); 
                    extract( keyvals, key+"", map ); 
                } else {
                    keyvals.add( new KeyVal( name +"."+ key, key, map)); 
                    extract( keyvals, name+"."+key, map ); 
                }
            } else {
                if ( name == null ) {
                    keyvals.add( new KeyVal( key+"", key, val)); 
                } else {
                    keyvals.add( new KeyVal( name +"."+ key, key, val)); 
                }
            }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" KeyVal "> 
    
    private class KeyVal {
        
        String path;
        Object key; 
        Object value;
        
        KeyVal( String path, Object key, Object value ) {
            this.path = path; 
            this.key = key; 
            this.value = value; 
        } 
        
        boolean accept( String expr ) { 
            if ( expr == null || expr.trim().length()==0 ) {
                return false; 
            } else { 
                return this.path.matches( expr ); 
            }
        }
        
        void copyTo( Map target ) {
            if ( target == null ) return; 
            
            Map current = target; 
            String[] names = path.split("\\."); 
            for ( int i=0; i<names.length-1; i++) {
                Object o = current.get( names[i]); 
                if ( o instanceof Map ) {
                    current = (Map)o; 
                } else {
                    Map map = new HashMap();
                    current.put(names[i], map); 
                    current = map; 
                }
            }
            
            current.put( names[names.length-1], this.value ); 
        } 
        
        public String toString() {
            StringBuilder sb = new StringBuilder(); 
            sb.append("["); 
            sb.append( path==null? "null": path ).append("=");
            sb.append( value==null? "null": value.toString() );
            sb.append("]");
            return sb.toString(); 
        }
    } 
    
    // </editor-fold> 
}
