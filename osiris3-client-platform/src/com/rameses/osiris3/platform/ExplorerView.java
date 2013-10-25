/*
 * ExplorerView.java
 *
 * Created on October 24, 2013, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *
 * @author wflores
 */
class ExplorerView extends JPanel 
{
    private HeaderRenderer header;
    
    public ExplorerView() {
        super.setLayout(new BorderLayout());
        Border border = BorderFactory.createLineBorder(new Color(150,150,150), 1);
        super.setBorder(border); 
        header = new HeaderRenderer();
        header.setText(" "); 
        add(header, BorderLayout.NORTH);
    }
    
    void setTitle(String title) {
        header.setText((title == null? " ": title)); 
    }
    
    void attachHeader() {
        remove(header); 
        add(header, BorderLayout.NORTH); 
    }
    
    private Color brighter(Color c, int value) {
        if (value < 0) return c;
        
        float[] hsb = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),new float[3]);
        int h = (int) (hsb[0] * 360);
        int s = (int) (hsb[1] * 100);
        int b = (int) (hsb[2] * 100);
        
        int rm = 0;
        b += value;
        if (b > 100) {
            rm = b - 100;
            b = 100;
        }
        s -= rm;
        if (s < 0) s = 0;
        
        int rgb = Color.HSBtoRGB(h/360.0f, s/100.0f, b/100.0f);
        return new Color(rgb);
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" HeaderRenderer (class) ">
    
    private class HeaderRenderer extends JLabel 
    {
        ExplorerView root = ExplorerView.this; 
        private HeaderBorder border;
        
        public HeaderRenderer() {
            setBorder(border = new HeaderBorder()); 
            setBackground(java.awt.SystemColor.control); 
        }
                        
        protected Color getHighlightColor() {
            return getBackground().brighter();
        }

        protected Color getShadowColor() { 
            return getBackground().darker(); 
        }

        public void setFont(Font font) {
            if (font == null) {
                super.setFont(font); 
                return;
            }
            
            Map attrs = new HashMap(); 
            attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            
            Font newFont = font.deriveFont(attrs);
            super.setFont(newFont); 
        }
                
        public void paint(Graphics g) 
        {
            int h = getHeight(), w = getWidth(); 
            Color oldColor = g.getColor();
            Color shadow = getShadowColor(); 
            Color bg = root.brighter(shadow, 30);
            Graphics2D g2 = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, bg, 0, h/2, root.brighter(shadow,25));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.setPaint(null);
            g2.setColor(root.brighter(shadow,22));
            g2.fillRoundRect(0, h/2, w, h, 5, 0);
            g2.dispose();
            g.setColor(oldColor); 
            super.paint(g); 
        } 
        
        // The following methods override the defaults for performance reasons
        public void validate() {}
        public void revalidate() {}
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}            
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" HeaderBorder (class) "> 
    
    private class HeaderBorder extends AbstractBorder 
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
    
}
