package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
        

/**
* workunit properties
* cols = choose only columns from schema, in the order displayed separated by commas.
* allowCreate = if true create button will be displayed. default is true
* allowOpen = if true open button will be displayed. default is true
* allowDelete = if true delete button will be displayed. default is false
* 
* init action must be called.
*/
public class CrudListModel {
        
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Service("QueryService")
    def service;

    @Service("PersistenceService")
    def persistenceService;

    @FormTitle
    def formTitle;
    
    def selectedItem;
    def list;
    def schemaName;
    def adapter;
    def schema;
    def entitySchemaName;   //used in case the view schema is not the same as entity schema
    def query = [:];
    def criteriaList = [];
    def queryForm;
    def whereStatement;
    String searchText;
    
    def cols = [];
  
    String role;
    String domain;
    String permission;
    List styleRules = [];
    List searchables;
    List orWhereList = [];

    String strCols;
    
    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    //overridables
    public def beforeQuery( def m ) {
        ;//do nothing
    }
    public def getQueryService() {
        return service;
    }
    
    public def getCustomFilter() {
        String s = workunit.info.workunit_properties.customFilter;
        if( s == null ) return null;
        return [s, [:]];
    }
    
    public def getTag() {
        return workunit.info.workunit_properties.tag;
    }
    
    public def getSchema() {
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
        def xschema = persistenceService.getSchema( [name:schemaName, adapter: adapter] );
        xschema.name = schemaName;
        if(adapter) xschema.adapter = adapter;
        return xschema;
    }
    //end overridables
    
    List getExtActions() {
        return Inv.lookupActions( schemaName+":list:extActions", [entity: selectedItem] );
    }
    
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

    boolean isDeleteAllowed() { 
        def allowDelete = workunit.info.workunit_properties.allowDelete;        
        if( allowDelete != 'true' ) return false;
        if( !role ) return true;
        def deletePermission = workunit.info.workunit_properties.deletePermission; 
        if(deletePermission!=null) deletePermission = schemaName+"."+deletePermission;
        return secProvider.checkPermission( domain, role, deletePermission );
    }
           
    boolean isAllowSearch() {
        return (searchables);
    }
    
    public String getTitle() {
        return workunit.title;
    }
    
    boolean _inited_ = false;
    void init() {
        if(_inited_ ) return;
        //load role and domain if any.
        queryForm = new Opener(outcome:'queryForm')
        domain = invoker.domain;
        role = invoker.role;
        formTitle = invoker.caption;
        
        schema = getSchema();
        
        //establish first what columns to include in internal columns
        def includeCols = new LinkedHashSet();
        def _includeCols = ".*";
        if( workunit.info.workunit_properties.includeCols ) {
            _includeCols = workunit.info.workunit_properties.includeCols;
        }
        //loop all fields to include.
        for( ic in _includeCols.split(",") ) {
            if(ic == "*") ic = ".*";
            for( fld in schema.fields ) {
                if(fld.jointype ) continue;
                if(!(fld.visible==null || fld.visible=='true' )) continue;
                if(fld.name.matches( ic.trim()) ) {
                    includeCols << fld;
                }
            }
        }
        
        strCols = workunit.info.workunit_properties.cols;
        
        //establish columns to display. The tricky part here is if cols are specified
        //it must be in the order it is specified. If 
        def zcols = new LinkedHashSet();
        def _displayCols = ".*";
        if( workunit.info.workunit_properties.cols ) {
            _displayCols = workunit.info.workunit_properties.cols;
        }
        for( ic in _displayCols.split(",") ) {
            if(ic == "*") ic = ".*";
            for( fld in includeCols ) {
                if(fld.name.matches( ic.trim()) ) {
                    zcols << fld;
                    //by default primary keys will be hidden.
                    if( fld.primary ) 
                        fld.selected = false;
                    else    
                        fld.selected = true;
                }
            }
        }
        cols.clear();
        zcols.each { c->
            cols << c;
        }
        includeCols.each { c->
            if( !cols.find{it.name == c.name} ) {
                cols << c;
            }
        }
        zcols.clear();
        includeCols.clear();
        cols.each {fld->
            if(!fld.caption) fld.caption = fld.name;            
        }
        searchables = schema.fields.findAll{ it.searchable == "true" }*.name;
        _inited_ = true;
    }
        
