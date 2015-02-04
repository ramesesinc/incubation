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
import com.rameses.beaninfo.editor.UIConstantsHAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsOrientationPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsVAlignmentPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XSeparatorBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XSeparator.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),  
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("lineColor", getBeanClass()),
            new PropertyDescriptor("lineShadow", getBeanClass()),
            
            installEditor(new PropertyDescriptor("orientation", getBeanClass()), UIConstantsOrientationPropertyEditor.class), 
            installEditor(new PropertyDescriptor("orientationHPosition", getBeanClass()), UIConstantsHAlignmentPropertyEditor.class), 
            installEditor(new PropertyDescriptor("orientationVPosition", getBeanClass()), UIConstantsVAlignmentPropertyEditor.class), 
            
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("padding", getBeanClass()) 
        }; 
    }
}
