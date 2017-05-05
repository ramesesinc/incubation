/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt.views;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author dell
 */
@Template(OKCancelPage.class)
public class FileNewPage extends javax.swing.JPanel {

    /**
     * Creates new form AddDocFilePage
     */
    public FileNewPage() {
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

        pnlheader = new javax.swing.JPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();
        jPanel1 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xButton1 = new com.rameses.rcp.control.XButton();
        xButton2 = new com.rameses.rcp.control.XButton();
        pnlImage = new com.rameses.rcp.control.XPanel();
        xImageGallery1 = new com.rameses.rcp.control.XImageGallery();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new java.awt.Dimension(336, 319));
        setLayout(new java.awt.BorderLayout());

        pnlheader.setLayout(new java.awt.BorderLayout());

        xFormPanel1.setPadding(new java.awt.Insets(0, 0, 0, 0));

        xComboBox1.setCaption("Type");
        xComboBox1.setExpression("#{item.title}");
        xComboBox1.setItemKey("objid");
        xComboBox1.setItems("fileTypes");
        xComboBox1.setName("entity.filetype"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(0, 20));
        xComboBox1.setRequired(true);
        xFormPanel1.add(xComboBox1);

        xTextField1.setCaption("Title");
        xTextField1.setName("entity.title"); // NOI18N
        xTextField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField1.setRequired(true);
        xTextField1.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 63));

        xTextArea1.setCaption("Keywords");
        xTextArea1.setName("entity.keywords"); // NOI18N
        xTextArea1.setPreferredSize(new java.awt.Dimension(0, 61));
        jScrollPane1.setViewportView(xTextArea1);

        xFormPanel1.add(jScrollPane1);

        pnlheader.add(xFormPanel1, java.awt.BorderLayout.NORTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 5, 0));
        jPanel1.setLayout(new com.rameses.rcp.control.layout.XLayout());

        xLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 20));
        xLabel1.setExpression("Attachments");
        xLabel1.setFontStyle("font-weight:bold;");
        jPanel1.add(xLabel1);

        xButton1.setDepends(new String[] {"entity.filetype"});
        xButton1.setDisableWhen("#{entity.filetype == null}");
        xButton1.setIconResource("images/clip.png");
        xButton1.setImmediate(true);
        xButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        xButton1.setName("attachFile"); // NOI18N
        xButton1.setText("Attach");
        jPanel1.add(xButton1);

        xButton2.setDepends(new String[] {"entity.filetype", "selectedAttachment"});
        xButton2.setDisableWhen("#{entity.filetype == null || selectedAttachment == null}");
        xButton2.setIconResource("images/toolbars/trash.png");
        xButton2.setImmediate(true);
        xButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        xButton2.setName("removeAttachment"); // NOI18N
        xButton2.setText("Remove");
        jPanel1.add(xButton2);

        pnlheader.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(pnlheader, java.awt.BorderLayout.NORTH);

        pnlImage.setDepends(new String[] {"entity.filetype"});
        pnlImage.setVisibleWhen("#{entity.filetype.toString().matches('jpg|jpeg|png')==true}");
        com.rameses.rcp.control.layout.YLayout yLayout1 = new com.rameses.rcp.control.layout.YLayout();
        yLayout1.setAutoFill(true);
        pnlImage.setLayout(yLayout1);

        xImageGallery1.setCellSize(new java.awt.Dimension(50, 50));
        xImageGallery1.setDepends(new String[] {"entity.filetype"});
        xImageGallery1.setHandler("listHandler");
        xImageGallery1.setName("selectedAttachment"); // NOI18N
        xImageGallery1.setVisibleWhen("#{entity.filetype == 'jpg'}");
        pnlImage.add(xImageGallery1);

        add(pnlImage, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XPanel pnlImage;
    private javax.swing.JPanel pnlheader;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XImageGallery xImageGallery1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
}
