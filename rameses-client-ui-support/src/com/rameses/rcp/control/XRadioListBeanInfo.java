/*
 * XRadioListBeanInfo.java
 *
 * Created on May 4, 2013, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import com.rameses.beaninfo.editor.SelectionModePropertyEditor;
import com.rameses.beaninfo.editor.SwingConstantsOrientation;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XRadioListBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XRadioList.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("fontStyle", getBeanClass()),
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"),
                        
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),            
            new PropertyDescriptor("itemExpression", getBeanClass()),
            new PropertyDescriptor("itemKey", getBeanClass()),
            new PropertyDescriptor("itemGap", getBeanClass()),
            new PropertyDescriptor("itemCount", getBeanClass()),
            
            installEditor(new PropertyDescriptor("orientation", getBeanClass()), SwingConstantsOrientation.class), 
            installEditor(new PropertyDescriptor("selectionMode", getBeanClass()), SelectionModePropertyEditor.class), 
            
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("padding", getBeanClass()),
            new PropertyDescriptor("varName", getBeanClass()),  
            new PropertyDescriptor("visibleWhen", getBeanClass()) 
        }; 
    }
    
}
