/*
 * XLayout.java
 *
 * Created on December 12, 2013, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/** 
 *
 * @author wflores
 */
public class XLayout implements LayoutManager
{
    private int spacing = 2; 
    
    public int getSpacing() { return spacing; }
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
    
    public void addLayoutComponent(String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize(parent);
    }
    
    private Dimension getLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) { 
            int w=0, h=0;
            boolean has_visible_components = false; 
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i]; 
                if (!c.isVisible()) continue;
                if (has_visible_components) w += getSpacing();
                    
                Dimension dim = c.getPreferredSize();
                w += dim.width;
                h = Math.max(dim.height, h); 
                has_visible_components = true; 
            }
            
            Insets margin = parent.getInsets();
            w += (margin.left + margin.right);
            h += (margin.top + margin.bottom);
            return new Dimension(w, h); 
        }
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) { 
            Insets margin = parent.getInsets();
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int x = margin.left;
            int y = margin.top;
            int h = ph - (margin.top + margin.bottom);
            
            boolean has_visible_components = false; 
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i]; 
                if (!c.isVisible()) continue;
                if (has_visible_components) x += getSpacing();
                    
                Dimension dim = c.getPreferredSize();
                c.setBounds(x, y, dim.width, h); 
                x += dim.width; 
                has_visible_components = true; 
            }
        }        
    }
        
}