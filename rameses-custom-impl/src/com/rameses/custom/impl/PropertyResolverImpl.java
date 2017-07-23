package com.rameses.custom.impl;
import com.rameses.common.PropertyResolver;


import com.rameses.util.ValueUtil;
import groovy.lang.MissingPropertyException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;


/**
 *
 * @author jaycverg
 */
public class PropertyResolverImpl extends PropertyResolver  {
    
    /**
     * we need to correct list type declarations because it is not handled
     * directly by apache bean utils. For example: data.infos[0].value. 
     * If this were a map, it would not work.The corrected way is
     * as follows:  data.(infos)[0].value. 
     */
    private String fixPropertyName(Object bean, String propertyName) {
        if(propertyName.indexOf("[")<0) return propertyName;
        String[] arr = propertyName.split("\\.");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<arr.length;i++) {
            if(i>0)sb.append(".");
            String x = arr[i];
            //do not process it already contains '('
            if( i>0 && x.indexOf("[")>0 && x.indexOf("(")<0) {
                sb.append("("+x.replace("[",")[") );
            }
            else {
                sb.append(x);
            }
        }
        return sb.toString();
    }
    
    public boolean setProperty(Object bean, String propertyName, Object value) 
    {
        boolean debug = "true".equals(System.getProperty("PropertyResolver.debug", "false"));        
        try {
            PropertyUtils.setProperty(bean, fixPropertyName(bean, propertyName), value);
            return true;
        } catch (Exception ex) {
            if (debug) ex.printStackTrace();
            
            return false;
        }
    }
    
    public Class getPropertyType(Object bean, String propertyName) {
        try {
            return PropertyUtils.getPropertyType(bean, fixPropertyName(bean,propertyName));
        } catch (Exception ex) {
            //System.out.println("error getProperty " + propertyName + "->" + ex.getMessage() );
            return null;
        }
    }
    
    public Object getProperty(Object bean, String propertyName) 
    {
        if ( bean == null ) return null; 
        if ( ValueUtil.isEmpty(propertyName) ) return null;

        boolean debug = "true".equals(System.getProperty("PropertyResolver.debug", "false"));
        String name = fixPropertyName(bean, propertyName); 
        
        try {
            return PropertyUtils.getProperty(bean, name);
            
        } catch (InvocationTargetException ite) { 
            if ( ite.getTargetException() != null ) { 
                System.out.println("[PropertyResolverImpl] "+ ite.getTargetException().getMessage() ); 
            }
            if (debug) { ite.printStackTrace(); } 
            
            return null; 
            
        } catch (Exception e) {
            if (debug) e.printStackTrace();
            if (e instanceof MissingPropertyException) return null; 
            
            return null;
        }
    }
    
}
