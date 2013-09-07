/*
 * MapObject.java
 *
 * Created on September 4, 2013, 9:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class MapObject 
{
    private Map map;
    private Object anObject;
    
    public MapObject(Object anObject) {
        this.anObject = anObject;
        if (anObject instanceof Map) {
            map = (Map) anObject; 
        }
    }
    
    public Object getObject() { return anObject; } 
    
    public Object get(String key) {
        return (map == null? null: map.get(key));
    }
    
    public void put(Object key, Object value) {
        map.put(key, value); 
    }
    
    public String getString(String name) {
        Object ov = get(name); 
        return (ov == null? null: ov.toString()); 
    }
    
    public Integer getInteger(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof Integer) {
            return (Integer)ov; 
        } else { 
            return Integer.valueOf(ov.toString());
        }
    }
    
    public Double getDouble(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof Double) {
            return (Double)ov; 
        } else { 
            return Double.valueOf(ov.toString());
        }
    }    
    
    public BigDecimal getDecimal(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof BigDecimal) {
            return (BigDecimal)ov; 
        } else { 
            return new BigDecimal(ov.toString()); 
        }
    } 
    
    public Boolean getBoolean(String name) {
        Object ov = get(name); 
        if (ov == null) {
            return null; 
        } else if (ov instanceof Boolean) {
            return (Boolean)ov; 
        } else { 
            return Boolean.valueOf(ov.toString());
        }
    } 
    
}
