/*
 * FormPage.java
 *
 * Created on April 24, 2013, 12:44 PM
 */

package com.rameses.osiris2.themes;

import com.rameses.rcp.control.layout.LayoutComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

/**
 *
 * @author  wflores
 */
public class FormPage extends javax.swing.JPanel {
    
    public FormPage() {
        initComponents();
        
        headertitle.setHideOnEmpty(true);
        xActionBar1.setHideOnEmpty(true);
        xabFormActions.setHideOnEmpty(true);
        xabNavActions.setHideOnEmpty(true);
        headergroup.setLayout(new HeaderLayout());
        headertoolbar.removeAll();        
        headertoolbar.setLayout(new ToolbarLayout()); 
        headertoolbar.add(leftactionpanel, "LEFT");
        headertoolbar.add(rightactionpanel, "RIGHT");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headergroup = new javax.swing.JPanel();
        headertitle = new com.rameses.rcp.control.XLabel();
        headertoolbar = new javax.swing.JPanel();
        leftactionpanel = new com.rameses.rcp.control.XHorizontalPanel();
        xabFormActions = new com.rameses.rcp.control.XActionBar();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        rightactionpanel = new com.rameses.rcp.control.XHorizontalPanel();
        xabNavActions = new com.rameses.rcp.control.XActionBar();
        xDropDownList1 = new com.rameses.rcp.control.XDropDownList();
        stylerule = new com.rameses.rcp.control.XStyleRule();

        setLayout(new java.awt.BorderLayout());

        headergroup.setLayout(new java.awt.BorderLayout());

        headertitle.setBackground(new java.awt.Color(255, 255, 255));
        headertitle.setExpression("#{title}");
        headertitle.setFontStyle("font-size:16; font-weight:bold;");
        headertitle.setIconResource("#{icon}");
        headertitle.setOpaque(true);
        headertitle.setPadding(new java.awt.Insets(2, 7, 2, 5));
        headergroup.add(headertitle, java.awt.BorderLayout.NORTH);

        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideLeft(true);
        xEtchedBorder1.setHideRight(true);
        headertoolbar.setBorder(xEtchedBorder1);
        headertoolbar.setLayout(new java.awt.BorderLayout());

        leftactionpanel.setBorderSeparator(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        xabFormActions.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        xabFormActions.setDepends(new String[] {"entity"});
        xabFormActions.setDynamic(true);
        xabFormActions.setName("formActions"); // NOI18N
        leftactionpanel.add(xabFormActions);

        xActionBar1.setBorder(null);
        xActionBar1.setDepends(new String[] {"entity"});
        xActionBar1.setDynamic(true);
        xActionBar1.setFormName("entityName");
        xActionBar1.setName("extActions"); // NOI18N
        leftactionpanel.add(xActionBar1);

        headertoolbar.add(leftactionpanel, java.awt.BorderLayout.WEST);

        rightactionpanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        rightactionpanel.setShowLeftSeparator(true);

        xabNavActions.setBorder(null);
        xabNavActions.setDepends(new String[] {"entity"});
        xabNavActions.setName("navActions"); // NOI18N
        xabNavActions.setShowCaptions(false);
        rightactionpanel.add(xabNavActions);

        xDropDownList1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/note.png"))); // NOI18N
        xDropDownList1.setContentAreaFilled(false);
        xDropDownList1.setFocusable(false);
        xDropDownList1.setHandler("messagelist");
        xDropDownList1.setHideOnEmptyResult(true);
        xDropDownList1.setMargin(new java.awt.Insets(0, 2, 0, 2));
        xDropDownList1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xDropDownList1ActionPerformed(evt);
            }
        });
        rightactionpanel.add(xDropDownList1);

        headertoolbar.add(rightactionpanel, java.awt.BorderLayout.EAST);

        headergroup.add(headertoolbar, java.awt.BorderLayout.SOUTH);

        add(headergroup, java.awt.BorderLayout.NORTH);

        stylerule.setName("styleRules"); // NOI18N
        add(stylerule, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void xDropDownList1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xDropDownList1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xDropDownList1ActionPerformed
    
    // <editor-fold defaultstate="collapsed" desc=" HeaderLayout ">

    private class HeaderLayout implements LayoutManager, LayoutManager2 
    {
        public void addLayoutComponent(String name, Component comp) {}        
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public void layoutContainer(Container parent) {
            layoutContainerImpl(parent);            
        }

        public void addLayoutComponent(Component comp, Object constraints) {}

        public Dimension maximumLayoutSize(Container target) {
            return getLayoutSize(target);
        }

        public float getLayoutAlignmentX(Container target) { 
            return 0.0f;
        }

        public float getLayoutAlignmentY(Container target) {
            return 0.0f;
        }

        public void invalidateLayout(Container target) {
            layoutContainerImpl(target);
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    w = Math.max(dim.width, w);
                    h += dim.height;
                }
                if (w > 0 || h > 0) {
                    Insets margin = parent.getInsets();
                    w += (margin.left + margin.right);
                    h += (margin.top + margin.bottom);
                }
                return new Dimension(w, h);
            }
        }

        private void layoutContainerImpl(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pwidth = parent.getWidth();
                int pheight = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pwidth - (margin.left + margin.right);
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, w, dim.height);
                    y += dim.height;
                }
            } 
        }
    }    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout ">

    private class ToolbarLayout implements LayoutManager, LayoutManager2 
    {
        private Component left;
        private Component right;
                
        public void addLayoutComponent(String name, Component comp) {
            synchronized (comp.getTreeLock()) {
                if ("LEFT".equals(name)) {
                    left = comp;
                } else if ("RIGHT".equals(name)) {
                    right = comp;
                }
            }
        }        
        public void removeLayoutComponent(Component comp) {
            synchronized (comp.getTreeLock()) {
                if (comp == left) {
                    left = null;
                } else if (comp == right) {
                    right = null;
                }
            } 
        }

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public void layoutContainer(Container parent) {
            layoutContainerImpl(parent);            
        }

        public void addLayoutComponent(Component comp, Object constraints) {
            synchronized (comp.getTreeLock()) {
                if ("LEFT".equals(constraints)) {
                    left = comp;
                } else if ("RIGHT".equals(constraints)) {
                    right = comp;
                }
            } 
        }

        public Dimension maximumLayoutSize(Container target) {
            return getLayoutSize(target);
        }

        public float getLayoutAlignmentX(Container target) { 
            return 0.0f;
        }

        public float getLayoutAlignmentY(Container target) {
            return 0.0f;
        }

        public void invalidateLayout(Container target) {
            layoutContainerImpl(target);
        }

        private Component[] resolveComponents() {
            Component[] comps = new Component[]{left, right};
            Component[] results = new Component[comps.length];            
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (c == null || !c.isVisible()) continue;
                if (c instanceof LayoutComponent) { 
                    LayoutComponent lc = (LayoutComponent)c; 
                    if (!lc.isVisibleInLayout()) continue;
                } 
                results[i] = c;
            } 
            return results;
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = resolveComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (c == null) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    w += dim.width;
                    h = Math.max(dim.height, h);
                }
                if (w > 0 || h > 0) {
                    Insets margin = parent.getInsets();
                    w += (margin.left + margin.right);
                    h += (margin.top + margin.bottom);
                }
                return new Dimension(w, h);
            }
        }

        private void layoutContainerImpl(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pwidth = parent.getWidth();
                int pheight = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pwidth - (margin.left + margin.right);
                int h = pheight - (margin.top + margin.bottom);
                Component[] comps = resolveComponents();
                if (comps[0] != null) {
                    //display left component
                    Dimension dim = comps[0].getPreferredSize();
                    comps[0].setBounds(x, y, dim.width, h);
                    x += dim.width;
                }
                if (comps[1] != null) {
                    //display right component
                    Dimension dim = comps[1].getPreferredSize();
                    int px = pwidth-margin.right-dim.width;
                    if (px > x) x = px;
                    
                    comps[1].setBounds(x, y, dim.width, h);
                } 
            } 
        }
    }    

    // </editor-fold>
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel headergroup;
    private com.rameses.rcp.control.XLabel headertitle;
    private javax.swing.JPanel headertoolbar;
    private com.rameses.rcp.control.XHorizontalPanel leftactionpanel;
    private com.rameses.rcp.control.XHorizontalPanel rightactionpanel;
    private com.rameses.rcp.control.XStyleRule stylerule;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XDropDownList xDropDownList1;
    private com.rameses.rcp.control.XActionBar xabFormActions;
    private com.rameses.rcp.control.XActionBar xabNavActions;
    // End of variables declaration//GEN-END:variables
    
}
