/*
 * XTextFieldBeanInfo.java
 *
 * Created on May 4, 2013, 9:34 AM
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
public class XFormulaEditorBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XFormulaEditor.class; 
        
        return beanClass;
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {   
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("cellPadding", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("handlerObject", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("keywordItems", getBeanClass()),
            
            new PropertyDescriptor("nullWhenEmpty", getBeanClass(), "isNullWhenEmpty", "setNullWhenEmpty"),
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),
            new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
            
            new PropertyDescriptor("disableWhen", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()),             
            
            installEditor(new PropertyDescriptor("textCase", getBeanClass()), TextCasePropertyEditor.class), 
            installEditor(new PropertyDescriptor("trimSpaceOption", getBeanClass()), TrimSpaceOptionPropertyEditor.class) 
        }; 
    }
}
