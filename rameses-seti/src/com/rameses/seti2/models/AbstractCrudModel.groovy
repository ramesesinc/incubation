package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
        
public abstract class AbstractCrudModel  {
    
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Caller
    def caller;
    
    public abstract String getFormType();
    
    List getExtActions() {
        def actions1 = []; 
        try { 
            def invs = Inv.lookup("formActions", null, { o-> 
                return o.workunitid == invoker.workunitid; 
            } as InvokerFilter ); 
        
            actions1 = buildActions( invs ); 
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup invokers caused by " + t.message);
        } 

        def actions2 = [];
        try { 
            def actionProvider = ClientContext.currentContext.actionProvider; 
            actions2 = actionProvider.lookupActions( schemaName+":"+ getFormType() +":formActions" );
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup invokers caused by " + t.message);
        }
        
        return (actions1 + actions2).sort{ (it.index==null? 0: it.index) };
    }    
    
    
    final def buildActions( invokers ) {
        def actions = []; 
        invokers.each{ actions << createAction( it ) }
        return actions; 
    }
    
    final def createAction( inv ) {
        def a = new Action( inv.getName() == null? inv.getCaption()+"" :inv.getName() );
        
        def invProps = [:]; 
        invProps += inv.properties;
        a.name = inv.action;
        a.caption = inv.caption;
        if ( inv.index ) a.index = inv.index; 
        
        a.icon = invProps.remove("icon");
        a.immediate = "true".equals(invProps.remove("immediate")+"");
        a.visibleWhen = invProps.remove("visibleWhen");
        a.update = "true".equals(invProps.remove("update")+"");
        
        def mnemonic = invProps.remove("mnemonic");
        if ( mnemonic ) a.mnemonic( mnemonic.toString().charAt(0) );
        
        def tooltip = invProps.remove("tooltip");
        if ( tooltip ) a.tooltip = tooltip;
        
        if ( !invProps.isEmpty() ) a.properties += invProps;

        a.properties.put("Action.Invoker", inv);
        return a;
    }    
}
        