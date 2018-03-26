/*
 * XDateFieldBeanInfo.java
 *
 * Created on September 2, 2013, 11:20 AM
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
public class XDateFieldBeanInfo extends ComponentBeanInfoSupport 
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XDateField.class;
        } 
        return beanClass;
    }

    protected void loadAdditionalBeanInfo(List<BeanInfo> list) {
        list.add( new UITextFieldBeanInfo(getBeanClass()));  
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "inputMask", true);
        add( list, "outputFormat", true);
        add( list, "valueFormat", true);
    }
}
