/*
 * XHtmlView.java
 *
 * Created on August 26, 2013, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.DocViewModel;
import com.rameses.rcp.common.HtmlViewModel;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.Task;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.Beans;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;

/**
 *
 * @author wflores
 */
public class XHtmlView extends JEditorPane implements UIControl, ActiveControl 
{
    private ControlProperty controlProperty;    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String fontStyle;
    private DocViewModel docModel; 
    
    public XHtmlView() {
        super(); 
        super.setContentType("text/html");
        super.setEditable(false); 
        addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                hyperlinkUpdateImpl(e); 
            }
        });
        
        try { 
            Font font = UIManager.getLookAndFeelDefaults().getFont("TextField.font"); 
            super.setFont(font); 
        } catch(Throwable t) {;} 
        
        if (Beans.isDesignTime()) {
            setPreferredSize(new Dimension(100,50));
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation "> 

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public void load() { 
        //setInputVerifier(UIInputUtil.VERIFIER);
    }

    public void refresh() { 
        DocViewModel newModel = null; 
        try {
            Object value = UIControlUtil.getBeanValue(this);            
            if (value instanceof DocViewModel) {
                newModel = (DocViewModel) value; 
                value = newModel.getValue();
            } 
            
            URL url = null;
            if (value == null) {
                //do nothing 
            } else if (value instanceof URL) {
                url = (URL) value;
            } else if (value.toString().startsWith("http://")) {
                url = new URL(value.toString()); 
            } else { 
                setText(value.toString()); 
            } 
            
            if (newModel != null) newModel.setProvider(new ViewProviderImpl());                
            if (docModel != null) docModel.setProvider(null);
            
            docModel = newModel; 
            if (url != null) { 
                URLWorkerTask uwt = new URLWorkerTask(url);
                ClientContext.getCurrentContext().getTaskManager().addTask(uwt); 
            }
        } catch(Throwable t) {
            setText("");
            if (newModel != null) newModel.setProvider(null); 
            
            System.out.println("[WARN] refresh failed caused by " + t.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
        } finally {
            
        }
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    public ControlProperty getControlProperty() { 
        if (controlProperty == null) {
            controlProperty = new ControlProperty();
        }
        return controlProperty; 
    }   
    
    public boolean isRequired() { 
        return getControlProperty().isRequired(); 
    }    
    public void setRequired(boolean required) {
        getControlProperty().setRequired(required);
    }
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
    } 
    
    public char getCaptionMnemonic() { 
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }    
    
    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    }    
    public void setShowCaption(boolean showCaption) {
        getControlProperty().setShowCaption(showCaption);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private void hyperlinkUpdateImpl(HyperlinkEvent e) {
        if (!(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)) return;

        Map params = new HashMap(); 
        AttributeSet aset = e.getSourceElement().getAttributes();
        Enumeration en = aset.getAttributeNames();
        while (en.hasMoreElements()) {
            Object k = en.nextElement();
            Object v = aset.getAttribute(k);
            if (v instanceof AttributeSet) {
                AttributeSet vset = (AttributeSet) v; 
                Enumeration ven = vset.getAttributeNames();
                while (ven.hasMoreElements()) {
                    Object vk = ven.nextElement();
                    Object vv = vset.getAttribute(vk); 
                    params.put(vk.toString(), vv); 
                }
            }
        } 
        
        Object href = params.get("href");
        if (href == null) return;
        
        String shref = href.toString();
        if (!shref.matches("[a-zA-Z0-9_]{1,}")) return;
        
        Object outcome = null; 
        try { 
            MethodResolver mresolver = MethodResolver.getInstance();
            outcome = mresolver.invoke(getBinding().getBean(), shref, new Object[]{params}); 
        } catch(Throwable t) {
            System.out.println("[WARN] error invoking method '"+shref+"' caused by " + t.getMessage()); 
        } 
        
        if (outcome instanceof Opener) {
            getBinding().fireNavigation((Opener)outcome); 
        } 
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" ViewProviderImpl (class) "> 
    
    private class ViewProviderImpl implements HtmlViewModel.ViewProvider 
    {        
        XHtmlView root = XHtmlView.this;
        
        public void insertText(String text) {}

        public String getText() { return root.getText(); } 
        public void setText(String text) {}

        public void load() {}

        public void refresh() {
            root.refresh(); 
        } 
        
        public void requestFocus() { 
            if (!root.isEnabled()) return;
            
            root.grabFocus(); 
            root.requestFocusInWindow(); 
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" URLWorkerTask (class) "> 
    
    private class URLWorkerTask extends Task { 
        
        XHtmlView root = XHtmlView.this;
        
        private URL url;
        private boolean done;
        
        URLWorkerTask(URL url) {
            this.url = url; 
        }
        
        public boolean accept() {
            return (done? false: true); 
        }

        public void execute() {
            try {
                root.setText("<html><body><b>Loading...</b></body></html>");
                if (url != null) root.setPage(url);                 
            } catch(Throwable t) { 
                root.setText("<html><body color=\"red\">error caused by "+t.getMessage()+"</body></html>"); 
                
                if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
            } finally { 
                done = true; 
            } 
        }    
    }
    
    // </editor-fold>
}
