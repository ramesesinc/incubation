/*
 * XOpenerFieldBeanInfo.java
 *
 * Created on June 11, 2013, 10:08 AM
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
public class XOpenerFieldBeanInfo extends ComponentBeanInfoSupport {
    
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XOpenerField.class;
        } 
        return beanClass;
    }

    protected void loadAdditionalBeanInfo(List<BeanInfo> list) { 
        list.add( new UITextFieldBeanInfo(getBeanClass())); 
    }

    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "handler", true ); 
        add( list, "handlerObject" ); 
        add( list, "varName" ); 
    }
}
