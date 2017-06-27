/*
 * XTextFieldBeanInfo.java
 *
 * Created on May 4, 2013, 9:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import com.rameses.beaninfo.editor.SwingConstantsHAlignment;
import com.rameses.beaninfo.editor.TextCasePropertyEditor;
import com.rameses.beaninfo.editor.TrimSpaceOptionPropertyEditor;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XTextFieldBeanInfo extends ComponentBeanInfo.Support {

    private Class beanClass;

    @Override
    public Image getIcon(int iconKind) {
        return loadImage("/icons/xtextfield.png");
    }

    protected Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XTextField.class;
        }
        return beanClass;
    }

    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException {
        PropertyDescriptor cap = new PropertyDescriptor("caption", getBeanClass());
        cap.setPreferred(true);
        
        PropertyDescriptor nm = new PropertyDescriptor("name", getBeanClass());
        nm.setPreferred(true);
        return new PropertyDescriptor[]{
                    new PropertyDescriptor("text", getBeanClass()),
                    nm,
                    cap,
                    new PropertyDescriptor("border", getBeanClass()),
                    new PropertyDescriptor("margin", getBeanClass()),
                    new PropertyDescriptor("fontStyle", getBeanClass()),
                    new PropertyDescriptor("disabledTextColor", getBeanClass()),
                    new PropertyDescriptor("actionCommand", getBeanClass()),
                    installEditor(new PropertyDescriptor("horizontalAlignment", getBeanClass()), SwingConstantsHAlignment.class),
                    
                    new PropertyDescriptor("captionFont", getBeanClass()),
                    new PropertyDescriptor("captionFontStyle", getBeanClass()),
                    new PropertyDescriptor("captionMnemonic", getBeanClass()),
                    new PropertyDescriptor("captionWidth", getBeanClass()),
                    new PropertyDescriptor("showCaption", getBeanClass(), "isShowCaption", "setShowCaption"),
                    new PropertyDescriptor("cellPadding", getBeanClass()),
                    new PropertyDescriptor("depends", getBeanClass()),
                    new PropertyDescriptor("filter", getBeanClass()),
                    new PropertyDescriptor("focusAccelerator", getBeanClass()),
                    new PropertyDescriptor("focusKeyStroke", getBeanClass()),
                    new PropertyDescriptor("hint", getBeanClass()),
                    new PropertyDescriptor("index", getBeanClass()),
                    new PropertyDescriptor("inputFormat", getBeanClass()),
                    new PropertyDescriptor("inputFormatErrorMsg", getBeanClass()),
                    new PropertyDescriptor("maxLength", getBeanClass()),
                    new PropertyDescriptor("spaceChar", getBeanClass()),
                    new PropertyDescriptor("nullWhenEmpty", getBeanClass(), "isNullWhenEmpty", "setNullWhenEmpty"),
                    new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),
                    new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"),
                    new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
                    new PropertyDescriptor("stretchWidth", getBeanClass()),
                    new PropertyDescriptor("stretchHeight", getBeanClass()),
                    new PropertyDescriptor("disableWhen", getBeanClass()),
                    new PropertyDescriptor("visibleWhen", getBeanClass()),
                    installEditor(new PropertyDescriptor("textCase", getBeanClass()), TextCasePropertyEditor.class),
                    installEditor(new PropertyDescriptor("trimSpaceOption", getBeanClass()), TrimSpaceOptionPropertyEditor.class)
                };


    }
}
