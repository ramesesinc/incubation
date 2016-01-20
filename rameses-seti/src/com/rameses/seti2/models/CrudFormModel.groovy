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
    String schemaName;
    
    String role;
    String domain;
    String permission;
    
    @FormTitle
    def title;
    
    def mode;
    
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

    List getExtActions() {
        return Inv.lookupActions( schemaName+":form:extActions", [entity: entity] );
    }
    
    void initSchema() {
        if( !schema ) {
            schema = schemaService.getSchema( [name: schemaName, adapter: adapter]  );
        }   
    }
    
    void init() {
        initSchema();
    }
    
    def create() {
        mode = "create";
        entity = [:];
        if(!schemaName) {
            def  _sname = workunit.info.workunit_properties.schemaName;
            if( _sname ) schemaName = _sname;
        }
        entity._schemaname = schemaName;
        init();
        return "create";
    }
    
    def open() {
        mode = "read";
        if(!schemaName) {
            def  _sname = workunit.info.workunit_properties.schemaName;
            if( _sname ) schemaName = _sname;
        }
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
        if(MsgBox.confirm('You are about to save this record. Proceed?')) {
            if( mode == 'create' ) {
                entity = service.create( entity );
            }
            else {
                //extract from the DataMap
                def e = entity.data();
                entity = service.update( e );
            }
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
    
    def showDropdownMenu() {
        def op = new PopupMenuOpener();
        op.add( new Opener(caption:'New', action:'create', target:'process') );
        
        //op.addAll( Inv.lookupOpeners("xxx:sample") );
        op.add( new Opener(caption:'Close', action:'_close', target:'process') );
        return op;
    }
        
}