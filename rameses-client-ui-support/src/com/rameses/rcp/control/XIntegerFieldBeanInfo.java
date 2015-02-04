/*
 * XDecimalFieldBeanInfo.java
 *
 * Created on May 8, 2013, 9:34 AM
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
public class XIntegerFieldBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XIntegerField.class; 
        
        return beanClass;
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("text", getBeanClass()),
            new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
            new PropertyDescriptor("focusable", getBeanClass()),
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
            new PropertyDescriptor("minValue", getBeanClass()),
            new PropertyDescriptor("maxValue", getBeanClass()),
            new PropertyDescriptor("pattern", getBeanClass()),
            
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("usePrimitiveValue", getBeanClass(), "isUsePrimitiveValue", "setUsePrimitiveValue"),
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired")
        }; 
    }
}
