/*
 * SigIdViewer.java
 *
 * Created on December 19, 2013, 8:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.sigid;

import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.SigIdResult;
import com.rameses.rcp.common.SigIdModel;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author wflores 
 */
public class SigIdViewer 
{
    public static void open(Map options) {
        new SigIdViewer(options).open();
    }    
    
    public static void open(SigIdModel model) {
        new SigIdViewer(model).open();
    }        
    
    private Map options;
    private SigIdModel model;
    private int width;
    private int height; 
    private boolean autoOpenMode;
    
    public SigIdViewer() {
        this(new SigIdModel()); 
    }

    public SigIdViewer(SigIdModel model) {
        this.model = (model == null? new SigIdModel(): model); 
    }     

    public SigIdViewer(Map options) {
        this.model = new SigIdModelProxy(options); 
    } 
        
    public byte[] open() { 
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 
        final SigIdPanel panel = new SigIdPanel(); 
        panel.setPenWidth(model.getPenWidth()); 
        panel.setImageXSize(model.getImageXSize());
        panel.setImageYSize(model.getImageYSize());
        
        JDialog dialog = null; 
        if (win instanceof Frame) {
            dialog = new JDialog((Frame) win); 
        } else if (win instanceof Dialog) {
            dialog = new JDialog((Dialog) win); 
        } else {
            dialog = new JDialog(); 
        } 
        
        final JDialog jdialog = dialog;        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        dialog.setModal(true); 
        dialog.setResizable(false); 
        dialog.setContentPane(panel);         
        dialog.setTitle(model.getTitle());
        dialog.setSize(model.getWidth(), model.getHeight());
        dialog.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { 
                try { 
                    panel.stop();
                } catch(Throwable t) {
                    JOptionPane.showMessageDialog(jdialog, "[ERROR] " + t.getClass().getName() + ": " + t.getMessage()); 
                } 
                
                oncloseImpl(); 
            }
            
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            
            public void windowOpened(WindowEvent e) { 
                try { 
                    panel.start(); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            jdialog.dispose(); 
                        }
                    }); 
                }
            }
        }); 
        panel.add(new SigIdPanel.SelectionListener(){
            public void onselect(SigIdResult info) {
                jdialog.dispose(); 
                onselectImpl(info);
            }

            public void onclose() {
                jdialog.dispose(); 
                oncloseImpl(); 
            }
        });
        
        centerWindow(dialog);
        dialog.setVisible(true); 
        return null; 
    }    
    
    private void centerWindow(Window win) {
        Dimension windim = win.getSize();
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(win.getGraphicsConfiguration()); 
        int scrwidth = scrdim.width - (margin.left + margin.right);
        int scrheight = scrdim.height - (margin.top + margin.bottom);
        int x = Math.max((scrwidth - windim.width) / 2, 0) + margin.left;
        int y = Math.max((scrheight - windim.height) / 2, 0) + margin.top;
        win.setLocation(x, y); 
    }
    
    private void onselectImpl(SigIdResult info) {
//        byte[] data = null;
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
//            ImageIO.write(image, "JPG", baos); 
//            data = baos.toByteArray();
//        } catch(Throwable t) {;} 
        
        if (model != null) {
            model.onselect(info);
        } 
    }
    
    private void oncloseImpl() {
        if (model != null) model.onclose();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" SigIdModelProxy "> 
    
    private class SigIdModelProxy extends SigIdModel 
    {
        private Map options; 
        private String title;
        private Integer width; 
        private Integer height;
        private Integer penWidth;
        private Integer imageXSize;
        private Integer imageYSize;
        private CallbackHandlerProxy onselectCallback;
        private CallbackHandlerProxy oncloseCallback;
        
        SigIdModelProxy(Map options) {
            this.options = options;
            this.title = getString(options, "title"); 
            this.width = getInt(options, "width"); 
            this.height = getInt(options, "height");
            this.penWidth = getInt(options, "penWidth"); 
            
            Object source = get(options, "onselect"); 
            if (source != null) onselectCallback = new CallbackHandlerProxy(source); 
            
            source = get(options, "onclose"); 
            if (source != null) oncloseCallback = new CallbackHandlerProxy(source); 
        }
        
        public String getTitle() {
            if (title == null) {
                return super.getTitle(); 
            } else { 
                return title; 
            } 
        }
        
        public int getWidth() {
            if (width == null) {
                return super.getWidth(); 
            } else {
                return width.intValue(); 
            }
        }
        
        public int getHeight() {
            if (height == null) {
                return super.getHeight(); 
            } else {
                return height.intValue(); 
            }
        }        
        
        public int getPenWidth() {
            if (penWidth == null) {
                return super.getPenWidth(); 
            } else {
                return penWidth.intValue(); 
            }
        }
        public int getImageXSize() {
            if (imageXSize == null) {
                return super.getImageXSize(); 
            } else {
                return imageXSize.intValue(); 
            }
        }    
        public int getImageYSize() {
            if (imageYSize == null) {
                return super.getImageYSize(); 
            } else {
                return imageYSize.intValue(); 
            }
        }        

        public void onselect(Object result) {
            if (onselectCallback == null) return;
            
            onselectCallback.call(result); 
        } 

        public void onclose() {
            if (oncloseCallback == null) return;
            
            oncloseCallback.call(); 
        } 
        
        private Integer getInt(Map map, String name) {
            try {
                return (Integer) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        }

        private String getString(Map map, String name) {
            try {
                Object o = map.get(name);
                return (o == null? null: o.toString()); 
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Object get(Map map, String name) {
            return (map == null? null: map.get(name)); 
        }
    }
    
    // </editor-fold>
}
