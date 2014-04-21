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
public class XImageGalleryBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XImageGallery.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            
            new PropertyDescriptor("cellSpacing", getBeanClass()), 
            new PropertyDescriptor("cellSize", getBeanClass()), 
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("handler", getBeanClass()), 
            new PropertyDescriptor("visibleWhen", getBeanClass()) 
        };        
    }
}
