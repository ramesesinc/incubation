/*
 * SigIdPanel.java
 *
 * Created on December 19, 2013, 10:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.sigid;

import com.rameses.rcp.common.SigIdResult;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.topaz.sigplus.SigPlus;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
class SigIdPanel extends JPanel 
{
    private JPanel toolbar;
    private SigPlus sigplus; 
    private Repainter repainter; 
    private int penWidth;
    private int imageXSize; 
    private int imageYSize;
    
    public SigIdPanel() {
        initComponent(); 
    }
    
    private void initComponent() {
        setLayout(new BorderLayout());         
        repainter = new Repainter();
        sigplus = new SigPlus(); 
        add(sigplus); 
        
        Border bout = BorderFactory.createEtchedBorder();
        Border bin = BorderFactory.createEmptyBorder(5,5,5,5); 
        toolbar = new JPanel(new BorderLayout());  
        toolbar.setBorder(BorderFactory.createCompoundBorder(bout, bin)); 
        add(toolbar, BorderLayout.SOUTH); 
        
        JPanel leftSection = new JPanel();
        JButton btnClear = new JButton(" Clear ");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onclearTablet();
            }
        });
        leftSection.add(btnClear);
        toolbar.add(leftSection, BorderLayout.WEST); 
        
        JPanel rightSection = new JPanel();
        JButton btnOK = new JButton("   OK   ");
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onselect();
            }
        });
        rightSection.add(btnOK);
        
        JButton btnCancel = new JButton(" Cancel ");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                oncancel();
            }
        });
        rightSection.add(btnCancel);        
        toolbar.add(rightSection, BorderLayout.EAST);         
    }
    
    void start() { 
        sigplus.setTabletModel("SignatureGem1X5"); 
        sigplus.setTabletComPort("HID1"); 
        sigplus.clearTablet();
        sigplus.setTabletState(0);
        sigplus.setTabletState(1);
        repainter.start();
    }
    
    void stop() {
        int pensize = (penWidth <= 0? 8: penWidth);
        int imgxsize = (imageXSize <= 0? 1000: imageXSize);
        int imgysize = (imageYSize <= 0? 350: imageYSize);
        
        sigplus.setTabletState(0); 
        sigplus.setImagePenWidth(pensize); 
        sigplus.setImageXSize(imgxsize); 
        sigplus.setImageYSize(imgysize); 
        repainter.stop();
    }
    
    void fireOnclose() {
        for (SelectionListener sl : listeners) { 
            sl.onclose(); 
        }         
    }
    
    public void setPenWidth(int penWidth) {
        this.penWidth = penWidth;
    }
    public void setImageXSize(int imageXSize) {
        this.imageXSize = imageXSize;
    }
    public void setImageYSize(int imageYSize) {
        this.imageYSize = imageYSize;
    }
    
    private void onclearTablet() {
        sigplus.clearTablet();
    }

    private void onselect() {
        stop();
        
        SigInfoImpl siginfo = new SigInfoImpl();
        siginfo.sigString = sigplus.getSigString(); 
        siginfo.keyReceipt = sigplus.getKeyReceipt();
        siginfo.keyString = sigplus.getKeyString(); 
        siginfo.sigImage = sigplus.sigImage();  
        //siginfo.getImageData(); 
        //siginfo.dump(); 
        
        for (SelectionListener sl : listeners) { 
            sl.onselect(siginfo); 
        } 
        fireOnclose(); 
    }
    
    private void oncancel() {
        stop(); 
        fireOnclose(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Repainter "> 
    
    private class Repainter 
    {
        SigIdPanel root = SigIdPanel.this; 
        
        boolean started;
        boolean cancelled;
        
        void start() {
            if (started) return;
            
            started = true;
            Runnable runnable = new Runnable() {
                public void run() {
                    runImpl();
                }
            };
            new Thread(runnable).start();
        }
        
        void stop() {
            cancelled = true; 
        }
        
        private void runImpl() {
            while (true) {
                if (cancelled) break;
                
                try {
                    Thread.sleep(250); 
                } catch(Throwable t) {;} 
                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            root.sigplus.repaint(); 
                        } catch(Throwable t) {;}
                    }
                });
            }
            started = false;
            cancelled = false;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout "> 
    
    private class ToolbarLayout implements LayoutManager 
    {
        SigIdPanel root = SigIdPanel.this;
        
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
                
                Component[] comps = parent.getComponents();
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
    
    // <editor-fold defaultstate="collapsed" desc=" SelectionListener support "> 
    
    private List<SelectionListener> listeners = new ArrayList();
    
    public void remove(SelectionListener sl) {
        if (sl != null) listeners.remove(sl); 
    }
    public void add(SelectionListener sl) {
        if (sl != null && !listeners.contains(sl)) {
            listeners.add(sl); 
        }
    }
    
    public static interface SelectionListener {
        void onselect(SigIdResult info);
        void onclose();
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SigInfoImpl "> 
    
    private class SigInfoImpl implements SigIdResult {
        private BufferedImage sigImage;
        private byte[] imageData;
        private String sigString;
        private String keyString;
        private int keyReceipt; 
        
        public String getSigString() { return sigString; }
        public String getKeyString() { return keyString; } 
        public int getKeyReceipt() { return keyReceipt; }

        public byte[] getImageData() { 
            if (sigImage == null) return null; 
            
            if (imageData == null) {
                int w = sigImage.getWidth(null); 
                int h = sigImage.getHeight(null); 
                int[] pixels = new int[(w * h) * 2];
                sigImage.setRGB(0, 0, 0, 0, pixels, 0, 0); 

                ByteArrayOutputStream fos = null; 
                try { 
                    fos = new ByteArrayOutputStream(); 
                    JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fos); 
                    jpeg.encode(sigImage); 
                    return fos.toByteArray(); 
                } catch(RuntimeException re) { 
                    throw re; 
                } catch(Exception e) { 
                    throw new RuntimeException(e.getMessage(), e);  
                } finally {
                    try { fos.close(); }catch(Throwable t){;} 
                }
            } 
            return imageData; 
        } 
        
        void dump() {
            System.out.println("image=" + getImageData());  
            System.out.println("sigImage=" + sigImage); 
            System.out.println("keyReceipt=" + keyReceipt);
            System.out.println("keyString=" + keyString);
            System.out.println("sigString=" + sigString);
        }
    }
    
    // </editor-fold>
}
