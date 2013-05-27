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
            new PropertyDescriptor("captionForeground", getBeanClass()),                        
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionOrientation", getBeanClass()),
            new PropertyDescriptor("captionPadding", getBeanClass()),
            
            installEditor(new PropertyDescriptor("captionHAlignment", getBeanClass()), SwingConstantsHAlignment.class),
            installEditor(new PropertyDescriptor("captionVAlignment", getBeanClass()), SwingConstantsVAlignment.class),
            
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),             
            new PropertyDescriptor("cellpadding", getBeanClass()),
            new PropertyDescriptor("cellspacing", getBeanClass()),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("emptyText", getBeanClass(), "isEmptyText", "setEmptyText"),
            new PropertyDescriptor("emptyWhen", getBeanClass(), "isEmptyWhen", "setEmptyWhen"),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("orientation", getBeanClass()),
            new PropertyDescriptor("padding", getBeanClass()),
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"),
            new PropertyDescriptor("showCategory", getBeanClass(), "isShowCategory", "setShowCategory"),
            new PropertyDescriptor("viewType", getBeanClass())            
        }; 
    }
}
