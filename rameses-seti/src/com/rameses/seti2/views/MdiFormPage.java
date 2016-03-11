/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.views;

import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author dell
 */
@Template(CrudFormPage.class)
public class MdiFormPage extends javax.swing.JPanel {

    /**
     * Creates new form MdiFormPage
     */
    public MdiFormPage() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xList1 = new com.rameses.rcp.control.XList();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(180);

        xSubFormPanel1.setDepends(new String[] {"selectedSection"});
        xSubFormPanel1.setDynamic(true);
        xSubFormPanel1.setHandler("selectedSection");
        xSubFormPanel1.setName("selectedSection"); // NOI18N

        javax.swing.GroupLayout xSubFormPanel1Layout = new javax.swing.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 214, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(xSubFormPanel1);

        xList1.setExpression("#{item.caption}");
        xList1.setItems("sections");
        xList1.setName("selectedSection"); // NOI18N
        xList1.setPreferredSize(new java.awt.Dimension(100, 100));
        jScrollPane1.setViewportView(xList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private com.rameses.rcp.control.XList xList1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables
}
