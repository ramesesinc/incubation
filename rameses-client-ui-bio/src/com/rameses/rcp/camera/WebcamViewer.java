/*
 * WebcamViewer.java
 *
 * Created on December 4, 2013, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.camera;

import com.github.sarxos.webcam.Webcam;
import com.rameses.rcp.common.*;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;
import javax.swing.JDialog;

/**
 *
 * @author wflores 
 */
public final class WebcamViewer 
{
    
    public static void open(Map options) {
        new WebcamViewer(options).open();
    }    
    
    private Map options;
    private int width;
    private int height; 
    private boolean autoOpenMode;
    
    public WebcamViewer() {
        this(null); 
    }

    public WebcamViewer(Map options) {
        this.options = options; 
        setSize(320, 240); 
        init();
    } 
    
    private void init() {
        Integer owidth = getInt(options, "width"); 
        Integer oheight = getInt(options, "height"); 
        if (owidth != null) width = owidth.intValue();
        if (oheight != null) height = oheight.intValue(); 
        
        Boolean bool = getBool(options, "autoOpenMode");
        if (bool != null) autoOpenMode = bool.booleanValue(); 
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height; 
    }
    
    public void setAutoOpenMode(boolean autoOpenMode) {
        this.autoOpenMode = autoOpenMode; 
    }
    
    public byte[] open() { 
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();         
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(width, height)); 
        webcam.setAutoOpenMode(autoOpenMode); 
        
        String title = getString(options, "title");
        if (title == null) title = "Camera";
        
        final WebcamPane pane = new WebcamPane(webcam);         
        JDialog dialog = null; 
        if (win instanceof Frame) {
            dialog = new JDialog((Frame) win); 
        } else if (win instanceof Dialog) {
            dialog = new JDialog((Dialog) win); 
        } else {
            dialog = new JDialog(); 
        } 
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        dialog.setModal(true);
        dialog.setResizable(false); 
        dialog.setTitle(title);
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { 
                try { 
                    pane.stop(); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                } 
                
                onclose(); 
            }
            
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            
            public void windowOpened(WindowEvent e) {
                pane.start(); 
            }
        }); 
        centerWindow(dialog);
        pane.addListener(new WebcamPaneListenerImpl(dialog)); 
        dialog.setVisible(true); 
        return null; 
    }
    
    private Integer getInt(Map map, String name) {
        try {
            return (Integer) map.get(name);
        } catch(Throwable t) { 
            return null; 
        }
    }
    
    private Boolean getBool(Map map, String name) {
        try {
            return (Boolean) map.get(name);
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
    
    // <editor-fold defaultstate="collapsed" desc=" WebcamPaneListenerImpl "> 
    
    private class WebcamPaneListenerImpl implements WebcamPaneListener
    {
        WebcamViewer root = WebcamViewer.this;
        
        private JDialog dialog;
        
        WebcamPaneListenerImpl(JDialog dialog) {
            this.dialog = dialog; 
        }
        
        public void onselect(byte[] bytes) {
            Map options = root.options;
            Object source = (options == null? null: options.get("onselect"));
            if (source == null) return;
            
            CallbackHandlerProxy proxy = new CallbackHandlerProxy(source); 
            proxy.call(bytes); 
        } 

        public void oncancel() {
            dialog.dispose(); 
            root.onclose(); 
        }
    }
    
    private void onclose() {
        Object source = (options == null? null: options.get("onclose"));
        if (source == null) return;

        CallbackHandlerProxy proxy = new CallbackHandlerProxy(source); 
        proxy.call(); 
    }
    
    // </editor-fold>
}
