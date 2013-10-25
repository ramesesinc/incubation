/*
 * OSPopupDialog.java
 *
 * Created on July 26, 2010, 11:45 AM
 * @author jaycverg
 */
package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowListener;
import com.rameses.platform.interfaces.ViewContext;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class OSPopupDialog extends JDialog implements SubWindow, WindowListener 
{
    private ViewContext viewContext;
    private OSPlatform platformImpl;
    private boolean canClose = true;
    private String id;
    
    private JComponent source;
    
    public OSPopupDialog() {
        super();
        init();
    }
    
    public OSPopupDialog(JFrame parent) {
        super(parent);
        init();
    }
    
    public OSPopupDialog(JDialog parent) {
        super(parent);
        init();
    }
    
    private void init() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }
    
    public JComponent getSource() { return source; } 
    public void setSource(JComponent source) { this.source = source; }
    
    public void setContentPane(Container contentPane) 
    {
        super.setContentPane(contentPane);
        if ( contentPane instanceof ViewContext ) {
            viewContext = (ViewContext) contentPane;
        }
    }   
        
    public boolean isCanClose() { return canClose; }    
    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
    }
    
    OSPlatform getPlatformImpl() { return platformImpl; }    
    void setPlatformImpl(OSPlatform platformImpl) {
        this.platformImpl = platformImpl;
    }
    
    public String getId() { return id; }    
    public void setId(String id) { this.id = id; }
    
    
    // <editor-fold defaultstate="collapsed" desc=" SubWindow implementation ">
    
    public void setListener(SubWindowListener listener) {}
    
    public String getName() { return getId(); } 
    
    public void closeWindow()     
    {
        if ( !canClose ) return;
        if ( viewContext != null && !viewContext.close() ) return;
        
        super.dispose();
        platformImpl.unregisterPopup(id); 
        //notify others
        getContentPane().firePropertyChange("Window.close", 0L, 1L); 
        //
        JComponent source = getSource();
        if (source != null) source.firePropertyChange("Window.close", false, true);
    }    
    
    public void update(Map windowAttributes) {
        if (windowAttributes == null || windowAttributes.isEmpty()) return;

        Object otitle = windowAttributes.remove("title");
        if (otitle != null) setTitle(otitle.toString());
        
        Object oid = windowAttributes.remove("id");
        if (oid != null) {
            String newId = oid.toString();
            String oldId = getName();
            if (newId != null && oldId != null && !newId.equals(oldId)) {
                platformImpl.unregisterPopup(oldId);
                setId(newId);
                platformImpl.registerPopup(newId, this); 
            }
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" WindowListener implementation ">
    
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    
    public void windowClosing(WindowEvent e) {
        closeWindow();
    }
    
    public void windowOpened(WindowEvent e) 
    {
        if ( viewContext != null ) {
            viewContext.display();
            viewContext.setSubWindow(this);
        }
    }
    
    // </editor-fold>    

}
