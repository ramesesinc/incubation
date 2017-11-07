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
    private String _cols_;
    private String _hiddenCols_;
    
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
    
    public void initColumn( def c ) {
        //do nothing
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
        def t = invoker.properties.tag;
        if(t) return t;
        return workunit.info.workunit_properties.tag;
    }
    
    public void setTag(def s) {
        _tag_ = s;
    }
    
    public void setSelectColNames(def s) {
        _cols_ = s;
    }
    
    public def getSelectColNames() {
        if(_cols_ !=null) return _cols_;
        def t = invoker.properties.cols;
        if(t) return t;
        return workunit.info.workunit_properties.cols;    
    }
    
    public void setHiddenCols(def s) {
        _hiddenCols_ = s;
    }
    
    public def getHiddenCols() {
        if(_hiddenCols_!=null) return _hiddenCols_;
        def t = invoker.properties.hiddenCols;
        if(t) return t;
        return workunit.info.workunit_properties.hiddenCols;    
    }
    
    private def _schema;
    public def getSchema() {
        if( _schema !=null ) return _schema;
        
        strCols = getSelectColNames();
        
        //this is for hidden columns
        
        if(!schemaName) 
            throw new Exception("Please specify a schema name in the workunit");
        
        if(!adapter) {
            adapter = workunit.info.workunit_properties.adapter; 
        }
        
        def map = [name:schemaName, adapter: adapter]; 
        map.debug = true;
        if ( strCols ) {
            map.colnames = strCols;
            if(hiddenCols) map.colnames = strCols + ","+hiddenCols;
        }
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
    
    boolean isSurroundSearch() {
        String s = invoker?.properties?.surroundSearch;
        if ( !s ) s = workunit?.info?.workunit_properties?.surroundSearch;
        return (s.toString().equals("false")? false: true); 
    }
           
    boolean isAllowSearch() {
        return (searchables);
    }
    
    void beforeInit(){}
    void afterInit(){}
    
    boolean _inited_ = false;
    void init() {
        if(_inited_ ) return;
        
        beforeInit();
        //load role and domain if any.
        if( pageExists("queryForm")) {
            queryForm = new Opener(outcome:'queryForm')
        }
        super.initRole();
        
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
        
        afterInit();
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

        def s1 = [];
        def s2 = [:];
        if( whereStatement!=null ) {
            s1 << whereStatement[0];
            s2.putAll( whereStatement[1]);
        };
        def xFilter = customFilter;
        if(xFilter!=null) {
            s1 << xFilter[0]
            if( xFilter.size() > 1 ) {
                s2.putAll( xFilter[1] );
            }
        }
        if( s1 ) {
            m.where = [ s1.join(" AND "), s2];
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
    
    public boolean getMultiSelect() {
        def g = invoker.properties.multiSelect;
        if(!g) {
            g = workunit.info.workunit_properties.multiSelect;
        }
        if(g) {
            try {
                return Boolean.parseBoolean( g );
            }
            catch(Exception ign){;}
        }
        return this._multiSelect;
    }
    
    public boolean isAutoResize() { 
        return true; 
    }
    
    Number convertNumber( Object value ) {
        try { 
            if ( value instanceof Number ) return value; 
 
            return new Long( value.toString());
        } catch(Throwable t) {
            return null; 
        } 
    }

    //These are overridable methods
    public def getColumnList() {
        if( schema == null )
            throw new Exception("schema is null. Please call init method")
        def zcols = [];
        //always add the primary keys
        String matcher = ".*";
        if(strCols) matcher = strCols.replace(",","|");
        def selCols = cols.findAll{it.selected == true &&  it.name.matches(matcher)};
        int maxSz = selCols.size();
        for( c in selCols ) {
            def cc = [:];
            cc.putAll( c );
            if(c.datatype) {
                cc.type = c.datatype;
            }
            cc.colindex = maxSz;

            def num = convertNumber( cc.width ); 
            if ( num != null ) cc.width = num.intValue();

            num = convertNumber( cc.minWidth ); 
            if ( num != null ) cc.minWidth = num.intValue();

            num = convertNumber( cc.maxWidth ); 
            if ( num != null ) cc.maxWidth = num.intValue();

            initColumn( cc );
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
        //zcols << [caption:''];
        return zcols;    
    }
    
    public def fetchList(def o ) {
        if( schema == null )
            throw new Exception("schema is null. Please call invoke method")
        if(!_inited_) throw new Exception("This workunit is not inited. Please call init action");
        def m = buildSelectQuery(o);
        return getQueryService().getList( m );     
    }
    
    final def _self = this; 
    def listHandler = [ 
        isAutoResize  : {
            return _self.isAutoResize(); 
        }, 
        isMultiSelect : {
            return getMultiSelect(); 
        }, 
        getRows : {
            return getRows();
        },
        getColumnList: {
            return getColumnList();
        },
        fetchList: { o->
            return fetchList(o);
        },
        onOpenItem: { o, colName -> 
            try {
                return open();  
            }
            catch(e) {
                return null;
            }
        }
    ] as PageListModel;
   
    
    void search() {
        orWhereList.clear();
        if( searchText ) {
            searchables.each { 
                def st = searchText+"%";
                if ( isSurroundSearch() ) st = "%"+st; 
                
                orWhereList << [ it + " like :searchtext", [searchtext: st] ]
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
            def dtype = c.field.type;
            if ( dtype == null ) dtype = "string";
            def op = c[(dtype+'operator')];
            if(i++>0) buff.append( " AND ");
            buff.append( c.field.name + ' ' + op.key + ' :' +c.field.extname );
            params.put( c.field.extname, c.value );
            if( op.key?.toUpperCase() == 'BETWEEN') {
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
            //d = Inv.lookupOpener( "crudform:create", p );
        }
        if(!d) throw new Exception("No handler found for . " + ename + ".create. Please check permission");
        if( !d.target ) d.target = 'window';
        return d;
    }
    
    def open() {
        if ( !isOpenAllowed() )
            throw new Exception("Open not allowed");
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
            //d = Inv.lookupOpener( "crudform:open", p );
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
    
    void reloadNodes(){
        nodeListHandler.reload();
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
    def _nodeList;
    private def _selectedNode;


    final def nodeListHandler = [
        fetchList : { 
            _nodeList = null;
            return getNodeList();
        }
    ] as ListPaneModel 

    
    //overridable source
    public def fetchNodeList(def m) {
        return queryService.getNodeList(m);
    }
    
    def getNodeList() {
        if(!_nodeList) {
            def m = [:];
            m._schemaname = schema.name;
            m.adapter = schema.adapter;   
            String _tag_ = getTag();
            if( _tag_ ) m._tag = _tag_;
            beforeFetchNodes( m );
            _nodeList = fetchNodeList( m );
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
