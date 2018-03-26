/*
 * XCameraBeanInfo.java
 *
 * Created on December 4, 2013, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XCameraBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XCamera.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        add( list, "accelerator" );
        add( list, "margin" );
        add( list, "mnemonic" );
        add( list, "text" );
        add( list, "icon" );
        add( list, "iconResource" );
        add( list, "target" );
        add( list, "handler", true );
        
        addBoolean( list, "borderPainted" );
        addBoolean( list, "contentAreaFilled" );
        addBoolean( list, "defaultCommand" );
        addBoolean( list, "immediate" );
        addBoolean( list, "focusable" );
    }
}
