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
import com.rameses.beaninfo.editor.CaptionOrientationPropertyEditor;
import com.rameses.beaninfo.editor.OrientationPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsHAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsVAlignmentPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XFormPanelBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XFormPanel.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            
            new PropertyDescriptor("addCaptionColon", getBeanClass(), "isAddCaptionColon", "setAddCaptionColon"),
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionBorder", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionFontStyle", getBeanClass()),
            new PropertyDescriptor("captionForeground", getBeanClass()),                        
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionPadding", getBeanClass()),
            
            installEditor(new PropertyDescriptor("captionOrientation", getBeanClass()), CaptionOrientationPropertyEditor.class), 
            installEditor(new PropertyDescriptor("captionHAlignment", getBeanClass()), UIConstantsHAlignmentPropertyEditor.class),
            installEditor(new PropertyDescriptor("captionVAlignment", getBeanClass()), UIConstantsVAlignmentPropertyEditor.class),
            
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),             
            new PropertyDescriptor("cellpadding", getBeanClass()),
            new PropertyDescriptor("cellspacing", getBeanClass()),
            
            new PropertyDescriptor("stretchWidth", getBeanClass()),
            new PropertyDescriptor("stretchHeight", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("emptyText", getBeanClass(), "isEmptyText", "setEmptyText"),
            new PropertyDescriptor("emptyWhen", getBeanClass(), "isEmptyWhen", "setEmptyWhen"),
            new PropertyDescriptor("index", getBeanClass()),
            
            installEditor(new PropertyDescriptor("orientation", getBeanClass()), OrientationPropertyEditor.class),
            
            new PropertyDescriptor("padding", getBeanClass()),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"),
            new PropertyDescriptor("showCategory", getBeanClass(), "isShowCategory", "setShowCategory"),
            new PropertyDescriptor("viewType", getBeanClass()), 
            new PropertyDescriptor("visibleWhen", getBeanClass())  
        }; 
    }
}
