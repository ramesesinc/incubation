/*
 * OpenerTextField.java
 *
 * Created on June 10, 2013, 5:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.common.DefaultCallbackHandler;
import com.rameses.rcp.common.LookupOpenerSupport;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.DefaultTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.UIInputWrapper;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.PanelUI;

/**
 *
 * @author wflores
 */
public class XOpenerField extends JPanel implements UIInputWrapper, Validatable, ActiveControl  
{
    private Dimension minimumSize = new Dimension(0,0);
    private DefaultTextFieldImpl textField; 
    private JButton button;
    private Layout layout;
    private Border border;

    private ControlProperty property = new ControlProperty();
    private ActionMessage actionMessage = new ActionMessage();    
    private Binding binding;    
    private Object handlerObject;    
    private String handler;
    private String expression;    
    private String varName = "item";
    private String inputFormat;  
    private String inputFormatErrorMsg;
    private String[] depends;    
    private boolean nullWhenEmpty = true;
    private int index;
    
    private TextDocument document;
    private TrimSpaceOption trimSpaceOption;
    private Object value;

    public XOpenerField() 
    {
        super();
        super.setLayout((layout = new Layout()));         
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents ">  
    
    private void initComponents() 
    {
        textField = new DefaultTextFieldImpl(); 
        textField.setDocument(document = new TextDocument());
        textField.putClientProperty(UIInput.class, this); 
        textField.putClientProperty(UIControl.class, this); 
        
        button = new JButton("...");
        button.setMargin(new Insets(0,0,0,0)); 
        button.setFocusable(false); 

        Dimension dim = textField.getPreferredSize();
        minimumSize = new Dimension(50, dim.height);
        setPreferredSize(new Dimension(Math.max(dim.width,100), dim.height)); 
        
        Border border = textField.getBorder(); 
        Insets margin = textField.getMargin();
        textField.setBorder(BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right)); 
        super.setBorder(border);         
        add(textField);
        add(button);

        document.setTextCase(TextCase.UPPER); 
        trimSpaceOption = TrimSpaceOption.NORMAL;
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireButtonClicked(e);
            }
        });
        
        new KeyboardAction().install(textField); 
    }

    public void removeFocusListener(FocusListener listener) 
    {
        if (listener != null) textField.removeFocusListener(listener);
    }
    
    public void addFocusListener(FocusListener listener) 
    { 
        if (listener != null) 
        {
            textField.removeFocusListener(listener);
            textField.addFocusListener(listener); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">  
    
    public LayoutManager getLayout() { return layout; }    
    public final void setLayout(LayoutManager mgr) {} 
    
    public Border getBorder() { return border; } 
    public void setBorder(Border border) 
    {
        this.border = border; 
        super.setBorder(border); 
    }
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) { this.handler = handler; }
    
    public Object getHandlerObject() { return handlerObject; } 
    public void setHandlerObject(Object handlerObject) {
        this.handlerObject = handlerObject; 
    }
    
    public String getExpression() { return expression; } 
    public void setExpression(String expression) {
        this.expression = expression; 
    }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public String getInputFormat() { return inputFormat; }    
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }
    
    public String getInputFormatErrorMsg() { return inputFormatErrorMsg; }    
    public void setInputFormatErrorMsg(String inputFormatErrorMsg) {
        this.inputFormatErrorMsg = inputFormatErrorMsg;
    }    
    
    public boolean isEditable() { return textField.isEditable(); } 
    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    } 

    public void setEnabled(boolean enabled) 
    {
        super.setEnabled(enabled); 
        textField.setEnabled(enabled);
        button.setEnabled(enabled); 
        
        boolean readonly = isReadonly(); 
        textField.setEditable(!readonly);
        button.setEnabled(!readonly);
    }
    
    public TextCase getTextCase() { 
        return document.getTextCase(); 
    } 
    public void setTextCase(TextCase textCase) { 
        document.setTextCase(textCase); 
    } 
    
    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }    
    public void setTrimSpaceOption(TrimSpaceOption trimSpaceOption) {
        this.trimSpaceOption = trimSpaceOption;
    } 
    
    public void setName(String name) 
    {
        super.setName(name);
        textField.setName(name);  
        
        if (Beans.isDesignTime()) textField.setText(name); 
    }    

    public void setUI(PanelUI ui) 
    {
        super.setUI(ui); 
                
        if ("true".equals(getClientProperty(JTable.class)+"")) {
            setBorder(BorderFactory.createEmptyBorder()); 
        }
        else 
        {
            Border border = UIManager.getLookAndFeelDefaults().getBorder("TextField.border");
            setBorder(border); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" owner methods ">  
    
    private void fireButtonClicked(ActionEvent e) 
    {
        try 
        {
            textField.getInputVerifierProxy().setEnabled(false);
            textField.allowSelectAll = false;
            onButtonClicked(e); 
        } 
        catch(Exception ex) {
            MsgBox.err(ex); 
        }
        finally 
        {
            textField.getInputVerifierProxy().setEnabled(true);  
        } 
    }
    
    
    public void onButtonClicked(ActionEvent e) { 
        processHandler();
    }
    
    protected Opener getOpener() 
    {
        Object result = null;
        String handler = getHandler();
        if ( !ValueUtil.isEmpty(handler) ) 
        {
            if ( handler.matches(".+:.+") ) 
            {
                //handler is an invoker type name
                result = LookupOpenerSupport.lookupOpener(handler, new HashMap()); 
            }
            else 
            {
                //check if there is a binding object passed by the JTable
                Binding oBinding = (Binding) getClientProperty(Binding.class); 
                if (oBinding == null) oBinding = getBinding(); 
                
                result = UIControlUtil.getBeanValue(oBinding, handler);
            }
        } 
        else if ( handlerObject != null ) { 
            result = handlerObject; 
        } 
        
        return (Opener) result;
    }
    
    private void processHandler() 
    {
        if (Beans.isDesignTime()) return;
        if (isReadonly()) return;

        Opener opener = getOpener();
        if (opener == null) 
        {
            MsgBox.alert("No available opener handler specified");
            return;
        }
        
        opener = ControlSupport.initOpener(opener, getBinding().getController());
        
        UIController uic = opener.getController(); 
        if (uic == null) 
            throw new IllegalStateException("'"+opener.getName()+"' opener must have a controller");
        
        try {
            UIControlUtil.setBeanValue(uic.getCodeBean(), "handler", new ApproveCallbackHandler()); 
        } catch(Exception ex) {
            System.out.println("Unable to set value for 'handler' property in " + uic.getCodeBean());
        } 
        
        try {
            UIControlUtil.setBeanValue(uic.getCodeBean(), "value", getValue()); 
        } catch(Exception ex) {
            System.out.println("Unable to set value for 'value' property in " + uic.getCodeBean());
        } 
        
        uic.setId(opener.getId());
        uic.setName(opener.getName());
        uic.setTitle(opener.getCaption());  
                
        UIControllerContext uicontext = new UIControllerContext(uic);
        String ctxId = uicontext.getId();
        if ( ctxId == null ) ctxId = getName() + handler;
        
        Platform platform = ClientContext.getCurrentContext().getPlatform();
        if ( platform.isWindowExists(ctxId) ) return;

        UIControllerPanel uipanel = new UIControllerPanel(uicontext);
        Map props = new HashMap();
        props.put("id", ctxId);
        props.put("title", uicontext.getTitle());

        try 
        {
            Map openerProps = opener.getProperties();
            props.put("width", openerProps.get("width"));
            props.put("height", openerProps.get("height"));
        } 
        catch(Exception ex){
        } 

        platform.showPopup(textField, uipanel, props); 
    }
    
    private void fireValueChanged() 
    {
        Object newValue = null;
        
        try 
        {
            textField.fireUpdateBackground(); 
            
            String expr = getExpression();
            if (expr == null || value instanceof String) 
            {
                newValue = value;
            }
            else 
            {
                try { 
                    newValue = UIControlUtil.evaluateExpr(createExpressionBean(), expr); 
                } catch(Exception ex) {;}
            }
        }
        catch(Exception ex) 
        {
            if (ClientContext.getCurrentContext().isDebugMode()) 
                ex.printStackTrace(); 
        }
        
        document.loadValue(newValue);
    } 
    
    private Object createExpressionBean() 
    {
        ExprBeanSupport beanSupport = new ExprBeanSupport(getBinding().getBean());
        Object itemBean = (value == null? new HashMap(): value);
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    }     
    
    public void grabFocus() 
    {
        if (textField.isEnabled())
            textField.grabFocus(); 
        else if (button.isEnabled()) 
            button.grabFocus(); 
    }

    public boolean hasFocus() { 
        return (textField.hasFocus() || button.hasFocus()); 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIInputWrapper implementation ">  
    
    public JComponent getEditorComponent() { return textField; }
    
    public Object getValue() 
    {
        if (document.isDirty())
        {
            String text = textField.getText();
            if (text == null || text.length() == 0)
                return (isNullWhenEmpty()? null: "");
            else 
                return text; 
        }
        else if (value == null) { 
            return (isNullWhenEmpty()? null: ""); 
        }
        else {
            return value;
        } 
    } 
    
    public void setValue(Object value) 
    {
        if (value instanceof EventObject) 
        {
            if (value instanceof KeyEvent)
            {
                KeyEvent ke = (KeyEvent) value;
                textField.setText( ke.getKeyChar()+"" );
                textField.allowSelectAll = false; 
            }
        } 
        else {
            textField.setText((value == null? "": value.toString()));
        }        
    }

    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }

    public boolean isReadonly() { 
        return textField.isReadonly(); 
    } 
    public void setReadonly(boolean readonly) 
    {
        textField.setReadonly(readonly); 
        button.setEnabled(!readonly); 
    }

    public void setRequestFocus(boolean focus) {
        if (focus) textField.requestFocus();
    }

    public boolean isImmediate() { return false; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void load() {
    }
    
    public void refresh() 
    {
        try {
            this.value = UIControlUtil.getBeanValue(this);
        } catch(Exception e) {
            this.value = null; 
        }
        
        fireValueChanged(); 
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (info instanceof PropertySupport.OpenerPropertyInfo) 
        {
            PropertySupport.OpenerPropertyInfo p = (PropertySupport.OpenerPropertyInfo) info; 
            if (p.getHandler() != null) 
            {
                setHandler(p.getHandler().toString());
                setExpression(p.getExpression()); 
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Validatable implementation ">  
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { 
        property.setCaption(caption);
    }

    public boolean isRequired() { return property.isRequired(); }
    public void setRequired(boolean required) {
        property.setRequired(required);
    }

    public ActionMessage getActionMessage() { return actionMessage; }
    
    public void validateInput() 
    {
        actionMessage.clearMessages();
        property.setErrorMessage(null);
        String text = textField.getText();
        if (ValueUtil.isEmpty(text)) 
        {
            if (isRequired()) 
                actionMessage.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
        } 
        else if (!ValueUtil.isEmpty(getInputFormat()) && !text.matches(getInputFormat()) ) 
        {
            String msg = null;
            if ( inputFormatErrorMsg != null )
                msg = inputFormatErrorMsg;
            else
                msg = "Invalid input format for {0}";
            
            actionMessage.addMessage(null, msg, new Object[]{ getCaption() });
        }
        
        if (actionMessage.hasMessages()) 
            property.setErrorMessage(actionMessage.toString());
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">  
    
    public ControlProperty getControlProperty() { return property; }
    
    public char getCaptionMnemonic() { 
        return property.getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char captionMnemonic) {
        property.setCaptionMnemonic(captionMnemonic);
    } 
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }    
    public void setCaptionWidth(int captionWidth) {
        property.setCaptionWidth(captionWidth);
    }    

    public boolean isShowCaption() {
        return property.isShowCaption();
    }    
    public void setShowCaption(boolean showCaption) {
        property.setShowCaption(showCaption);
    }    
    
    public Font getCaptionFont() {
        return property.getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }    
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultTextFieldImpl (class) ">      
    
    private class DefaultTextFieldImpl extends DefaultTextField 
    {
        boolean allowSelectAll = true;
        
        protected InputVerifier getChildInputVerifier() {
            return UIInputUtil.VERIFIER;
        }
        
        public UIInput getUIInput() { 
            return XOpenerField.this; 
        }
        
        protected void onfocusGained(FocusEvent e) 
        {
            if (allowSelectAll) selectAll(); 

            allowSelectAll = true;
            document.reset(); 
        }

        void fireUpdateBackground() {
            super.updateBackground(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Layout (class) ">  
    
    private class Layout implements LayoutManager
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(minimumSize.width, minimumSize.height);
        }

        public Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                Dimension dim = textField.getPreferredSize();
                int w = dim.width, h = dim.height;
                
                dim = button.getPreferredSize();
                w += dim.width;
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
        }        
        
        public void layoutContainer(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int pWidth = parent.getWidth();
                int pHeight = parent.getHeight();
                
                Insets margin = parent.getInsets();                
                int x = margin.left, y = margin.top;
                int h = pHeight - (margin.top + margin.bottom);
                
                Dimension btnSize = button.getPreferredSize();
                int btnX = (pWidth - margin.right) - btnSize.width; 
                button.setBounds(btnX, y, btnSize.width, h); 
                
                Dimension txtSize = textField.getPreferredSize();
                int txtW = (btnX - x);
                textField.setBounds(x, y, txtW, h);                 
            }            
        }        
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ApproveCallbackHandler (class) ">          
    
    private class ApproveCallbackHandler extends DefaultCallbackHandler
    {
        private XOpenerField root = XOpenerField.this;
        
        public Object call(Object[] args) 
        {
            if (args == null || args.length == 0) return null; 
            
            root.value = args[0]; 
            root.fireValueChanged(); 
            root.putClientProperty("updateBeanValue", true);
            return null; 
        }      
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" KeyboardAction (class) ">          
    
    private class KeyboardAction implements ActionListener
    {
        private JComponent component;
        
        void install(JComponent component) 
        {
            this.component = component; 
            
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
            component.registerKeyboardAction(this, ks, JComponent.WHEN_FOCUSED); 
        }
        
        public void actionPerformed(ActionEvent e) {
            fireButtonClicked(e);
        } 
    } 
    
    // </editor-fold>    
}
