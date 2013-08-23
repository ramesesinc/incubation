/*
 * LogicalFunc.java
 *
 * Created on May 21, 2013, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.functions;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public final class LogicalFunc {

    public static Object iif( boolean cond, Object o1, Object o2) {
        if(cond)
            return o1;
        else
            return o2;
    }
    
    public static Object ifEmpty( Object data, Object retval ) {
        if( !empty(data) ) 
            return data;
        else
            return retval;
    }
    
    /***
     * This evaluates arrays, list, numbers, etc.
     */
    public static boolean empty( Object data ) {
        if(data==null) return true;
        if(data.toString().trim().length() == 0 ) return true;
        if(data instanceof Collection ) {
            if(((Collection)data).size() == 0) return true;
        }
        else if( data.getClass().isArray() ) {
            if( ((Object[])data).length == 0 ) return true;
        }
        else if( data instanceof Map ) {
            if( ((Map)data).size() == 0 ) return true;
        }
        return false;
    }
    
    
    
}
