/*
 * XButtonBeanInfo.java
 *
 * Created on May 4, 2013, 11:00 AM
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
public class XSubFormPanelBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XSubFormPanel.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"), 
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("handlerObject", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()) 
        }; 
    }
}
