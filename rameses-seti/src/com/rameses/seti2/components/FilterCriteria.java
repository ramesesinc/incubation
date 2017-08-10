/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.components;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.control.XComponentPanel;
import com.rameses.rcp.ui.annotations.ComponentBean;


/**
 *
 * @author Elmo Nazareno
 */
@ComponentBean("com.rameses.seti2.components.FilterCriteriaComponent")
public class FilterCriteria extends XComponentPanel {

    private String handler;
    
    /**
     * Creates new form FilterComponent
     */
    public FilterCriteria() {
        initComponents();
    }

    @Override
    public void afterLoad() {
        com.rameses.rcp.common.ComponentBean cb = (com.rameses.rcp.common.ComponentBean)getComponentBean();
        if(getHandler()==null)
            System.out.println("Error in FilterCriteria. Please provide a handler that extends FilterCriteriaModel");
        if( getHandler()!=null ) {
            cb.setProperty("handler", getProperty(getHandler()));
        }
        try {
            MethodResolver mr = MethodResolver.getInstance();
            mr.invoke(cb, "init", null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xButton2 = new com.rameses.rcp.control.XButton();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(572, 30));

        xButton2.setImmediate(true);
        xButton2.setName("clear"); // NOI18N
        xButton2.setText("Clear");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 437, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        xFormPanel3.setDynamic(true);
        xFormPanel3.setName("formControls"); // NOI18N
        xFormPanel3.setShowCaption(false);
        add(xFormPanel3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the handler
     */
    public String getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }

    //@Override
    //protected void refreshItems() {
        //super.refreshItems();
    //}

}
