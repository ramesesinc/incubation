/*
 * XFingerPrintBeanInfo.java
 *
 * Created on December 8, 2013, 11:19 PM
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
public class XFingerPrintBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XFingerPrint.class;
        
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
            new PropertyDescriptor("fontStyle", getBeanClass()),
            new PropertyDescriptor("icon", getBeanClass()),
            new PropertyDescriptor("iconResource", getBeanClass()),
            new PropertyDescriptor("borderPainted", getBeanClass(), "isBorderPainted", "setBorderPainted"),
            new PropertyDescriptor("contentAreaFilled", getBeanClass(), "isContentAreaFilled", "setContentAreaFilled"),
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("defaultCommand", getBeanClass(), "isDefaultCommand", "setDefaultCommand"),
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("immediate", getBeanClass(), "isImmediate", "setImmediate"),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("target", getBeanClass()),  
            new PropertyDescriptor("focusable", getBeanClass(), "isFocusable", "setFocusable"), 
            
            new PropertyDescriptor("handler", getBeanClass())
        };        
    }
}
