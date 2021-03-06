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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        jPanel5 = new javax.swing.JPanel();
        xActionBar2 = new com.rameses.rcp.control.XActionBar();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
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
        jPanel4 = new javax.swing.JPanel();
        xLabel2 = new com.rameses.rcp.control.XLabel();

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
        jPanel5.setBorder(xEtchedBorder1);
        jPanel5.setLayout(new java.awt.BorderLayout());

        xActionBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar2.setDepends(new String[] {"selectedEntity"});
        xActionBar2.setDynamic(true);
        xActionBar2.setFormName("formName");
        xActionBar2.setName("formActions"); // NOI18N
        jPanel5.add(xActionBar2, java.awt.BorderLayout.WEST);

        xSubFormPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        xSubFormPanel1.setHandler("queryForm");

        org.jdesktop.layout.GroupLayout xSubFormPanel1Layout = new org.jdesktop.layout.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 35, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 25, Short.MAX_VALUE)
        );

        jPanel5.add(xSubFormPanel1, java.awt.BorderLayout.EAST);

        pnlHeader.add(jPanel5, java.awt.BorderLayout.CENTER);

        add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pnlBody.setLayout(new java.awt.BorderLayout());

        xSplitView1.setDividerLocation(200);

        jScrollPane1.setName("leftview"); // NOI18N

        xTree2.setHandler("nodeModel");
        xTree2.setName("selectedNode"); // NOI18N
        jScrollPane1.setViewportView(xTree2);

        xSplitView1.add(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel1.setLayout(new java.awt.BorderLayout());

        lblTitle1.setBackground(new java.awt.Color(160, 160, 160));
        lblTitle1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lblTitle1.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle1.setExpression("#{listTitle}");
        lblTitle1.setFontStyle("font-size:14; font-weight:bold;");
        lblTitle1.setName("selectedOpenNode"); // NOI18N
        lblTitle1.setOpaque(true);
        lblTitle1.setPadding(new java.awt.Insets(2, 7, 2, 5));
        lblTitle1.setVisibleWhen("#{selectedOpenNode != null}");
        jPanel1.add(lblTitle1, java.awt.BorderLayout.NORTH);

        xDataTable1.setHandler("listHandler");
        xDataTable1.setImmediate(true);
        xDataTable1.setName("selectedEntity"); // NOI18N
        xDataTable1.setVarStatus("itemStat");
        jPanel1.add(xDataTable1, java.awt.BorderLayout.CENTER);

        navBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 25));
        navBar.setName("navActions"); // NOI18N
        xHorizontalPanel2.add(navBar);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        jPanel3.setLayout(new java.awt.BorderLayout());

        xLabel1.setDepends(new String[] {"selectedEntity"});
        xLabel1.setExpression("#{recordCountInfo}");
        xLabel1.setUseHtml(true);
        jPanel3.add(xLabel1, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        jPanel4.setLayout(new java.awt.BorderLayout());

        xLabel2.setDepends(new String[] {"selectedEntity"});
        xLabel2.setExpression("#{pageCountInfo}");
        xLabel2.setUseHtml(true);
        jPanel4.add(xLabel2, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel4);

        jPanel1.add(xHorizontalPanel2, java.awt.BorderLayout.SOUTH);

        xSplitView1.add(jPanel1);

        pnlBody.add(xSplitView1, java.awt.BorderLayout.CENTER);

        add(pnlBody, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XLabel lblTitle;
    private com.rameses.rcp.control.XLabel lblTitle1;
    private com.rameses.rcp.control.XActionBar navBar;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlHeader;
    private com.rameses.rcp.control.XActionBar xActionBar2;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XHorizontalPanel xHorizontalPanel2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XSplitView xSplitView1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    private com.rameses.rcp.control.XTree xTree2;
    // End of variables declaration//GEN-END:variables
    
}
