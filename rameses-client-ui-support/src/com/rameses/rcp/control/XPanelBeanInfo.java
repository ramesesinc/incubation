/*
 * XPanelBeanInfo.java
 *
 * Created on April 21, 2014, 9:32 AM
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
public class XPanelBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XPanel.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()) 
        }; 
    }
}
