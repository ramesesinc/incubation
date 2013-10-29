/*
 * MainLayout.java
 *
 * Created on October 24, 2013, 11:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *
 * @author wflores
 */
class MainWindowLayout implements LayoutManager 
{
    public final static String MENUBAR_SECTION = "MENUBAR";
    public final static String TOOLBAR_SECTION = "TOOLBAR";
    public final static String CONTENT_SECTION = "CONTENT";
    public final static String STATUSBAR_SECTION = "STATUSBAR";
    
    private Component menubar;
    private Component toolbar;
    private Component content;
    private Component statusbar;
    
    public MainWindowLayout()
    {
    }
    
    public void addLayoutComponent(String name, Component comp) { 
        if (comp == null) return;
        
        synchronized (comp.getTreeLock()) {
            if (name == null) name = CONTENT_SECTION;

            if (MENUBAR_SECTION.equalsIgnoreCase(name)) {
                menubar = comp;
            } else if (TOOLBAR_SECTION.equalsIgnoreCase(name)) {
                toolbar = comp;
            } else if (STATUSBAR_SECTION.equalsIgnoreCase(name)) {
                statusbar = comp;
            } else if (CONTENT_SECTION.equalsIgnoreCase(name)) {
                content = comp; 
            } 
        } 
    }    
    public void removeLayoutComponent(Component comp) {
        if (comp == null) return;
        
        synchronized (comp.getTreeLock()) {
            if (menubar != null && menubar.equals(comp)) {
                menubar = null; 
            } else if (toolbar != null && toolbar.equals(comp)) {
                toolbar = null; 
            } else if (statusbar != null && statusbar.equals(comp)) {
                statusbar = null; 
            } else if (content != null && content.equals(comp)) {
                content = null; 
            } 
        } 
    } 
    
    public Component getLayoutComponent(Object constraints) {
        String name = (constraints == null? null: constraints.toString());
        if (name == null || name.length() == 0) return null; 
        
        if (MENUBAR_SECTION.equalsIgnoreCase(name)) {
            return menubar; 
        } else if (TOOLBAR_SECTION.equalsIgnoreCase(name)) {
            return toolbar; 
        } else if (STATUSBAR_SECTION.equalsIgnoreCase(name)) {
            return statusbar; 
        } else if (CONTENT_SECTION.equalsIgnoreCase(name)) {
            return content; 
        } else {
            return null; 
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int width=0, height=0;

            if (menubar != null && menubar.isVisible()) {
                Dimension dim = menubar.getMinimumSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            }
            if (toolbar != null && toolbar.isVisible()) {
                Dimension dim = toolbar.getMinimumSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (statusbar != null && statusbar.isVisible()) {
                Dimension dim = statusbar.getMinimumSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (content != null && content.isVisible()) {
                Dimension dim = content.getMinimumSize();
                if (width == 0) width = dim.width;
                
                height += dim.height;
            } 
            
            Insets insets = parent.getInsets();
            width += insets.left + insets.right;
            height += insets.top + insets.bottom;
            return new Dimension(width, height); 
        }
    }

    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int width=0, height=0;

            if (menubar != null && menubar.isVisible()) {
                Dimension dim = menubar.getPreferredSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            }
            if (toolbar != null && toolbar.isVisible()) {
                Dimension dim = toolbar.getPreferredSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (statusbar != null && statusbar.isVisible()) {
                Dimension dim = statusbar.getPreferredSize();
                width = Math.max(dim.width, width); 
                height += dim.height;
            } 
            if (content != null && content.isVisible()) {
                Dimension dim = content.getPreferredSize();
                if (width == 0) width = dim.width;
                
                height += dim.height;
            } 
            
            Insets insets = parent.getInsets();
            width += insets.left + insets.right;
            height += insets.top + insets.bottom;
            return new Dimension(width, height); 
        }
    }    
    
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets margin = parent.getInsets();            
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int x = margin.left;
            int y = margin.top;
            int w = pw - (margin.left + margin.right);
           
            if (menubar != null && menubar.isVisible()) {
                Dimension dim = menubar.getPreferredSize();
                menubar.setBounds(x, y, w, dim.height); 
                y += dim.height;
            }
            if (toolbar != null && toolbar.isVisible()) {
                Dimension dim = toolbar.getPreferredSize();
                toolbar.setBounds(x, y, w, dim.height); 
                y += dim.height;
            } 
            
            int cy = y;
            int ch = (ph-margin.bottom)-y;            
            if (ch <= 0) return;
            
            if (statusbar != null && statusbar.isVisible()) {
                Dimension dim = statusbar.getPreferredSize();
                int y0 = (ch - dim.height)+cy;
                if (y0 <= 0) return;
                
                statusbar.setBounds(x, y0, w, dim.height); 
                ch = y0 - y; 
            } 
            
            if (ch <= 0) return; 
            if (content != null && content.isVisible()) {
                Dimension dim = content.getPreferredSize();
                content.setBounds(x, cy, w, ch); 
            }
        }
    } 

}
