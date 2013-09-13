/*
 * XTabbedPane.java
 *
 * Created on October 1, 2010, 4:50 PM
 * @author jaycverg
 */

package com.rameses.rcp.control; 

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener; 
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TabbedPaneModel;
import com.rameses.rcp.control.tabbedpane.TabbedItemPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.OpenerProvider;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.ThemeUI;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.Warning;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class XTabbedPane extends JTabbedPane implements UIControl 
{    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private boolean dynamic;
    private String disableWhen;
    
    private int oldIndex;
    private List<Opener> openers = new ArrayList();
    private boolean nameAutoLookupAsOpener = false;
    
    private TabbedPaneModel model;
    private boolean noSelectionAllowed;
        
    public XTabbedPane() 
    {
        super();
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents ">
    
    private void initComponents() 
    {
        if (Beans.isDesignTime()) {
            addTab("Tab 1", new JPanel());
            addTab("Tab 2", new JPanel());
        }

        setPreferredSize(new Dimension(100,50)); 
        Font f = ThemeUI.getFont("XTabbedPane.font");
        if ( f != null ) setFont( f );        
    }
    
    //</editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">
    
    public boolean isNameAutoLookupAsOpener() { return nameAutoLookupAsOpener; }    
    public void setNameAutoLookupAsOpener(boolean nameAutoLookupAsOpener) {
        this.nameAutoLookupAsOpener = nameAutoLookupAsOpener;
    }        
        
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public String getDisableWhen() { return disableWhen; } 
    public void setDisableWhen(String disableWhen) {
        this.disableWhen = disableWhen;
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">    
    
    public void setBinding(Binding binding) { this.binding = binding; }
    public Binding getBinding() { return binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
        
    public void load() { 
        try {
            if ( !dynamic ) loadTabs();
        } catch(Throwable t) {
            System.out.println("[WARN] error loading tabs caused by " + t.getMessage());
        }
    }
    
    public void refresh() {
        boolean dynamic = isDynamic();
        try {
            if (dynamic) loadTabs();
        } catch(Throwable t) {
            System.out.println("[WARN] error loading tabs caused by " + t.getMessage()); 
        } 

        ExpressionResolver er = ExpressionResolver.getInstance();
        String expr = getDisableWhen();
        if (expr != null && expr.length() > 0) {
            try {
                boolean b = er.evalBoolean(expr, getBinding().getBean()); 
                setEnabled(!b); 
            } catch(Throwable t){;} 
        } 
        
        if (!dynamic) {
            Component comp = getSelectedComponent(); 
            if (comp instanceof TabbedItemPanel) {
                TabbedItemPanel itemPanel = (TabbedItemPanel)comp;
                itemPanel.refreshContent(); 
            }
        }
        
        int tabCount = getTabCount();
        for (int i=0; i<tabCount; i++) {
            Component c = getComponentAt(i);
            if (!(c instanceof TabbedItemPanel)) continue;
            
            Opener op = ((TabbedItemPanel) c).getOpener(); 
            Object ov = op.getProperties().get("disableWhen");
            String disableWhen = (ov == null? null: ov.toString()); 
            if (disableWhen == null || disableWhen.length() == 0) continue;
            
            try {
                boolean b = er.evalBoolean(disableWhen, getBinding().getBean()); 
                setEnabledAt(i, !b); 
            } catch(Throwable t){;} 
        }
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    public void setSelectedIndex(int index) { 
        if (noSelectionAllowed) return; 
        
        try { 
            Component c = getComponentAt(index);
            if (c instanceof TabbedItemPanel) {
                TabbedItemPanel itemPanel = (TabbedItemPanel)c; 
                Opener opener = itemPanel.getOpener(); 
                if (model != null) { 
                    boolean b = model.beforeSelect(opener, index);  
                    if (!b) return; 
                } 
                
                if (!itemPanel.hasContent()) {
                    itemPanel.loadContent(); 
                } else {
                    itemPanel.refreshContent(); 
                }
            }
        } catch(Throwable t) {
            if (t instanceof Warning) {
                MsgBox.alert(t.getMessage()); 
            } else { 
                MsgBox.err(t); 
            } 
            return;
        }
        
        this.oldIndex = getSelectedIndex();
        super.setSelectedIndex(index);
    }
    
    private void loadTabs() {
        loadOpeners();
        removeAll();
                
        ExpressionResolver expRes = ExpressionResolver.getInstance();
        for (Opener op: openers) {
            Object ov = op.getProperties().get("visibleWhen");
            String sv = (ov == null? null: ov.toString()); 
            boolean allowed = true;
            if (sv != null && sv.length() > 0) {
                try {
                    allowed = expRes.evalBoolean(sv, getBinding().getBean()); 
                } catch(Throwable t){;} 
            }
            if (!allowed) continue;

            TabbedItemPanel itemPanel = new TabbedItemPanel(op);
            itemPanel.setProvider(new TabbedItemProvider()); 
            try {
                noSelectionAllowed = true;
                super.addTab(op.getCaption(), getOpenerIcon(op), itemPanel); 
            } catch(Throwable t) {
                //do nothing 
            } finally {
                noSelectionAllowed = false;
            }            
        }
        
        if (getTabCount() > 0) setSelectedIndex(0);        
    }
    
    private Icon getOpenerIcon(Opener o) {
        Object ov = o.getProperties().get("icon");
        if (ov != null) { 
            return ImageIconSupport.getInstance().getIcon(ov.toString());
        } else { 
            return null; 
        }
    }
    
    private void loadOpeners() {
        openers.clear();
        
        OpenerProvider openerProvider = ClientContext.getCurrentContext().getOpenerProvider(); 
        if (openerProvider == null) {
            System.out.println("[WARN] opener provider is not set in ClientContext");
            return;
        }
        
        Object value = null;
        String name = getName();
        if (name != null && name.length() > 0) {
            if (name.matches(".+:.+")) {
                value = openerProvider.lookupOpeners(name, new HashMap()); 
            } else {
                try { 
                    value = UIControlUtil.getBeanValue(this); 
                } catch(Throwable t){;}
            }
        }
        
        TabbedPaneModel newModel = null;
        if (value instanceof TabbedPaneModel) {
            newModel = (TabbedPaneModel)value; 
            newModel.setProvider(getProviderImpl()); 
            value = newModel.getOpeners(); 
        } 

        if (value == null) {
            //do nothing
        } else if (value.getClass().isArray()) {
            Opener[] arrays = (Opener[]) value;
            for (Opener o: arrays) openers.add(o); 
            
        } else if (value instanceof Collection) {
            openers.addAll((Collection) value);
        } 
        
        if (this.model != null) this.model.setProvider(null);
        
        this.model = newModel; 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl ">
    
    private ProviderImpl providerImpl; 
    
    private ProviderImpl getProviderImpl() {
        if (providerImpl == null) {
            providerImpl = new ProviderImpl();
        } 
        return providerImpl;
    }
    
    private class ProviderImpl implements TabbedPaneModel.Provider 
    {
        XTabbedPane root = XTabbedPane.this;
        
        public Object getBinding() {
            return root.getBinding(); 
        }        

        public Object lookupOpener(String invokerType, Map params) { 
            OpenerProvider openerProvider = ClientContext.getCurrentContext().getOpenerProvider(); 
            if (openerProvider == null) {
                System.out.println("[WARN] opener provider is not set in ClientContext");
                return new ArrayList(); 
            }              
            return openerProvider.lookupOpener(invokerType, params); 
        } 
        
        public List lookupOpeners(String invokerType, Map params) { 
            OpenerProvider openerProvider = ClientContext.getCurrentContext().getOpenerProvider(); 
            if (openerProvider == null) {
                System.out.println("[WARN] opener provider is not set in ClientContext");
                return new ArrayList(); 
            }              
            return openerProvider.lookupOpeners(invokerType, params); 
        } 

        public void reload() { 
            Component comp = root.getSelectedComponent(); 
            if (!(comp instanceof TabbedItemPanel)) return;
            
            TabbedItemPanel itemPanel = (TabbedItemPanel)comp;
            itemPanel.reloadContent(); 
        }
        
        public void refresh() { 
            Component comp = root.getSelectedComponent(); 
            if (!(comp instanceof TabbedItemPanel)) return;
            
            TabbedItemPanel itemPanel = (TabbedItemPanel)comp;
            itemPanel.refreshContent(); 
        }        
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" TabbedItemProvider ">
    
    private class TabbedItemProvider implements TabbedItemPanel.Provider
    {
        XTabbedPane root = XTabbedPane.this;

        public Binding getBinding() {
            return root.getBinding(); 
        }

        public Map getOpenerParams(Object o) {
            if (root.model == null) return null;
            
            return root.model.getOpenerParams(o); 
        }
    }
    
    // </editor-fold>
}
