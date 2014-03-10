/*
 * WebcamPane.java
 *
 * Created on December 4, 2013, 9:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.support.ImageIconSupport;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author wflores
 */
class WebcamPane extends JPanel 
{
    private WebcamViewer viewer;
    private Webcam webcam;
    private WebcamPanel wcp;
    private JToolBar toolbar;
    private JButton btnShoot;
    private JComboBox cboSizes;
    
    private List<WebcamPaneListener> listeners;
    
    private boolean autoCloseOnSelect;    
    
    public WebcamPane(WebcamViewer viewer, Webcam webcam) {
        this.listeners = new ArrayList(); 
        this.viewer = viewer;
        this.webcam = webcam;         
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        webcam.addWebcamListener(new WebcamPanelSupport()); 
        wcp = new WebcamPanel(webcam, webcam.getViewSize(), false); 
        wcp.setFPSDisplayed(true);
        add(wcp); 
        
        toolbar = new JToolBar();
        toolbar.setFloatable(false);        
        toolbar.setRollover(false); 
        toolbar.setLayout(new ToolbarLayout()); 
        toolbar.setBorder(BorderFactory.createEmptyBorder(2,5,5,5)); 
        add(toolbar); 
        
        Vector<ViewSize> sizes = new Vector();
        for (Dimension dim : webcam.getViewSizes()) {
            sizes.add(new ViewSize(dim));
        }
        for (Dimension dim : webcam.getCustomViewSizes()) {
            sizes.add(new ViewSize(dim));
        }        
        cboSizes = new JComboBox(sizes);
        cboSizes.setSelectedItem(new ViewSize(webcam.getViewSize()));
        cboSizes.addItemListener(new ViewSizeSelector());
        toolbar.add(cboSizes);
        toolbar.add(Box.createHorizontalStrut(25));
        
        btnShoot = new JButton("Shoot");
        btnShoot.setEnabled(false);
        btnShoot.setFocusPainted(false); 
        btnShoot.setIcon(ImageIconSupport.getInstance().getIcon("images/toolbars/camera.png"));  
        btnShoot.addActionListener(new ShootActionSupport(btnShoot));
        toolbar.add(btnShoot); 
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFocusPainted(false); 
        btnCancel.setIcon(ImageIconSupport.getInstance().getIcon("images/toolbars/cancel.png")); 
        btnCancel.addActionListener(new CancelActionSupport()); 
        toolbar.add(btnCancel);         
    }
    
    // </editor-fold>
    
    private class ViewSize 
    {
        private Dimension size;
        
        ViewSize(Dimension size) {
            this.size = size;
        }
        
        public Dimension getSize() { return size; } 
        public String toString() {
            if (size == null) return "null";
            
            return size.width + " x " + size.height;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ViewSize)) return false;
            
