/*
 * XTabbedPaneBeanInfo.java
 *
 * Created on September 2, 10:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import com.rameses.beaninfo.editor.TabLayoutPolicyEditor;
import com.rameses.beaninfo.editor.TabPlacementEditor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XTabbedPaneBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass;
    
    protected Class getBeanClass() 
    {
        if (beanClass == null) beanClass = XTabbedPane.class;
        
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
            new PropertyDescriptor("disableWhen", getBeanClass()), 
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("handler", getBeanClass()), 
            new PropertyDescriptor("index", getBeanClass()), 
            new PropertyDescriptor("items", getBeanClass()), 
            new PropertyDescriptor("nameAutoLookupAsOpener", getBeanClass(), "isNameAutoLookupAsOpener", "setNameAutoLookupAsOpener"),
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()), 
            new PropertyDescriptor("visibleWhen", getBeanClass()),
            
            installEditor(new PropertyDescriptor("tabLayoutPolicy", getBeanClass()), TabLayoutPolicyEditor.class),  
            installEditor(new PropertyDescriptor("tabPlacement", getBeanClass()), TabPlacementEditor.class) 
        }; 
    }
    
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[] {
            new ComponentBeanInfo() 
        }; 
    }    
    
}
