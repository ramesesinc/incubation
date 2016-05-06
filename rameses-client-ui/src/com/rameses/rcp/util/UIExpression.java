/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.util;

import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import java.awt.Component;

/**
 *
 * @author wflores 
 */
public class UIExpression {
    
    public boolean isEmpty( String expr ) { 
        return ( expr==null || expr.trim().length()==0 ); 
    } 
    
    public Object getBindingBean( UIControl uic ) { 
        if ( uic == null ) return null; 
        
        Binding binding = uic.getBinding(); 
        return ( binding == null? null : binding.getBean()); 
    }
    
    public void disableWhen( UIControl uic, String expr ) {
        if ( uic==null || isEmpty(expr) ) return; 
        
        try { 
            Object bean = getBindingBean( uic ); 
            if ( bean == null ) return; 
            
            if ( uic instanceof Component ) {
                Component comp = (Component) uic; 
                boolean b = UIControlUtil.evaluateExprBoolean( bean, expr ); 
                comp.setEnabled( !b ); 
            } 
        } catch(Throwable t) {
            t.printStackTrace(); 
        } 
    }
    
    public void enableWhen( UIControl uic, String expr ) {
        if ( uic==null || isEmpty(expr) ) return; 
        
        try { 
            Object bean = getBindingBean( uic ); 
            if ( bean == null ) return; 
            
            if ( uic instanceof Component ) {
                Component comp = (Component) uic; 
                boolean b = UIControlUtil.evaluateExprBoolean( bean, expr ); 
                comp.setEnabled( b ); 
            } 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 
    } 
    
    public void visibleWhen( UIControl uic, String expr ) {
        if ( uic==null || isEmpty(expr) ) return; 
        
        try { 
            Object bean = getBindingBean( uic ); 
            if ( bean == null ) return; 
            
            if ( uic instanceof Component ) {
                Component comp = (Component) uic; 
                boolean b = UIControlUtil.evaluateExprBoolean( bean, expr ); 
                comp.setVisible( b );  
            } 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 
    } 
}
