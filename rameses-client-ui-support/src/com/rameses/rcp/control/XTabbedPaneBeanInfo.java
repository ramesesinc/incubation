/*
 * XTabbedPaneBeanInfo.java
 *
 * Created on September 2, 10:00 PM
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
public class XTabbedPaneBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XTabbedPane.class;
        
        return beanClass; 
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("depends", getBeanClass()), 
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("nameAutoLookupAsOpener", getBeanClass(), "isNameAutoLookupAsOpener", "setNameAutoLookupAsOpener") 
            
        }; 
    }
}