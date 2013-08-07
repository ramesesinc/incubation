/*
 * TableBorders.java
 *
 * Created on August 5, 2013, 4:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.support.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *
 * @author wflores
 */
public class TableBorders 
{
    
    public TableBorders() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultBorder (class) ">
    
    public static class DefaultBorder extends AbstractBorder 
    {
        private Insets PADDING = new Insets(1, 1, 1, 1); 

        public Insets getBorderInsets(Component c)       {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.left = insets.bottom = insets.right = 0; 
            insets.top += PADDING.top;
            insets.left += PADDING.left;
            insets.bottom += PADDING.bottom;
            insets.right += PADDING.right;
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            g.drawRect(0, 0, w-1, h-1);
            g.setColor(oldColor); 
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HeaderBorder (class) "> 
    
    public static class HeaderBorder extends AbstractBorder 
    {
        private boolean hideTop = true;
        private boolean hideLeft = false;
        private boolean hideBottom = false;
        private boolean hideRight = false;
        
        public HeaderBorder() {}
        
        public HeaderBorder(boolean hideTop, boolean hideLeft,  boolean hideBottom, boolean hideRight) {
            this.hideTop = hideTop;
            this.hideLeft = hideLeft;
            this.hideBottom = hideBottom;
            this.hideRight = hideRight;
        }
        
        public void setHideTop(boolean hideTop) { this.hideTop = hideTop; }
        public void setHideLeft(boolean hideLeft) { this.hideLeft = hideLeft; }
        public void setHideBottom(boolean hideBottom) { this.hideBottom = hideBottom; }
        public void setHideRight(boolean hideRight) { this.hideRight = hideRight; }        
                
        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.bottom = 5; 
            insets.left = insets.right = 5; 
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            if (!hideTop) g.drawLine(0, 0, w, 0); 
            
            g.setColor(getHighlightColor(c));
            if (!hideLeft) g.drawLine(0, 2, 0, h-5); 
            
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            if (!hideBottom) g.drawLine(0, h-2, w, h-2); 
            
            g.setColor(getShadowColor(c));
            if (!hideRight) g.drawLine(w-1, 2, w-1, h-5);

            g.setColor(oldColor); 
        }
        
        protected Color getHighlightColor(Component c) {
            return c.getBackground().brighter();
        }

        protected Color getShadowColor(Component c) {
            return c.getBackground().darker();
        }  
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RowBorder (class) "> 
    
    public static class RowBorder extends AbstractBorder 
    {
        private Color defaultColor = java.awt.SystemColor.control; 
        
        public RowBorder() {}
        
        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.bottom = 2; 
            insets.left = insets.right = 2; 
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            Color newColor = ColorUtil.brighter(defaultColor.darker(), 20);
            g.setColor(newColor);
            g.drawLine(w-1, 0, w-1, h); 
            g.setColor(oldColor); 
        }  
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ViewPortBorder (class) "> 
    
    public static class ViewPortBorder extends AbstractBorder 
    {
        private Color defaultColor = java.awt.SystemColor.control; 
        
        public ViewPortBorder() {}
        
        public Insets getBorderInsets(Component c, Insets insets) {
            if (insets == null) insets = new Insets(0,0,0,0);
            
            insets.top = insets.bottom = 5; 
            insets.left = insets.right = 5; 
            return insets; 
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color oldColor = g.getColor();
            Color newColor = ColorUtil.brighter(defaultColor.darker(), 20);
            g.setColor(newColor);
            g.drawLine(0, 0, 0, h); 
            g.setColor(oldColor); 
        }        
    }
    
    // </editor-fold>        
    
}
