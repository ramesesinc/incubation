/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.components;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.ColumnPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author elmonazareno
 */
public class ListPanelBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = ListPanel.class;
        }
        return beanClass; 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "columns", true, ColumnPropertyEditor.class ); 
        add( list, "handler", true); 
        add( list, "dynamic", true); 
    }
    
}
