/*
 * ExplorerViewListPage.java
 *
 * Created on April 24, 2013, 12:44 PM
 */

package com.rameses.osiris2.themes;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  wflores
 */
public class ExplorerViewListPage extends javax.swing.JPanel {
    
    public ExplorerViewListPage() {
        initComponents();
        lblTitle.setFont(Font.decode("-bold-18"));
        
        Map props = new HashMap();
        props.put("showTopBorder", true); 
        props.put("showBottomBorder", false); 
        navBar.putClientProperty("Border.properties", props);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        jPanel4 = new javax.swing.JPanel();
        xActionBar2 = new com.rameses.rcp.control.XActionBar();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        navBar = new com.rameses.rcp.control.XActionBar();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        xSubFormPanel1.setHandler("queryForm");
        org.jdesktop.layout.GroupLayout xSubFormPanel1Layout = new org.jdesktop.layout.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 587, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 20, Short.MAX_VALUE)
        );
        jPanel1.add(xSubFormPanel1, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setExpression("#{title}");
        lblTitle.setOpaque(true);
        lblTitle.setPadding(new java.awt.Insets(5, 7, 5, 5));
        jPanel3.add(lblTitle, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(null);

        jPanel4.setOpaque(false);
        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        xActionBar2.setDepends(new String[] {"selectedEntity"});
        xActionBar2.setDynamic(true);
        xActionBar2.setName("formActions");
        jPanel2.add(xActionBar2, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        xDataTable1.setHandler("listHandler");
        xDataTable1.setImmediate(true);
        xDataTable1.setName("selectedEntity");
        add(xDataTable1, java.awt.BorderLayout.CENTER);

        navBar.setName("navActions");
        add(navBar, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private com.rameses.rcp.control.XLabel lblTitle;
    private com.rameses.rcp.control.XActionBar navBar;
    private com.rameses.rcp.control.XActionBar xActionBar2;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables
    
}
