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
    
    String role;
    String domain;
    String permission;
    List styleRules = [];
    def schema;
    
    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    public abstract String getFormType();
    
    public String getSchemaName() {
        return workunit?.info?.workunit_properties?.schemaName;
    }
    
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
    
    //shared security features
    boolean isCreateAllowed() { 
        def allowCreate = workunit.info.workunit_properties.allowCreate;        
        if( allowCreate == 'false' ) return false;
        if( !role ) return true;
        def createPermission = workunit.info.workunit_properties.createPermission;   
        if(createPermission!=null) createPermission = schemaName+"."+createPermission;
        return secProvider.checkPermission( domain, role, createPermission );
    }
        
    boolean isOpenAllowed() { 
        def allowOpen = workunit.info.workunit_properties.allowOpen;        
        if( allowOpen == 'false' ) return false;
        if( !role ) return true;
        def openPermission = workunit.info.workunit_properties.openPermission; 
        if(openPermission!=null) openPermission = schemaName+"."+openPermission;
        return secProvider.checkPermission( domain, role, openPermission );
    }

    boolean isEditAllowed() { 
        def allowEdit = workunit.info.workunit_properties.allowEdit;        
        if( allowEdit == 'false' ) return false;        
        if( mode != 'read') return false;
        if( !role ) return true;
        def editPermission = workunit.info.workunit_properties.editPermission; 
        if(editPermission!=null) editPermission = schemaName+"."+editPermission;
        return secProvider.checkPermission( domain, role, editPermission );
    }

    
    boolean isDeleteAllowed() { 
        def allowDelete = workunit.info.workunit_properties.allowDelete;        
        if( allowDelete != 'true' ) return false;
        if( !role ) return true;
        def deletePermission = workunit.info.workunit_properties.deletePermission; 
        if(deletePermission!=null) deletePermission = schemaName+"."+deletePermission;
        return secProvider.checkPermission( domain, role, deletePermission );
    }
    
    def showMenu() {
        def op = new PopupMenuOpener();
        //op.add( new ListAction(caption:'New', name:'create', obj:this, binding: binding) );
        try {
            op.addAll( Inv.lookupOpeners(schemaName+":" + getFormType() + ":menuActions") );
        } catch(Throwable ign){;}
        
        op.add( new com.rameses.seti2.models.PopupAction(caption:'Close', name:'_close', obj:this, binding:binding) );
        return op;
    }
    
}
        