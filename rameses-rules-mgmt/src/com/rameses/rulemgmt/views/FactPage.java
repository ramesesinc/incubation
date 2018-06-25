/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rulemgmt.views;

import com.rameses.rcp.ui.annotations.Template;
import com.rameses.seti2.views.CrudFormPage;

/**
 *
 * @author dell
 */
@Template(CrudFormPage.class)
public class FactPage extends javax.swing.JPanel {

    /**
     * Creates new form FactPage
     */
    public FactPage() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xTabbedPane1 = new com.rameses.rcp.control.XTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xTextField3 = new com.rameses.rcp.control.XTextField();
        xTextField13 = new com.rameses.rcp.control.XTextField();
        xTextField2 = new com.rameses.rcp.control.XTextField();
        xTextField11 = new com.rameses.rcp.control.XTextField();
        xTextField12 = new com.rameses.rcp.control.XTextField();
        xIntegerField1 = new com.rameses.rcp.control.XIntegerField();
        xTextField4 = new com.rameses.rcp.control.XTextField();
        xTextField5 = new com.rameses.rcp.control.XTextField();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xCheckBox1 = new com.rameses.rcp.control.XCheckBox();
        xTextField6 = new com.rameses.rcp.control.XTextField();
        xTextField7 = new com.rameses.rcp.control.XTextField();
        xTextField8 = new com.rameses.rcp.control.XTextField();
        xTextField9 = new com.rameses.rcp.control.XTextField();
        xTextField10 = new com.rameses.rcp.control.XTextField();
        jPanel1 = new javax.swing.JPanel();
        xButton2 = new com.rameses.rcp.control.XButton();
        xButton3 = new com.rameses.rcp.control.XButton();
        xButton4 = new com.rameses.rcp.control.XButton();
        xButton1 = new com.rameses.rcp.control.XButton();
        xButton5 = new com.rameses.rcp.control.XButton();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        xPanel1 = new com.rameses.rcp.control.XPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xList1 = new com.rameses.rcp.control.XList();
        jLabel1 = new javax.swing.JLabel();

        xFormPanel1.setCaptionWidth(120);

