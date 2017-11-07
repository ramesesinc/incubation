/*
 * LookupPage.java
 *
 * Created on April 24, 2013, 12:44 PM
 */

package com.rameses.osiris2.themes;


public class OKCancelPage extends javax.swing.JPanel {
    
    public OKCancelPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        jPanel2 = new javax.swing.JPanel();
        xButton1 = new com.rameses.rcp.control.XButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        xButton2 = new com.rameses.rcp.control.XButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout());

        xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar1.setName("extActions"); // NOI18N
        xActionBar1.setUseToolBar(false);
        jPanel3.add(xActionBar1, java.awt.BorderLayout.NORTH);

        jPanel1.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        xButton1.setText("   OK   ");
        xButton1.setDefaultCommand(true);
        xButton1.setName("doOk"); // NOI18N
        xButton1.setPreferredSize(new java.awt.Dimension(66, 23));
        jPanel2.add(xButton1);
        jPanel2.add(filler1);

        xButton2.setText("Cancel");
        xButton2.setImmediate(true);
        xButton2.setName("doCancel"); // NOI18N
        xButton2.setPreferredSize(new java.awt.Dimension(66, 23));
        jPanel2.add(xButton2);

        jPanel1.add(jPanel2, java.awt.BorderLayout.EAST);

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    // End of variables declaration//GEN-END:variables
    
}
