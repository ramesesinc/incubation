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
import com.rameses.beaninfo.editor.ScrollbarHPolicyPropertyEditor;
import com.rameses.beaninfo.editor.ScrollbarVPolicyPropertyEditor;
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
            
            new PropertyDescriptor("cellBorder", getBeanClass()),             
            new PropertyDescriptor("cellSpacing", getBeanClass()), 
            new PropertyDescriptor("cellSize", getBeanClass()), 
            new PropertyDescriptor("cols", getBeanClass()), 
            new PropertyDescriptor("depends", getBeanClass()), 
            new PropertyDescriptor("enabledWhen", getBeanClass()),  
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("handler", getBeanClass()), 
            new PropertyDescriptor("selectionBorderColor", getBeanClass()),             
            new PropertyDescriptor("visibleWhen", getBeanClass()), 
            
            installEditor(new PropertyDescriptor("scrollbarHPolicy", getBeanClass()), ScrollbarHPolicyPropertyEditor.class), 
            installEditor(new PropertyDescriptor("scrollbarVPolicy", getBeanClass()), ScrollbarVPolicyPropertyEditor.class) 
        }; 
    }
}
