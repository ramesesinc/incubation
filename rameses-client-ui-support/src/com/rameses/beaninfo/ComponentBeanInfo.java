/*
 * ComponentBeanInfo.java
 *
 * Created on May 4, 2013, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo;

import com.rameses.rcp.common.MsgBox;
import java.awt.Component;
import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 *
 * @author wflores
 */
public class ComponentBeanInfo extends SimpleBeanInfo 
{
    
    public PropertyDescriptor[] getPropertyDescriptors() 
    {
        try 
        {
            return new PropertyDescriptor[] {
                new PropertyDescriptor("background", Component.class, "getBackground", "setBackground"),                
                new PropertyDescriptor("foreground", Component.class, "getForeground", "setForeground"),
                
                new PropertyDescriptor("enabled", Component.class, "isEnabled", "setEnabled"),                
                new PropertyDescriptor("preferredSize", Component.class),
                new PropertyDescriptor("font", Component.class), 
                
                new PropertyDescriptor("name", Component.class),
                new PropertyDescriptor("visible", Component.class) 
            };
        } 
        catch (IntrospectionException ie) {
            return super.getPropertyDescriptors();
        }
    }
    
    
    public abstract static class Support extends SimpleBeanInfo
    {
        private String iconName;
        private Class beanClass;

        public Support() {
            this.beanClass = getBeanClass(); 
        }
                
        protected abstract PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException; 
        protected abstract Class getBeanClass();         

        public BeanDescriptor getBeanDescriptor() {
            return new BeanDescriptor(this.beanClass);
        }

        public Image getIcon(int paramInt)
        {
            return null; 
            //if (this.iconName == null) return null;
            //return Utilities.loadImage("org/netbeans/modules/form/beaninfo/awt/" + this.iconName + ".gif");
        }

        public BeanInfo[] getAdditionalBeanInfo() {
            return new BeanInfo[] { new ComponentBeanInfo() };
        }

        public PropertyDescriptor[] getPropertyDescriptors()
        {
            try {
                return createPropertyDescriptors(); 
            } 
            catch (IntrospectionException ie) {
                MsgBox.err(ie); 
                return null;
            }
        }  
        
        protected PropertyDescriptor installEditor(PropertyDescriptor pd, Class editorClass) 
        {
            pd.setPropertyEditorClass(editorClass); 
            return pd; 
        } 
    } 
}
