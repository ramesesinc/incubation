/*
 * XLineBorder.java
 *
 * Created on October 18, 2010, 10:18 AM
 * @author jaycverg
 */

package com.rameses.rcp.control.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

public class XLineBorder extends AbstractBorder 
{
    private Color background;
    private Color color;
    private Insets padding;
    private int thickness;
    private int arc;
    
    private boolean hideTop;
    private boolean hideLeft;
    private boolean hideBottom;
    private boolean hideRight;
    
    public XLineBorder() {
        setLineColor(Color.BLACK);
        setThickness(1);
        setPadding(null); 
    }

    public Color getBackground() { return background; }    
    public void setBackground(Color background) { this.background = background; }  
    
    public Color getLineColor() { return color; }    
    public void setLineColor(Color color) { this.color = color; }
    
    public int getThickness() { return thickness; }    
    public void setThickness(int thickness) { this.thickness = thickness; }
    
    public int getArc() { return arc; }    
    public void setArc(int arc) { this.arc = arc; }
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) {  
        this.padding = (padding == null? new Insets(0,0,0,0): padding); 
    }
    
    public boolean isHideTop() { return hideTop; }
    public void setHideTop(boolean hideTop) { this.hideTop = hideTop; }

    public boolean isHideLeft() { return hideLeft; }
    public void setHideLeft(boolean hideLeft) { this.hideLeft = hideLeft; }

    public boolean isHideBottom() { return hideBottom; }
    public void setHideBottom(boolean hideBottom) { this.hideBottom = hideBottom; }

    public boolean isHideRight() { return hideRight; }
    public void setHideRight(boolean hideRight) { this.hideRight = hideRight; }    
            
    public Insets getBorderInsets(Component c) {
        Insets ins = new Insets(0,0,0,0);
        return getBorderInsets(c, ins);
    }
    
    public Insets getBorderInsets(Component c, Insets ins) {
        if (ins == null) ins = new Insets(0, 0, 0, 0);
                
        ins.top = ins.left = ins.bottom = ins.right = 0;
        int thickness0 = getThickness();
        if (thickness0 > 0) 
        {
            if (!isHideTop()) ins.top+=thickness0;
            if (!isHideLeft()) ins.left+=thickness0;
            if (!isHideBottom()) ins.bottom+=thickness0;
            if (!isHideRight()) ins.right+=thickness0;
        }
        
        ins.top += padding.top;
        ins.left += padding.left;
        ins.bottom += padding.bottom;
        ins.right += padding.right;
        return ins;
    }    
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int thickness0 = getThickness();
        if (thickness0 <= 0) return;
        
        Color newColor = getLineColor(); 
        if (newColor == null) return;
        
        Graphics2D g2 = (Graphics2D) g.create();        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if ( background != null ) { 
            g2.setColor(background);  
            g2.fillRoundRect(x, y, width-1, height-1, arc, arc);
        }
        
        Color oldColor = g.getColor();
        g.setColor(newColor);
        if (!isHideTop()) {
            for (int i=0; i<thickness0; i++) {
                g2.drawLine(x, y+i, width, y+i);
            } 
        } 
        
        if (!isHideLeft()) {
            for (int i=0; i<thickness0; i++) {
                g2.drawLine(x+i, y, x+i, height);
            }
        } 
        
        if (!isHideBottom()) {
            for (int i=1; i<=thickness0; i++) {
                g2.drawLine(x, height-i, width, height-i);
            }
        } 
        
        if (!isHideRight()) {
            for (int i=1; i<=thickness0; i++) {
                g2.drawLine(width-i, y, width-i, height);
            }
        } 
        
        g2.dispose();
    }    
}
 