package com.rameses.osiris2.reports.templates;

import javax.swing.JPanel;


public class ReportViewPage extends JPanel {
    
    public ReportViewPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        xReportPanel1 = new com.rameses.osiris2.reports.ui.XReportPanel();

        setPreferredSize(new java.awt.Dimension(449, 261));
        setLayout(new java.awt.BorderLayout());

        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideLeft(true);
        xEtchedBorder1.setHideRight(true);
        jPanel2.setBorder(xEtchedBorder1);
        jPanel2.setLayout(new java.awt.BorderLayout());

        xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar1.setDynamic(true);
        xActionBar1.setName("reportActions"); // NOI18N
        jPanel2.add(xActionBar1, java.awt.BorderLayout.WEST);

        xSubFormPanel1.setHandler("queryForm");
        xSubFormPanel1.setName(""); // NOI18N

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

        jPanel2.add(xSubFormPanel1, java.awt.BorderLayout.EAST);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        xReportPanel1.setName("report"); // NOI18N
        add(xReportPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.osiris2.reports.ui.XReportPanel xReportPanel1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables
    
}
