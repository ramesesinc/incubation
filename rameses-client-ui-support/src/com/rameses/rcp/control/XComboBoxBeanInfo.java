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
public class XComboBoxBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XComboBox.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("allowNull", getBeanClass(), "isAllowNull", "setAllowNull"),
            new PropertyDescriptor("border", getBeanClass()), 
            new PropertyDescriptor("fontStyle", getBeanClass()), 
                        
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("emptyText", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("fieldType", getBeanClass()),
            new PropertyDescriptor("immediate", getBeanClass(), "isImmediate", "setImmediate"),
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("itemKey", getBeanClass()),
            new PropertyDescriptor("items", getBeanClass()),
            new PropertyDescriptor("itemsObject", getBeanClass()),
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"), 
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            new PropertyDescriptor("varName", getBeanClass()), 
            
            new PropertyDescriptor("disableWhen", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass())            
        }; 
    }
}
