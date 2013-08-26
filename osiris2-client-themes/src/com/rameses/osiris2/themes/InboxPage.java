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
public class InboxPage extends javax.swing.JPanel {
    
    public InboxPage() {
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
        jPanel2 = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        xHorizontalPanel1 = new com.rameses.rcp.control.XHorizontalPanel();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        xActionBar2 = new com.rameses.rcp.control.XActionBar();
        pnlBody = new javax.swing.JPanel();
        xSplitView1 = new com.rameses.rcp.control.XSplitView();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTree2 = new com.rameses.rcp.control.XTree();
        jPanel1 = new javax.swing.JPanel();
        lblTitle1 = new com.rameses.rcp.control.XLabel();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        xHorizontalPanel2 = new com.rameses.rcp.control.XHorizontalPanel();
        navBar = new com.rameses.rcp.control.XActionBar();
        jPanel3 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();

        setLayout(new java.awt.BorderLayout());

        pnlHeader.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setExpression("#{title}");
        lblTitle.setFontStyle("font-size:16; font-weight:bold;");
        lblTitle.setOpaque(true);
        lblTitle.setPadding(new java.awt.Insets(2, 7, 2, 5));
        jPanel2.add(lblTitle, java.awt.BorderLayout.NORTH);

        pnlHeader.add(jPanel2, java.awt.BorderLayout.NORTH);

        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideLeft(true);
        xEtchedBorder1.setHideRight(true);
        xHorizontalPanel1.setBorder(xEtchedBorder1);
        xHorizontalPanel1.setBorderSeparator(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xSubFormPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        xSubFormPanel1.setHandler("queryForm");
        org.jdesktop.layout.GroupLayout xSubFormPanel1Layout = new org.jdesktop.layout.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        xHorizontalPanel1.add(xSubFormPanel1);

        xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar1.setDepends(new String[] {"selectedEntity"});
        xActionBar1.setName("defaultFormActions");
        xHorizontalPanel1.add(xActionBar1);

        xActionBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar2.setDepends(new String[] {"selectedEntity"});
        xActionBar2.setDynamic(true);
        xActionBar2.setFormName("formName");
        xActionBar2.setName("formActions");
        xHorizontalPanel1.add(xActionBar2);

        pnlHeader.add(xHorizontalPanel1, java.awt.BorderLayout.SOUTH);

        add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlBody.setLayout(new java.awt.BorderLayout());

        pnlBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        xSplitView1.setDividerLocation(200);
        jScrollPane1.setName("leftview");
        xTree2.setHandler("nodeModel");
        xTree2.setName("selectedNode");
        jScrollPane1.setViewportView(xTree2);

        xSplitView1.add(jScrollPane1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lblTitle1.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle1.setBorder(new com.rameses.rcp.control.border.XEtchedBorder());
        lblTitle1.setDepends(new String[] {"selectedNode"});
        lblTitle1.setExpression("#{listTitle}");
        lblTitle1.setFontStyle("font-size:16; font-weight:bold;");
        lblTitle1.setOpaque(true);
        lblTitle1.setPadding(new java.awt.Insets(2, 7, 2, 5));
        jPanel1.add(lblTitle1, java.awt.BorderLayout.NORTH);

        xDataTable1.setHandler("listHandler");
        xDataTable1.setImmediate(true);
        xDataTable1.setName("selectedEntity");
        xDataTable1.setVarStatus("itemStat");
        jPanel1.add(xDataTable1, java.awt.BorderLayout.CENTER);

        navBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 50));
        navBar.setName("navActions");
        xHorizontalPanel2.add(navBar);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        xLabel1.setDepends(new String[] {"selectedEntity"});
        xLabel1.setExpression("#{footerInfo}");
        xLabel1.setUseHtml(true);
        jPanel3.add(xLabel1, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel3);

        jPanel1.add(xHorizontalPanel2, java.awt.BorderLayout.SOUTH);

        xSplitView1.add(jPanel1);

        pnlBody.add(xSplitView1, java.awt.BorderLayout.CENTER);

        add(pnlBody, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XLabel lblTitle;
    private com.rameses.rcp.control.XLabel lblTitle1;
    private com.rameses.rcp.control.XActionBar navBar;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlHeader;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XActionBar xActionBar2;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XHorizontalPanel xHorizontalPanel1;
    private com.rameses.rcp.control.XHorizontalPanel xHorizontalPanel2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XSplitView xSplitView1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    private com.rameses.rcp.control.XTree xTree2;
    // End of variables declaration//GEN-END:variables
    
}
