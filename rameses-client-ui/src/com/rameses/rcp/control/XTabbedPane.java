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
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.support.ThemeUI;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.Warning;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class XTabbedPane extends JTabbedPane implements UIControl, MouseEventSupport.ComponentInfo 
{    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private boolean dynamic;
    private String disableWhen;
    private String handler;

    private int oldIndex;
    private List<Opener> openers = new ArrayList();
    private boolean nameAutoLookupAsOpener = false;
    
    private TabbedPaneModel model;
    private boolean noSelectionAllowed;
   
    private int stretchWidth;
    private int stretchHeight;     
    
    public XTabbedPane() {
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
        new MouseEventSupport(this).install(); 
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
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) { this.handler = handler; } 
        
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
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynammic", isDynamic());
        map.put("disableWhen", getDisableWhen()); 
        return map;
    }     
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
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
                
                if (itemPanel.hasContent()) {
                    itemPanel.refreshContent(); 
                } else {
                    itemPanel.loadContent();                     
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
        
        EventQueue.invokeLater(new Runnable() {
            public void run() { 
                fireSelectionChanged(); 
            }
        });
    }
    
    private void fireSelectionChanged() {
        String name = getName();
        if (name == null || name.length() == 0) return;

        Object value = null;
        Component comp = getSelectedComponent();
        if (comp instanceof TabbedItemPanel) {
            TabbedItemPanel p = (TabbedItemPanel) comp;
            value = p.getOpener();
        }
        
        try { 
            UIControlUtil.setBeanValue(getBinding(), name, value); 
            getBinding().notifyDepends(this); 
        } catch(Throwable t) {;}
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
                
                char mnemonic = '\u0000'; 
                Object propval = op.getProperties().get("mnemonic"); 
                String sval = (propval == null? null: propval.toString()); 
                if (sval != null && sval.length() > 0) {
                    mnemonic = propval.toString().charAt(0); 
                } 
                itemPanel.setMnemonic(mnemonic); 
                
                String scaption = op.getCaption(); 
                if (mnemonic != '\u0000') {
                    String findstr = mnemonic+"";
                    StringBuilder sb = new StringBuilder();
                    if (scaption != null) { 
                        sb.append(scaption); 
                    }
                    
                    int idx = sb.toString().toLowerCase().indexOf(findstr.toLowerCase());
                    if (idx >= 0) {
                        char c = sb.charAt(idx); 
                        sb.replace(idx, idx+1, "<u>"+c+"</u>");
                    }
                    scaption = "<html>"+ sb.toString() + "</html>"; 
                }

                super.addTab(scaption, getOpenerIcon(op), itemPanel); 
                
            } catch(Throwable t) {
                //do nothing 
            } finally {
                noSelectionAllowed = false;
            } 
            
            getKeyboardActionManager().register( itemPanel ); 
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
        String handler = getHandler();
        if (handler != null && handler.length() > 0) {
            if (handler.matches(".+:.+")) {
                value = openerProvider.lookupOpeners(handler, new HashMap()); 
            } else {
                value = UIControlUtil.getBeanValue(getBinding(), handler); 
            }
        }
        
        TabbedPaneModel newModel = null;
        if (value instanceof TabbedPaneModel) {
            newModel = (TabbedPaneModel)value; 
            newModel.setProvider(getProviderImpl()); 
        } else {
            newModel = new TabbedPaneModelImpl(value);
        }

        List<Opener> list = newModel.getOpeners(); 
        if (list != null) openers.addAll(list); 
        
        TabbedPaneModel oldModel = this.model;
        if (oldModel != null) oldModel.setProvider(null); 
        
        this.model = newModel; 
    } 
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" DefaultTabbedPaneModel ">
    
    private class TabbedPaneModelImpl extends TabbedPaneModel 
    {
        private List<Opener> list;
        
        TabbedPaneModelImpl(Object value) {
            list = new ArrayList();
            
            if (value == null) {
                //do nothing
            } else if (value.getClass().isArray()) {
                Opener[] arrays = (Opener[]) value;
                for (Opener o: arrays) list.add(o); 

            } else if (value instanceof Collection) {
                list.addAll((Collection) value);
            }  
        }

        public List<Opener> getOpeners() { 
            return list; 
        }
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

        public Object getSelectedItem() {
            Component comp = root.getSelectedComponent(); 
            if (comp instanceof TabbedItemPanel) {
                return ((TabbedItemPanel) comp).getOpener(); 
            } else {
                return null; 
            }
        }

        public int getSelectedIndex() {
            return root.getSelectedIndex(); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" TabbedItemKeyboardAction ">
    
    private KeyboardActionManager keyActionMgr;
    private KeyboardActionManager getKeyboardActionManager() {
        if (keyActionMgr == null) {
            keyActionMgr = new KeyboardActionManager();
        }
        return keyActionMgr; 
    }
    
    private class KeyboardActionManager  {
        
        XTabbedPane root = XTabbedPane.this;
        
        private void register(TabbedItemPanel itemPanel) {
            if (itemPanel == null) { return; } 
            
            char mchar = itemPanel.getMnemonic();
            if (mchar == '\u0000') { return; }
            
            TabbedItemSelector selector = new TabbedItemSelector(itemPanel, mchar);
            root.getActionMap().put(selector.key, selector);            
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(selector.ks, selector.key);
        } 
    } 
    
    private class TabbedItemSelector extends AbstractAction {
        
        XTabbedPane root = XTabbedPane.this;
        
        TabbedItemPanel panel; 
        KeyStroke ks;
        Object key;
        char mchar;
        
        TabbedItemSelector( TabbedItemPanel panel, char mchar ) {
            this.panel = panel; 
            this.ks = KeyStroke.getKeyStroke("alt " + Character.toUpperCase(mchar)); 
            this.key = "TabbedItemSelector_open_" + mchar;
        }
        
        public void actionPerformed(ActionEvent e) {
            int idx = root.indexOfComponent( panel ); 
            if (idx >= 0) {
                root.setSelectedIndex( idx ); 
            } else { 
                root.getActionMap().remove(key); 
            } 
        } 
    } 
    
    // </editor-fold>
}