    public def buildSelectQuery(Map o) {
        def m = [:];
        if(o) m.putAll(o);
        if(query) m.putAll(query);
        m._schemaname = schema.name;
        m.adapter = schema.adapter;
        def primKeys = cols.findAll{it.primary==true}*.name.join(",");
        
        //build the columns to retrieve
        def arr = cols.findAll{it.selected==true}*.name.join(",");
        m.select = [primKeys, arr].join(",") ;
        if(customFilter!=null) {
            if( customFilter.size() !=2 ) 
                throw new Exception("Custom Filter must have a statement and parameter")
            if( whereStatement==null ) {
                whereStatement= customFilter;
            }
            else {
                whereStatement[0] = customFilter[0] + ' AND ' + whereStatement[0];
                whereStatement[1].putAll( customFilter[1] );
            }
        } 

        if( whereStatement!=null) {
            m.where = whereStatement;
        }
        
        if( orWhereList.size() > 0 ) {
            m.orWhereList = orWhereList;
        }
        if( getTag()!=null ) {
            m._tag = getTag();
        }        
        beforeQuery( m );
        return m;
    }
    
    def listHandler = [
        getColumnList: {
            if( schema == null )
                throw new Exception("schema is null. Please call init method")
            def zcols = [];
            //always add the primary keys
            for( c in cols.findAll{it.selected == true} ) {
                def cc = [:];
                cc.putAll( c );
                zcols << cc;
            }
            zcols << [caption:''];
            return zcols;
        },
        fetchList: { o->
            if( schema == null )
                throw new Exception("schema is null. Please call invoke method")
            if(!_inited_) throw new Exception("This workunit is not inited. Please call init action");
            def m = buildSelectQuery(o);
            return getQueryService().getList( m );
        },
        onOpenItem: { o, colName ->
            return open();
        }
    ] as PageListModel;
    
    void search() {
        orWhereList.clear();
        if( searchText ) {
            searchables.each {
                orWhereList << [ it + " like :searchtext", ["searchtext": "%"+searchText+"%"]  ]
            }
        }
        listHandler.doSearch();
    }
    
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
            //we call doSearch to set the start at 0
            listHandler.doSearch(); 
        }
        return Inv.lookupOpener( "crud:showcriteria", [cols: cols, handler:h, criteriaList: criteriaList] );
    }
            
    def selectColumns() {
        def h = {
            listHandler.reloadAll();
        }
        return Inv.lookupOpener( "crud:selectcolumns", [columnList: cols, onselect:h] );
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
        if( !selectedItem ) 
            throw new Exception("Please select an item");
        def d = null;
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        def p = [schema:schema, schemaName:ename, adapter:adapter, entity: selectedItem];
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
        if(!selectedItem) return;
        if( !MsgBox.confirm('You are about to delete this record. Proceed?')) return;
        def m = [:];
        //show only primary key of the main element.
        schema.fields.findAll{it.primary}.each {
            m.put( it.name, selectedItem.get(it.name));
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
        //load first all data.
        def m = buildSelectQuery([:]);
        int i = 0;
        def buffList = [];
        while( true ) {
            m._start = i;
            m._limit = 50;
            def l = getQueryService().getList( m );
            buffList.addAll( l );
            if( l.size() < 50  ) {
                break;
            }
            i=i+50;
        }
        def reportModel = [
            title: formTitle,
            columns : cols.findAll{ it.selected == true }
        ]
        return Inv.lookupOpener( "dynamic_report:print", [reportData:buffList, reportModel:reportModel] );
    }
    

    def showMenu() {
        def op = new PopupMenuOpener();
        //op.add( new ListAction(caption:'New', name:'create', obj:this, binding: binding) );
        try {
            op.addAll( Inv.lookupOpeners(schemaName+":list:menuActions") );
        } catch(Throwable ign){;}
        
        op.add( new com.rameses.seti2.models.PopupAction(caption:'Close', name:'_close', obj:this, binding:binding) );
        return op;
    }
    
    //if there are nodes
    private _nodeList;
    private def _selectedNode;
    
    def getNodeList() {
        if(!_nodeList) {
            def m = [:];
            m._schemaname = schema.name;
            m.adapter = schema.adapter;            
            _nodeList = queryService.getNodeList( m );
        }
        return _nodeList;
    }
    
    void setSelectedNode(def n) {
        _selectedNode = n;
        query.put("node", n);
        listHandler.reload();
    }
    
    def getSelectedNode() {
        return _selectedNode;
    }    
    
}
