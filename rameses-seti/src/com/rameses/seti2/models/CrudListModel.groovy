package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.*;
        

/**
* workunit properties
* cols = choose only columns from schema, in the order displayed separated by commas.
* allowCreate = if true create button will be displayed. default is true
* allowOpen = if true open button will be displayed. default is true
* allowDelete = if true delete button will be displayed. default is false
* 
* init action must be called.
*/
public class CrudListModel extends AbstractCrudModel {
        
    def selectedItem;
    def list;
    def adapter;
    
    def query = [:];
    def _findBy = [:];
    def criteriaList = [];
    def queryForm;
    def whereStatement;
    String searchText;
    def cols = [];
    
    List searchables;
    List orWhereList = [];

    String strCols;
    
    String _entitySchemaName_;   //used in case the view schema is not the same as entity schema
    
    private String _tag_;
    
    boolean _multiSelect; 
    
    String getFormType() {
        return 'list';
    }
    
    public def getEntityContext() {
        return selectedItem;
    }
    
    public def getFindBy() {
        return _findBy;
    }
    
    public String getEntitySchemaName() {
        if( !_entitySchemaName_ ) {
            return workunit.info.workunit_properties.entitySchemaName;
        }
        return _entitySchemaName_;
    } 
    
    public void setEntitySchemaName( String s ) {
        this._entitySchemaName_ = s;
    }
    
    //overridables
    public void beforeQuery( def m ) {
        ;//do nothing
    }
    
    public def beforeFetchNodes( def m ) {
        ;//do nothing
    }
    
    public def getCustomFilter() {
        String s = invoker.properties.customFilter;
        if( s!=null ) {
            return [s, [:]];
        }
        s = workunit.info.workunit_properties.customFilter;
        if( s != null ) return [s, [:]];
        return null;
    }
    
    public def getTag() {
        if( _tag_ !=null) return _tag_;
        return workunit.info.workunit_properties.tag;
    }
    
    public void setTag(def s) {
        _tag_ = s;
    }
    
    private def _schema;
    public def getSchema() {
        if( _schema !=null ) return _schema;
        
        strCols = invoker.properties.cols;
        if(!strCols) {
            strCols = workunit.info.workunit_properties.cols;    
        }
        
        if(!schemaName) 
            throw new Exception("Please specify a schema name in the workunit");
        
        if(!adapter) {
            adapter = workunit.info.workunit_properties.adapter; 
        }
        
        def map = [name:schemaName, adapter: adapter]; 
        if ( strCols ) map.colnames = strCols;
        _schema = getPersistenceService().getSchema( map );
        _schema.name = schemaName;
        if(adapter) _schema.adapter = adapter;
        return _schema;
    }
    //end overridables
    
    public String getOrderBy() {
        String s = invoker.properties.orderBy;
        if( s ) return s;
        return workunit.info.workunit_properties.orderBy;
    }
           
    boolean isAllowSearch() {
        return (searchables);
    }
    
    boolean _inited_ = false;
    void init() {
        if(_inited_ ) return;
        //load role and domain if any.
        if( pageExists("queryForm")) {
            queryForm = new Opener(outcome:'queryForm')
        }
        domain = invoker.domain;
        role = invoker.role;
        
        schema = getSchema();
        cols.clear();
        for( it in  schema.fields) {  
            if(it.jointype) continue;
            if ( it.primary==true ) {
                if( it.source != schema.name ) {
                    it.visible = false;
                    it.hidden = 'true'
                }
                else {
                    it.selectable = true;
                    it.selected = ( it.visible=='true' ); 
                }
            } 
            else if ( it.visible==null || it.visible=='true' ) {
                it.selected = true; 
            } 
            if ( !it.caption ) it.caption = it.name; 
            cols << it; 
        }

        searchables = schema.fields.findAll{ it.searchable == "true" }*.name;
        _inited_ = true;
    }
        
