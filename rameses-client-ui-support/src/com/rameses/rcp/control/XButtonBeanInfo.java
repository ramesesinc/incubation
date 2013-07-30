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
public class XButtonBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XButton.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("accelerator", getBeanClass()),            
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("margin", getBeanClass()),
            new PropertyDescriptor("mnemonic", getBeanClass()),
            new PropertyDescriptor("text", getBeanClass()),     
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("defaultCommand", getBeanClass(), "isDefaultCommand", "setDefaultCommand"),
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("disableWhen", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("immediate", getBeanClass(), "isImmediate", "setImmediate"),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("target", getBeanClass()),            
            new PropertyDescriptor("visibleWhen", getBeanClass())            
        };        
    }
}
