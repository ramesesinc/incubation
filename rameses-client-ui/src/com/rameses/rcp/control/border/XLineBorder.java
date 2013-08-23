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
import javax.swing.BorderFactory;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

public class XLineBorder extends AbstractBorder 
{
    private Border sourceBorder; 
    private Color color;
    private Insets padding;
    private int thickness;
    
    private boolean hideTop;
    private boolean hideLeft;
    private boolean hideBottom;
    private boolean hideRight;
    
    public XLineBorder() {
        this.color = Color.BLACK;
        this.thickness = 1;
        this.padding = new Insets(0,0,0,0);        
    }

    public Color getLineColor() { return color; }    
    public void setLineColor(Color color) { this.color = color; }
    
    public int getThickness() { return thickness; }    
    public void setThickness(int thickness) { this.thickness = thickness; }
    
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
        return getBorderInsets(c, new Insets(0,0,0,0));
    }
    
    public Insets getBorderInsets(Component c, Insets ins) {
        if (ins == null) ins = new Insets(0, 0, 0, 0);
                
        ins.top = ins.left = ins.bottom = ins.right = 0;
        Color color0 = getLineColor();        
        int thickness0 = getThickness();
        sourceBorder = BorderFactory.createLineBorder(color0, thickness0); 
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
        if (sourceBorder == null) return;
        
        Color oldColor = g.getColor();        
        Graphics2D g2 = (Graphics2D) g.create();        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        sourceBorder.paintBorder(c, g, x, y, width, height);         
        g.setColor(oldColor);
        if (isHideTop()) {
            for (int i=0; i<thickness0; i++) {
                g2.drawLine(x, y+i, width, y+i);
            } 
        }        
        if (isHideLeft()) {
            for (int i=0; i<thickness0; i++) {
                g2.drawLine(x+i, y, x+i, height);
            }
        } 
        if (isHideBottom()) {
            for (int i=1; i<=thickness0; i++) {
                g2.drawLine(x, height-i, width, height-i);
            }
        } 
        if (isHideRight()) {
            for (int i=1; i<=thickness0; i++) {
                g2.drawLine(width-i, y, width-i, height);
            }
        }         
        g2.dispose();
    }    
}
 