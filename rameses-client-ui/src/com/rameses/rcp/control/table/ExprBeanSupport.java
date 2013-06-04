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
        if (key == null) return null; 
        if (key.toString().equals(itemName)) return item;
        
        String skey = key.toString(); 
        String propName = "get" + skey.substring(0,1).toUpperCase() + skey.substring(1); 
        Object[] params = new Object[]{};
        Method method = findGetterMethod(root, propName, params); 
        if (method == null) return null;
        
        try {
            return method.invoke(root, params);
        } catch (IllegalArgumentException ex) {
            //do nothing
        } catch (IllegalAccessException ex) {
            //do nothing
        } catch (InvocationTargetException ex) {
            //do nothing
        }
        return null;
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

        try {
            return method.invoke(this, args); 
        } catch (IllegalArgumentException ex) {
            //do nothing
        } catch (InvocationTargetException ex) {
            //do nothing
        } catch (IllegalAccessException ex) {
            //do nothing
        } catch (NullPointerException npe) {
            //do nothing
        }           
        return null; 
    }   
    
    private Method findGetterMethod(Object bean, String name, Object[] args) 
    {
        if (bean == null || name == null) return null;
        if (args == null) args = new Object[]{};
        
        Class beanClass = bean.getClass();
        Method[] methods = beanClass.getMethods(); 
        for (int i=0; i<methods.length; i++) 
        {
            Method m = methods[i];
            if (!m.getName().equals(name)) continue;
            
            int paramSize = (m.getParameterTypes() == null? 0: m.getParameterTypes().length); 
            int argSize = (args == null? 0: args.length); 
            if (paramSize == argSize) return m;
        }
        return null;
    }    
}
