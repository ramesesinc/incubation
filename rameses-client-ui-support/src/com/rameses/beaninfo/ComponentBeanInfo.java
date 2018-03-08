/*
 * ComponentBeanInfo.java
 *
 * Created on May 4, 2013, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo;

import java.awt.Component;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

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
                
                createPropertyDescriptor("name", Component.class, true),
                new PropertyDescriptor("visible", Component.class), 
                new PropertyDescriptor("opaque", JComponent.class, "isOpaque", "setOpaque"),
                new PropertyDescriptor("toolTipText", JComponent.class)
            };
        } 
        catch (IntrospectionException ie) {
            return super.getPropertyDescriptors();
        }
    }
    
    protected PropertyDescriptor createPropertyDescriptor( String name, Class beanClass, boolean preferred ) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor( name, beanClass); 
        if ( preferred ) pd.setPreferred( preferred ); 
        return pd; 
    }
    
    
    public abstract static class Support extends SimpleBeanInfo {
    
        private String _icon = "gear.png";        
        private Class beanClass;

        public Support() {
            this.beanClass = getBeanClass(); 
        }
                
        protected abstract PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException; 
        protected abstract Class getBeanClass();         

        public BeanDescriptor getBeanDescriptor() {
            return new BeanDescriptor(this.beanClass, null);
        }
        
        public BeanInfo[] getAdditionalBeanInfo() {
            return new BeanInfo[] { new ComponentBeanInfo() };
        }

        public PropertyDescriptor[] getPropertyDescriptors() {
            try {
                if (Beans.isDesignTime()) 
                    return createPropertyDescriptors(); 
                else 
                    return super.getPropertyDescriptors(); 
            } catch (IntrospectionException ie) {
                ie.printStackTrace();
                showError(ie);
                return null;
            }
        }  
        
        protected PropertyDescriptor installEditor(PropertyDescriptor pd, Class editorClass) {
            pd.setPropertyEditorClass(editorClass); 
            return pd; 
        } 
        
        private void showError(Throwable t) {
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Window window = kfm.getActiveWindow();
            String errmsg = t.getClass().getName() + ": " + t.getMessage();
            JOptionPane.showMessageDialog(window, errmsg, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    } 
}
