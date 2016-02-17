package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
        
public class CrudFormModel {
        
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Service("PersistenceService")
    def service;

    @Service("SchemaService")
    def schemaService;
    
    @Caller
    def caller;

    def selectedEntity;
    
    def adapter;
    def schema;
    def entity;
    
    String role;
    String domain;
    String permission;
    
    def mode;
    
    List styleRules = [];
    
    @FormTitle
    String getTitle() {
        return getSchemaName();
    }

    public String getSchemaName() {
        return workunit?.info?.workunit_properties?.schemaName;
    }

    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    boolean isCreateAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".create" );
    }
        
    boolean isOpenAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".open" );
    }

    boolean isDeleteAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".delete" );
    }
                
    boolean isEditAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".edit" );
    }

    /*
    List getExtActions() {
        return Inv.lookupActions( schemaName+":form:extActions", [entity: entity] );
    }
    */
    
    def showMenu() {
        def op = new PopupMenuOpener();
        try {
            op.addAll( Inv.lookupOpeners(schemaName+":form:menuActions") );
        }
        catch(Exception ign){;}
        op.add( new com.rameses.seti2.models.PopupAction(caption:'Close', name:'_close', obj:this, binding: binding) );
        return op;
    }
    
    void initSchema() {
        if( !schema ) {
            schema = schemaService.getSchema( [name: schemaName, adapter: adapter]  );
        }   
    }
    
    void init() {
        initSchema();
        styleRules << new StyleRule("entity.*", "#{mode=='read'}").add("enabled", false);
        styleRules << new StyleRule("entity.*", "#{mode!='read'}").add("enabled", true);
    }
    
    def create() {
        mode = "create";
        entity = [:];
        entity._schemaname = schemaName;
        init();
        return "create";
    }
    
    def open() {
        mode = "read";
        //we need to set the schemaname that will be used for open
        entity._schemaname = schemaName;
        entity = service.read( entity );
        
        //we need to set the schema name for update.
        entity._schemaname = schemaName;
        init();
        return "read";
    }
    
    def edit() {
        mode = "edit";
        entity = new DataMap( entity );
        return "edit";
    }
    
    def unedit() {
        mode = "read";
        entity.unedit();
        entity = entity.data();
        return "read";
    }
    
    def save() {
        if(!MsgBox.confirm('You are about to save this record. Proceed?')) return null;
        if( mode == 'create' ) {
            entity = service.create( entity );
        }
        else {
            //extract from the DataMap
            def e = entity.data();
            entity = service.update( e );
        }
        mode = "read";
        try {
            caller?.refresh();
        }
        catch(ign){;}
        return "read";
    }
    
    void undo() {
        def v = entity.undo();
        //formPanel.reload();
    }
    
    def showInfo() {
        throw new Exception("No info handler found");
    }
        
    def showHelp() {
        throw new Exception("No help handler found");
    }

    def moveUp() {
        if( caller?.listHandler ) {
            caller.listHandler.moveBackRecord();
            if( caller.selectedEntity ) {
                entity = caller.selectedEntity;
                return open();
            }
            else {
                return "_close";
            }
        }
    }

    def moveDown() {
        if( caller?.listHandler ) {
            caller.listHandler.moveNextRecord();
            if( caller.selectedEntity ) {
                entity = caller.selectedEntity;
                return open();
            }
            else {
                return "_close";
            }
        }
    }
    
}
