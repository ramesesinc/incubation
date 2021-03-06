/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rulemgmt.views;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author dell
 */
@Template(OKCancelPage.class)
public class RulegroupLookupPage extends javax.swing.JPanel {

    /**
     * Creates new form RulegroupLookupPage
     */
    public RulegroupLookupPage() {
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

        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xComboBox2 = new com.rameses.rcp.control.XComboBox();
        xComboBox3 = new com.rameses.rcp.control.XComboBox();

        xFormPanel1.setCaptionVAlignment(com.rameses.rcp.constant.UIConstants.CENTER);

        xComboBox2.setCaption("Ruleset");
        xComboBox2.setExpression("#{item.name}");
        xComboBox2.setItemKey("name");
        xComboBox2.setItems("rulesets");
        xComboBox2.setName("entity.ruleset"); // NOI18N
        xComboBox2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xComboBox2);

        xComboBox3.setCaption("Rule Group");
        xComboBox3.setDepends(new String[] {"entity.ruleset"});
        xComboBox3.setDynamic(true);
        xComboBox3.setExpression("#{item.name}");
        xComboBox3.setItemKey("name");
        xComboBox3.setItems("rulegroups");
        xComboBox3.setName("entity.rulegroup"); // NOI18N
        xComboBox3.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xComboBox3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XComboBox xComboBox2;
    private com.rameses.rcp.control.XComboBox xComboBox3;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    // End of variables declaration//GEN-END:variables
}
