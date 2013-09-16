/*
 * XScrollPane.java
 *
 * Created on September 16, 2013, 2:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 *
 * @author wflores
 */
public class XScrollPane extends JScrollPane implements UIControl 
{
    private Binding binding;
    private String[] depends; 
    private int index;
    
    private FocusChangeListener focusChangeListener;
    
    public XScrollPane() {
        this.focusChangeListener = new FocusChangeListener(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation "> 

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public void load() { 
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.removePropertyChangeListener("focusOwner", focusChangeListener);
        kfm.addPropertyChangeListener("focusOwner", focusChangeListener); 
    }

    public void refresh() { 
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" FocusChangeListener ">
    
    private class FocusChangeListener implements PropertyChangeListener
    {
        XScrollPane root = XScrollPane.this;
        
        public void propertyChange(PropertyChangeEvent evt) {
            Object newValue = evt.getNewValue();
            if (!(newValue instanceof JComponent)) return;

            JComponent view = (JComponent) root.getViewport().getView();
            if (view == null) return;
            
            JComponent focused = (JComponent) newValue;
            if (view.isAncestorOf(focused)) {
                view.scrollRectToVisible(focused.getBounds());
            }
        } 
    }
    
    // </editor-fold>
}
