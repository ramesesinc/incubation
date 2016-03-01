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
    def itemHandlers = [:];     //holder for all specific item handlers
    
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
    
    private void buildStyleRules() {
        styleRules.clear();
        styleRules << new StyleRule("entity.*", "#{mode=='read'}").add("enabled", false);
        styleRules << new StyleRule("entity.*", "#{mode!='read'}").add("enabled", true);
        
        //loop each editable field. for editables it is only editable during create.
        def editables = schema.columns.findAll{ it.editable == "false" }*.name;
        if(editables) {
            def flds = "entity.("+editables.join("|")+")";
            styleRules << new StyleRule( flds, "#{mode!='create'}").add("enabled",false);
            styleRules << new StyleRule( flds, "#{mode=='create'}").add("enabled",true);
        }
        def requires = schema.columns.findAll{ it.required == true }*.name;
        if( requires ) {
            def flds = "entity.("+requires.join("|")+")";
            styleRules << new StyleRule( flds, "#{mode!='read'}").add("required",true);
            styleRules << new StyleRule( flds, "#{mode=='read'}").add("required",false);
        };
        def itemNames = schema.items*.name;
        if(itemNames) {
            def flds = "itemHandlers.("+itemNames.join("|")+"):item.*";
            styleRules << new StyleRule( flds, "#{mode=='read'}").add("editable",false);
            styleRules << new StyleRule( flds, "#{mode!='read'}").add("editable",true);
        }
    }
    
    void init() {
        initSchema();
        buildStyleRules();
        buildItemHandlers();
    }
    
    def create() {
        mode = "create";
        entity = [:];
        entity._schemaname = schemaName;
        init();
        return null;
    }
    
    def open() {
        mode = "read";
        //we need to set the schemaname that will be used for open
        entity._schemaname = schemaName;
        entity = service.read( entity );
        
        //we need to reset the schema name for update.
        entity._schemaname = schemaName;
        init();
        return null;
    }
    
    def edit() {
        mode = "edit";
        entity = new DataMap( entity );
        return null;
    }
    
    def unedit() {
        mode = "read";
        entity.unedit();
        entity = entity.data();
        return null;
    }
    
    def save() {
        if(!MsgBox.confirm('You are about to save this record. Proceed?')) return null;
        
        if( mode == 'create' ) {
            entity = service.create( entity );
        }
        else {
            //extract from the DataMap. Right now we'll use the pure data first
            //we'll change this later to diff.
            def e = entity.data();
            service.update( e );
            loadData();
        }
        mode = "read";
        try {
            caller?.refresh();
        }
        catch(ign){;}
        return null;
    }
    
    void undo() {
        def v = entity.undo();
        //formPanel.reload();
    }
    
     /***************************************************************************
    * upper right buttons
    ***************************************************************************/
    boolean getCanDebug() { 
        return ClientContext.getCurrentContext().getAppEnv().get("app.debug");
    }

    def showDebugInfo() {
        def e = entity;
        if( mode == 'edit' ) e = entity.data();
        
        def sb = new StringBuilder();
        e.each { k,v->
            sb.append( k+"="+v + "\n");
        }
        MsgBox.alert( sb.toString() );
        println sb.toString();
    }
    
    def showInfo() {
        throw new Exception("No info handler found");
    }
        
    def showHelp() {
        throw new Exception("No help handler found");
    }
    
    /*************************************************************************
    * Navigation Controls
    **************************************************************************/
    boolean getShowNavigation() {
        if( !caller?.listHandler ) return false;
        return (mode == 'read');
    }
    
    void moveUp() {
        if( caller?.listHandler ) {
            caller.listHandler.moveBackRecord();
            reloadEntity();
        }
    }

    void moveDown() {
        if( caller?.listHandler ) {
            caller.listHandler.moveNextRecord();
            reloadEntity();
        }
    }
    
    void loadData() {
        entity._schemaname = schemaName;
        entity = service.read( entity );
        itemHandlers.values().each {
            it.reload();
        }
    }
    
    def reloadEntity() {
        if( caller.selectedEntity ) {
            entity = caller.selectedEntity;
            loadData();
        }
    }
    
    /*************************************************************************
    * This part here is for item handlers.  
    **************************************************************************/
    
   
    private void buildItemHandlers() {
        itemHandlers.clear();
        if( schema.items ) {
            schema.items.each { item->
                def s = new SubItemHandler( subSchema: item, handler: itemHandler );
                itemHandlers.put( item.name, s );
            }
        }
    }
    
    
    public def openItem(def itemName, def item, def colName) {
        MsgBox.alert( "open item " + itemName + " item " + item + " col "+colName);
        return null;
    }
    
    public boolean beforeColumnUpdate( def name, def colName, def  item, def newValue ) {
       return true;
    }
    
    public void afterColumnUpdate(def name, def columnName, Object item) {
        
    }
    
    def itemHandler = [ 
        fetchList: { name, params ->
            if( entity.get(name)==null ) entity.put(name, [] );
            return entity.get(name);
        },
        addItem : {name, item->
            entity.get(name).add( item );
        },       
        removeItem : {name, item->
            String dname = name +"::deleted";
            if( entity.get(dname) == null ) {
                entity.put(dname, []);
            }
            entity.get(dname).add( item );
            entity.get(name).remove( item );
        },
        openItem: { name, item, colName ->
            return openItem(subSchema.name, item, colName);
        },
        beforeColumnUpdate: { name, item, colName, newValue ->
            return beforeColumnUpdate( name, colName, item, newValue ); 
        },
        afterColumnUpdate: {name, item, colName ->
            afterColumnUpdate( name, colName, item );
        }    
    ];    
    
  
}


public class SubItemHandler extends EditorListModel {
    
    def subSchema;
    def handler;
    def cols = [];
    
    public List<Map> getColumnList() {
        cols.clear();
        for( i in subSchema.columns ) {
            if( i.visible == 'false' ) continue;
            def c = [name: i.name, caption: i.caption];
            c.type = 'text';
            c.editable = true;
            cols << c;
        }
        return cols;
    }
    
    public List fetchList(Map params) {
        return handler.fetchList(subSchema.name, params);
    }
    
    protected void onAddItem(Object item) {
        handler.addItem(subSchema.name, item);
    }        

    protected boolean onRemoveItem(Object item) {
        handler.removeItem(subSchema.name, item);
        return true;
    } 
        
    protected Object onOpenItem(Object item, String columnName) {
        return handler.openItem(subSchema.name, item, columnName);
    }

    protected boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
        return handler.beforeColumnUpdate(subSchema.name, item, columnName, newValue);
    }

    protected void afterColumnUpdate(Object item, String columnName) {
        handler.afterColumnUpdate(subSchema.name, item, columnName);
    }
    
   
}