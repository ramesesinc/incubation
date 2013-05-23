/*
 * FormPage.java
 *
 * Created on April 24, 2013, 12:44 PM
 */

package com.rameses.osiris2.themes;

import com.rameses.rcp.control.border.XToolbarBorder;
import java.awt.Font;

/**
 *
 * @author  wflores
 */
public class FormPage extends javax.swing.JPanel {
    
    public FormPage() {
        initComponents();
        lblTitle.setFont(Font.decode("-bold-18"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        xHorizontalPanel1 = new com.rameses.rcp.control.XHorizontalPanel();
        xabFormActions = new com.rameses.rcp.control.XActionBar();
        xabNavActions = new com.rameses.rcp.control.XActionBar();
        xStyleRule1 = new com.rameses.rcp.control.XStyleRule();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblTitle.setExpression("#{title}");
        lblTitle.setOpaque(true);
        jPanel1.add(lblTitle, java.awt.BorderLayout.NORTH);

        xHorizontalPanel1.setBorder(new XToolbarBorder());
        xabFormActions.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        xabFormActions.setName("formActions");
        xabFormActions.setShowCaptions(false);
        xHorizontalPanel1.add(xabFormActions);

        xabNavActions.setBorder(null);
        xabNavActions.setName("navActions");
        xabNavActions.setShowCaptions(false);
        xHorizontalPanel1.add(xabNavActions);

        jPanel1.add(xHorizontalPanel1, java.awt.BorderLayout.SOUTH);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        xStyleRule1.setName("styleRules");
        add(xStyleRule1, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private com.rameses.rcp.control.XLabel lblTitle;
    private com.rameses.rcp.control.XHorizontalPanel xHorizontalPanel1;
    private com.rameses.rcp.control.XStyleRule xStyleRule1;
    private com.rameses.rcp.control.XActionBar xabFormActions;
    private com.rameses.rcp.control.XActionBar xabNavActions;
    // End of variables declaration//GEN-END:variables
    
}
