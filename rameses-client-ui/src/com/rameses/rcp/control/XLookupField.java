package com.rameses.rcp.control;

import com.rameses.common.PropertyResolver;
import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.common.LookupHandler;
import com.rameses.rcp.common.LookupModel;
import com.rameses.rcp.common.LookupOpenerSupport;
import com.rameses.rcp.common.LookupSelector;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.text.IconedTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.support.TextEditorSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIFocusableContainer;
import com.rameses.rcp.ui.UISelector;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public class XLookupField extends IconedTextField implements UIFocusableContainer, UISelector, Validatable, ActiveControl, ActionListener, LookupSelector   
{
    protected ControlProperty property = new ControlProperty();
    protected ActionMessage actionMessage = new ActionMessage();
    
    private LookupHandlerProxy lookupHandlerProxy = new LookupHandlerProxy();
    private LookupInputSupport inputSupport = new LookupInputSupport();
    private TrimSpaceOption trimSpaceOption = TrimSpaceOption.ALL;
    private TextDocument document = new TextDocument();
        
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String hint;
    private String handler;
    private Object handlerObject;    
    private Object selectedValue;
    private String expression;
    private boolean transferFocusOnSelect = true;    
    private boolean dirty;
    private boolean readonly;
    private boolean loaded;
    private boolean nullWhenEmpty = true; 
    
    public XLookupField() 
    {
        super("com/rameses/rcp/icons/search.png");
        setOrientation( super.ICON_ON_RIGHT );  
        TextEditorSupport.install(this);
        
        document.setTextCase(TextCase.UPPER); 
        setDocument(document); 
        
        if (Beans.isDesignTime()) { 
            document.setTextCase(TextCase.NONE); 
        } 
    }    

    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters ">
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) { 
        this.nullWhenEmpty = nullWhenEmpty; 
    }
    
    public String getHint() { return hint; } 
    public void setHint(String hint) { this.hint = hint; }    
    
    public boolean isReadonly() { return readonly; }
    public void setReadonly(boolean readonly) { 
        this.readonly = readonly; 
        setEditable(!readonly);
        setFocusable(!readonly);        
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
        super.setText(expression);
    }
    
    public String getText() 
    {
        if ( Beans.isDesignTime() ) 
        {
            if ( !ValueUtil.isEmpty(expression) )
                return expression;
            else if ( !ValueUtil.isEmpty(getName()) )
                return getName();
            else
                return super.getText();
        } 
        else { 
            return super.getText();
        }
    }
    
    public boolean isTransferFocusOnSelect() { return transferFocusOnSelect; }    
    public void setTransferFocusOnSelect(boolean transerFocusOnSelect) {
        this.transferFocusOnSelect = transerFocusOnSelect;
    }    
    
    public TextCase getTextCase() { return document.getTextCase(); }    
    public void setTextCase(TextCase textCase) {
        document.setTextCase(textCase);
    }
    
    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }    
    public void setTrimSpaceOption(TrimSpaceOption option) {
        this.trimSpaceOption = option;
    }    
    
    private UIInputUtil.Support getInputSupport() 
    {
        if (inputSupport.delegate == null)
        {
            Object o = getClientProperty(UIInputUtil.Support.class); 
            if (o != null) inputSupport.delegate = (UIInputUtil.Support) o;
        }
        return inputSupport;
    }     
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (!(info instanceof PropertySupport.LookupPropertyInfo)) return; 

        PropertySupport.LookupPropertyInfo lkp = (PropertySupport.LookupPropertyInfo) info; 
        if (lkp.getHandler() instanceof String)
            setHandler(lkp.getHandler().toString()); 
        else 
            setHandlerObject(lkp.getHandler()); 
        
        setExpression(lkp.getExpression()); 
    }    
   
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="  Override methods ">
    
    protected InputVerifier getChildInputVerifier() { 
        return inputSupport; 
    }        

    protected void onprocessKeyEvent(KeyEvent e) 
    {
        if (e.isActionKey() || e.isAltDown() || e.isControlDown()) return; 
        
        dirty = true; 
    }        
    
    // </editor-fold>
       
    // <editor-fold defaultstate="collapsed" desc="  UIFocusableContainer implementation  ">
    
    public boolean focusFirstInput() {
        requestFocusInWindow(); 
        return true; 
    }    
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public void refresh() 
    {
        Object expval = null;
        if ( !ValueUtil.isEmpty(expression) ) 
        {
            Object bean = binding.getBean(); 
            if (bean != null) 
                expval = UIControlUtil.evaluateExpr(bean, expression); 
        }
        
        setText((expval == null? null: expval.toString()));         
    }

    public void load() 
    {
        dirty = false; 
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);        
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  UISelector implementation ">
    
    public void setValue(Object value) 
    {
        if ( value instanceof KeyEvent ) {
            setText( ((KeyEvent) value).getKeyChar()+"" );
        } 
        else 
        {
            if ( value != null )
                setText(value.toString());
            else
                setText("");
        }
        
        this.dirty = false; 
    } 
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  Validatable implementation  ">
    
    public String getCaption() { return property.getCaption(); }    
    public void setCaption(String caption) { 
        property.setCaption(caption); 
    } 

    public boolean isRequired() { return property.isRequired(); }    
    public void setRequired(boolean required) {
        property.setRequired(required);
    }
    
    public char getCaptionMnemonic() { 
        return property.getCaptionMnemonic();
    }
    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }
    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
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
    
    public void setCaptionFont(Font font) {
        property.setCaptionFont(font);
    }
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }
    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }    

    public ActionMessage getActionMessage() { return actionMessage; } 
    
    public void validateInput() 
    {
        actionMessage.clearMessages();
        property.setErrorMessage(null);
        if ( ValueUtil.isEmpty(getText()) ) 
        {
            if (isRequired()) 
                actionMessage.addMessage("1001", "{0} is required.", new Object[] { getCaption() });

        }         
        if ( actionMessage.hasMessages() ) {
            property.setErrorMessage( actionMessage.toString() );
        }        
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  ActiveControl implementation  "> 
    
    public ControlProperty getControlProperty() {
        return property; 
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="  ActionListener/LookupSelector implementation  ">
    
    protected void onactionPerformed(ActionEvent e) {
        fireLookup(); 
    }       
    
    private void fireLookup() 
    {
        if (Beans.isDesignTime()) return;

        try 
        {
            getInputVerifierProxy().setEnabled(false);    
            loadHandler();
            loaded = true;
            
            lookupHandlerProxy.getModel().setSelector(this);            
            boolean show = lookupHandlerProxy.getModel().show( getText() );            
            if ( show ) 
            {
                UIController c =  lookupHandlerProxy.getController(); 
                if ( c == null ) return; //should use a default lookup handler
                
                UIControllerContext uic = new UIControllerContext(c);
                Platform platform = ClientContext.getCurrentContext().getPlatform();
                String conId = uic.getId();
                if ( conId == null ) conId = getName() + handler;
                if ( platform.isWindowExists(conId) ) return;
                
                UIControllerPanel lookupPanel = new UIControllerPanel( uic );
                
                Map props = new HashMap();
                props.put("id", conId);
                props.put("title", uic .getTitle());
                
                platform.showPopup(this, lookupPanel, props);
            }
        } 
        catch(Exception e) 
        {
            MsgBox.err(e);
            getInputVerifierProxy().setEnabled(true); 
        }
    }    
    
    public void select(Object value) 
    {
        selectedValue = value;        
        getInputSupport().setValue(getName(), selectedValue);         
        putClientProperty("updateBeanValue", true); 
        getInputVerifierProxy().setEnabled(true);
        
        if ( transferFocusOnSelect )
            this.transferFocus();
        else
            this.requestFocus();        
    }

    public void cancelSelection() 
    {
        putClientProperty("updateBeanValue", false); 
        getInputVerifierProxy().setEnabled(true);
        this.requestFocus();         
    }    
    
    private void loadHandler()
    {
        Object o = null;
        if ( !ValueUtil.isEmpty(handler) ) 
        {
            if ( handler.matches(".+:.+") ) //handler is a module:workunit name
                o = LookupOpenerSupport.lookupOpener(handler, new HashMap()); 
            else 
            {
                //check if there is a binding object passed by the JTable
                Binding tableBinding = (Binding) getClientProperty(Binding.class); 
                if (tableBinding == null) tableBinding = getBinding(); 
                
                o = UIControlUtil.getBeanValue(tableBinding, handler);
            }
        } 
        else if ( handlerObject != null ) { 
            o = handlerObject;
        } 
        
        if (o == null) return;
        
        if (o instanceof LookupHandler) { 
            lookupHandlerProxy.setHandler((LookupHandler) o); 
        } 
        else if (o instanceof Opener)
        {
            Opener opener = (Opener) o;             
            opener = ControlSupport.initOpener( opener, getBinding().getController() );
            lookupHandlerProxy.setOpener(opener); 
        }
    }

    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="  LookupHandlerProxy (Class)  ">
    
    private class LookupHandlerProxy implements LookupHandler 
    {
        private LookupModel model;
        private LookupHandler handler;
        private Opener opener;
        private Object onselectCallback;
        private Object onemptyCallback;
        
        LookupModel getModel() { return model; }
        UIController getController() { 
            return (opener == null? null: opener.getController()); 
        } 
        
        boolean hasOnselectCallback() { return (onselectCallback != null); } 
        boolean hasOnemptyCallback() { return (onemptyCallback != null); } 
        
        void setHandler(LookupHandler handler) { this.handler = handler; }
        void setOpener(Opener opener) 
        { 
            if (opener.getParams() != null) 
            {
                onselectCallback = opener.getParams().get("onselect");
                onemptyCallback = opener.getParams().get("onempty");
            }
            
            UIController controller = opener.getController(); 
            if (controller == null) 
                throw new IllegalStateException("'"+opener.getName()+"' opener must have a controller");
            
            if (!(controller.getCodeBean() instanceof LookupModel))
                throw new IllegalStateException("'"+opener.getName()+"' opener controller must be an instance of LookupModel");
                  
            controller.setId(opener.getId());
            controller.setName(opener.getName());
            controller.setTitle(opener.getCaption());            
            model = (LookupModel) controller.getCodeBean(); 
            this.opener = opener; 
        }
        
        public Object getOpener() 
        {
            if (handler != null) return handler.getOpener(); 
            
            return opener;
        }

        public void onselect(Object item) 
        {
            if (handler != null) handler.onselect(item);
        }
        
        void invokeOnempty(Object value) {
            invokeHandler(onemptyCallback, value);
        }
        
        void invokeOnselect(Object item) {
            invokeHandler(onselectCallback, item);
        }        
        
        private void invokeHandler(Object handler, Object item)
        {
            if (handler == null) return;

            Method method = null;         
            Class clazz = handler.getClass();
            try { method = clazz.getMethod("call", new Class[]{Object.class}); }catch(Exception ign){;} 

            try 
            {
                if (method != null) 
                    method.invoke(handler, new Object[]{item}); 
            }
            catch(Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex); 
            }
        }        
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="  LookupInputSupport (Class)  ">
    
    private class LookupInputSupport extends InputVerifier implements UIInputUtil.Support 
    {
        UIInputUtil.Support delegate;
        
        public boolean verify(JComponent input) 
        {
            if (!dirty) return true; 
            
            if (isReadonly() || !isEnabled() || !isEditable()) return true;
            
            /*
             *  workaround fix when called by the JTable
             */
            if (!loaded) 
            {
                loadHandler();
                loaded = true;
            }             
            
            String text = getText(); 
            if ( !ValueUtil.isEmpty(getExpression()) && ValueUtil.isEmpty(text) )  
            {
                Object value = isNullWhenEmpty()? null: new HashMap(); 
                if (lookupHandlerProxy.hasOnemptyCallback()) 
                    lookupHandlerProxy.invokeOnempty(value); 
                else 
                    updateBeanValue(input.getName(), value); 
                    
                selectedValue = null;
            } 
            
            publishUpdates(); 
            return true; 
        }  
        
        public void setValue(String name, Object value) 
        {
            if (lookupHandlerProxy.hasOnselectCallback()) 
                lookupHandlerProxy.invokeOnselect(value); 
            else 
                updateBeanValue(name, value);

            publishUpdates(); 
            dirty = false; 
        }      
        
        private void updateBeanValue(String name, Object value) 
        {
            if (name == null) return;
            
            if (delegate == null) 
            {
                //handle the updating of the bean
                Object bean = binding.getBean();
                if (bean != null) 
                {
                    PropertyResolver resolver =PropertyResolver.getInstance();
                    resolver.setProperty(binding.getBean(), name, value); 
                } 
            } 
            else 
            {
                //delegate the updating of the bean
                delegate.setValue(name, value); 
            }
        }
        
        private void publishUpdates() 
        {
            if (delegate == null) 
            {
                //only do refresh and notifications when no delegator is set
                refresh();
                binding.notifyDepends(XLookupField.this);
            }             
        }
    }
    
    // </editor-fold>    
    
}
