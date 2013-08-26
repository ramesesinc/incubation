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
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author wflores
 */
public class XHtmlView extends JEditorPane implements UIControl //, ActiveControl 
{    
    private Binding binding;
    private String[] depends;
    private int index;
    private boolean nullWhenEmpty = true;
    private boolean readonly;
    private String linkAction;
    private String baseUrl;
    
    private ControlProperty property = new ControlProperty();
    
    
    public XHtmlView() {
        super();
        
        super.setContentType("text/html");
        super.setEditable(false); 
        addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                hyperlinkUpdateImpl(e); 
            }
        });
    }

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
        try {
            Object value = UIControlUtil.getBeanValue(this);
            if (value == null) {
                //do nothing 
            } else if (value instanceof URL) {
                ((HTMLDocument) getDocument()).setBase((URL)value);
            } else if (value.toString().startsWith("http://")) {
                URL url = new URL(value.toString()); 
                ((HTMLDocument) getDocument()).setBase(url);
            } else {
                setText(value.toString()); 
            }
        }
        catch(Throwable t) {
            setText("");
            System.out.println("[WARN] refresh failed caused by " + t.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
        } 
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
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
}
