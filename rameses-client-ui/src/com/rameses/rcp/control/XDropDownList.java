/*
 * XDropDownList.java
 *
 * Created on June 9, 2014, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.DataListModel;
import com.rameses.rcp.common.ObjectProxy;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.border.XEtchedBorder;
import com.rameses.rcp.control.list.ListPane;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.ErrorDialog;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

/**
 *
 * @author wflores 
 */
public class XDropDownList extends JButton implements UIControl, ActiveControl 
{
    private Binding binding;
    private String[] depends;
    private int index;
    private ControlProperty property;

    private String varName = "item";    
    private String visibleWhen;
    private String expression;
    private String itemExpression;
    private String handler; 
    private Object handlerObject; 
    private Object modelObject;
    private MetaInfo metaInfo;
    private AbstractListDataProvider model; 
    private ActionListenerImpl actionListener;

    private String title;
    private String iconResource; 
    private String accelerator;
    private KeyStroke acceleratorKS;  

    private Dimension popupSize;
    private int cellHeight;
    
    public XDropDownList() {
        initComponent(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent ">
    
    private void initComponent() {
        addActionListener(getActionListener()); 
        setCellHeight(-1);
    }
    
    private ActionListener getActionListener() {
        if (actionListener == null) {
            actionListener = new ActionListenerImpl();
        }
        return actionListener;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getExpression() { return expression; } 
    public void setExpression(String expression) {
        this.expression = expression; 
    }
    
    public String getItemExpression() { return itemExpression; } 
    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression; 
    }    
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    }
    
    public Object getHandlerObject() { return handlerObject; }
    public void setHandlerObject(Object handlerObject) {
        this.handlerObject = handlerObject;
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) {
        this.varName = varName; 
    }
    
    public String getAccelerator() { return accelerator; }
    public void setAccelerator(String accelerator) {
        this.accelerator = accelerator;
        
        try {
            if (acceleratorKS != null) unregisterKeyboardAction(acceleratorKS);
            
            acceleratorKS = KeyStroke.getKeyStroke(accelerator);
            
            if (acceleratorKS != null)
                registerKeyboardAction(getActionListener(), acceleratorKS, JComponent.WHEN_IN_FOCUSED_WINDOW);
        } catch(Exception ign) {;}
    }    
    
    public String getIconResource() { return iconResource; } 
    public void setIconResource(String iconResource) {
        this.iconResource = iconResource; 
        setIcon(ImageIconSupport.getInstance().getIcon(iconResource)); 
    } 
    
    public String getTitle() { return title; } 
    public void setTitle(String title) {
        this.title = title; 
    }
    
    public int getCellHeight() { return cellHeight; } 
    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }
    
    public Dimension getPopupSize() { return popupSize; } 
    public void setPopupSize(Dimension popupSize) {
        this.popupSize = popupSize; 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Font support implementation ">
    
    private FontSupport fontSupport;
    private Font sourceFont;     
    private String fontStyle; 
    
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }    
    
    public void setFont(Font font) { 
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            if (!attrs.isEmpty()) sourceFont = sourceFont.deriveFont(attrs);
        }
        
