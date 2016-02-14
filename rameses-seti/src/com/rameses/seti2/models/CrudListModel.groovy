package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
        
/**
 * workunit properties that can be configured:
 * allowCreate, allowOpen, allowDelete
 *
*/
public class CrudListModel {
        
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Service("QueryService")
    def queryService;

    @Service("SchemaService")
    def schemaService;

    @Service("PersistenceService")
    def persistenceService;

    
    @FormTitle
    def formTitle;
    
    def selectedEntity;
    def list;
    def schemaName;
    def adapter;
    def schema;
    def entitySchemaName;   //used in case the view schema is not the same as entity schema
    def query = [:];
    def criteriaList = [];
    def queryForm;
    def whereStatement;
    
    String role;
    String domain;
    String permission;
    String selectCols = "*";
    
    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    List getExtActions() {
        return Inv.lookupActions( schemaName+":list:extActions", [entity: selectedEntity] );
    }
    
    boolean isCreateAllowed() { 
        def allowCreate = workunit.info.workunit_properties.allowCreate;        
        if( allowCreate == 'false' ) return false;
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".create" );
    }
        
    boolean isOpenAllowed() { 
        def allowOpen = workunit.info.workunit_properties.allowOpen;        
        if( allowOpen == 'false' ) return false;
        
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".open" );
    }

    boolean isDeleteAllowed() { 
        def allowDelete = workunit.info.workunit_properties.allowDelete;        
        if( allowDelete != 'true' ) return false;
        
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".delete" );
    }
                
    public String getTitle() {
        return workunit.title;
    }
    
    public String getRecordCountInfo() {
        return "";
        //return  listHandler.rowCount + " Record(s). " ; 
    }
        
    public String getPageCountInfo() {
        return "Page " + listHandler.pageIndex + " of ? " + listHandler.pageCount;
    }
    
    /**
    * choose only columns from schema 
    */
    void init() {
        //load role and domain if any.
        queryForm = new Opener(outcome:'queryForm')
        domain = invoker.domain;
        role = invoker.role;
        formTitle = invoker.caption;
        
        if(!schemaName) {
            schemaName = workunit.info.workunit_properties.schemaName;
        }
        if(!entitySchemaName) {
            entitySchemaName = workunit.info.workunit_properties.entitySchemaName;
        }
        if(!schemaName) 
            throw new Exception("Please specify a schema name in the workunit");
        
        if(!adapter) {
            adapter = workunit.info.workunit_properties.adapter; 
        }
        schema = schemaService.getSchema( [name:schemaName, adapter: adapter] );
        schema.name = schemaName;
        if(adapter) schema.adapter = adapter;
        
        //build the initial select columns
        if(workunit.info.workunit_properties.cols!=null ) {
            selectCols = workunit.info.workunit_properties.cols;
            def arr = selectCols.split(",");
            arr.each { c->
                schema.columns.find{ it.name ==  c.trim() }?.selected = true;
            }
        }
        if(selectCols == "*") {
            schema.columns.each{ it.selected = true };
        }
        schema.columns.findAll{ it.primary == true }.each{ it.selected = false; }
        schema.columns.findAll{!it.caption}.each {
            it.caption = it.title;
            if(!it.caption) it.caption = it.name;
        }
    }
        
    def listHandler = [
        getColumnList: {
            if( schema == null )
                throw new Exception("schema is null. Please call invoke method")
            def cols = [];
            for( c in schema.columns.findAll{it.selected==true} ) {
                def cc = [:];
                cc.putAll( c );
                cols << cc;
            }
            cols << [caption:''];
            return cols;
        },
        fetchList: { o->
            if( schema == null )
                throw new Exception("schema is null. Please call invoke method")
                
            def m = [:];
            m.putAll(o);
            m.putAll(query);
            m.schemaname = schema.name;
            m.adapter = schema.adapter;
            
            //build the columns to retrieve
            def arr = schema.columns.findAll{it.primary==true || it.selected == true }*.name;
            m.select = arr.join(",");
            if( whereStatement !=null ) {
                m.where = whereStatement;
            }
            return queryService.getList( m );
        },
        onOpenItem: { o, colName ->
            return open();
        }
    ] as PageListModel;
    
    
    //returns the where element
    def buildWhereStatement() {
        def buff = new StringBuilder();
        def params = [:]
        int i = 0;
        for( c in criteriaList*.entry ) {
            if(i++>0) buff.append( " AND ");
            buff.append( c.field.name + ' ' + c.operator.key + ' :' +c.field.extname );
            params.put( c.field.extname, c.value );
            if( c.operator.key?.toUpperCase() == 'BETWEEN') {
                buff.append( " AND :"+c.field.extname+"2" );
                params.put( c.field.extname+"2", c.value2 );
            }
        };
        return [buff.toString(), params];
    }
    
    def showFilter() {
        def h = { o->
            criteriaList.clear();
            criteriaList.addAll( o );     
            if( criteriaList.size() > 0 ) {
                whereStatement = buildWhereStatement(); 
            }
            else {
                whereStatement = null;       
            }
            listHandler.reload(); 
        }
        return Inv.lookupOpener( "crud:showcriteria", [schema: schema, handler:h, criteriaList: criteriaList] );
    }
            
    def selectColumns() {
        def h = {
            listHandler.reloadAll();
        }
        return Inv.lookupOpener( "crud:selectcolumns", [schema: schema, onselect:h] );
    }
    
    def create() {
        def d = null;
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        def p = [schema:schema, schemaName:ename, adapter:adapter];
        p.title = "New " + workunit.title; 
        try {
            d = Inv.lookupOpener( ename + ":create", p );
        }
        catch(e) {
            d = Inv.lookupOpener( "crudform:create", p );
        }
        if(!d) throw new Exception("No handler found for . " + ename + ".create. Please check permission");
        return d;
    }
    
    def open() {
        if( !selectedEntity ) 
            throw new Exception("Please select an item");
        def d = null;
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        def p = [schema:schema, schemaName:ename, adapter:adapter, entity: selectedEntity];
        p.title = "Open " + workunit.title;
        try {
            d = Inv.lookupOpener( ename + ":open", p );
        }
        catch(e) {
            d = Inv.lookupOpener( "crudform:open", p );
        }
        if(!d) throw new Exception("No handler found for . " + ename + ".open. Please check permission");
        return d;
    }
    
    void removeEntity() {
        if(!selectedEntity) return;
        if( !MsgBox.confirm('You are about to delete this record. Proceed?')) return;
        def m = [:];
        schema.columns.findAll{it.primary}.each {
            m.put( it.name, selectedEntity.get(it.name));
        }
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        m._schemaname = ename;
        persistenceService.removeEntity( m );
        listHandler.reload();
    }
    
    void refresh() {
        listHandler.reload();
    }
    
    def print() {
        return Inv.lookupOpener( "crudlist:print", [reportData:'elmox'] );
    }
    
    void search() {
        throw new Exception("Search not yet implemented");
    }

    def showDropdownMenu() {
        def op = new PopupMenuOpener();
        op.add( new ListAction(caption:'New', name:'create', obj:this, binding: binding) );
        op.addAll( Inv.lookupOpeners(schemaName+":list:menuActions") );
        op.add( new ListAction(caption:'Close', name:'_close', obj:this, binding: binding) );
        return op;
    }
    
}

public class ListAction extends Action {
    def obj;
    def binding;
    def execute() {
        if( getName().startsWith("_")) {
            binding.fireNavigation(getName());
        }
        else {
            return obj.invokeMethod(getName(), null);
        }
    }
    
}