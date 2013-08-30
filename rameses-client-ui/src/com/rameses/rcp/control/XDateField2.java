/*
 * XDateField2.java
 *
 * Created on August 28, 2013, 11:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.text.AbstractDateField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.TextEditorSupport;
import com.rameses.rcp.support.ThemeUI;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.Beans;
import javax.swing.InputVerifier;

/**
 *
 * @author wflores
 */
public class XDateField2 extends AbstractDateField implements UIInput, ActiveControl 
{
    private Binding binding; 
    private String[] depends; 
    private int index;
    private boolean nullWhenEmpty;
    private boolean immediate;

    private ControlProperty controlProperty;    
    
    public XDateField2() {
        TextEditorSupport.install(this);
        
        //set default font
        Font font = ThemeUI.getFont("XTextField.font");
        if (font != null) setFont(font);
        
        Color disableTextColor = ThemeUI.getColor("XTextField.disabledTextColor");
        if (disableTextColor != null) setDisabledTextColor(disableTextColor);        
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">
    
    public void setName(String name) {
        super.setName(name); 
        if (Beans.isDesignTime()) 
            super.setText((name == null? "": name)); 
    }
    
    protected InputVerifier getChildInputVerifier() {
        return UIInputUtil.VERIFIER; 
    }    
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" UIInput implementation ">

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
        
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }

    public boolean isImmediate() { return immediate; }
    public void setImmediate(boolean immediate) {
        this.immediate = immediate; 
    }
    
    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }
    
    public void setRequestFocus(boolean focus) {
    }

    public void load() {
    }

    public void refresh() {
        try 
        {
            updateBackground(); 
            reloadDocument();
            
            Object value = UIControlUtil.getBeanValue(this);            
            setValue(value); 
        } 
        catch(Exception e) 
        {
            setValue(null);
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace(); 
        }         
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public Object getValue() { 
        return super.getValue(); 
    }
    public void setValue(Object value) {
        super.setValue(value); 
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 

    public ControlProperty getControlProperty() { 
        if (controlProperty == null) {
            controlProperty = new ControlProperty();
        }
        return controlProperty; 
    }   

    public boolean isRequired() { 
        return getControlProperty().isRequired(); 
    }    
    public void setRequired(boolean required) {
        getControlProperty().setRequired(required);
    }

    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
    } 

    public char getCaptionMnemonic() { 
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }    

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    }    
    public void setShowCaption(boolean showCaption) {
        getControlProperty().setShowCaption(showCaption);
    }

    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }

    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }     

    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>

}
