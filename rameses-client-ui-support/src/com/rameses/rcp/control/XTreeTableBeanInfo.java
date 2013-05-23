/*
 * XTreeBeanInfo.java
 *
 * Created on May 4, 2013, 9:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XTreeTableBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XTreeTable.class; 
        
        return beanClass;
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("autoResize", getBeanClass(), "isAutoResize", "setAutoResize"),            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("errorBackground", getBeanClass()),
            new PropertyDescriptor("errorForeground", getBeanClass()),
            new PropertyDescriptor("evenBackground", getBeanClass()),
            new PropertyDescriptor("evenForeground", getBeanClass()),            
            new PropertyDescriptor("gridColor", getBeanClass()),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("multiselect", getBeanClass(), "isMultiselect", "setMultiselect"),
            new PropertyDescriptor("oddBackground", getBeanClass()),
            new PropertyDescriptor("oddForeground", getBeanClass()) 
        }; 
    }
}
