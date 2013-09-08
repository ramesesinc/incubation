/*
 * InboxSearchPage.java
 *
 * Created on April 24, 2013, 2:13 PM
 */

package com.rameses.osiris2.themes;

/**
 *
 * @author  wflores
 */
public class InboxSearchPage extends javax.swing.JPanel {
    
    public InboxSearchPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        defaultLabel1 = new com.rameses.rcp.control.text.DefaultLabel();
        xActionTextField1 = new com.rameses.rcp.control.XActionTextField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));
        defaultLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        defaultLabel1.setDisplayedMnemonic('s');
        defaultLabel1.setForeground(new java.awt.Color(80, 80, 80));
        defaultLabel1.setText("Search");
        defaultLabel1.setFontStyle("font-weight:bold;");

        xActionTextField1.setActionName("search");
        xActionTextField1.setCaption("Search");
        xActionTextField1.setFocusAccelerator('s');
        xActionTextField1.setFocusKeyStroke("F3");
        xActionTextField1.setName("query.searchtext");
        xActionTextField1.setPreferredSize(new java.awt.Dimension(250, 22));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(defaultLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(xActionTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 187, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(defaultLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(xActionTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.text.DefaultLabel defaultLabel1;
    private com.rameses.rcp.control.XActionTextField xActionTextField1;
    // End of variables declaration//GEN-END:variables
    
}
