/*
 * XActionFieldBeanInfo.java
 *
 * Created on December 7, 2013, 10:43 AM
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
public class XActionFieldBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XActionField.class; 
        
        return beanClass;
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()), 
            new PropertyDescriptor("fontStyle", getBeanClass()), 
            new PropertyDescriptor("handler", getBeanClass()), 
            new PropertyDescriptor("spacing", getBeanClass()), 
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"), 
            
            new PropertyDescriptor("actionFont", getBeanClass()), 
            new PropertyDescriptor("actionFontStyle", getBeanClass()), 
            new PropertyDescriptor("actionIcon", getBeanClass()), 
            new PropertyDescriptor("actionText", getBeanClass()), 
            new PropertyDescriptor("actionTextMargin", getBeanClass()), 
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("disableWhen", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass())
        }; 
    }
}
