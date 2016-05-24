package com.rameses.seti2.models;
 
import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;

public abstract class AbstractCrudModel  {
    
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Caller
    def caller;
    
    @SubWindow
    def subWindow;
    
    @Script("User")
    def userInfo;
            
    @Service("PersistenceService")
    def persistenceSvc;
    
    @Service("QueryService")
    def qryService;
    
    String role;
    String domain;
    String permission;
    List styleRules = [];
    def schema;
    
    boolean debug = false;
    
    private String _schemaName_ ;
    
    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    public abstract String getFormType();
    
    //entity context is used in the expression
    public abstract def getEntityContext();
    
    public String getSchemaName() {
        if( _schemaName_ )
            return _schemaName_;
        else    
            return workunit?.info?.workunit_properties?.schemaName;
    }
    
    public void setSchemaName( String s ) {
        this._schemaName_ = s;
    }
    
    public def getPersistenceService() {
        return persistenceSvc;
    }
    
    public def getQueryService() {
        return qryService;
    }
    
    @FormTitle
    String getWindowTitle() {
        if( invoker.properties.windowTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.windowTitle,this);
        }
        else {
            return getTitle();
        }
    }
    
    String getTitle() {
        if( invoker.properties.formTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formTitle,this);
        }
        if( invoker.caption ) {
            return invoker.caption;
        }
        return getSchemaName();
    }
    
    @FormId
    String getFormId() {
        if( invoker.properties.formId ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formId,this);
        }
        return workunit.workunit.id;
    }
    
    //this is used for getting the actions
    public String getFormName() {
        return schemaName+":"+ getFormType();
    }
        
    /*
    def extActions;
    public List getExtActions() {
        if(extActions) return extActions;
        def actions1 = []; 
        try { 
            Inv.lookup("formActions", null, { o-> 
                if(o.workunitid == invoker.workunitid) {
                    actions1 << createAction( o );
                } 
                return false;
            } as InvokerFilter ); 
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
        extActions = (actions1.unique() + actions2.unique());
        return extActions.sort{ (it.index==null? 0: it.index) };
    }    
    */
   
    void updateWindowProperties() {
        if(invoker.properties.target == 'window') {
            if(subWindow!=null) {
                subWindow.update( [title: getTitle() ] );
            }
        }
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
        if( allowCreate ) {
            if (allowCreate == 'false') return false;
            if (allowCreate.startsWith('#{')){
                try {
                    boolean t = ExpressionResolver.getInstance().evalBoolean(allowCreate, [entity:getEntityContext()] );
                    if(t == false) return false;
                }
                catch(ign){
                    println 'Expression Error: ' + allowCreate;
                    return false;
                }
            }
        }
        if( !role ) return true;
        def createPermission = workunit.info.workunit_properties.createPermission;   
        if(createPermission!=null) createPermission = schemaName+"."+createPermission;
        return secProvider.checkPermission( domain, role, createPermission );
    }
        
    boolean isOpenAllowed() { 
        def allowOpen = workunit.info.workunit_properties.allowOpen;  
        if( allowOpen ) {
            if (allowOpen == 'false') return false;
            if (allowOpen.startsWith('#{')){
                try {
                    boolean t = ExpressionResolver.getInstance().evalBoolean(allowOpen, [entity:getEntityContext()] );
                    if(t == false) return false;
                } catch(ign){
                    println 'Expression Error: ' + allowOpen;
                    return false;
                }
            }
        }
        if( !role ) return true;
        def openPermission = workunit.info.workunit_properties.openPermission; 
        if(openPermission!=null) openPermission = schemaName+"."+openPermission;
        return secProvider.checkPermission( domain, role, openPermission );
    }

    boolean isEditAllowed() { 
        def allowEdit = workunit.info.workunit_properties.allowEdit;        
         if( allowEdit ) {
            if (allowEdit == 'false') return false;
            if (allowEdit.startsWith('#{')){
                try {
                    boolean t = ExpressionResolver.getInstance().evalBoolean(allowEdit, [entity:getEntityContext()] );
                    if(t == false) return false;
                }
                catch(ign){
                    println 'Expression Error: ' + allowEdit;
                    return false;
                }
            }
        }
        if( !role ) return true;
        def editPermission = workunit.info.workunit_properties.editPermission; 
        if(editPermission!=null) editPermission = schemaName+"."+editPermission;
        return secProvider.checkPermission( domain, role, editPermission );
    }

    boolean isDeleteAllowed() { 
        def allowDelete = workunit.info.workunit_properties.allowDelete;  
        if( allowDelete ) {
            if (allowDelete == 'false') return false;
            if (allowDelete.startsWith('#{')){
                try {
                    boolean t = ExpressionResolver.getInstance().evalBoolean(allowDelete, [entity:getEntityContext()] );
                    if(t == false) return false;
                }
                catch(ign){
                    println 'Expression Error: ' + allowDelete;
                    return false;
                }
            }
        }
        if( !role ) return true;
        def deletePermission = workunit.info.workunit_properties.deletePermission; 
        if(deletePermission!=null) deletePermission = schemaName+"."+deletePermission;
        return secProvider.checkPermission( domain, role, deletePermission );
    }
    
    def showMenu() {
        def op = new PopupMenuOpener();
        //op.add( new ListAction(caption:'New', name:'create', obj:this, binding: binding) );
        try {
            op.addAll( Inv.lookupOpeners(schemaName+":" + getFormType() + ":menuActions", [entity:entityContext]) );
        } catch(Throwable ign){;}
        
        op.add( new com.rameses.seti2.models.PopupAction(caption:'Close', name:'_close', obj:this, binding:binding) );
        return op;
    }
    
    //information about the user
    public def getUser() {
        def app = userInfo.env;
        return [objid: app.USERID, name: app.NAME, fullname: app.FULLNAME, username: app.USER ];
    }
    
    def showInfo() {
        throw new Exception("No info handler found");
    }
        
    def showHelp() {
        throw new Exception("No help handler found");
    }
    
    protected boolean pageExists(String pageName) {
        if( !workunit.views ) return false;
        def z = workunit.views.find{ it.name == pageName };
        if(z) return true;
        return false;
    }
    
}
        