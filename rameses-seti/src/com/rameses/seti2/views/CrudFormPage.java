/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.views;

/**
 *
 * @author dell
 */
public class CrudFormPage extends javax.swing.JPanel {

    /**
     * Creates new form CrudFormPage
     */
    public CrudFormPage() {
        initComponents();
        btnSave.setToolTipText("Save");
        btnSave.setAccelerator("ctrl S");
        btnCreate.setToolTipText("New");
        btnCreate.setAccelerator("ctrl N");
        btnEdit.setToolTipText("Edit");
        btnEdit.setAccelerator("ctrl E");
        btnUndo.setToolTipText("Undo");
        btnUndo.setAccelerator("ctrl U");
        btnCancel.setToolTipText("Cancel Edit");
        btnCancel.setAccelerator("ctrl C");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xStyleRule1 = new com.rameses.rcp.control.XStyleRule();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jToolBar3 = new javax.swing.JToolBar();
        xButton1 = new com.rameses.rcp.control.XButton();
        xButton2 = new com.rameses.rcp.control.XButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnCancel1 = new com.rameses.rcp.control.XButton();
        btnCreate = new com.rameses.rcp.control.XButton();
        btnEdit = new com.rameses.rcp.control.XButton();
        btnSave = new com.rameses.rcp.control.XButton();
        btnUndo = new com.rameses.rcp.control.XButton();
        btnCancel = new com.rameses.rcp.control.XButton();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();

        setLayout(new java.awt.BorderLayout());

        xStyleRule1.setName("styleRules"); // NOI18N

        javax.swing.GroupLayout xStyleRule1Layout = new javax.swing.GroupLayout(xStyleRule1);
        xStyleRule1.setLayout(xStyleRule1Layout);
        xStyleRule1Layout.setHorizontalGroup(
            xStyleRule1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        xStyleRule1Layout.setVerticalGroup(
            xStyleRule1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        add(xStyleRule1, java.awt.BorderLayout.LINE_START);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setPreferredSize(new java.awt.Dimension(420, 60));
        jPanel2.setLayout(new com.rameses.rcp.control.layout.YLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(420, 35));

        xLabel1.setBackground(new java.awt.Color(255, 255, 255));
        xLabel1.setExpression("#{title}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        xLabel1.setOpaque(true);
        xLabel1.setPreferredSize(new java.awt.Dimension(41, 30));

        jToolBar3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToolBar3.setRollover(true);
        jToolBar3.setOpaque(false);

        xButton1.setBackground(new java.awt.Color(255, 255, 255));
        xButton1.setCaption("\\");
            xButton1.setFocusable(false);
            xButton1.setIconResource("images/help.png");
            xButton1.setName("showHelp"); // NOI18N
            xButton1.setOpaque(true);
            jToolBar3.add(xButton1);

            xButton2.setBackground(new java.awt.Color(255, 255, 255));
            xButton2.setCaption("");
            xButton2.setFocusable(false);
            xButton2.setIconResource("images/info.png");
            xButton2.setName("showInfo"); // NOI18N
            xButton2.setOpaque(true);
            jToolBar3.add(xButton2);

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(xLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(xLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );

            jPanel2.add(jPanel3);

            jToolBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
            jToolBar1.setRollover(true);
            jToolBar1.setPreferredSize(new java.awt.Dimension(100, 30));

            btnCancel1.setCaption("");
            btnCancel1.setFocusable(false);
            btnCancel1.setIconResource("images/menu.png");
            btnCancel1.setImmediate(true);
            btnCancel1.setName("showMenu"); // NOI18N
            jToolBar1.add(btnCancel1);

            btnCreate.setCaption("");
            btnCreate.setFocusable(false);
            btnCreate.setIconResource("images/toolbars/create.png");
            btnCreate.setName("create"); // NOI18N
            btnCreate.setVisibleWhen("#{mode=='read'}");
            jToolBar1.add(btnCreate);

            btnEdit.setCaption("");
            btnEdit.setFocusable(false);
            btnEdit.setIconResource("images/toolbars/edit.png");
            btnEdit.setName("edit"); // NOI18N
            btnEdit.setVisibleWhen("#{mode=='read'}");
            jToolBar1.add(btnEdit);

            btnSave.setCaption("");
            btnSave.setFocusable(false);
            btnSave.setIconResource("images/toolbars/save.png");
            btnSave.setName("save"); // NOI18N
            btnSave.setVisibleWhen("#{mode!='read'}");
            jToolBar1.add(btnSave);

            btnUndo.setCaption("");
            btnUndo.setFocusable(false);
            btnUndo.setIconResource("images/toolbars/undo.png");
            btnUndo.setImmediate(true);
            btnUndo.setName("undo"); // NOI18N
            btnUndo.setVisibleWhen("#{mode!='read'}");
            jToolBar1.add(btnUndo);

            btnCancel.setCaption("");
            btnCancel.setFocusable(false);
            btnCancel.setIconResource("images/toolbars/cancel.png");
            btnCancel.setImmediate(true);
            btnCancel.setName("unedit"); // NOI18N
            btnCancel.setVisibleWhen("#{mode=='edit'}");
            jToolBar1.add(btnCancel);

            xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
            xActionBar1.setName("extActions"); // NOI18N
            jToolBar1.add(xActionBar1);

            jPanel2.add(jToolBar1);

            add(jPanel2, java.awt.BorderLayout.NORTH);
        }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XButton btnCancel;
    private com.rameses.rcp.control.XButton btnCancel1;
    private com.rameses.rcp.control.XButton btnCreate;
    private com.rameses.rcp.control.XButton btnEdit;
    private com.rameses.rcp.control.XButton btnSave;
    private com.rameses.rcp.control.XButton btnUndo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XStyleRule xStyleRule1;
    // End of variables declaration//GEN-END:variables
}