        xLabel1.setCaption("Fact Class");
        xLabel1.setExpression("#{ entity.factclass }");
        xLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        xLabel1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel1);

        xTextField3.setCaption("Fact Class");
        xTextField3.setDisableWhen("");
        xTextField3.setName("entity.factclass"); // NOI18N
        xTextField3.setVisibleWhen("#{ mode == 'create' }");
        xTextField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField3.setRequired(true);
        xTextField3.setSpaceChar('_');
        xTextField3.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField3);

        xTextField13.setCaption("Fact Super Class");
        xTextField13.setName("entity.factsuperclass"); // NOI18N
        xTextField13.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField13.setSpaceChar('_');
        xTextField13.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField13);

        xTextField2.setCaption("Title");
        xTextField2.setName("entity.title"); // NOI18N
        xTextField2.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField2.setRequired(true);
        xTextField2.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField2);

        xTextField11.setCaption("Built-in Constraints");
        xTextField11.setName("entity.builtinconstraints"); // NOI18N
        xTextField11.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField11.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField11);

        xTextField12.setCaption("Domain");
        xTextField12.setName("entity.domain"); // NOI18N
        xTextField12.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField12.setRequired(true);
        xTextField12.setSpaceChar('_');
        xFormPanel1.add(xTextField12);

        xIntegerField1.setCaption("Sort Order");
        xIntegerField1.setName("entity.sortorder"); // NOI18N
        xFormPanel1.add(xIntegerField1);

        xTextField4.setCaption("Handler");
        xTextField4.setName("entity.handler"); // NOI18N
        xTextField4.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField4.setSpaceChar('_');
        xTextField4.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField4);

        xTextField5.setCaption("Default Var.Name");
        xTextField5.setName("entity.defaultvarname"); // NOI18N
        xTextField5.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField5.setSpaceChar('_');
        xFormPanel1.add(xTextField5);

        xFormPanel3.setCaptionWidth(120);

        xCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xCheckBox1.setCaption("Is Dynamic?");
        xCheckBox1.setCheckValue(1);
        xCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        xCheckBox1.setName("entity.dynamic"); // NOI18N
        xCheckBox1.setUncheckValue(0);
        xFormPanel3.add(xCheckBox1);

        xTextField6.setCaption("Lookup Handler");
        xTextField6.setDepends(new String[] {"entity.dynamic"});
        xTextField6.setName("entity.lookuphandler"); // NOI18N
        xTextField6.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField6.setSpaceChar('_');
        xTextField6.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xTextField6.setVisibleWhen("#{ entity.dynamic == 1 }");
        xFormPanel3.add(xTextField6);

        xTextField7.setCaption("Lookup Key");
        xTextField7.setDepends(new String[] {"entity.dynamic"});
        xTextField7.setName("entity.lookupkey"); // NOI18N
        xTextField7.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField7.setSpaceChar('_');
        xTextField7.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xTextField7.setVisibleWhen("#{ entity.dynamic == 1 }");
        xFormPanel3.add(xTextField7);

        xTextField8.setCaption("Lookup Value");
        xTextField8.setDepends(new String[] {"entity.dynamic"});
        xTextField8.setName("entity.lookupvalue"); // NOI18N
        xTextField8.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField8.setSpaceChar('_');
        xTextField8.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xTextField8.setVisibleWhen("#{ entity.dynamic == 1 }");
        xFormPanel3.add(xTextField8);

        xTextField9.setCaption("Lookup Data Type");
        xTextField9.setDepends(new String[] {"entity.dynamic"});
        xTextField9.setName("entity.lookupdatatype"); // NOI18N
        xTextField9.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField9.setSpaceChar('_');
        xTextField9.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xTextField9.setVisibleWhen("#{ entity.dynamic == 1 }");
        xFormPanel3.add(xTextField9);

        xTextField10.setCaption("Dynamic Field name");
        xTextField10.setDepends(new String[] {"entity.dynamic"});
        xTextField10.setName("entity.dynamicfieldname"); // NOI18N
        xTextField10.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField10.setSpaceChar('_');
        xTextField10.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xTextField10.setVisibleWhen("#{ entity.dynamic == 1 }");
        xFormPanel3.add(xTextField10);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(xFormPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(xFormPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        xTabbedPane1.addTab("General Info", jPanel2);

        xButton2.setName("addField"); // NOI18N
        xButton2.setVisibleWhen("#{mode!='read'}");
        xButton2.setImmediate(true);
        xButton2.setText("Add");

        xButton3.setName("editField"); // NOI18N
        xButton3.setVisibleWhen("#{mode!='read'}");
        xButton3.setImmediate(true);
        xButton3.setText("Edit");

        xButton4.setName("removeField"); // NOI18N
        xButton4.setVisibleWhen("#{mode!='read'}");
        xButton4.setImmediate(true);
        xButton4.setText("Remove");

        xButton1.setName("shiftUp"); // NOI18N
        xButton1.setVisibleWhen("#{mode!='read'}");
        xButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        xButton1.setText("Up");

        xButton5.setName("shiftDown"); // NOI18N
        xButton5.setVisibleWhen("#{mode!='read'}");
        xButton5.setImmediate(true);
        xButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        xButton5.setText("Dn");

        xDataTable1.setHandler("fieldModel");
        xDataTable1.setName("selectedField"); // NOI18N
        xDataTable1.setColumns(new com.rameses.rcp.common.Column[]{
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "name"}
                , new Object[]{"caption", "Name"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "title"}
                , new Object[]{"caption", "Title"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "datatype"}
                , new Object[]{"caption", "Data Type"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "handler"}
                , new Object[]{"caption", "Handler"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            })
        });
        xDataTable1.setVarStatus("varStatus");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 447, Short.MAX_VALUE)
                .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(xDataTable1, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(374, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(xDataTable1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                    .addGap(45, 45, 45)))
        );

        xTabbedPane1.addTab("Fields", jPanel1);

        xList1.setDynamic(true);
        xList1.setExpression("#{item.ruleset}");
        xList1.setItems("entity.rulesets");
        xList1.setName("selectedRuleset"); // NOI18N
        jScrollPane1.setViewportView(xList1);

        jLabel1.setText("Associated rulesets");

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                .addContainerGap(456, Short.MAX_VALUE))
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(236, Short.MAX_VALUE))
        );

        xTabbedPane1.addTab("Rulesets", xPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XButton xButton3;
    private com.rameses.rcp.control.XButton xButton4;
    private com.rameses.rcp.control.XButton xButton5;
    private com.rameses.rcp.control.XCheckBox xCheckBox1;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XIntegerField xIntegerField1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XList xList1;
    private com.rameses.rcp.control.XPanel xPanel1;
    private com.rameses.rcp.control.XTabbedPane xTabbedPane1;
    private com.rameses.rcp.control.XTextField xTextField10;
    private com.rameses.rcp.control.XTextField xTextField11;
    private com.rameses.rcp.control.XTextField xTextField12;
    private com.rameses.rcp.control.XTextField xTextField13;
    private com.rameses.rcp.control.XTextField xTextField2;
    private com.rameses.rcp.control.XTextField xTextField3;
    private com.rameses.rcp.control.XTextField xTextField4;
    private com.rameses.rcp.control.XTextField xTextField5;
    private com.rameses.rcp.control.XTextField xTextField6;
    private com.rameses.rcp.control.XTextField xTextField7;
    private com.rameses.rcp.control.XTextField xTextField8;
    private com.rameses.rcp.control.XTextField xTextField9;
    // End of variables declaration//GEN-END:variables
}
