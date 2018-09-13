package com.rameses.seti2.components;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.ColumnPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

public class DataListBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = DataList.class;
        }
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "autoResize" ); 
        addBoolean( list, "showHorizontalLines" ); 
        addBoolean( list, "showVerticalLines" ); 
        addBoolean( list, "multiSelect" ); 
        add( list, "formActions", true ); 
        add( list, "items", true ); 
        
        add( list, "columns", true, ColumnPropertyEditor.class ); 
        add( list, "handler", true); 
        add( list, "id"); 
        add( list, "rowHeight" ); 
        add( list, "rows" ); 
        add( list, "actionContext", true ); 
        add( list, "menuContext", true ); 
        add( list, "styleRule" ); 
    }
}
