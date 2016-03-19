/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.views;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.Template;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;


/**
 *
 * @author dell
 */ 
@Template(OKCancelPage.class)
public class CrudLookupPage extends javax.swing.JPanel {

    /**
     * Creates new form CrudFormPage
     */
    public CrudLookupPage() {
        initComponents();
        btnDelete.setToolTipText("Delete");
        btnCreate.setToolTipText("New");
        btnOpen.setToolTipText("Open");
        btnPrint.setToolTipText("Print");
        btnRefresh.setToolTipText("Refresh");
        
        btnFilter.setToolTipText("Filter Criteria");
        btnSelectColumn.setToolTipText("Select Columns");
        sidebarpanel.setLayout(new SideBarLayout());
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" SideBarLayout ">
    
    private class SideBarLayout implements LayoutManager, LayoutManager2 
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Component getVisibleComponent(Component[] comps) {
            Component selected = null;
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (c.isVisible()) selected = c;
            }
            return selected;
        }
        
        public void layoutContainer(Container parent) {
            layoutContainerImpl(parent);            
        }

        public void addLayoutComponent(Component comp, Object constraints) {}

        public Dimension maximumLayoutSize(Container target) {
            return getLayoutSize(target);
        }

        public float getLayoutAlignmentX(Container target) { 
            return 0.5f;
        }

        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        public void invalidateLayout(Container target) {
            layoutContainerImpl(target);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component comp = getVisibleComponent(parent.getComponents()); 
                if (comp != null) {
                    Dimension dim = comp.getPreferredSize();
                    w = dim.width;
                    h = dim.height;
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
                Component comp = getVisibleComponent(parent.getComponents()); 
                if (comp != null) comp.setBounds(x, y, w, h);
            }            
        }
    }    
    
    // </editor-fold>    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jToolBar3 = new javax.swing.JToolBar();
        xButton1 = new com.rameses.rcp.control.XButton();
        xButton2 = new com.rameses.rcp.control.XButton();
        jPanel8 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnCancel1 = new com.rameses.rcp.control.XButton();
        btnCreate = new com.rameses.rcp.control.XButton();
        btnOpen = new com.rameses.rcp.control.XButton();
        btnDelete = new com.rameses.rcp.control.XButton();
        btnPrint = new com.rameses.rcp.control.XButton();
        btnFilter = new com.rameses.rcp.control.XButton();
        btnRefresh = new com.rameses.rcp.control.XButton();
        btnSelectColumn = new com.rameses.rcp.control.XButton();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        jPanel7 = new javax.swing.JPanel();
        xActionTextField1 = new com.rameses.rcp.control.XActionTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        listPanel1 = new com.rameses.seti2.components.ListPanel();
        sidebarpanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 727, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setPreferredSize(new java.awt.Dimension(420, 65));
        jPanel2.setLayout(new com.rameses.rcp.control.layout.YLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(420, 35));

        xLabel1.setBackground(new java.awt.Color(255, 255, 255));
        xLabel1.setExpression("#{title}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        xLabel1.setOpaque(true);
        xLabel1.setPreferredSize(new java.awt.Dimension(41, 30));

        jToolBar3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToolBar3.setRollover(true);
        jToolBar3.setOpaque(false);

        xButton1.setBackground(new java.awt.Color(255, 255, 255));
        xButton1.setCaption("\\");
            xButton1.setFocusable(false);
            xButton1.setIconResource("images/help.png");
            xButton1.setName("showHelp"); // NOI18N
            xButton1.setOpaque(true);
            jToolBar3.add(xButton1);

            xButton2.setBackground(new java.awt.Color(255, 255, 255));
            xButton2.setCaption("");
            xButton2.setFocusable(false);
            xButton2.setIconResource("images/info.png");
            xButton2.setName("showInfo"); // NOI18N
            xButton2.setOpaque(true);
            jToolBar3.add(xButton2);

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(xLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(xLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );

            jPanel2.add(jPanel3);

            jPanel8.setLayout(new java.awt.BorderLayout());

            jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            jToolBar1.setFloatable(false);
            jToolBar1.setRollover(true);

            btnCancel1.setCaption("");
            btnCancel1.setFocusable(false);
            btnCancel1.setIconResource("images/menu.png");
            btnCancel1.setImmediate(true);
            btnCancel1.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnCancel1.setName("showMenu"); // NOI18N
            jToolBar1.add(btnCancel1);

            btnCreate.setAccelerator("ctrl N");
            btnCreate.setCaption("");
            btnCreate.setFocusable(false);
            btnCreate.setIconResource("images/toolbars/create.png");
            btnCreate.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnCreate.setName("create"); // NOI18N
            btnCreate.setVisibleWhen("#{createAllowed}");
            jToolBar1.add(btnCreate);

            btnOpen.setAccelerator("ctrl O");
            btnOpen.setCaption("");
            btnOpen.setFocusable(false);
            btnOpen.setIconResource("images/toolbars/open.png");
            btnOpen.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnOpen.setName("open"); // NOI18N
            btnOpen.setVisibleWhen("#{openAllowed}");
            jToolBar1.add(btnOpen);

            btnDelete.setCaption("");
            btnDelete.setFocusable(false);
            btnDelete.setIconResource("images/toolbars/trash.png");
            btnDelete.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnDelete.setName("removeEntity"); // NOI18N
            btnDelete.setVisibleWhen("#{deleteAllowed}");
            jToolBar1.add(btnDelete);

            btnPrint.setAccelerator("ctrl P");
            btnPrint.setCaption("");
            btnPrint.setFocusable(false);
            btnPrint.setIconResource("images/toolbars/printer.png");
            btnPrint.setImmediate(true);
            btnPrint.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnPrint.setName("print"); // NOI18N
            jToolBar1.add(btnPrint);

            btnFilter.setAccelerator("ctrl F");
            btnFilter.setCaption("");
            btnFilter.setFocusable(false);
            btnFilter.setIconResource("images/toolbars/filter.png");
            btnFilter.setImmediate(true);
            btnFilter.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnFilter.setName("showFilter"); // NOI18N
            jToolBar1.add(btnFilter);

            btnRefresh.setAccelerator("ctrl R");
            btnRefresh.setCaption("");
            btnRefresh.setFocusable(false);
            btnRefresh.setIconResource("images/toolbars/refresh.png");
            btnRefresh.setImmediate(true);
            btnRefresh.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnRefresh.setName("refresh"); // NOI18N
            jToolBar1.add(btnRefresh);

            btnSelectColumn.setCaption("");
            btnSelectColumn.setFocusable(false);
            btnSelectColumn.setIconResource("images/toolbars/table-column.png");
            btnSelectColumn.setImmediate(true);
            btnSelectColumn.setMargin(new java.awt.Insets(1, 1, 1, 1));
            btnSelectColumn.setName("selectColumns"); // NOI18N
            jToolBar1.add(btnSelectColumn);

            xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
            xActionBar1.setName("extActions"); // NOI18N
            jToolBar1.add(xActionBar1);

            jPanel8.add(jToolBar1, java.awt.BorderLayout.WEST);

            jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
            jPanel7.setLayout(new java.awt.BorderLayout());

            xActionTextField1.setActionName("search");
            xActionTextField1.setMaxLength(50);
            xActionTextField1.setName("searchText"); // NOI18N
            xActionTextField1.setPreferredSize(new java.awt.Dimension(180, 20));
            xActionTextField1.setVisibleWhen("#{allowSearch == true}");
            jPanel7.add(xActionTextField1, java.awt.BorderLayout.EAST);

            jPanel8.add(jPanel7, java.awt.BorderLayout.CENTER);

            jPanel2.add(jPanel8);

            add(jPanel2, java.awt.BorderLayout.NORTH);

            jPanel4.setLayout(new java.awt.BorderLayout());

            jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
            jPanel6.setLayout(new java.awt.BorderLayout());

            listPanel1.setName("selectedItem"); // NOI18N
            jPanel6.add(listPanel1, java.awt.BorderLayout.CENTER);

            jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

            sidebarpanel.setName("sidebar"); // NOI18N

            javax.swing.GroupLayout sidebarpanelLayout = new javax.swing.GroupLayout(sidebarpanel);
            sidebarpanel.setLayout(sidebarpanelLayout);
            sidebarpanelLayout.setHorizontalGroup(
                sidebarpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 120, Short.MAX_VALUE)
            );
            sidebarpanelLayout.setVerticalGroup(
                sidebarpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 292, Short.MAX_VALUE)
            );

            jPanel4.add(sidebarpanel, java.awt.BorderLayout.LINE_START);

            add(jPanel4, java.awt.BorderLayout.CENTER);
        }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XButton btnCancel1;
    private com.rameses.rcp.control.XButton btnCreate;
    private com.rameses.rcp.control.XButton btnDelete;
    private com.rameses.rcp.control.XButton btnFilter;
    private com.rameses.rcp.control.XButton btnOpen;
    private com.rameses.rcp.control.XButton btnPrint;
    private com.rameses.rcp.control.XButton btnRefresh;
    private com.rameses.rcp.control.XButton btnSelectColumn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private com.rameses.seti2.components.ListPanel listPanel1;
    private javax.swing.JPanel sidebarpanel;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XActionTextField xActionTextField1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XLabel xLabel1;
    // End of variables declaration//GEN-END:variables
}
