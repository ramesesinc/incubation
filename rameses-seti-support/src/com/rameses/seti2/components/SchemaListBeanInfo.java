package com.rameses.seti2.components;

import com.rameses.beaninfo.ComponentBeanInfo;
import com.rameses.beaninfo.editor.ColumnPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class SchemaListBeanInfo extends ComponentBeanInfo.Support {
    
    private Class beanClass;
    
    protected Class getBeanClass() {
        if (beanClass == null) beanClass = SchemaList.class;
        
        return beanClass; 
    }
    
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException {
        return new PropertyDescriptor[] {
            new PropertyDescriptor("border", getBeanClass()),
            new PropertyDescriptor("autoResize", getBeanClass(), "isAutoResize", "setAutoResize"),
            new PropertyDescriptor("showHorizontalLines", getBeanClass(), "isShowHorizontalLines", "setShowHorizontalLines"), 
            new PropertyDescriptor("showVerticalLines", getBeanClass(), "isShowVerticalLines", "setShowVerticalLines"),
            
            installEditor(new PropertyDescriptor("columns", getBeanClass()), ColumnPropertyEditor.class), 
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("rowHeight", getBeanClass()),
            new PropertyDescriptor("visibleWhen", getBeanClass()),
            
            createPropertyDescriptor("schemaName", true),
            createPropertyDescriptor("customFilter", true),
            createPropertyDescriptor("queryName", true),
            createPropertyDescriptor("orderBy", true),
            createPropertyDescriptor("groupBy", true),
            createPropertyDescriptor("hiddenCols", true),
            
            new PropertyDescriptor("allowCreate", getBeanClass(), "isAllowCreate", "setAllowCreate"),
            new PropertyDescriptor("allowDelete", getBeanClass(), "isAllowDelete", "setAllowDelete"),
            new PropertyDescriptor("allowOpen", getBeanClass(), "isAllowOpen", "setAllowOpen") 
        }; 
    }
    
    protected PropertyDescriptor createPropertyDescriptor( String name, boolean preferred ) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor( name, getBeanClass()); 
        if ( preferred ) pd.setPreferred( preferred ); 
        return pd; 
    }
}
