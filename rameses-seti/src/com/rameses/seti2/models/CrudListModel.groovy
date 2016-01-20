package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
        
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
    def qry;
    def criteriaList = [];
    def queryForm;
    
    String role;
    String domain;
    String permission;
    String selectCols = "*";
    
    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    List getExtActions() {
        return Inv.lookupActions( schemaName+":list:extActions", [entity: selectedEntity] );
    }
    
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
    
    
    void init() {
        //load role and domain if any.
        queryForm = new Opener(outcome:'queryForm')
        domain = invoker.domain;
        role = invoker.role;
        formTitle = invoker.caption;
        
        if(!schemaName) {
            schemaName = workunit.info.workunit_properties.schemaName;
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
                if(cc.name.contains("_")) cc.name = cc.name.replace("_", ".");
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
            m.name = schema.name;
            m.adapter = schema.adapter;
            
            //build the columns to retrieve
            def arr = schema.columns.findAll{it.primary==true || it.selected == true }*.name;
            m.select = arr.join(",");
            
            return queryService.getList( m );
        }
    ] as PageListModel;
    
    def buildFilter() {
        def buff = new StringBuilder();
        def params = [:]
        boolean _first = true;
        for( c in criteriaList ) {
            if( _first ) _first = false;
            else buff.append( " AND ");
            buff.append( c.name + ' ' + c.entry.operator.key + ' $P{' +c.name+ '}' );
            params.put( c.name, c.entry.value );
        };
        println buff.toString();
        params.each {k,v->
            println k+"="+v;
        }
            
        return buff.toString();
    }
    
    def showFilter() {
        def h = { o->
            criteriaList.clear();
            criteriaList.addAll( o );     
            buildFilter();
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
        def p = [schema:schema, schemaName:schemaName, adapter:adapter];
        p.title = "New " + workunit.title; 
        try {
            d = Inv.lookupOpener( schemaName + ":create", p );
        }
        catch(e) {
            d = Inv.lookupOpener( "crudform:create", p );
        }
        if(!d) throw new Exception("No handler found for . " + schemaName + ".create. Please check permission");
        return d;
    }
    
    def open() {
        if( !selectedEntity ) 
            throw new Exception("Please select an item");
        def d = null;
        def p = [schema:schema, schemaName:schemaName, adapter:adapter, entity: selectedEntity];
        p.title = "Open " + workunit.title;
        try {
            d = Inv.lookupOpener( schemaName + ":open", p );
        }
        catch(e) {
            d = Inv.lookupOpener( "crudform:open", p );
        }
        if(!d) throw new Exception("No handler found for . " + schemaName + ".open. Please check permission");
        return d;
    }
    
    void removeEntity() {
        if(!selectedEntity) return;
        if( !MsgBox.confirm('You are about to delete this record. Proceed?')) return;
        def m = [:];
        schema.columns.findAll{it.primary}.each {
            m.put( it.name, selectedEntity.get(it.name));
        }
        m._schemaname = schemaName;
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