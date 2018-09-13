package com.rameses.seti2.components;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.ColumnPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

public class SchemaListBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = SchemaList.class;
        }
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "autoResize" ); 
        addBoolean( list, "showHorizontalLines" ); 
        addBoolean( list, "showVerticalLines" ); 
        addBoolean( list, "multiSelect" ); 
        addBoolean( list, "allowCreate" ); 
        addBoolean( list, "allowDelete" ); 
        addBoolean( list, "allowOpen" ); 
        addBoolean( list, "allowSearch" ); 
        addBoolean( list, "showFilter" ); 
        addBoolean( list, "showRefresh" ); 
        addBoolean( list, "showInfo" ); 
        addBoolean( list, "showNavbar" ); 
        addBoolean( list, "showRowHeader" ); 
        addBoolean( list, "showColumnHeader" ); 
        
        add( list, "formActions", true ); 
        
        add( list, "columns", true, ColumnPropertyEditor.class ); 
        add( list, "handler", true); 
        add( list, "handlerName", true); 
        add( list, "id"); 
        add( list, "rowHeight" ); 
        add( list, "rows" ); 
        add( list, "actionContext", true ); 
        add( list, "menuContext", true ); 
        add( list, "schemaName", true ); 
        add( list, "entityName", true ); 

        add( list, "customFilter", true ); 
        add( list, "queryName", true ); 
        add( list, "orderBy", true ); 
        add( list, "groupBy", true ); 
        add( list, "hiddenCols", true ); 
        add( list, "styleRule" ); 
    }
}