        super.setFont(sourceFont); 
    }     
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        
        if (sourceFont == null) sourceFont = super.getFont(); 
        
        Font font = sourceFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        if (!attrs.isEmpty()) font = font.deriveFont(attrs); 
        
        super.setFont(font); 
    } 
    
    // </editor-fold>        
 
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex(int index) {
        this.index = index; 
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o); 
    } 
        
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public void load() { 
    }

    public void refresh() { 
        Binding binding = getBinding(); 
        Object bean = (binding == null? null: binding.getBean()); 
        Object value = UIControlUtil.getBeanValue(bean, getName()); 
        
        try { 
            Object handlerObj = getHandlerObject();
            String handler = getHandler();      
            if (handler != null && handler.length() > 0) {
                handlerObj = UIControlUtil.getBeanValue(bean, handler);
            }
            
            if (handlerObj instanceof AbstractListDataProvider) {
                model = (AbstractListDataProvider) handlerObj;
            } else if (handlerObj instanceof List) {
                model = new ListModelImpl((List)handlerObj); 
            } else if (handlerObj instanceof Object[]) {
                model = new ListModelImpl((Object[])handlerObj); 
            } else {
                model = new ListModelImpl(handlerObj); 
            } 
            
            modelObject = handlerObj;
            metaInfo = new ObjectProxy().create(modelObject, MetaInfo.class); 
            
            setText(value+""); 
            String expression = getExpression();
            if (expression != null && expression.length() > 0) {
                Object exprBean = createExpressionBean(value); 
                Object result = UIControlUtil.evaluateExpr(exprBean, expression);
                setText(result == null? "": "<html>"+result+"</html>"); 
            }             
        } catch(Throwable t) {
            setText("");
            ErrorDialog.show(t, this); 
        }
        
        try {
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                Object exprBean = createExpressionBean(value); 
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(exprBean, visibleWhen);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                setVisible(b); 
            }             
        } catch(Throwable t) {;}         
    }
    
    private Object createExpressionBean(Object itemBean) { 
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    public ControlProperty getControlProperty() {
        if (property == null) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font font) {
        getControlProperty().setCaptionFont(font);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
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
        
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }
    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ListModelImpl ">
    
    private class ListModelImpl extends DataListModel  
    {
        private List list;
        private CallbackHandlerProxy callback;
        
        ListModelImpl(List list) {
            this.list = list; 
        }
        
        ListModelImpl(Object[] values) {
            list = Arrays.asList(values); 
        } 
        
        ListModelImpl(Object value) {
            list = new ArrayList();
            if (value == null) return;
            
            if (isCallback(value)) { 
                callback = new CallbackHandlerProxy(value); 
            } else {
                list.add(value); 
            } 
        }

        public List fetchList(Map params) {
            if (callback == null) {
                return list; 
            } else {
                return (List) callback.call(params); 
            }
        }
        
        
        private boolean isCallback(Object value) {
            if (value == null) return false; 
            
            try {
                Method method = value.getClass().getMethod("call", new Class[]{Object.class});  
                return (method == null? false: true); 
            } catch(Throwable t) {
                return false; 
            }
        }
    } 
    
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" popup helper methods ">
    
    private class ActionListenerImpl implements ActionListener, ListPane.Handler 
    { 
        XDropDownList root = XDropDownList.this;
                
        public void actionPerformed(ActionEvent e) {
            if (root.model == null) return;

            JPopupMenu jpm = root.getPopup();
            jpm.setVisible(false);

            final ListPane lp = new ListPane() {
                protected String getItemText(Object item) {
                    return root.getItemText(item); 
                }
            };
            lp.setCellHeight(root.getCellHeight()); 
            lp.addHandler(this); 
            lp.setModel(root.model);
            
            Dimension dim = lp.getPreferredSize();
            if (dim.width < 200) dim.width = 200;

            lp.setPreferredSize(new Dimension(dim.width, dim.height)); 
            jpm.removeAll();
            
            Dimension popdim = root.getPopupSize();
            if (popdim != null) lp.setPreferredSize(popdim); 

            String title = root.getPreferredTitle(); 
            if (title != null && title.length() > 0) { 
                XEtchedBorder border = new XEtchedBorder(); 
                border.setPadding(new Insets(3,5,3,5)); 
                border.setHideTop(true); 
                border.setHideLeft(true); 
                border.setHideRight(true); 

                JLabel header = new JLabel(); 
                header.setBorder(border); 
                header.setText("<html>"+ title +"</html>"); 
                jpm.add(header); 
            } 
            jpm.add(lp); 
            jpm.pack();
            
            
            Rectangle rect = root.getBounds(); 
            jpm.show(root, 0, rect.height); 
            lp.requestFocus(); 
        }        

        public void onselect(Object item) {
            root.onselectImpl(item); 
        }
    } 
    
    protected String getItemText(Object item) {
        String expression = getItemExpression();
        if (expression == null || expression.length() == 0) {
            return (item == null? null: item.toString()); 
        } 
        
        Object exprBean = createExpressionBean(item); 
        Object result = UIControlUtil.evaluateExpr(exprBean, expression); 
        return (result == null? null: result.toString()); 
    }
    
    private void onselectImpl(Object item) {
        if (model == null) return;
        
        Method method = getCallbackMethod(modelObject, "onselect"); 
        if (method == null) {
            getPopup().setVisible(false); 
            return;
        }
        
        try { 
            Object outcome = metaInfo.onselect(item); 
            
            getPopup().setVisible(false); 
            transferFocus(); 
            if (outcome instanceof Opener) {
                getBinding().fireNavigation(outcome); 
            } 
        } catch(Throwable t) {
            ErrorDialog.show(t, this); 
        }
    }
    
    
    private JPopupMenu jpopup;
    private JPopupMenu getPopup() {
        if (jpopup == null) {
            jpopup = new JPopupMenu();
            jpopup.setLayout(new PopupLayout()); 
            jpopup.setLightWeightPopupEnabled(true); 
            
            Border bout = BorderFactory.createLineBorder(Color.decode("#808080"));
            Border bin = BorderFactory.createEmptyBorder(1,1,1,1);
            jpopup.setBorder(BorderFactory.createCompoundBorder(bout, bin)); 
        }
        return jpopup;
    }  
        
    private Method getCallbackMethod(Object obj, String name) {
        if (obj == null || name == null) return null;
        
        Class clazz = obj.getClass();
        try {
            Method m = clazz.getMethod(name, new Class[]{Object[].class});
            if (m != null) return m; 
        } catch(Throwable t) {;} 
        
        try {
            Method m = clazz.getMethod(name, new Class[]{Object.class});
            if (m != null) return m; 
        } catch(Throwable t) {;} 
        
        return null; 
    }
    
    private Method getGetterMethod(Object obj, String name) {
        if (obj == null || name == null) return null;
        
        try {
            Method m = obj.getClass().getMethod(name, new Class[]{});
            if (m != null) return m; 
        } catch(Throwable t) {;} 
        
        return null; 
    }    
    
    private String getPreferredTitle() {
        
        try {
            Object result = metaInfo.getTitle(); 
            if (result != null) return result.toString(); 
        } catch(Throwable t){;} 
        
        return getTitle(); 
    }
        
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" PopupLayout ">
    
    private class PopupLayout implements LayoutManager 
    {
        XDropDownList root = XDropDownList.this;
                
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    
                    Dimension dim = c.getPreferredSize();
                    w = Math.max(w, dim.width); 
                    h += dim.height;
                }
                
                Insets margin = parent.getInsets();                
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w, h);
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);                
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue; 
                    
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, w, dim.height); 
                    y += dim.height; 
                }                
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" MetaInfo ">
    
    public static interface MetaInfo { 
        Object onselect(Object value); 
        Object getTitle();
        int getRowHeight();
        int getHeight();
        int getWidth();
    }
    
    // </editor-fold>
    
}