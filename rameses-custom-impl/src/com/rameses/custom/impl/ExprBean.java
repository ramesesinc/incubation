/*
 * BeanWrapper.java
 *
 * Created on February 18, 2011, 1:59 PM
 * @author jaycverg
 */

package com.rameses.custom.impl;

import com.rameses.common.PropertyResolver;

import java.util.HashMap;
import java.util.Map;


public class ExprBean extends HashMap {
    
    private Object bean;
    private Map map;

    //for missing properties, return the following object as return value
    private Object emptyReturnValue;
    
    public ExprBean() {
    }
    
    public ExprBean(Object bean) {
        this(bean, null);
    }
    
    public ExprBean(Object bean, Map extended) {
        if(bean instanceof Map) {
            this.map = (Map)bean;
        } else {
            this.bean = bean;
        }
        if( extended != null ) super.putAll(extended);
    }
    
    public Object put(Object key, Object value) {
        //Groovy script template feeds the 'out' property
        //do not feed this one to the actual bean
        if( "out".equals(key)) {
            return super.put(key, value);
        }
        
        if(bean!=null) {
            try {
                PropertyResolver res = PropertyResolver.getInstance();
                boolean success = res.setProperty(bean, key.toString(), value);
                if( !success )
                    throw new Exception("error set field ->" + key );
                return value;
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        else {
            return map.put( key, value );    
        }
    }
    
    public Object get(Object key) {
        if( "out".equals(key))
            return super.get("out");
        
        Object value = null;    
        if(bean!=null) {
            try {
                PropertyResolver res = PropertyResolver.getInstance();
                value = res.getProperty(bean, key.toString());
            } catch(Exception e) {
                //System.out.println("field " + key + " does not exist");
            }
        }
        else {
            value = map.get(key);    
        }
        if( value != null ) return value;
        return emptyReturnValue;
    }
    
//    public Object getEmptyReturnValue() {
//        return emptyReturnValue;
//    }
//
//    public void setEmptyReturnValue(Object emptyReturnValue) {
//        this.emptyReturnValue = emptyReturnValue;
//    }
    
}
