/*
 * ExprBeanSupport.java
 *
 * Created on June 1, 2013, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ExprBeanSupport extends HashMap implements InvocationHandler 
{
    private Object root;
    private Object item;
    private String itemName;
    private String _toString;
    
    public ExprBeanSupport(Object root) 
    {
        super();
        this.root = root; 
        this._toString = "$ExprBeanSupport@"+new UID(); 
    }
    
    public void setItem(String name, Object item) 
    {
        if (name == null) name = "item";
        
        this.itemName = name;
        this.item = item;
    }

    public Object get(Object key) 
    {
        if (itemName != null && itemName.equals(key+"")) 
            return item;
        else
            return super.get(key); 
    }
    
    public Object put(Object key, Object value) 
    {
        //restrict from adding key values to this bean 
        return null; 
    }     
    
    public Object createProxy() 
    {
        ClassLoader classLoader = root.getClass().getClassLoader();
        Class[] interfaces = root.getClass().getInterfaces(); 
        List<Class> classes = new ArrayList<Class>();
        for (int i=0; i<interfaces.length; i++) 
            classes.add(interfaces[i]); 
        
        if (!(root instanceof Map)) classes.add(Map.class); 
                
        return Proxy.newProxyInstance(classLoader, classes.toArray(new Class[]{}), this); 
    } 

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        if ("toString".equals(method.getName()))  return _toString;
        else if ("hashCode".equals(method.getName())) return proxy.hashCode();

        Object[] beans = new Object[]{root, this};
        for (int i=0; i<beans.length; i++) 
        {
            Object bean = beans[i];
            try {
                return method.invoke(bean, args); 
            } catch (IllegalArgumentException ex) {
                //do nothing 
            } catch (InvocationTargetException ex) {
                //do nothing`
            } catch (IllegalAccessException ex) {
                //do nothing
            } catch (NullPointerException npe) {
                //do nothing
            }           
        }
        return null; 
    }      
}