    public def buildSelectQuery(Map o) {
        def m = [debug:debug];
        if(o) m.putAll(o);
        if(query) {
            m.putAll(query);
        };
        if(getFindBy()) {
            m.findBy = getFindBy();
        };
        m._schemaname = schema.name;
        m.adapter = schema.adapter;
        
        def primKeys = cols.findAll{it.primary==true && it.source==schema.name}*.name;
        def arr = cols.findAll{ it.hidden=='true' || it.selected==true }*.name; 
        
        //build the columns to retrieve
        m.select = (primKeys + arr).unique().join(",") ;
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
        if( !ValueUtil.isEmpty(getOrderBy()) ) {
            m.orderBy = getOrderBy();
        }
        beforeQuery( m );
        return m;
    }
    
    public int getRows() {
        return 20;
    }
    
    public void setMultiSelect( boolean b ) { 
        this._multiSelect = b; 
    } 
    
    def listHandler = [ 
        isMultiSelect: {
            return _multiSelect; 
        }, 
        getRows : {
            return getRows();
        },
        getColumnList: {
            if( schema == null )
                throw new Exception("schema is null. Please call init method")
            def zcols = [];
            //always add the primary keys
            def selCols = cols.findAll{it.selected == true};
            int maxSz = selCols.size();
            for( c in selCols ) {
                def cc = [:];
                cc.putAll( c );
                if(c.datatype) {
                    cc.type = c.datatype;
                }
                cc.colindex = maxSz;
                zcols << cc;
            }
            //sort the columns based on the order in strCols
            int i = 0;
            if( strCols ) {
                def arr = strCols.split(",");
                for( ss in arr ) {
                    def g = zcols.find{ it.name == ss.trim() }
                    if( g ) g.colindex = (i++);
                }
            }
            zcols = zcols.sort{ it.colindex };
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
            if ( isOpenAllowed() ) { 
                return open(); 
            } else {
                return null;  
            } 
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
        def c = cols.findAll{ it.selectable != 'false' }
        return Inv.lookupOpener( "crud:selectcolumns", [columnList: c, onselect:h] );
    }
    
    def create() {
        def d = null;
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        def p = [ schemaName:ename, adapter:adapter];
        p.title = "New " + workunit.title; 
        try {
            d = Inv.lookupOpener( ename + ":create", p );
        }
        catch(e) {
            d = Inv.lookupOpener( "crudform:create", p );
        }
        if(!d) throw new Exception("No handler found for . " + ename + ".create. Please check permission");
        if( !d.target ) d.target = 'window';
        return d;
    }
    
    def open() {
        if( !selectedItem ) 
            throw new Exception("Please select an item");
        def d = null;
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        def p = [ schemaName:ename, adapter:adapter, entity: selectedItem];
        p.title = "Open " + workunit.title;
        try {
            d = Inv.lookupOpener( ename + ":open", p );
        }
        catch(e) {
            d = Inv.lookupOpener( "crudform:open", p );
        }
        if(!d) throw new Exception("No handler found for . " + ename + ".open. Please check permission");
        if( !d.target ) d.target = 'window';
        return d;
    }
    
    void removeEntity() {
        if(!selectedItem) return;
        if( !MsgBox.confirm('You are about to delete this record. Proceed?')) return;
        def m = [:];
        def ename = (!entitySchemaName)? schemaName : entitySchemaName;
        m._schemaname = ename;
        //show only primary key of the main element.
        schema.fields.findAll{it.primary && it.source==ename}.each {
            m.put( it.name, selectedItem.get(it.name));
        }
        getPersistenceService().removeEntity( m );
        listHandler.reload();
    }
    
    void refresh() {
        listHandler.reload();
    }
    
    void reload() {
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
            title: getTitle(),
            columns : cols.findAll{ it.selected == true }
        ]
        return Inv.lookupOpener( "dynamic_report:print", [reportData:buffList, reportModel:reportModel] );
    }
    
    //if there are nodes
    private _nodeList;
    private def _selectedNode;
    
    def getNodeList() {
        if(!_nodeList) {
            def m = [:];
            m._schemaname = schema.name;
            m.adapter = schema.adapter;   
            beforeFetchNodes( m );
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
