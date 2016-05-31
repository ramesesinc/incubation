/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.views;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.control.XDropDownList;
import com.rameses.rcp.swing.UIVisibility;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;



/**
 *
 * @author dell
 */
public class CrudFormPage extends javax.swing.JPanel {

    private NoteRenderer renderer;
    
    /**
     * Creates new form CrudFormPage
     */
    public CrudFormPage() {
        initComponents();
        btnSave.setToolTipText("Save");
        btnCreate.setToolTipText("New");
        btnEdit.setToolTipText("Edit");
        btnUndo.setToolTipText("Undo");
        btnCancel.setToolTipText("Cancel Edit");
        
        btnInfo.setToolTipText("Info");
        btnDebug.setToolTipText("Debug Info");
        btnHelp.setToolTipText("Help");
        
        //hide the status panel for now 
        pnlstat.setVisible(false); 
        
        renderer = new NoteRenderer();
        xDropDownList1.setRenderer(renderer);
        xDropDownList1.setVisibility(new NoteVisibility(renderer));         
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
        pnlstat = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnCancel1 = new com.rameses.rcp.control.XButton();
        btnCreate = new com.rameses.rcp.control.XButton();
        btnEdit = new com.rameses.rcp.control.XButton();
        btnSave = new com.rameses.rcp.control.XButton();
        btnUndo = new com.rameses.rcp.control.XButton();
        btnCancel = new com.rameses.rcp.control.XButton();
        btnPrint = new com.rameses.rcp.control.XButton();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        xActionBar2 = new com.rameses.rcp.control.XActionBar();
        btnDebug = new com.rameses.rcp.control.XButton();
        btnInfo = new com.rameses.rcp.control.XButton();
        btnHelp = new com.rameses.rcp.control.XButton();
        btnCancel2 = new com.rameses.rcp.control.XButton();
        btnUp = new com.rameses.rcp.control.XButton();
        btnDown = new com.rameses.rcp.control.XButton();
        xDropDownList1 = new com.rameses.rcp.control.XDropDownList();

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
            .addGap(0, 262, Short.MAX_VALUE)
        );

        add(xStyleRule1, java.awt.BorderLayout.LINE_START);

        pnlstat.setPreferredSize(new java.awt.Dimension(400, 25));

