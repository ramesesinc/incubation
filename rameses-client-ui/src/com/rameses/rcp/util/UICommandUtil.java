package com.rameses.rcp.util;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.control.XButton;
import com.rameses.rcp.framework.*;
import com.rameses.rcp.ui.UICommand;

import com.rameses.rcp.common.Action;
import com.rameses.util.BusinessException;
import com.rameses.util.ExceptionManager;
import com.rameses.util.IgnoreException;
import com.rameses.util.ValueUtil;
import java.beans.Beans;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author jaycverg
 */
public class UICommandUtil {
    
    public static void processAction(UICommand command) 
    {
        if ( Beans.isDesignTime() ) return;

        ClientContext ctx = ClientContext.getCurrentContext();
        MethodResolver resolver = MethodResolver.getInstance();        
        try 
        {
            Binding binding = command.getBinding();
            
            binding.formCommit();
            validate(command, binding);
            
            String target = ValueUtil.isEmpty(command.getTarget())? "parent": command.getTarget();
            NavigatablePanel navPanel = UIControlUtil.getParentPanel((JComponent)command, target);
            if ( !"parent".equals(target) ) {
                UIControllerContext rootCon = (UIControllerContext) navPanel.getControllers().peek();
                Binding rootBinding = rootCon.getCurrentView().getBinding();
                validate(command, rootBinding);
            }
            
            //set parameters
            XButton btn = (XButton) command;
            ControlSupport.setProperties( binding.getBean(), btn.getParams());
            
            //notify handlers who hooked before execution
            binding.getActionHandlerSupport().fireBeforeExecute();
            
            Object outcome = null;
            String action = command.getActionName();
            if ( btn.getClientProperty(Action.class.getName()) != null ) 
            {
                Action a = (Action) btn.getClientProperty(Action.class.getName());
                outcome = a.execute();                
            } 
            else if ( action != null ) 
            {
                Action oAction = (Action) btn.getClientProperty(Action.class); 
                Object value = (oAction == null? null: oAction.getProperties().get("Action.menus")); 
                if (value instanceof List) {
                    List<Action> actionList = (List) value;
                    if (actionList.size() > 0) {
                        if (actionList.size() == 1) {
                            outcome = actionList.get(0).execute(); 
                        }
                    }
                }
                else if ( !action.startsWith("_")) {
                    Object[] actionParams = new Object[]{};
                    Object actionInvoker = btn.getClientProperty("Action.Invoker");
                    if (actionInvoker != null) actionParams = new Object[]{ actionInvoker };
                    
                    if (hasMethod(binding.getBean(), action, actionParams))
                        outcome = resolver.invoke(binding.getBean(), action, actionParams);
                    else 
                        outcome = resolver.invoke(binding.getBean(), action, null, null); 
                } 
                else { 
                    outcome = action;
                }
                
                if ( command.isUpdate() ) binding.update();
            }
            
            //notify handlers who hooked after execution
            binding.getActionHandlerSupport().fireAfterExecute(); 
            
            NavigationHandler handler = ctx.getNavigationHandler();
            if ( handler != null ) handler.navigate(navPanel, command, outcome);
        } 
        catch(Exception ex) 
        {
            Exception e = ExceptionManager.getOriginal(ex); 
            if (e instanceof IgnoreException) return;
            
            if (!ExceptionManager.getInstance().handleError(e))
                ctx.getPlatform().showError((JComponent) command, ex);
        }
    }
    
    public static void processAction(JComponent invoker, Binding binding, Action action) 
    {
        if ( Beans.isDesignTime() ) return;

        ClientContext ctx = ClientContext.getCurrentContext();
        MethodResolver resolver = MethodResolver.getInstance();  
        try 
        {
            Object[] actionParams = new Object[]{};
            Object actionInvoker = action.getProperties().get("Action.Invoker");
            if (actionInvoker != null) actionParams = new Object[]{ actionInvoker };

            Object outcome = null;            
            String command = action.getName();
            if (hasMethod(binding.getBean(), command, actionParams))
                outcome = resolver.invoke(binding.getBean(), command, actionParams);
            else 
                outcome = resolver.invoke(binding.getBean(), command, null, null); 
            
            if (outcome != null) binding.fireNavigation(outcome);
        }
        catch(Exception ex) 
        {
            Exception e = ExceptionManager.getOriginal(ex); 
            if (e instanceof IgnoreException) return;
            
            if (!ExceptionManager.getInstance().handleError(e))
                ctx.getPlatform().showError(invoker, ex);
        }        
    }
    
    private static void validate(UICommand command, Binding binding) throws BusinessException 
    {
        if ( binding == null ) return;
        if ( !command.isUpdate() && command.isImmediate() ) return;
        
        binding.validate();
    }
    
    private static boolean hasMethod(Object bean, String name, Object[] args) 
    {
        if (bean == null || name == null) return false;
        
        Class beanClass = bean.getClass();
        while (beanClass != null) 
        {
            Method[] methods = beanClass.getMethods(); 
            for (int i=0; i<methods.length; i++) 
            {
                Method m = methods[i];
                if (!m.getName().equals(name)) continue;

                int paramSize = (m.getParameterTypes() == null? 0: m.getParameterTypes().length); 
                int argSize = (args == null? 0: args.length); 
                if (paramSize == argSize && paramSize == 0) return true;
                if (paramSize == argSize && m.getParameterTypes()[0] == Object.class) return true; 
            }
            beanClass = beanClass.getSuperclass(); 
        }
        return false;
    }
}
