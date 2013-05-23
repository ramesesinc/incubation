/*
 * AbstractColumnHandler.java
 *
 * Created on May 21, 2013, 11:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class ColumnHandler extends HashMap
{
    public ColumnHandler() {
    }

    public final Object put(Object key, Object value) 
    {
        boolean success = setValue(key, value); 
        if (success) return value; 
        
        return super.put(key, value); 
    }

    public final Object get(Object key) 
    {
        Method m = findGetterMethod((key==null? null: key.toString()));
        if (m == null) return super.get(key); 
                        
        try {
            return m.invoke(this, new Object[]{});
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex); 
        }
    }
    
    public final Map set(String name, Object value) 
    {
        put(name, value); 
        return this; 
    }
    
    private Class getValueTypeFor(Object key) 
    {
        if (key == null || key.toString().length() == 0) return null;
        
        Method m = findGetterMethod(key.toString()); 
        return (m == null? null: m.getReturnType()); 
    }
    
    private boolean setValue(Object key, Object value) 
    {
        String name = (key == null? null: key.toString()); 
        Method getterMethod = findGetterMethod(name);
        if (getterMethod == null) return false; 
        
        Class valueType = getterMethod.getReturnType();
        if (valueType == null) valueType = Object.class;

        Method setterMethod = findSetterMethod(name, new Class[]{valueType}); 
        if (setterMethod == null) return false; 
                
        try 
        {
            setterMethod.invoke(this, new Object[]{value});
            return true; 
        } 
        catch (RuntimeException re) {
            throw re;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex); 
        }
    }
    
    private Method findGetterMethod(String name) 
    {
        if (name == null || name.length() == 0) return null; 
        
        String methodName = "get" + name.substring(0,1).toUpperCase() + name.substring(1); 
        Method[] methods = getClass().getMethods(); 
        for (Method m : methods) { 
            if (m.getName().equals(methodName)) return m;
        } 
        return null;
    }
    
    private Method findSetterMethod(String name, Class[] paramTypes) 
    {
        if (name == null || name.length() == 0) return null; 
        if (paramTypes == null) paramTypes = new Class[]{};
                
        String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1); 
        Method[] methods = getClass().getMethods(); 
        for (Method m : methods) 
        { 
            if (m.getName().equals(methodName)) 
            {
                Class[] sourceTypes = m.getParameterTypes();
                if (sourceTypes == null) sourceTypes = new Class[]{};                
                if (sourceTypes.length != paramTypes.length) continue; 
                
                for (int i=0; i<sourceTypes.length; i++) {
                    if (sourceTypes[i] != paramTypes[i]) continue; 
                }                
                return m; 
            }
        } 
        return null;
    }    
}
