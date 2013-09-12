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
public class XLabelBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XLabel.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("text", getBeanClass()), 
            
            installEditor(new PropertyDescriptor("horizontalAlignment", getBeanClass()), SwingConstantsHAlignment.class), 
            installEditor(new PropertyDescriptor("verticalAlignment", getBeanClass()), SwingConstantsVAlignment.class), 
            
            new PropertyDescriptor("caption", getBeanClass()),
            new PropertyDescriptor("captionFont", getBeanClass()),
            new PropertyDescriptor("captionMnemonic", getBeanClass()),
            new PropertyDescriptor("captionWidth", getBeanClass()),
            new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),              
            new PropertyDescriptor("cellPadding", getBeanClass()),
            
            new PropertyDescriptor("antiAliasOn", getBeanClass(), "isAntiAliasOn", "setAntiAliasOn"),
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("expression", getBeanClass()),
            new PropertyDescriptor("fontStyle", getBeanClass()),
            new PropertyDescriptor("for", getBeanClass()),
            new PropertyDescriptor("format", getBeanClass()),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("padding", getBeanClass()), 
            new PropertyDescriptor("useHtml", getBeanClass(), "isUseHtml", "setUseHtml"),            
            new PropertyDescriptor("varName", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass(), "isVisibleWhen", "setVisibleWhen")              
        }; 
    }
    
}
