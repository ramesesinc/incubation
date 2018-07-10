/*
 * PreferencePage.java
 *
 * Created on October 29, 2010, 8:39 AM
 */

package system.preferences.ui;

import com.rameses.util.ConfigProperties;

/**
 *
 * @author  ms
 */

public class UpdateCenterPage extends javax.swing.JPanel {
    
    /** Creates new form PreferencePage */
    public UpdateCenterPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        jLabel3 = new javax.swing.JLabel();
        xStyleRule1 = new com.rameses.rcp.control.XStyleRule();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();

        setPreferredSize(new java.awt.Dimension(407, 427));

        jLabel1.setText("Application URL");

        xTextField1.setName("appUrl"); // NOI18N
        xTextField1.setTextCase(com.rameses.rcp.constant.TextCase.NONE);

        jLabel3.setText("<html>Specify the settings for the <b>Module Update Center</b></html>");

        xStyleRule1.setName("styles"); // NOI18N

        org.jdesktop.layout.GroupLayout xStyleRule1Layout = new org.jdesktop.layout.GroupLayout(xStyleRule1);
        xStyleRule1.setLayout(xStyleRule1Layout);
        xStyleRule1Layout.setHorizontalGroup(
            xStyleRule1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 20, Short.MAX_VALUE)
        );
        xStyleRule1Layout.setVerticalGroup(
            xStyleRule1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 20, Short.MAX_VALUE)
        );

        xActionBar1.setName("formActions"); // NOI18N
        xActionBar1.setUseToolBar(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(22, 22, 22)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, xActionBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(xStyleRule1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, xTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(xStyleRule1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(19, 19, 19)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(xTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 297, Short.MAX_VALUE)
                .add(xActionBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XStyleRule xStyleRule1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
    
}
