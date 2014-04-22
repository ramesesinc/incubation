/*
 * XSuggestBeanInfo.java
 *
 * Created on May 4, 2013, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import com.rameses.beaninfo.editor.SuggestTypePropertyEditor;
import com.rameses.beaninfo.editor.TextCasePropertyEditor;
import com.rameses.beaninfo.editor.TrimSpaceOptionPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XSuggestBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XSuggest.class; 
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("text", getBeanClass()),
            new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
            new PropertyDescriptor("fontStyle", getBeanClass()), 
            new PropertyDescriptor("disabledTextColor", getBeanClass()), 
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
                        
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("focusAccelerator", getBeanClass()),
            new PropertyDescriptor("focusKeyStroke", getBeanClass()),            
            new PropertyDescriptor("hint", getBeanClass()),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("handlerObject", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("itemExpression", getBeanClass()),
            new PropertyDescriptor("varName", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()),
            
            installEditor(new PropertyDescriptor("textCase", getBeanClass()), TextCasePropertyEditor.class),
            installEditor(new PropertyDescriptor("trimSpaceOption", getBeanClass()), TrimSpaceOptionPropertyEditor.class),
            installEditor(new PropertyDescriptor("type", getBeanClass()), SuggestTypePropertyEditor.class), 
            
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),  
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired")
        }; 
    }
}
