/*
 * RuleConstraintDecimalHandler.java
 *
 * Created on September 30, 2013, 9:12 AM
 */

package com.rameses.rulemgmt.action;

import com.rameses.rcp.ui.annotations.StyleSheet;

/**
 *
 * @author  Elmo
 */
@StyleSheet
public class VarHandler extends javax.swing.JPanel {
    
    /** Creates new form RuleConstraintDecimalHandler */
    public VarHandler() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        xComboBox2 = new com.rameses.rcp.control.XComboBox();

        xComboBox2.setDynamic(true);
        xComboBox2.setExpression("#{item.name}");
        xComboBox2.setItems("varList");
        xComboBox2.setName("actionParam.var");
        xComboBox2.setPreferredSize(new java.awt.Dimension(150, 22));
        xComboBox2.setShowCaption(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(xComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(186, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XComboBox xComboBox2;
    // End of variables declaration//GEN-END:variables
    
}