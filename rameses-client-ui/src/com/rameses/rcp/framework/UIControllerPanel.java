package com.rameses.rcp.framework;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.ViewContext;
import com.rameses.rcp.common.Opener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author jaycverg
 */
public class UIControllerPanel extends JPanel implements NavigatablePanel, ViewContext, ContentPane.View   
{    
    private Stack<UIControllerContext> controllers = new Stack();
    private boolean defaultBtnAdded;
    
    private SubWindow parent;
    
    
    public UIControllerPanel() {
        initComponent();
    } 
    
    public UIControllerPanel(UIControllerContext controller) {
        initComponent();
        controllers.push(controller);        
        _build();
    }
    
    private void initComponent() {
        super.setLayout(new BorderLayout());
        setName("root");
        
        //attach the default button when this panel is already
        //attached to its rootpane
        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                if ( getDefaultButton() != null && !defaultBtnAdded ) {
                    attachDefaultButton();
                }
            }
            
            public void ancestorMoved(AncestorEvent event) {}
            public void ancestorRemoved(AncestorEvent event) {}
        });  
    }
    
    //visible in the package
    void attachDefaultButton() {
        JRootPane rp = getRootPane();
        JButton btn = getDefaultButton();
        if ( btn != null && rp != null && rp.getDefaultButton() != btn ) {
            rp.setDefaultButton( btn );
            defaultBtnAdded = true;
        } else {
            defaultBtnAdded = false;
        }
    }
    
    private JButton getDefaultButton() {
        UIControllerContext current = getCurrentController();
        if ( current == null ) return null;
        if ( current.getCurrentView() == null ) return null;
        
        return current.getCurrentView().getBinding().getDefaultButton();
    }
    
    private void _build() {
        removeAll();

        UIViewPanel view = null; 
        UIControllerContext current = getCurrentController();
        if ( current != null ) {
            view = current.getCurrentView();
            Binding binding = view.getBinding();
            binding.setViewContext(this);
            
            Object viewname = view.getClientProperty("View.name"); 
            boolean activated = "true".equals(view.getClientProperty("UIViewPanel.activated")+"");
            if (!activated) {
                binding.fireActivatePage(viewname); 
                view.putClientProperty("UIViewPanel.activated", "true"); 
            } 
                        
            add( view ); 
            view.refresh(); 
            view.requestFocusInWindow();
            binding.focusFirstInput(); 
            binding.fireAfterRefresh(viewname);          
        } 
        
        //SwingUtilities.updateComponentTreeUI(this);
        Runnable proc = new Runnable() {
            public void run() { 
                revalidate();
                repaint();
            }
        };
        
        if ( EventQueue.isDispatchThread() ) {
            proc.run(); 
        } else {
            EventQueue.invokeLater( proc ); 
        } 
    }
        
    public void setLayout(LayoutManager mgr) {;}
    
    public Stack<UIControllerContext> getControllers() {
        return controllers;
    }
    
    public void setControllers(Stack<UIControllerContext> controllers) {
        this.controllers = controllers;
        _build();
    }
    
    public UIControllerContext getCurrentController() {
        if ( !controllers.empty() ) {
            return (UIControllerContext) controllers.peek();
        }
        return null;
    }
    
    public void renderView() {
        _build();
    }
    
    public boolean close() {
        try {
            return getCurrentController().getCurrentView().getBinding().close();
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    
    public void display() {
        UIControllerContext current = getCurrentController();
        if ( current != null ) {
            UIViewPanel p = current.getCurrentView();
            p.getBinding().focusFirstInput();
        }
    }
    
    public void setSubWindow(SubWindow subWindow) {
        this.parent = subWindow;
    }
    
    public SubWindow getSubWindow() {
        return parent;
    }  
    
    public void activate() {
        try {
            getCurrentController().getCurrentView().getBinding().activate(); 
        } catch( Throwable t ) {
            t.printStackTrace();
        }
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" ContentPane.View implementation ">
    
    public Map getInfo() {
        UIControllerContext current = getCurrentController(); 
        if (current == null) return null;
        
        UIController uic = current.getController(); 
        return (uic == null? null: uic.getInfo()); 
    }
    
    public void showInfo() {
        UIControllerContext current = getCurrentController(); 
        if (current == null) return;
        
        Map info = getInfo();
        if (info == null || info.isEmpty()) return;
        
        OpenerProvider op = ClientContext.getCurrentContext().getOpenerProvider(); 
        if (op == null) return;
        
        Map params = new HashMap(); 
        params.put("info", info); 
        Opener opener = null; 
        try { 
            opener = op.lookupOpener("workunit-info:show", params);
            opener.setTarget("popup"); 
        } catch(Throwable t) {;} 
        
        if (opener == null) return;
            
        UIViewPanel uiv = current.getCurrentView();
        Binding binding = (uiv == null? null: uiv.getBinding());
        if (binding == null) return;
        
        binding.fireNavigation(opener); 
    }
    
    // </editor-fold>
}
