/*
 * ExplorerViewListPage2.java
 *
 * Created on April 24, 2013, 12:44 PM
 */

package com.rameses.osiris2.themes; 

/**
 *
 * @author  wflores
 */
public class ExplorerViewListPage extends javax.swing.JPanel {
    
    public ExplorerViewListPage() {
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
        jPanel3 = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        jPanel1 = new javax.swing.JPanel();
        xActionBar2 = new com.rameses.rcp.control.XActionBar();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        pnlBody = new javax.swing.JPanel();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        xHorizontalPanel2 = new com.rameses.rcp.control.XHorizontalPanel();
        navBar1 = new com.rameses.rcp.control.XActionBar();
        jPanel4 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jPanel5 = new javax.swing.JPanel();
        xLabel2 = new com.rameses.rcp.control.XLabel();

        setLayout(new java.awt.BorderLayout());

        pnlHeader.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideBottom(true);
        xEtchedBorder1.setHideRight(true);
        jPanel3.setBorder(xEtchedBorder1);
        jPanel3.setPreferredSize(new java.awt.Dimension(154, 30));
        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setExpression("#{listHandler.title}");
        lblTitle.setFontStyle("font-size: 14; font-weight:bold;");
        lblTitle.setName("nodechange");
        lblTitle.setOpaque(true);
        lblTitle.setPadding(new java.awt.Insets(2, 7, 2, 5));
        jPanel3.add(lblTitle, java.awt.BorderLayout.WEST);

        xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar1.setButtonBorderPainted(false);
        xActionBar1.setButtonContentAreaFilled(false);
        xActionBar1.setDynamic(true);
        xActionBar1.setName("listHandler.nodeActions");
        xActionBar1.setOpaque(false);
        xActionBar1.setShowCaptions(false);
        xActionBar1.setUseToolBar(false);
        jPanel3.add(xActionBar1, java.awt.BorderLayout.CENTER);

        pnlHeader.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder2 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder2.setHideRight(true);
        jPanel1.setBorder(xEtchedBorder2);
        xActionBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar2.setDepends(new String[] {"listHandler.selectedEntity", "nodechange"});
        xActionBar2.setDynamic(true);
        xActionBar2.setName("listHandler.formActions");
        jPanel1.add(xActionBar2, java.awt.BorderLayout.WEST);

        xSubFormPanel1.setDepends(new String[] {"nodechange"});
        xSubFormPanel1.setDynamic(true);
        xSubFormPanel1.setHandler("queryForm");
        xSubFormPanel1.setName("queryForm");
        xSubFormPanel1.setVisibleWhen("#{queryFormVisible == true}");
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

        pnlHeader.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlBody.setLayout(new java.awt.BorderLayout());

        pnlBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 0, 2));
        xDataTable1.setHandler("listHandler");
        xDataTable1.setImmediate(true);
        xDataTable1.setName("listHandler.selectedEntity");
        pnlBody.add(xDataTable1, java.awt.BorderLayout.CENTER);

        add(pnlBody, java.awt.BorderLayout.CENTER);

        navBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 25));
        navBar1.setName("listHandler.navActions");
        xHorizontalPanel2.add(navBar1);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        xLabel1.setDepends(new String[] {"listHandler.selectedEntity"});
        xLabel1.setExpression("#{listHandler.recordCountInfo}");
        xLabel1.setUseHtml(true);
        jPanel4.add(xLabel1, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        xLabel2.setDepends(new String[] {"listHandler.selectedEntity"});
        xLabel2.setExpression("#{listHandler.pageCountInfo}");
        xLabel2.setUseHtml(true);
        jPanel5.add(xLabel2, java.awt.BorderLayout.CENTER);

        xHorizontalPanel2.add(jPanel5);

        add(xHorizontalPanel2, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private com.rameses.rcp.control.XLabel lblTitle;
    private com.rameses.rcp.control.XActionBar navBar1;
    private javax.swing.JPanel pnlBody;
    private javax.swing.JPanel pnlHeader;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XActionBar xActionBar2;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XHorizontalPanel xHorizontalPanel2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables
    
}
