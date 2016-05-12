/*
 * ButtonExtendedPage.java
 *
 * Created on August 22, 2013, 5:43 PM
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.ButtonColumnHandler;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.LabelColumnHandler;

/**
 *
 * @author  wflores
 */
public class ButtonExtendedPage extends javax.swing.JPanel implements IExtendedPage
{    
    private ButtonColumnHandler typeHandler;
    
    public ButtonExtendedPage() {
        initComponents();
    }
    
    public Column.TypeHandler getTypeHandler() { return typeHandler; }
    public void setTypeHandler(Column.TypeHandler typeHandler) {
        ButtonColumnHandler newhandler = new ButtonColumnHandler(); 
        if ( typeHandler instanceof ButtonColumnHandler ) {
            ButtonColumnHandler old = (ButtonColumnHandler) typeHandler; 
            newhandler.setVisibleWhen( old.getVisibleWhen() ); 
        } 
        this.typeHandler = newhandler; 
    } 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        txtVisibleWhen = new com.rameses.rcp.swingx.TextField();

        jLabel2.setText("Visible When:");

        txtVisibleWhen.setName("visibleWhen"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtVisibleWhen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 297, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtVisibleWhen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private com.rameses.rcp.swingx.TextField txtVisibleWhen;
    // End of variables declaration//GEN-END:variables
    
}
