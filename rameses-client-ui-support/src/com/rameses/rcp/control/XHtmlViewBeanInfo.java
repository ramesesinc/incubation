package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class XHtmlViewBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass; 
    
    public Class getBeanClass() 
    { 
        if (beanClass == null)  beanClass = XHtmlView.class; 
        
        return beanClass; 
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] 
        {
            new PropertyDescriptor("depends", getBeanClass()),            
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("name", getBeanClass())
        };        
    }    
}
