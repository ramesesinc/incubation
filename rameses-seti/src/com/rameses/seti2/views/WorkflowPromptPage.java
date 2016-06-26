/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.views;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author dell
 */
@Template(OKCancelPage.class)
public class WorkflowPromptPage extends javax.swing.JPanel {

    /**
     * Creates new form WorkflowPrompt
     */
    public WorkflowPromptPage() {
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
        xLookupField1 = new com.rameses.rcp.control.XLookupField();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        xLookupField1.setCaption("Assignee");
        xLookupField1.setExpression("#{item.name}");
        xLookupField1.setHandler("lookupAssignees");
        xLookupField1.setName("info.assignee"); // NOI18N
        xLookupField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xLookupField1.setVisibleWhen("#{showAssigneeList==true}");
        xFormPanel1.add(xLookupField1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 100));

        xTextArea1.setCaption("Message");
        xTextArea1.setCellPadding(new java.awt.Insets(10, 0, 0, 0));
        xTextArea1.setName("info.message"); // NOI18N
        xTextArea1.setRequired(true);
        jScrollPane1.setViewportView(xTextArea1);

        xFormPanel1.add(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XLookupField xLookupField1;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    // End of variables declaration//GEN-END:variables
}
