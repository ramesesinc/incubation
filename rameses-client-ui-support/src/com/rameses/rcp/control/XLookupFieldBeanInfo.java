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
import com.rameses.beaninfo.editor.TextCasePropertyEditor;
import com.rameses.beaninfo.editor.TrimSpaceOptionPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XLookupFieldBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XLookupField.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("text", getBeanClass()),
            new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
                        
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("hint", getBeanClass()),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("handlerObject", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("orientation", getBeanClass()),
            new PropertyDescriptor("returnFields", getBeanClass()),
            new PropertyDescriptor("varName", getBeanClass()),
            
            installEditor(new PropertyDescriptor("textCase", getBeanClass()), TextCasePropertyEditor.class),
            installEditor(new PropertyDescriptor("trimSpaceOption", getBeanClass()), TrimSpaceOptionPropertyEditor.class),
            
            new PropertyDescriptor("nullWhenEmpty", getBeanClass(), "isNullWhenEmpty", "setNullWhenEmpty"),  
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),  
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"), 
            new PropertyDescriptor("transferFocusOnSelect", getBeanClass(), "isTransferFocusOnSelect", "setTransferFocusOnSelect") 
        }; 
    }
}
