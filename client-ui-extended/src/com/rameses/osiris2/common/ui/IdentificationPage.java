/*
 * IndividualEntityPage.java
 *
 * Created on August 14, 2013, 2:17 PM
 */

package com.rameses.osiris2.common.ui;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  wflores
 */
@Template(OKCancelPage.class)
public class IdentificationPage extends javax.swing.JPanel {
    
    public IdentificationPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xTextField6 = new com.rameses.rcp.control.XTextField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xDateField2 = new com.rameses.rcp.control.XDateField();

        xComboBox1.setCaption("ID Types");
        xComboBox1.setCaptionWidth(100);
        xComboBox1.setExpression("#{item.value}");
        xComboBox1.setItemKey("key");
        xComboBox1.setItems("idTypes");
        xComboBox1.setName("item.idtype");
        xComboBox1.setPreferredSize(new java.awt.Dimension(150, 22));
        xComboBox1.setRequired(true);
        xFormPanel1.add(xComboBox1);

        xTextField6.setCaption("ID No");
        xTextField6.setCaptionWidth(100);
        xTextField6.setName("item.idno");
        xTextField6.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField6.setRequired(true);
        xFormPanel1.add(xTextField6);

        xDateField1.setCaption("Date Issued");
        xDateField1.setCaptionWidth(100);
        xDateField1.setName("item.dtissued");
        xDateField1.setPreferredSize(new java.awt.Dimension(100, 19));
        xDateField1.setRequired(true);
        xFormPanel1.add(xDateField1);

        xDateField2.setCaption("Date Expiry");
        xDateField2.setCaptionWidth(100);
        xDateField2.setName("item.dtexpiry");
        xDateField2.setPreferredSize(new java.awt.Dimension(100, 19));
        xDateField2.setRequired(true);
        xFormPanel1.add(xDateField2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(xFormPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 401, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(xFormPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XTextField xTextField6;
    // End of variables declaration//GEN-END:variables
    
}
