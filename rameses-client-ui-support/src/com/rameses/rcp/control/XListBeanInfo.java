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
import com.rameses.beaninfo.editor.XListLayoutOrientationEditor;
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
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("cellPadding", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),
            
            installEditor(new PropertyDescriptor("cellHorizontalAlignment", getBeanClass()), SwingConstantsHAlignment.class), 
            installEditor(new PropertyDescriptor("cellVerticalAlignment", getBeanClass()), SwingConstantsVAlignment.class),
            
            installEditor(new PropertyDescriptor("cellHorizontalTextPosition", getBeanClass()), SwingConstantsHAlignment.class), 
            installEditor(new PropertyDescriptor("cellVerticalTextPosition", getBeanClass()), SwingConstantsVAlignment.class),
            
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("fixedCellHeight", getBeanClass()),
            new PropertyDescriptor("fixedCellWidth", getBeanClass()),
            new PropertyDescriptor("fontStyle", getBeanClass()),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("items", getBeanClass()),
            new PropertyDescriptor("multiselect", getBeanClass(), "isMultiselect", "setMultiselect"),
            new PropertyDescriptor("openAction", getBeanClass()),
            new PropertyDescriptor("padding", getBeanClass()), 
            new PropertyDescriptor("varName", getBeanClass()),  
            new PropertyDescriptor("varStatus", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()),
            new PropertyDescriptor("visibleRowCount", getBeanClass()), 
            
            installEditor(new PropertyDescriptor("layoutOrientation", getBeanClass()), XListLayoutOrientationEditor.class) 
        }; 
    }
}
