/*
 * UIControlUtil.java
 *
 * Created on July 8, 2010, 9:40 AM
 * @author jaycverg
 */

package com.rameses.rcp.util;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.NavigatablePanel;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.ui.UIControl;
import com.rameses.common.ExpressionResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import javax.swing.JComponent;


public class UIControlUtil 
{

    public static void setBeanValue(Binding binding, String name, Object value) {
        setBeanValue(binding.getBean(), name, value); 
    }
    
    public static void setBeanValue(Object bean, String name, Object value) 
    {
        PropertyResolver resolver = PropertyResolver.getInstance();
        resolver.setProperty(bean, name, value); 
    }    
    
    public static Object getBeanValue(UIControl control) {
        return getBeanValue(control, control.getName());
    }
    
    public static Object getBeanValue(UIControl control, String property) {
        Binding binding = control.getBinding(); 
        Object beanValue = getBeanValue(control.getBinding(), property);
        if ( beanValue != null ) return beanValue;
        
        Object userObj = control.getClientProperty(UIControl.KEY_USER_OBJECT); 
        if (userObj != null) {
            try {
                return PropertyResolver.getInstance().getProperty(userObj, "value"); 
            } catch(Throwable t){ ; } 
        } 
        return null; 
    } 
    
    public static Object getBeanValue(Binding binding, String property) { 
        return getBeanValue(binding.getBean(), property); 
    } 
    
    public static Object getBeanValue(Object bean, String property) {
        if (bean == null || property == null || property.length() == 0) return null;
        
        PropertyResolver resolver = PropertyResolver.getInstance();
        try { 
            return resolver.getProperty(bean, property); 
        } catch(Throwable t) {  
            return null; 
        } 
    } 
    
    public static Class getValueType(UIControl control, String property) 
    {
        PropertyResolver resolver = PropertyResolver.getInstance();
        Object bean = control.getBinding().getBean();
        try { 
            return resolver.getPropertyType(bean, property);
        } catch(NullPointerException npe) {
            return null;
        }
    }
    
    public static Object evaluateExpr(Object bean, String expression) 
    {
        if (bean == null || expression == null) return null; 
        
        ExpressionResolver er = ExpressionResolver.getInstance();
        try 
        { 
            String result = er.evalString(expression, bean); 
            if (result != null && "null".equals(result)) return null; 
                
            return result;
        } 
        catch(NullPointerException npe) {
            return null; 
        }
    }
    
    public static boolean evaluateExprBoolean(Object bean, String expression) 
    {
        if (bean == null || expression == null) return false; 
        
        ExpressionResolver er = ExpressionResolver.getInstance();
        try { 
            return er.evalBoolean(expression, bean); 
        } catch(NullPointerException npe) {
            return false; 
        }
    } 
    
    public static Object evaluate(Object bean, String expression) 
    {
        if (bean == null || expression == null) return null; 
        
        ExpressionResolver er = ExpressionResolver.getInstance();
        try { 
            return er.eval(expression, bean);
        } catch(NullPointerException npe) {
            return null; 
        }
    }       
    
    public static int compare(UIControl control, Object control2) 
    {
        if ( control2 == null || !(control2 instanceof UIControl)) return 0;
        return control.getIndex() - ((UIControl) control2).getIndex();
    }
    
    public static NavigatablePanel getParentPanel(JComponent comp, String target) 
    {
        NavigatablePanel panel = null;
        if ( panel == null ) 
        {
            Container parent = comp.getParent();
            while( parent != null ) {
                if ( parent instanceof NavigatablePanel ) {
                    panel = (NavigatablePanel) parent;
                }
                if ( (panel != null && "parent".equals(target)) || (parent instanceof UIControllerPanel && "root".equals(parent.getName())) ) {
                    break;
                }
                parent = parent.getParent();
            }
            if ( panel != null ) {
                comp.putClientProperty(NavigatablePanel.class, panel);
            }
        }
        return panel;
    }
    
    public static void validate(List<Validatable> validatables, ActionMessage actionMessage) {
        for ( Validatable vc: validatables ) {
            validate(vc, actionMessage);
        }
    }
    
    public static void validate(Validatable vc, ActionMessage actionMessage) {
        Component comp = null;
        if ( vc instanceof Component ) {
            comp = (Component) vc;
            if ( !comp.isFocusable() || !comp.isEnabled() || !comp.isShowing() || comp.getParent() == null ) {
                //do not validate non-focusable, disabled, or hidden fields.
                return;
            }
        }
        if ( vc instanceof UIInput ) {
            //do not validate readonly fields
            if ( ((UIInput)vc).isReadonly() ) return;
        }
        
        vc.validateInput();
        
        ActionMessage ac = vc.getActionMessage();
        if ( ac != null && ac.hasMessages() ) 
        {
            if ( ValueUtil.isEmpty(actionMessage.getSource()) )
                actionMessage.setSource( comp );
            
            actionMessage.addMessage(ac);
        }
    } 
}
