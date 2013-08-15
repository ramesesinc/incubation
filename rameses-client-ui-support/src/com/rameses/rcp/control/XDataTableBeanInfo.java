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
import com.rameses.beaninfo.editor.ColumnPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author wflores
 */
public class XDataTableBeanInfo extends ComponentBeanInfo.Support
{
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = XDataTable.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("autoResize", getBeanClass(), "isAutoResize", "setAutoResize"),
            new PropertyDescriptor("showHorizontalLines", getBeanClass(), "isShowHorizontalLines", "setShowHorizontalLines"), 
            new PropertyDescriptor("showVerticalLines", getBeanClass(), "isShowVerticalLines", "setShowVerticalLines"),
                        
            installEditor(new PropertyDescriptor("columns", getBeanClass()), ColumnPropertyEditor.class), 
            
            new PropertyDescriptor("cellSpacing", getBeanClass()),
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("editable", getBeanClass(), "isEditable", "setEditable"),
            new PropertyDescriptor("errorBackground", getBeanClass()),
            new PropertyDescriptor("errorForeground", getBeanClass()),
            new PropertyDescriptor("evenBackground", getBeanClass()),
            new PropertyDescriptor("evenForeground", getBeanClass()),
            new PropertyDescriptor("gridColor", getBeanClass()),
            new PropertyDescriptor("handler", getBeanClass()),
            new PropertyDescriptor("immediate", getBeanClass(), "isImmediate", "setImmediate"),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("items", getBeanClass()),
            new PropertyDescriptor("id", getBeanClass()),
            new PropertyDescriptor("varName", getBeanClass()),
            new PropertyDescriptor("varStatus", getBeanClass()), 
            new PropertyDescriptor("multiSelectName", getBeanClass()),             
            new PropertyDescriptor("oddBackground", getBeanClass()),
            new PropertyDescriptor("oddForeground", getBeanClass()),
            new PropertyDescriptor("readonlyWhen", getBeanClass()),
            new PropertyDescriptor("readonly", getBeanClass(), "isReadonly", "setReadonly"),            
            new PropertyDescriptor("required", getBeanClass(), "isRequired", "setRequired"),
            new PropertyDescriptor("rowHeight", getBeanClass()),
            new PropertyDescriptor("rowHeaderHeight", getBeanClass()),
            new PropertyDescriptor("showRowHeader", getBeanClass(), "isShowRowHeader", "setShowRowHeader") 
        }; 
    }
}
