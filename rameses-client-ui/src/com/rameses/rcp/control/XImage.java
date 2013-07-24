/*
 * XImage.java
 *
 * Created on July 19, 2013, 9:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.border.CSSBorder;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.ThemeUI;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.Beans;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class XImage extends JLabel implements UIControl
{
    private Binding binding;
    private String[] depends;
    private int index; 
    private boolean dynamic;
    
    private Border sourceBorder;
    private Insets padding;
    private String iconResource;
    private String borderCSS;
    
    public XImage() 
    {
        super();
        setPadding(null); 
        setBorder((Border) null); 
        
        //default font
        Font f = ThemeUI.getFont("XLabel.font");
        if (f != null) setFont(f); 
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    public void setName(String name) { 
        super.setName(name); 
        
        if (Beans.isDesignTime()) 
            super.setText((name==null? "": name));
    } 
    
    public void setBorder(Border border) 
    {
        BorderWrapper wrapper = new BorderWrapper(border, getPadding()); 
        super.setBorder(wrapper); 
        this.sourceBorder = wrapper.getBorder(); 
    }
    
    public void setBorder(String uiresource) 
    {
        try 
        { 
            Border border = UIManager.getLookAndFeelDefaults().getBorder(uiresource); 
            if (border != null) setBorder(border); 
        } 
        catch(Exception ex) {;} 
    } 
    
    public String getBorderCSS() { return borderCSS; }
    public void setBorderCSS(String borderCSS) {
        this.borderCSS = borderCSS; 
        setBorder(CSSBorder.parse(borderCSS)); 
    }
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) { 
        this.padding = (padding == null? new Insets(1,3,1,1): padding); 
        setBorder(this.sourceBorder);         
    }
    
    public void setText(String text) { 
        if (Beans.isDesignTime()) super.setText(text); 
    }
    
    public String getIconResource() { return iconResource; } 
    public void setIconResource(String iconResource) { 
        this.iconResource = iconResource; 
        getImageIcon(); //loads the icon
        repaint(); 
    } 
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; } 
    
        
    private ImageIcon getImageIcon() 
    {
        String iconRes = getIconResource();
        if (iconRes == null) return null;

        ImageIconSupport iis = ImageIconSupport.getInstance(); 
        if (isDynamic()) iis.removeIcon(iconRes); 

        return iis.getIcon(iconRes); 
    } 
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">    
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }

    public void load() {
    }

    public void refresh() {
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BorderWrapper (class) ">

    private class BorderWrapper extends AbstractBorder
    {   
        XImage root = XImage.this;
        private Border border;
        private Insets padding;
        
        BorderWrapper(Border border, Insets padding) {
            if (border instanceof BorderWrapper) 
                this.border = ((BorderWrapper) border).getBorder(); 
            else 
                this.border = border; 
            
            this.padding = copy(padding); 
        }
        
        public Border getBorder() { return border; } 
        
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }
        
        public Insets getBorderInsets(Component c, Insets ins) {
            if (ins == null) new Insets(0,0,0,0);
            
            ins.top = ins.left = ins.bottom = ins.right = 0;
            if (border != null) 
            {
                Insets ins0 = border.getBorderInsets(c); 
                ins.top += ins0.top;
                ins.left += ins0.left;
                ins.bottom += ins0.bottom;
                ins.right += ins0.right;
            }
            
            ins.top += padding.top;
            ins.left += padding.left;
            ins.bottom += padding.bottom;
            ins.right += padding.right;
            return ins; 
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (border != null) border.paintBorder(c, g, x, y, w, h); 
        }
        
        private Insets copy(Insets padding) {
            if (padding == null) return new Insets(0, 0, 0, 0);
            
            return new Insets(padding.top, padding.left, padding.bottom, padding.right); 
        }
    }

    // </editor-fold>
}
