/*
 * XMaskFieldBeanInfo.java
 *
 * Created on September 2, 2013, 1:34 PM
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
public class XMaskFieldBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XMaskField.class; 
        
        return beanClass;
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("text", getBeanClass()),
            new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
            new PropertyDescriptor("border", getBeanClass()), 
            new PropertyDescriptor("margin", getBeanClass()), 
            new PropertyDescriptor("fontStyle", getBeanClass()), 
            new PropertyDescriptor("disabledTextColor", getBeanClass()), 
            new PropertyDescriptor("actionCommand", getBeanClass()), 
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("focusAccelerator", getBeanClass()),
            new PropertyDescriptor("focusKeyStroke", getBeanClass()),            
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("includeLiteral", getBeanClass()),
            new PropertyDescriptor("mask", getBeanClass()),
            
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("disableWhen", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()),              
            
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired")
        }; 
    }
}