            ViewSize vs = (ViewSize)obj;
            Dimension dim1 = vs.getSize(); 
            Dimension dim2 = getSize();
            return (dim1.width==dim2.width && dim1.height==dim2.height);
        }
    }
    
    private class ViewSizeSelector implements ItemListener 
    {
        ViewSizeSelector() {
            
        }
        
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ViewSize vs = (ViewSize) e.getItem();
                final Dimension size = vs.getSize();
                
                try { 
                    stop(); 
                } catch(Throwable t) {
                    JOptionPane.showMessageDialog(WebcamPane.this, "[ERROR] " + t.getClass().getName() + ": " + t.getMessage()); 
                } 

                try { 
                    for (WebcamPaneListener listener : listeners) { 
                        listener.oncancel(); 
                    } 
                } catch(Throwable t) {
                    JOptionPane.showMessageDialog(WebcamPane.this, "[ERROR] " + t.getClass().getName() + ": " + t.getMessage()); 
                }   
                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        viewer.setSize(size.width, size.height);
                        WebcamViewer.PREFERRED_SIZE = new Dimension(size.width, size.height); 
                        viewer.open(); 
                    } 
                });
            }
        }
    }    
    
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters "> 
    
    public void setLayout(LayoutManager mgr){
    } 
    
    void setAutoCloseOnSelect(boolean autoCloseOnSelect) {
        this.autoCloseOnSelect = autoCloseOnSelect;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods "> 

    public void removeListener(WebcamPaneListener listener) {
        if (listener != null) listeners.remove(listener); 
    }
    
    public void addListener(WebcamPaneListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener); 
        }
    }
    
    public void start() {
        wcp.start(); 
    }
    
    public void stop() { 
        wcp.stop(); 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
        WebcamPane root = WebcamPane.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component c = root.wcp;
                if (c != null) {
                    Dimension dim = c.getPreferredSize(); 
                    w = dim.width;
                    h = dim.height; 
                }
                c = root.toolbar;
                if (c != null) {
                    Dimension dim = c.getPreferredSize(); 
                    w = Math.max(dim.width, w); 
                    h += dim.height; 
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) { 
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component c = root.wcp;
                if (c != null) {
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, w, dim.height);
                    y += dim.height;
                } 
                
                c = root.toolbar;
                if (c != null) {
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, w, dim.height);
                }
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout "> 
    
    private class ToolbarLayout implements LayoutManager 
    {
        WebcamPane root = WebcamPane.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    w += dim.width + 3;
                    h = Math.max(dim.height, h); 
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) { 
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                int size = 0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    size += dim.width + 3;
                }
                
                x = Math.max((w - size) / 2, 0) + margin.left; 
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, dim.width, h); 
                    x += dim.width + 3; 
                }
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" WebcamPanelSupport "> 
    
    private class WebcamPanelSupport implements WebcamListener
    {
        public void webcamOpen(WebcamEvent we) {
            btnShoot.setEnabled(true);
            btnShoot.repaint();
            btnShoot.requestFocus(); 
        }

        public void webcamClosed(WebcamEvent we) {
            btnShoot.setEnabled(false);
            btnShoot.repaint();
            if (btnShoot.hasFocus()) btnShoot.transferFocus();
        }

        public void webcamDisposed(WebcamEvent we) {
            btnShoot.setEnabled(false);
            btnShoot.repaint();
            if (btnShoot.hasFocus()) btnShoot.transferFocus();
        }

        public void webcamImageObtained(WebcamEvent we) {
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ShootActionSupport "> 
    
    private class ShootActionSupport implements ActionListener 
    {
        WebcamPane root = WebcamPane.this;
        
        private JButton button;
        
        ShootActionSupport(JButton button) {
            this.button = button;
        }
        
        public void actionPerformed(ActionEvent e) {
            button.setEnabled(false); 
            button.setText("Processing...");
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    runImpl(); 
                }
            });
        } 
        
        private void runImpl() {
            try {
                shoot();
            } catch(Throwable t) {
                MsgBox.err(t); 
            } finally {
                button.setText("Shoot"); 
                button.setEnabled(true); 
            }
        }
        
        private void shoot() {
            BufferedImage bi = null; 
            try { 
                bi = root.webcam.getImage(); 
            } catch(Throwable t) { 
                MsgBox.err(t); 
            } 

            if (bi == null) return; 

            byte[] bytes = null; 
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ImageIO.write(bi, "JPG", baos); 
                bytes = baos.toByteArray(); 
            } catch (Throwable t) { 
                MsgBox.err(t);  
                return; 
            } 

            if (bytes == null) return;

            try { 
                if (autoCloseOnSelect) root.stop(); 
            } catch(Throwable t){;} 
            
            for (WebcamPaneListener listener : root.listeners) { 
                listener.onselect(bytes); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CancelActionSupport "> 
    
    private class CancelActionSupport implements ActionListener 
    {
        WebcamPane root = WebcamPane.this;
        
        public void actionPerformed(ActionEvent e) {
            try { 
                root.stop(); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            } 
            
            try { 
                cancel(); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            }            
        } 
        
        private void cancel() {
            for (WebcamPaneListener listener : root.listeners) { 
                listener.oncancel(); 
            } 
        }
    }
    
    // </editor-fold>
}
