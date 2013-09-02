/*
 * UIInputUtil.java
 *
 * Created on June 21, 2010, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.util;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.UIInput;
import com.rameses.common.PropertyResolver;
import com.rameses.util.ExceptionManager;
import com.rameses.util.ValueUtil;
import java.beans.Beans;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *
 * @author jaycverg
 */
public class UIInputUtil {
    
    public static UIInputVerifier VERIFIER = new UIInputVerifier();
    
    public static class UIInputVerifier extends InputVerifier 
    {
        public boolean verify(JComponent input) 
        {
            if ( Beans.isDesignTime() ) return true;
            
            UIInput control = null;
            if (input instanceof UIInput) 
                control = (UIInput) input;
            else 
                throw new IllegalStateException("UIInputVerifier should be used for UIInput controls only.");
            
            if ( control.isReadonly() || !input.isEnabled() ) return true;
            if ( input instanceof JTextComponent && !((JTextComponent) input).isEditable() ) return true;
            if ( input.getParent() == null ) return true;
            
            updateBeanValue(control);
            return true;
        }
        
    }
    
    public static synchronized void updateBeanValue(UIInput control) {
        updateBeanValue(control, true, true);
    }
    
    public static synchronized void updateBeanValue(UIInput control, boolean addLog, boolean refresh) 
    {
        try 
        {
            Support support = (Support) ((JComponent) control).getClientProperty(UIInputUtil.Support.class); 
            if (support != null) {
                if (control instanceof JComponent)
                    support.setValue(control.getName(), control.getValue(), (JComponent)control);
                else 
                    support.setValue(control.getName(), control.getValue()); 
                
                return;
            }
            
            Binding binding = control.getBinding();
            if (binding == null) return;
            
            Object bean = binding.getBean();
            if (bean == null) return;
            
            ClientContext ctx = ClientContext.getCurrentContext();
            PropertyResolver resolver = PropertyResolver.getInstance();
            String name = control.getName();
            if (ValueUtil.isEmpty(name)) return;
            
            Object inputValue = control.getValue();
            Object beanValue = resolver.getProperty(bean, name);
            boolean forceUpdate = false;
            if (control instanceof JComponent) {
                //if the input is a JTable check for the flag
                Object value = ((JComponent) control).getClientProperty(JTable.class);
                forceUpdate = (value != null);
            }
            
            if (forceUpdate || !ValueUtil.isEqual(inputValue, beanValue)) {
                resolver.setProperty(bean, name, inputValue);
                if ( addLog )  
                    binding.getChangeLog().addEntry(bean, name, beanValue, inputValue);
                
                binding.getValueChangeSupport().notify(name, inputValue);
                
                if ( refresh && control instanceof JTextComponent ) {
                    JTextComponent jtxt = (JTextComponent) control;
                    int oldCaretPos = jtxt.getCaretPosition(); 
                    
                    try { 
                        control.refresh(); 
                    } catch(RuntimeException re) {
                        throw re;
                    } catch(Exception e) {
                        throw new RuntimeException(e.getMessage(), e); 
                    } finally {
                        try {
                            jtxt.setCaretPosition(oldCaretPos); 
                        } catch(Exception ign) {;} 
                    }
                    
                    jtxt.putClientProperty("CaretPosition", oldCaretPos); 
                }
                
                binding.notifyDepends(control);
            }
            else {
                control.refresh(); 
            }
        } 
        catch(Exception e) 
        {
            Exception src = ExceptionManager.getOriginal(e);
            if ( !ExceptionManager.getInstance().handleError(src) ) {
                ClientContext.getCurrentContext().getPlatform().showError((JComponent) control, e);
            }
        }
    }
    
    
    public static interface Support 
    {
        void setValue(String name, Object value);         
        void setValue(String name, Object value, JComponent jcomp); 
    }    
}
