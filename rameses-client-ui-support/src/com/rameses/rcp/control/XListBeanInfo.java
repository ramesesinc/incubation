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
import com.rameses.beaninfo.editor.SwingConstantsHAlignment;
import com.rameses.beaninfo.editor.SwingConstantsVAlignment;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XListBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XList.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            installEditor(new PropertyDescriptor("cellHorizontalAlignment", getBeanClass()), SwingConstantsHAlignment.class), 
            installEditor(new PropertyDescriptor("cellVerticalAlignment", getBeanClass()), SwingConstantsVAlignment.class),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("items", getBeanClass()),
            new PropertyDescriptor("multiselect", getBeanClass(), "isMultiselect", "setMultiselect"),
            new PropertyDescriptor("openAction", getBeanClass()),
            new PropertyDescriptor("padding", getBeanClass()), 
            new PropertyDescriptor("varName", getBeanClass()) 
        }; 
    }
}
