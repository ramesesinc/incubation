package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class XHtmlEditorBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass; 
    
    public Class getBeanClass() 
    { 
        if (beanClass == null)  beanClass = XHtmlEditor.class; 
        
        return beanClass; 
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] 
        {
            new PropertyDescriptor("border", getBeanClass()), 
            new PropertyDescriptor("depends", getBeanClass()), 
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("name", getBeanClass()),
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),            
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("itemExpression", getBeanClass()),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"), 
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            new PropertyDescriptor("varName", getBeanClass()), 
            new PropertyDescriptor("visibleWhen", getBeanClass())            
        };        
    }    
}
