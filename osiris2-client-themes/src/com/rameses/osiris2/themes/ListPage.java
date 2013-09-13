/*
 * ListPage.java
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
public class ListPage extends javax.swing.JPanel {
    
    public ListPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlHeader = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        jPanel1 = new javax.swing.JPanel();
        xActionBar2 = new com.rameses.rcp.control.XActionBar();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        pnlBody = new javax.swing.JPanel();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        xHorizontalPanel2 = new com.rameses.rcp.control.XHorizontalPanel();
        navBar1 = new com.rameses.rcp.control.XActionBar();
        jPanel3 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jPanel4 = new javax.swing.JPanel();
        xLabel2 = new com.rameses.rcp.control.XLabel();

        setLayout(new java.awt.BorderLayout());

        pnlHeader.setLayout(new java.awt.BorderLayout());

        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setExpression("#{title}");
        lblTitle.setFontStyle("font-weight:bold; font-size:16;");
        lblTitle.setOpaque(true);
        lblTitle.setPadding(new java.awt.Insets(2, 7, 2, 5));
        pnlHeader.add(lblTitle, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideLeft(true);
        xEtchedBorder1.setHideRight(true);
        jPanel1.setBorder(xEtchedBorder1);
        xActionBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar2.setDepends(new String[] {"selectedEntity"});
        xActionBar2.setFormName("formName");
        xActionBar2.setName("formActions");
        jPanel1.add(xActionBar2, java.awt.BorderLayout.WEST);

        xSubFormPanel1.setHandler("queryForm");
        org.jdesktop.layout.GroupLayout xSubFormPanel1Layout = new org.jdesktop.layout.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 40, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 25, Short.MAX_VALUE)
        );
        jPanel1.add(xSubFormPanel1, java.awt.BorderLayout.EAST);

        pnlHeader.add(jPanel1, java.awt.BorderLayout.SOUTH);

        add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlBody.setLayout(new java.awt.BorderLayout());

        pnlBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 0, 2));
        xDataTable1.setHandler("listHandler");
        xDataTable1.setImmediate(true);
        xDataTable1.setName("selectedEntity");
        pnlBody.add(xDataTable1, java.awt.BorderLayout.CENTER);

        add(pnlBody, java.awt.BorderLayout.CENTER);

        navBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 25));
        navBar1.setName("navActions");
        xHorizontalPanel2.add(navBar1);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        xLabel1.setDepends(new String[] {"selectedEntity"});
        xLabel1.setExpression("#{recordCountInfo}");
        xLabel1.setUseHtml(true);
        jPanel3.add(xLabel1, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        xLabel2.setDepends(new String[] {"selectedEntity"});
        xLabel2.setExpression("#{pageCountInfo}");
        xLabel2.setUseHtml(true);
        jPanel4.add(xLabel2, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel4);

        add(xHorizontalPanel2, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private com.rameses.rcp.control.XLabel lblTitle;
    private com.rameses.rcp.control.XActionBar navBar1;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlHeader;
    private com.rameses.rcp.control.XActionBar xActionBar2;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XHorizontalPanel xHorizontalPanel2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables
    
}
