/*
 * DefaultTextField.java
 *
 * Created on May 8, 2013, 2:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.InputVerifier;
import javax.swing.JTextField;

/**
 *
 * @author wflores
 */
public class DefaultTextField extends JTextField 
{
    private InputVerifierProxy inputVerifierProxy;    
    
    public DefaultTextField() 
    {
        initDefaults(); 
        resetInputVerifierProxy(); 
    }

    protected void initDefaults() {}
        
    protected InputVerifier getMainInputVerifier() { return null; } 
    protected InputVerifier getChildInputVerifier() { return null; } 
    
    protected void resetInputVerifierProxy() 
    {
        inputVerifierProxy = new InputVerifierProxy(getMainInputVerifier()); 
        inputVerifierProxy.setChild(getChildInputVerifier());        
        super.setInputVerifier(inputVerifierProxy); 
        super.putClientProperty(InputVerifierProxy.class, inputVerifierProxy);  
    }
    
    public final InputVerifier getInputVerifier() { return inputVerifierProxy; }    
    public final void setInputVerifier(InputVerifier verifier) { 
        inputVerifierProxy.setEnabled((verifier == null? false: true)); 
    } 
    
    public final InputVerifierProxy getInputVerifierProxy() { return inputVerifierProxy; }
    
    protected final void processFocusEvent(FocusEvent e) 
    {
        if (e.getID() == FocusEvent.FOCUS_GAINED) 
        {
            resetInputVerifierProxy(); 
            inputVerifierProxy.setEnabled(true); 
            super.setInputVerifier(inputVerifierProxy); 
            try { onfocusGained(e); } catch(Exception ex) {;} 
        } 
        else if (e.getID() == FocusEvent.FOCUS_LOST) 
        { 
            try { onfocusLost(e); } catch(Exception ex) {;} 
            inputVerifierProxy.setEnabled(false);
        } 
        
        super.processFocusEvent(e); 
    }     
    
    protected void onfocusGained(FocusEvent e) {
    }
    
    protected void onfocusLost(FocusEvent e) {
    }    
    
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) 
    {
        if ("enableInputVerifier".equals(propertyName)) 
            inputVerifierProxy.setEnabled(newValue); 
        else if ("detachInputVerifier".equals(propertyName)) 
            super.setInputVerifier((newValue == true? null: inputVerifierProxy));
        
        onpropertyChange(propertyName, oldValue, newValue); 
        super.firePropertyChange(propertyName, oldValue, newValue); 
    } 
        
    protected void onpropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

    protected void processKeyEvent(KeyEvent e) 
    {
        onprocessKeyEvent(e); 
        super.processKeyEvent(e); 
    }
    
    protected void onprocessKeyEvent(KeyEvent e){
    } 
    
}
