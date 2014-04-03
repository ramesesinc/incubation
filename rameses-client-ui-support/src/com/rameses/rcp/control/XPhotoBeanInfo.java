/*
 * XPhotoBeanInfo.java
 *
 * Created on December 4, 2013, 11:10 AM
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
public class XPhotoBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XPhoto.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("text", getBeanClass()), 
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("noImageIcon", getBeanClass()), 
            new PropertyDescriptor("noImageBackground", getBeanClass()), 
            new PropertyDescriptor("noImageForeground", getBeanClass()), 
            new PropertyDescriptor("showNoImageIcon", getBeanClass(), "isShowNoImageIcon", "setShowNoImageIcon"), 
            new PropertyDescriptor("showNoImageText", getBeanClass(), "isShowNoImageText", "setShowNoImageText")
        };        
    }
}
