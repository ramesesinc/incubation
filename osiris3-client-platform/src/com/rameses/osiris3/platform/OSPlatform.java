/*
 * OSPlatform.java
 *
 * Created on October 24, 2013, 9:35 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.MainWindow;
import com.rameses.platform.interfaces.Platform;
import com.rameses.platform.interfaces.SubWindow;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
class OSPlatform implements Platform 
{
    private OSManager osManager; 
    private OSMainWindow osMainWindow; 
    private Map<String,Component> popups = new HashMap(); 
    
    public OSPlatform(OSManager osManager) {
        this.osManager = osManager;
        this.osMainWindow = osManager.getMainWindow(); 
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">

    void unregisterPopup(String id) {
        if (id != null) popups.remove(id); 
    } 
    
    void registerPopup(String id, Component comp) {
        if (id ==  null || comp == null) return;
        
        popups.put(id, comp);
    }
    
    Component findPopup(String id) {
        if (id == null) return null;
        
        return popups.get(id); 
    }
    
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Platform implementation ">
    
    public void showStartupWindow(JComponent actionSource, JComponent comp, Map props) {
        String id = (String) props.get("id");
        if (id == null || id.trim().length() == 0) 
            throw new IllegalStateException("id is required for a page.");
        
        if (osMainWindow.findWindow(id) != null) return;
        
        OSTabbedView view = new OSTabbedView(id, comp, this, false);
        view.setTitle((String) props.get("title"));
        osMainWindow.setComponent(view, MainWindow.CONTENT); 
    } 

    public void showWindow(JComponent actionSource, JComponent comp, Map props) {
        String id = (String) props.remove("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");
        
        if (osMainWindow.findWindow(id) != null) return;
        if (osMainWindow.findExplorer(id) != null) return;
        
        String title = (String) props.get("title");
        if (title == null || title.length() == 0) title = id;
        
        String canClose = (String) props.get("canclose");
        OSTabbedView view = new OSTabbedView(id, comp, this);
        view.setCanClose(!"false".equals(canClose));
        view.setTitle(title);
        
        String windowmode = (String) props.get("windowmode");
        if ("explorer".equals(windowmode)) { 
            osMainWindow.setComponent(view, windowmode); 
        } else { 
            osMainWindow.setComponent(view, MainWindow.CONTENT); 
        } 
    }

    public void showPopup(JComponent actionSource, JComponent comp, Map props) {
        String id = (String) props.remove("id");
        if (id == null || id.trim().length() == 0)
            throw new IllegalStateException("id is required for a page.");
        
        if (popups.containsKey(id)) return;
        
        String title = (String) props.get("title");
        if (title == null || title.length() == 0) title = id;
        
        Component parent = getParentWindow(actionSource);
        OSPopupDialog dialog = null;
        
        if (parent instanceof JDialog) 
            dialog = new OSPopupDialog((JDialog) parent);
        else if ( parent instanceof JFrame ) 
            dialog = new OSPopupDialog((JFrame) parent);
        else 
            dialog = new OSPopupDialog(); 
        
        if (!props.isEmpty()) setProperties(dialog, props);
        
        final OSPopupDialog dx = dialog;
        dx.setId(id);        
        dx.setModal(true);
        dx.setTitle(title);
        dx.setPlatformImpl(this);
        dx.setContentPane(comp);
        
        String modal = props.get("modal")+""; 
        if ("false".equalsIgnoreCase(modal)) {
            dx.setModal(false); 
        } 
        dx.pack();
        
        Dimension dim = dx.getSize();
        int width = toInt(props.get("width"));
        int height = toInt(props.get("height"));
        int pWidth = (width<=0? dim.width: width);
        int pHeight = (height<=0? dim.height: height); 
        dx.setSize(pWidth, pHeight); 
        dx.setLocationRelativeTo(parent);
        dx.setSource(actionSource);
        
        if ("false".equals(props.get("resizable")+"")) dx.setResizable(false);
        if ("true".equals(props.get("alwaysOnTop")+"")) dx.setAlwaysOnTop(true);
        if ("true".equals(props.get("undecorated")+"")) dx.setUndecorated(true); 
        
        KeyStroke ks = KeyStroke.getKeyStroke("ctrl shift I");  
        ActionListener al = new ShowInfoAction(comp); 
        JRootPane rootPane = dx.getRootPane(); 
        rootPane.registerKeyboardAction(al, ks, JComponent.WHEN_IN_FOCUSED_WINDOW); 
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                dx.setVisible(true);
            }
        });
        registerPopup(id, dx);
    }

    public void showFloatingWindow(JComponent owner, JComponent comp, Map props) {
        showPopup(owner, comp, props);
    }

    public boolean isWindowExists(String id) {
        if (popups.containsKey(id)) return true; 
        if (osMainWindow.findWindow(id) != null) return true; 
        
        return (osMainWindow.findExplorer(id) != null);
    }

    public void activateWindow(String id) {
        if (popups.containsKey(id)) {
            Component comp = popups.get(id);
            if (comp != null) comp.requestFocus(); 
            
        } else {
            Component comp = osMainWindow.findExplorer(id); 
            if (comp != null) { 
                comp.requestFocus();  
            } else {
                comp = osMainWindow.findWindow(id); 
                if (comp == null) return;
                
                if (comp instanceof OSTabbedView) {
                    ((OSTabbedView) comp).activate(); 
                } else {
                    comp.requestFocus(); 
                }
            }
        }
    }

    public void closeWindow(String id) {
        if (id == null) return;
        
        Component comp = popups.get(id);
        if (comp == null) comp = osMainWindow.findWindow(id); 
        if (comp == null) comp = osMainWindow.findExplorer(id); 
        if (comp instanceof SubWindow) {
            ((SubWindow) comp).closeWindow(); 
        }
    }

    public void showError(JComponent actionSource, Exception e) {
        ErrorDialog.show(e, actionSource); 
    }

    public boolean showConfirm(JComponent actionSource, Object message) {
        Component parent = getParentWindow(actionSource);
        int retval = JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return (retval== JOptionPane.YES_OPTION);
    }

    public void showInfo(JComponent actionSource, Object message) {
        Component parent = getParentWindow(actionSource);
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showAlert(JComponent actionSource, Object message) {
        Component parent = getParentWindow(actionSource);
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public Object showInput(JComponent actionSource, Object message) { 
        Component parent = getParentWindow(actionSource);
        return JOptionPane.showInputDialog(parent, message);
    }

    public MainWindow getMainWindow() {
        return osMainWindow;
    }

    public void shutdown() {
        osMainWindow.close(); 
    } 

    public void logoff() {
        closeAllPopups(); 
        osManager.reinitialize(); 
    }

    public void lock() {
    }

    public void unlock() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    private Window getParentWindow(JComponent source) {
        if (source == null ) {
            Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
            if (w != null && w.isShowing()) return w;

            return osMainWindow.getComponent();
        } 
        return SwingUtilities.getWindowAncestor(source);
    }
    
    private void setProperties(OSPopupDialog bean, Map props) {
        if ("false".equalsIgnoreCase(props.get("resizable")+"")) 
            bean.setResizable(false); 
        if ("true".equalsIgnoreCase(props.get("alwaysOnTop")+"")) 
            bean.setAlwaysOnTop(true); 
        if ("false".equalsIgnoreCase(props.get("enabled")+"")) 
            bean.setEnabled(false); 
        if ("true".equalsIgnoreCase(props.get("undecorated")+"")) 
            bean.setUndecorated(true); 
    }  
    
    private int toInt(Object value) 
    {
        if (value == null) 
            return -1; 
        else if (value instanceof Number)
            return ((Number) value).intValue();
        
        try {
            return Integer.parseInt(value.toString()); 
        } catch(Exception ex) {
            return -1; 
        } 
    } 
    
    private void closeAllPopups() {
        Iterator<Component> itr = popups.values().iterator(); 
        while (itr.hasNext()) {
            Component c = itr.next();
            if (c instanceof SubWindow) {
                ((SubWindow)c).closeWindow(); 
            }
        }
        popups.clear();
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ShowInfoAction ">
    
    private class ShowInfoAction implements ActionListener 
    {
        private Component source;
        
        ShowInfoAction(Component source) {
            this.source = source; 
        }
        
        public void actionPerformed(ActionEvent e) { 
            if (!(source instanceof ContentPane.View)) return; 
            
            ((ContentPane.View) source).showInfo(); 
        } 
    }
    
    // </editor-fold>    
}
