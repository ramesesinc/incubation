/*
 * XDecimalFieldBeanInfo.java
 *
 * Created on May 8, 2013, 9:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.UITextFieldBeanInfo;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XIntegerFieldBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) {
            beanClass = XIntegerField.class;
        } 
        return beanClass;
    }

    protected void loadAdditionalBeanInfo(List<BeanInfo> list) {
        list.add( new UITextFieldBeanInfo(getBeanClass())); 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "usePrimitiveValue", true );
        
        add( list, "minValue", true );
        add( list, "maxValue", true );
        add( list, "pattern", true );
    }
}
