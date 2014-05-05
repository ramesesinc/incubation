/*
 * XPanel.java
 *
 * Created on April 21, 2014, 9:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.CardLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class XPanel extends JPanel implements UIControl, ControlContainer 
{
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String visibleWhen;
    
    public XPanel() {
    }

    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding; 
    }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex(int index) {
        this.index = index; 
    }

    public void load() {
    }

    public void refresh() {
        try {
            Object value = UIControlUtil.getBeanValue(getBinding(), getName());
            LayoutManager lm = getLayout();
            if (lm instanceof CardLayout) { 
                CardLayout cardlayout = (CardLayout)lm; 
                cardlayout.show(this, value+""); 
            } 
        } catch(Throwable t){;}
        
        try { 
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                Object bean = getBinding().getBean();
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(bean, visibleWhen);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                setVisible(b); 
            } 
        } catch(Throwable t) {;} 
                
        revalidate();
        repaint();        
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ControlContainer implementation "> 
    
    public boolean isHasNonDynamicContents() { 
        return true; 
    }

    public UIControl find(String name) {
        return null; 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    

    // </editor-fold>
}
