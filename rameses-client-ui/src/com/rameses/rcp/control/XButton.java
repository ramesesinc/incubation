package com.rameses.rcp.control;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.util.UICommandUtil;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.ui.UICommand;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 *
 * @author jaycverg
 */
public class XButton extends JButton implements UICommand, ActionListener, ActiveControl 
{
    private int index;
    private String[] depends;
    private Binding binding;
    private boolean immediate;
    private boolean update;
    private ControlProperty property = new ControlProperty();
    private String target;
    private boolean defaultCommand;
    private String expression;
    private Map params = new HashMap();
    private String permission;
    private String visibleWhen;
    private String disableWhen;
    
    private String accelerator;
    private KeyStroke acceleratorKS;
    private String fontStyle;
        
    public XButton() {
        setOpaque(false);
        addActionListener(this);
    }
    
        
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }  
    
    public String getAccelerator() { return accelerator; }
    public void setAccelerator(String accelerator) {
        this.accelerator = accelerator;
        
        try {
            if (acceleratorKS != null) unregisterKeyboardAction(acceleratorKS);
            
            acceleratorKS = KeyStroke.getKeyStroke(accelerator);
            
            if (acceleratorKS != null)
                registerKeyboardAction(this, acceleratorKS, JComponent.WHEN_IN_FOCUSED_WINDOW);
        } catch(Exception ign) {;}
    }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String getActionName() { return getName(); }
    
    public boolean isImmediate() { return immediate; }
    public void setImmediate(boolean immediate) { this.immediate = immediate; }
    
    public ControlProperty getControlProperty() { return property; }
    
    public boolean isShowCaption() { 
        return property.isShowCaption(); 
    }
    public void setShowCaption(boolean show) { 
        property.setShowCaption(show); 
    }
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { 
        property.setCaption(caption); 
    }
    
    public int getCaptionWidth() { return property.getCaptionWidth(); }
    public void setCaptionWidth(int width) { 
        property.setCaptionWidth(width); 
    }
    
    public Font getCaptionFont() { return property.getCaptionFont(); }    
    public void setCaptionFont(Font f) { 
        property.setCaptionFont(f); 
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() { return property.getCellPadding(); }    
    public void setCellPadding(Insets padding) { 
        property.setCellPadding(padding);
    }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }
    
    public boolean isDefaultCommand() { return defaultCommand; }
    public void setDefaultCommand(boolean defaultCommand) {
        this.defaultCommand = defaultCommand;
    }
    
    public String getExpression() { return expression; }
    public void setExpression(String expression) {
        this.expression = expression;
        setText(expression);
    }
    
    public Map getParams() { return params; }
    public void setParams(Map params) { this.params = params; }
    
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    
    public String getVisibleWhen() { return visibleWhen; }
    public void setVisibleWhen(String visibleWhen) { this.visibleWhen = visibleWhen; }
    
    public String getDisableWhen() { return disableWhen; }
    public void setDisableWhen(String disableWhen) { this.disableWhen = disableWhen; }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {}    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UICommand implementation ">
    
    public void refresh() 
    {
        if (!ValueUtil.isEmpty(expression)) 
        {
            Object result = UIControlUtil.evaluateExpr(binding.getBean(), expression);
            setText((result==null? "": result.toString()));
        }
        
        if (!ValueUtil.isEmpty(visibleWhen)) 
        {
            boolean result = UIControlUtil.evaluateExprBoolean(binding.getBean(), visibleWhen);
            if ( !result ) 
                setVisible(false);
            else if (!isVisible()) 
                setVisible(true);
        }
        
        if (!ValueUtil.isEmpty(disableWhen)) 
        {
            boolean result = UIControlUtil.evaluateExprBoolean(binding.getBean(), disableWhen);
            if ( !result ) 
                setEnabled(true);
            else if (isEnabled()) 
                setEnabled(false);
        } 
    }
    
    public void load() {}
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void actionPerformed(ActionEvent e) {
        final Object outcome = UICommandUtil.processAction(this); 
        if (outcome instanceof PopupMenuOpener) {
            PopupMenuOpener menu = (PopupMenuOpener) outcome;
            List items = menu.getItems();
            if (items == null || items.isEmpty()) return;
            
            if (items.size() == 1 && menu.isExecuteOnSingleResult()) { 
                Object o = menu.getFirst(); 
                if (o instanceof Opener) 
                    UICommandUtil.processAction(XButton.this, getBinding(), (Opener)o); 
                else 
                    ((Action)o).execute(); 
            } 
            else { 
                EventQueue.invokeLater(new Runnable() {
                    public void run() { 
                        show((PopupMenuOpener) outcome); 
                    } 
                }); 
            }
        }
    }   
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PopupMenu Support ">    

    private JPopupMenu popup;
    
    protected void show(PopupMenuOpener menu) { 
        if (popup == null) 
            popup = new JPopupMenu(); 
        else 
            popup.setVisible(false); 
        
        popup.removeAll();         
        for (Object o: menu.getItems()) {
            ActionMenuItem ami = null; 
            if (o instanceof Opener) 
                ami = new ActionMenuItem((Opener)o);
            else 
                ami = new ActionMenuItem((Action)o);
            
            Dimension dim = ami.getPreferredSize();
            ami.setPreferredSize(new Dimension(Math.max(dim.width, 100), dim.height)); 
            popup.add(ami); 
        } 
        popup.pack();
        
        Rectangle rect = XButton.this.getBounds();
        popup.show(XButton.this, 0, rect.height); 
        popup.requestFocus(); 
    } 
    
    private class ActionMenuItem extends JMenuItem 
    {
        XButton root = XButton.this;
        private Object source;
        
        ActionMenuItem(Opener anOpener) {
            this.source = anOpener;
            setText(anOpener.getCaption());            
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    invokeOpener(e);
                }
            });
            
            Object ov = anOpener.getProperties().get("mnemonic");
            if (ov != null && ov.toString().trim().length() > 0) 
                setMnemonic(ov.toString().trim().charAt(0));
            
            ov = anOpener.getProperties().get("icon");
            if (ov != null && ov.toString().length() > 0) 
                setIcon(ImageIconSupport.getInstance().getIcon(ov.toString()));
        }
        
        ActionMenuItem(Action anAction) {
            this.source = anAction;
            setText(anAction.getCaption());            
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    invokeAction(e);
                }
            });
            
            setMnemonic(anAction.getMnemonic()); 
            
            String sicon = anAction.getIcon();
            if (sicon != null && sicon.length() > 0) 
                setIcon(ImageIconSupport.getInstance().getIcon(sicon));
        }        
        
        void invokeOpener(ActionEvent e) {
            try {
                UICommandUtil.processAction(root, root.getBinding(), (Opener) source); 
            } catch(Exception ex) { 
                MsgBox.err(ex); 
            } 
        } 
        
        void invokeAction(ActionEvent e) {
            try { 
                Object outcome = ((Action) source).execute(); 
                if (outcome instanceof Opener) 
                    UICommandUtil.processAction(root, root.getBinding(), (Opener)outcome); 
            } catch(Exception ex) { 
                MsgBox.err(ex); 
            } 
        }         
    }
    
    // </editor-fold>    
    
}
