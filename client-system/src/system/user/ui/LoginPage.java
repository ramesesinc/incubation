/*
 * LoginPage.java
 *
 * Created on March 31, 2012, 11:49 AM
 */

package system.user.ui;

import com.rameses.rcp.ui.annotations.Template;
import system.template.ui.TemplatePage;

/**
 *
 * @author  JAYROME VERGARA
 */
//@Template(TemplatePage.class)
public class LoginPage extends javax.swing.JPanel {
    
    /**
     * Creates new form LoginPage
     */
    public LoginPage() {
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
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jLabel2 = new javax.swing.JLabel();
        formPanel1 = new com.rameses.rcp.util.FormPanel();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xPasswordField1 = new com.rameses.rcp.control.XPasswordField();
        xTextField2 = new com.rameses.rcp.control.XTextField();
        xCheckBox1 = new com.rameses.rcp.control.XCheckBox();
        xButton1 = new com.rameses.rcp.control.XButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel1.setLayout(new com.rameses.rcp.control.layout.YLayout());

        xLabel1.setExpression("Login");
        xLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        xLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 25, 0));
        xLabel1.setFontStyle("font-size:18;");
        xLabel1.setUseHtml(true);
        jPanel1.add(xLabel1);

        jLabel2.setText("<html>Please enter your user name and password.</html>");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 25, 0));
        jPanel1.add(jLabel2);

        formPanel1.setCaptionVAlignment(com.rameses.rcp.constant.UIConstants.CENTER);
        formPanel1.setCaptionWidth(120);
        formPanel1.setPadding(new java.awt.Insets(0, 5, 0, 0));

        xTextField1.setCaption("Username");
        xTextField1.setName("uid"); // NOI18N
        xTextField1.setCaptionMnemonic('u');
        xTextField1.setIndex(-10);
        xTextField1.setPreferredSize(new java.awt.Dimension(210, 24));
        xTextField1.setRequired(true);
        xTextField1.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        formPanel1.add(xTextField1);

        xPasswordField1.setCaption("Password");
        xPasswordField1.setName("pwd"); // NOI18N
        xPasswordField1.setText("xPasswordField1");
        xPasswordField1.setForeground(new java.awt.Color(102, 102, 102));
        xPasswordField1.setPreferredSize(new java.awt.Dimension(210, 24));
        xPasswordField1.setRequired(true);
        formPanel1.add(xPasswordField1);

        xTextField2.setCaption("Client Code");
        xTextField2.setName("clientcode"); // NOI18N
        xTextField2.setPreferredSize(new java.awt.Dimension(120, 24));
        formPanel1.add(xTextField2);

        xCheckBox1.setCaption("");
        xCheckBox1.setName("offline"); // NOI18N
        xCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xCheckBox1.setCellPadding(new java.awt.Insets(10, 0, 10, 0));
        xCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        xCheckBox1.setReadonly(true);
        xCheckBox1.setText("Work Offline");
        formPanel1.add(xCheckBox1);

        xButton1.setCaption(" ");
        xButton1.setMnemonic('l');
        xButton1.setName("login"); // NOI18N
        xButton1.setCellPadding(new java.awt.Insets(20, 0, 20, 0));
        xButton1.setDefaultCommand(true);
        xButton1.setMargin(new java.awt.Insets(5, 14, 5, 14));
        xButton1.setText("  Login  ");
        formPanel1.add(xButton1);

        jPanel1.add(formPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 434, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.util.FormPanel formPanel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XCheckBox xCheckBox1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XPasswordField xPasswordField1;
    private com.rameses.rcp.control.XTextField xTextField1;
    private com.rameses.rcp.control.XTextField xTextField2;
    // End of variables declaration//GEN-END:variables
    
}
