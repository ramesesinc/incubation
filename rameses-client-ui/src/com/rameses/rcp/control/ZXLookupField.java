package com.rameses.rcp.control;

import com.rameses.common.PropertyResolver;
import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.common.LookupHandler;
import com.rameses.rcp.common.LookupModel;
import com.rameses.rcp.common.LookupSelector;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 *
 * @author jaycverg
 */
public class ZXLookupField extends AbstractIconedTextField implements LookupSelector   
{
    private LookupHandlerProxy lookupHandlerProxy = new LookupHandlerProxy();
    private LookupInputSupport inputSupport = new LookupInputSupport();
    
    private String handler;
    private Object handlerObject;    
    private Object selectedValue;
    private String expression;
    private boolean transferFocusOnSelect = true;    
    private boolean dirty;
    
    public ZXLookupField() 
    {
        super("com/rameses/rcp/icons/search.png");
        setOrientation( super.ICON_ON_RIGHT );
    }
    
    public void actionPerformed(){
        fireLookup();
    }
    
    public void validateInput() {
        actionMessage.clearMessages();
        property.setErrorMessage(null);
        if ( isRequired() && ValueUtil.isEmpty( getValue() ) ) {
            actionMessage.addMessage("1001", "{0} is required.", new Object[] {getCaption()});
            property.setErrorMessage(actionMessage.toString());
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="  refresh/load  ">
    
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
        super.load();
        loadHandler();
    }
    
    private void loadHandler()
    {
        Object o = null;
        if ( !ValueUtil.isEmpty(handler) ) 
        {
            if ( handler.matches(".+:.+") ) //handler is a module:workunit name
                o = new Opener(handler);
            else
                o = UIControlUtil.getBeanValue(this, handler);
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

    protected void onprocessKeyEvent(KeyEvent e) 
    {
        if (e.isActionKey() || e.isAltDown() || e.isControlDown()) return; 
        
        dirty = true; 
    }
       
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  lookup dialog support  ">
    
    private void fireLookup() 
    {
        if (Beans.isDesignTime()) return;

        try 
        {
            getInputVerifierProxy().setEnabled(false);             
            loadHandler();
            
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
    
    private UIInputUtil.Support getInputSupport() 
    {
        Object o = getClientProperty(UIInputUtil.Support.class); 
        if (o == null) return inputSupport; 
        
        return (UIInputUtil.Support) o; 
    }     
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    protected InputVerifier getChildInputVerifier() { 
        return inputSupport; 
    }
    
    public Object getValue() { return selectedValue; }     
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
    
    public boolean isTransferFocusOnSelect() { return transferFocusOnSelect; }    
    public void setTransferFocusOnSelect(boolean transerFocusOnSelect) {
        this.transferFocusOnSelect = transerFocusOnSelect;
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
        
        void invokeOnempty() {
            invokeHandler(onemptyCallback, null);
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
        public boolean verify(JComponent input) 
        {
            if (!dirty) return true; 
            
            if (isReadonly() || !isEnabled() || !isEditable()) return true;
            
            String text = getText(); 
            if ((text == null || text.length() == 0) && selectedValue != null)  
            {
                if (lookupHandlerProxy.hasOnemptyCallback()) 
                    lookupHandlerProxy.invokeOnempty();                 
                else 
                    updateBeanValue(input.getName(), null); 
                    
                selectedValue = null;
            }            
            refresh();
            binding.notifyDepends(ZXLookupField.this);
            return true; 
        }  
        
        public void setValue(String name, Object value) 
        {
            if (lookupHandlerProxy.hasOnselectCallback()) 
                lookupHandlerProxy.invokeOnselect(value); 
            else 
                updateBeanValue(name, value);

            refresh();
            binding.notifyDepends(ZXLookupField.this);
            dirty = false; 
        }      
        
        private void updateBeanValue(String name, Object value) 
        {
            if (name == null) return;
            
            Object bean = binding.getBean();
            if (bean != null) 
            {
                PropertyResolver resolver = PropertyResolver.getInstance();
                resolver.setProperty(binding.getBean(), name, value); 
            } 
        }
    }
    
    // </editor-fold>
        
}