        javax.swing.GroupLayout pnlstatLayout = new javax.swing.GroupLayout(pnlstat);
        pnlstat.setLayout(pnlstatLayout);
        pnlstatLayout.setHorizontalGroup(
            pnlstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 727, Short.MAX_VALUE)
        );
        pnlstatLayout.setVerticalGroup(
            pnlstatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        add(pnlstat, java.awt.BorderLayout.PAGE_END);

        jPanel2.setPreferredSize(new java.awt.Dimension(420, 70));
        jPanel2.setLayout(new com.rameses.rcp.control.layout.YLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(420, 35));
        jPanel3.setLayout(new java.awt.BorderLayout());

        xLabel1.setBackground(new java.awt.Color(255, 255, 255));
        xLabel1.setExpression("#{title}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        xLabel1.setOpaque(true);
        xLabel1.setPreferredSize(new java.awt.Dimension(41, 30));
        jPanel3.add(xLabel1, java.awt.BorderLayout.NORTH);

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

        btnCreate.setAccelerator("ctrl N");
        btnCreate.setCaption("");
        btnCreate.setFocusable(false);
        btnCreate.setIconResource("images/toolbars/create.png");
        btnCreate.setName("create"); // NOI18N
        btnCreate.setVisibleWhen("#{createAllowed}");
        jToolBar1.add(btnCreate);

        btnEdit.setAccelerator("ctrl E");
        btnEdit.setCaption("");
        btnEdit.setFocusable(false);
        btnEdit.setIconResource("images/toolbars/edit.png");
        btnEdit.setName("edit"); // NOI18N
        btnEdit.setVisibleWhen("#{editAllowed}");
        jToolBar1.add(btnEdit);

        btnSave.setAccelerator("ctrl S");
        btnSave.setCaption("");
        btnSave.setFocusable(false);
        btnSave.setIconResource("images/toolbars/save.png");
        btnSave.setName("save"); // NOI18N
        btnSave.setVisibleWhen("#{saveAllowed}");
        jToolBar1.add(btnSave);

        btnUndo.setAccelerator("ctrl U");
        btnUndo.setCaption("");
        btnUndo.setFocusable(false);
        btnUndo.setIconResource("images/toolbars/undo.png");
        btnUndo.setImmediate(true);
        btnUndo.setName("undo"); // NOI18N
        btnUndo.setVisibleWhen("#{undoAllowed}");
        jToolBar1.add(btnUndo);

        btnCancel.setCaption("");
        btnCancel.setFocusable(false);
        btnCancel.setIconResource("images/toolbars/cancel.png");
        btnCancel.setImmediate(true);
        btnCancel.setName("unedit"); // NOI18N
        btnCancel.setVisibleWhen("#{cancelEditAllowed}");
        jToolBar1.add(btnCancel);

        btnPrint.setAccelerator("ctrl P");
        btnPrint.setCaption("");
        btnPrint.setFocusable(false);
        btnPrint.setIconResource("images/toolbars/printer.png");
        btnPrint.setImmediate(true);
        btnPrint.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnPrint.setName("viewReport"); // NOI18N
        btnPrint.setVisibleWhen("#{viewReportAllowed}");
        jToolBar1.add(btnPrint);

        xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        xActionBar1.setDynamic(true);
        xActionBar1.setFormName("formName");
        jToolBar1.add(xActionBar1);

        xActionBar2.setBorder(null);
        xActionBar2.setName("navActions"); // NOI18N
        xActionBar2.setOrientationHAlignment("RIGHT");
        xActionBar2.setPreferredSize(new java.awt.Dimension(100, 29));
        xActionBar2.setTextPosition("CENTER");
        jToolBar1.add(xActionBar2);

        btnDebug.setBackground(new java.awt.Color(255, 255, 255));
        btnDebug.setCaption("\\");
            btnDebug.setFocusable(false);
            btnDebug.setIconResource("images/debug.png");
            btnDebug.setImmediate(true);
            btnDebug.setName("showDebugInfo"); // NOI18N
            btnDebug.setVisibleWhen("#{ canDebug == true }");
            jToolBar1.add(btnDebug);

            btnInfo.setBackground(new java.awt.Color(255, 255, 255));
            btnInfo.setCaption("");
            btnInfo.setFocusable(false);
            btnInfo.setIconResource("images/info.png");
            btnInfo.setImmediate(true);
            btnInfo.setName("showInfo"); // NOI18N
            jToolBar1.add(btnInfo);

            btnHelp.setBackground(new java.awt.Color(255, 255, 255));
            btnHelp.setCaption("\\");
                btnHelp.setFocusable(false);
                btnHelp.setIconResource("images/help.png");
                btnHelp.setImmediate(true);
                btnHelp.setName("showHelp"); // NOI18N
                jToolBar1.add(btnHelp);

                btnCancel2.setCaption("");
                btnCancel2.setFocusable(false);
                btnCancel2.setIconResource("images/toolbars/refresh.png");
                btnCancel2.setImmediate(true);
                btnCancel2.setName("reloadEntity"); // NOI18N
                btnCancel2.setVisibleWhen("#{mode == 'read' }");
                jToolBar1.add(btnCancel2);

                btnUp.setFocusable(false);
                btnUp.setIconResource("images/toolbars/arrow_up.png");
                btnUp.setImmediate(true);
                btnUp.setName("moveUp"); // NOI18N
                btnUp.setVisibleWhen("#{showNavigation==true}");
                jToolBar1.add(btnUp);

                btnDown.setFocusable(false);
                btnDown.setIconResource("images/toolbars/arrow_down.png");
                btnDown.setImmediate(true);
                btnDown.setName("moveDown"); // NOI18N
                btnDown.setVisibleWhen("#{showNavigation==true}");
                jToolBar1.add(btnDown);

                xDropDownList1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/note.png"))); // NOI18N
                xDropDownList1.setContentAreaFilled(false);
                xDropDownList1.setFocusable(false);
                xDropDownList1.setHandler("messagelist");
                xDropDownList1.setHideOnEmptyResult(true);
                xDropDownList1.setMargin(new java.awt.Insets(0, 2, 0, 2));
                xDropDownList1.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        xDropDownList1ActionPerformed(evt);
                    }
                });
                jToolBar1.add(xDropDownList1);

                jPanel2.add(jToolBar1);

                add(jPanel2, java.awt.BorderLayout.NORTH);
            }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc=" LayeredPanel ">

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (constraints == null || BorderLayout.CENTER.equals(constraints)) {
            LayeredPanel layer = getLayeredPanel(); 
            if (layer.getParent() == null) {
                super.addImpl(layer, constraints, index); 
            } 
            layer.removeAll();
            layer.add(comp, new Integer(0)); 
            layer.layout.contentComp = comp; 
            layer.layout.overlayComp = renderer.getComponent();
            layer.add(layer.layout.overlayComp, new Integer(1)); 
        } else {
            super.addImpl(comp, constraints, index); 
        }
    }

    @Override
    public void remove(Component comp) {
        synchronized (getTreeLock()) { 
            getLayeredPanel().remove(comp); 
        }
        super.remove(comp);
    }
    
    private LayeredPanel layeredPanel;
    private LayeredPanel getLayeredPanel() {
        if (layeredPanel == null) {
            layeredPanel = new LayeredPanel();
        }
        return layeredPanel;
    }
    
    
    private class LayeredPanel extends JLayeredPane {
        
        LayeredLayout layout;
        JComponent overlay;
        
        LayeredPanel() {
            super();
            super.setLayout(layout=new LayeredLayout()); 
        }
        
        @Override
        public void setLayout(LayoutManager mgr) {
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LayeredLayout ">
    
    private class LayeredLayout implements LayoutManager {

        public final static String CONTENT = "Content";
        public final static String OVERLAY = "Overlay";
        
        Component contentComp;
        Component overlayComp;
        
        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            synchronized (comp.getTreeLock()) {
                if (comp == null) { return; } 
                
                if (comp == overlayComp) {
                    overlayComp = null; 
                } else if (comp == contentComp) {
                    contentComp = null; 
                } 
            } 
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);
                if (contentComp != null && contentComp.isVisible()) {
                    Dimension d = contentComp.getPreferredSize();
                    dim.width += d.width;
                    dim.height = Math.max(d.height, dim.height);
                }
                
                Insets insets = parent.getInsets();
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;
                return dim;
            }   
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);
                if (contentComp != null && contentComp.isVisible()) {
                    Dimension d = contentComp.getMinimumSize();
                    dim.width += d.width;
                    dim.height = Math.max(d.height, dim.height);
                }
                
                Insets insets = parent.getInsets();
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;
                return dim;
            }            
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth(), ph = parent.getHeight(); 
                int x = margin.left, y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom); 
                if (contentComp != null && contentComp.isVisible()) {
                    contentComp.setSize(w, contentComp.getHeight());
                    Dimension d = contentComp.getPreferredSize();
                    contentComp.setBounds(x, y, w, h);
                }
                
                boolean b = (renderer == null? false: renderer.isVisible()); 
                if (overlayComp != null && b) {
                    int r = pw - margin.right - 5;
                    Dimension d = overlayComp.getPreferredSize(); 
                    overlayComp.setBounds(Math.max(r-d.width,0), y, d.width, d.height);
                }
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" NoteRenderer ">
    
    private class NoteRenderer implements XDropDownList.Renderer {
        
        AbstractListDataProvider model;
        JScrollPane jsp;
        JEditorPane view;
        JPanel panel;
        boolean visible;
        
        NoteRenderer() {
            getComponent(); 
        }
        
        @Override
        public void setModel(AbstractListDataProvider model) {
            this.model = model; 
        }

        @Override
        public void refresh() {
            visible = false; 
            if (model != null) {
                visible = (model.getDataListSize() > 0);
            } 
            
            if (visible) {
                StringBuilder builder = new StringBuilder();
                builder.append("<html>"); 
                builder.append("<body>"); 
                int counter = 0;
                List list = model.getDataList(); 
                for (Object item : list) {
                    String text = xDropDownList1.getItemText(item); 
                    if (counter > 0) {
                        builder.append("<br/><hr/>"); 
                    }
                    builder.append("<p style=\"padding:0;\">"); 
                    builder.append(text); 
                    builder.append("</p>"); 
                    counter += 1;
                }
                builder.append("</body>"); 
                builder.append("</html>"); 
                view.setText(builder.toString()); 
                view.repaint(); 
            }
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
            
            getLayeredPanel().remove(getComponent()); 
            if (visible) { 
                int idx = getLayeredPanel().getComponentCount();
                getLayeredPanel().layout.overlayComp = getComponent(); 
                getLayeredPanel().add(getComponent(), new Integer(idx)); 
            } 
            getLayeredPanel().revalidate();
            getLayeredPanel().repaint();
        }
        
        Component getComponent() {
            if (panel == null) {
                Color bgcolor = new Color(254, 255, 208);
                view = new JEditorPane();
                view.setContentType("text/html");
                view.setEditable(false); 
                view.setBackground(bgcolor); 
                view.setBorder(BorderFactory.createEmptyBorder(0,5,0,3));
                
                jsp = new JScrollPane(view); 
                jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
                jsp.setBorder(BorderFactory.createEmptyBorder());
                
                panel = new JPanel() {
                    
//                    @Override
//                    public Insets getInsets() {
//                        Insets ins = super.getInsets();
//                        if (ins == null) ins = new Insets(0,0,0,0);
//                        
//                        ins.top += 1;
//                        ins.left += 1;
//                        ins.bottom += 1;
//                        ins.right += 1;
//                        return ins; 
//                    }
                    
                    @Override
                    public void paint(Graphics g) {
                        super.paint(g); 
                        Graphics g2 = g.create();
                        g2.setColor(Color.decode("#cfcfcf"));  
                        //g2.drawRect(0, 0, getWidth()-1, getHeight()-1); 
                        //g2.setColor(Color.decode("#a0a0a0"));  
                        //g2.drawRect(1, 1, getWidth()-2, getHeight()-2); 
                        g2.dispose();
                    }
                };
                panel.setPreferredSize(new Dimension(250, 250)); 
                panel.setLayout(new BorderLayout());
                panel.setBorder(BorderFactory.createLineBorder(Color.decode("#afafaf"))); 
                panel.setOpaque(false); 
                panel.add(jsp); 
                
                HTMLEditorKit kit = (HTMLEditorKit)view.getEditorKit();
                kit.getStyleSheet().addRule("P {margin:3; }"); 
                
            }
            return panel; 
        }
    } 
    
    private class NoteVisibility implements UIVisibility {

        private NoteRenderer renderer; 
        
        NoteVisibility(NoteRenderer renderer) {
            this.renderer = renderer; 
        }
        
        @Override
        public boolean isVisible() {
            AbstractListDataProvider model = renderer.model;
            if (model == null) { return false; } 
            
            return (model.getDataListSize() > 0); 
        }
    }
    
    // </editor-fold>
    
    
    
    private void xDropDownList1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xDropDownList1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xDropDownList1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XButton btnCancel;
    private com.rameses.rcp.control.XButton btnCancel1;
    private com.rameses.rcp.control.XButton btnCancel2;
    private com.rameses.rcp.control.XButton btnCreate;
    private com.rameses.rcp.control.XButton btnDebug;
    private com.rameses.rcp.control.XButton btnDown;
    private com.rameses.rcp.control.XButton btnEdit;
    private com.rameses.rcp.control.XButton btnHelp;
    private com.rameses.rcp.control.XButton btnInfo;
    private com.rameses.rcp.control.XButton btnPrint;
    private com.rameses.rcp.control.XButton btnSave;
    private com.rameses.rcp.control.XButton btnUndo;
    private com.rameses.rcp.control.XButton btnUp;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlstat;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XActionBar xActionBar2;
    private com.rameses.rcp.control.XDropDownList xDropDownList1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XStyleRule xStyleRule1;
    // End of variables declaration//GEN-END:variables
}
