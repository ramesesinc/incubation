/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.jobscheduler;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author dell
 */
@StyleSheet
public class JobSchedulerSMSActionForm extends javax.swing.JPanel {

    /**
     * Creates new form JobSchedulerForm
     */
    public JobSchedulerSMSActionForm() {
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

        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();
        xButton1 = new com.rameses.rcp.control.XButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        xTextArea2 = new com.rameses.rcp.control.XTextArea();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xButton2 = new com.rameses.rcp.control.XButton();
        xButton3 = new com.rameses.rcp.control.XButton();

        xFormPanel1.setCaptionWidth(120);

        xComboBox1.setCaption("Datasource");
        xComboBox1.setItems("datasourceList");
        xComboBox1.setName("entity.adapter"); // NOI18N
        xFormPanel1.add(xComboBox1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 100));

        xTextArea1.setCaption("SQL");
        xTextArea1.setName("entity.sql"); // NOI18N
        xTextArea1.setPreferredSize(new java.awt.Dimension(0, 61));
        jScrollPane1.setViewportView(xTextArea1);

        xFormPanel1.add(jScrollPane1);

        xButton1.setCaption("");
        xButton1.setName("testSQL"); // NOI18N
        xButton1.setText("Test ");
        xFormPanel1.add(xButton1);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(0, 100));
        jScrollPane2.setRequestFocusEnabled(false);

        xTextArea2.setCaption("Message Template");
        xTextArea2.setName("entity.messagetemplate"); // NOI18N
        xTextArea2.setPreferredSize(new java.awt.Dimension(0, 61));
        jScrollPane2.setViewportView(xTextArea2);

        xFormPanel1.add(jScrollPane2);

        xFormPanel2.setCaption("");
        xFormPanel2.setOrientation(com.rameses.rcp.constant.UIConstants.HORIZONTAL);
        xFormPanel2.setPadding(new java.awt.Insets(0, 0, 5, 5));

        xButton2.setCaption("");
        xButton2.setName("viewTemplate"); // NOI18N
        xButton2.setShowCaption(false);
        xButton2.setText("View Sample Template");
        xFormPanel2.add(xButton2);

        xButton3.setCaption("");
        xButton3.setName("sendSMS"); // NOI18N
        xButton3.setShowCaption(false);
        xButton3.setText("Send SMS");
        xFormPanel2.add(xButton3);

        xFormPanel1.add(xFormPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XButton xButton3;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    private com.rameses.rcp.control.XTextArea xTextArea2;
    // End of variables declaration//GEN-END:variables
}
