package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;
        
public class CrudFormModel implements SubItemListener {
        
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Service("PersistenceService")
    def service;
    
    @Service("QueryService")
    def qryService;

    @Caller
    def caller;

    @SubWindow
    def subWindow;
    
    def adapter;
    def schema;
    def entity;
    
    String role;
    String domain;
    String permission;

    //overridable services
    public def getPersistenceService() {
        return service;
    }
    
    def mode;
    
    List styleRules = [];
    def itemHandlers = [:];     //holder for all specific item handlers
    
    //used for mdi forms.
    def selectedSection;
    def sections;
    
    @FormTitle
    String getTitle() {
        if( invoker.properties.formTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formTitle,entity);
        }
        if( invoker.caption ) {
            return invoker.caption;
        }
        return getSchemaName();
    }
    
    @FormId
    String getFormId() {
        if( invoker.properties.formId ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formId,entity);
        }
        return workunit.workunit.id;
    }
    
    public String getSchemaName() {
        return workunit?.info?.workunit_properties?.schemaName;
    }

    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    boolean isCreateAllowed() { 
        if( mode != 'read') return false;
        def allowCreate = workunit.info.workunit_properties.allowCreate;   
        if( allowCreate == 'false' ) return false;        
        if( !role ) return true;
        def createPermission = workunit.info.workunit_properties.createPermission;   
        if(createPermission!=null) createPermission = schemaName+"."+createPermission;
        return secProvider.checkPermission( domain, role, createPermission );
    }
     
    boolean isDeleteAllowed() { 
        def allowDelete = workunit.info.workunit_properties.allowDelete;        
        if( allowDelete == 'false' ) return false;        
        if( mode != 'read') return false;
        if( !role ) return true;
        def deletePermission = workunit.info.workunit_properties.deletePermission; 
        if(deletePermission!=null) deletePermission = schemaName+"."+deletePermission;
        return secProvider.checkPermission( domain, role, deletePermission );
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

    boolean isSaveAllowed() {
        return ( mode != 'read');
    }
    
    boolean isUndoAllowed() {
        return ( mode != 'read');
    }
    
    boolean isCancelEditAllowed() {
        return ( mode == 'edit');
    }

    List getExtActions() {
        def actions = []; 
        try { 
            actions = InvokerUtil.lookupActions("formActions", { o->
                return o.workunitid == invoker.workunitid; 
            } as InvokerFilter ); 
        } 
        catch(Throwable t) {
            System.out.println("[WARN] error lookup actions caused by " + t.message);
        } 
        def actions2 = Inv.lookupActions( schemaName+":form:formActions", [entity: entity] );
        return (actions + actions2).sort{ (it.index==null? 0: it.index) };
    }
    
    public void afterInit(){;}
    public void afterCreate(){;}
    public void afterOpen(){;}
    public void afterEdit(){;}
    
    
    public void beforeSave(def mode){;}
    public void afterCreateData(String name, def data){;}
    
    //override for items
    public void afterFetchItems(String name, def data){;}
    
    def showMenu() {
        def op = new PopupMenuOpener();
        try {
            op.addAll( Inv.lookupOpeners(schemaName+":form:menuActions") );
        }
        catch(Exception ign){;}
        op.add( new com.rameses.seti2.models.PopupAction(caption:'Close', name:'_close', obj:this, binding: binding) );
        return op;
    }
    
    protected void buildStyleRules() {
        styleRules.clear();
        styleRules << new StyleRule("entity.*", "#{mode=='read'}").add("enabled", false);
        styleRules << new StyleRule("entity.*", "#{mode!='read'}").add("enabled", true);
        
        //loop each editable field. for editables it is only editable during create.
        def editables = schema.fields.findAll{ it.editable == "false" }*.name;
        if(editables) {
            def flds = "entity.("+editables.join("|")+")";
            styleRules << new StyleRule( flds, "#{mode!='create'}").add("enabled",false);
            styleRules << new StyleRule( flds, "#{mode=='create'}").add("enabled",true);
        }
        def requires = schema.fields.findAll{ it.required == true || it.primary == true }*.name;
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
        //style rules for style types:
        def codeStyles = schema.fields.findAll{ it.style == 'code' }*.name;
        if(codeStyles) {
            def n = "entity.("+codeStyles.join("|")+")";
            //char 95 is underscore
            styleRules << new StyleRule( n, "#{true}").add("textCase",TextCase.UPPER).add("spaceChar",(char)95);
        }
    }
    
    protected void buildSections() {
        //for items with sections....
        try {
            sections = Inv.lookupOpeners(schemaName + ":section",[:]);
        } 
        catch(Exception ex){;}
    }
    
    boolean _inited_ = false;
    
    void init() {
        if( !schemaName )
        throw new Exception("Please provide a schema name. Put it in workunit schemaName or override the getSchemaName()");
        if( _inited_ ) return;
        if( !schema ) {
            schema = service.getSchema( [name: schemaName, adapter: adapter]  );
        }   
        buildStyleRules();
        buildItemHandlers();
        buildSections();
        _inited_ = true;
        afterInit()
    }
    
    def createData( String _schemaname, def schemaDef  ) {
        def map = [:];
        map._schemaname = _schemaname;
        schemaDef.fields.each {
            //generate id only if primary, and schema name is this context
            if( it.prefix && it.primary && it.source == _schemaname ) {
                EntityUtil.putNestedValue( map, it.extname, it.prefix+new UID());
            }
            if( it.defaultValue!=null) {
                Object val = it.defaultValue;
                EntityUtil.putNestedValue( map, it.extname, val );
            }
            if( it.serializer !=null ) {
                EntityUtil.putNestedValue( map, it.extname, [:] );
            }
        }
        afterCreateData( _schemaname, map );
        return map;
    }
    
    void initNewData() {
        entity = createData( schemaName, schema );
        //reload the schema items
        itemHandlers.each { k,v->
            v.reload();
        }
    }

    def create() {
        mode = "create";
        init();
        initNewData();
        afterCreate();
        return null;
    }
    
    def open() {
        mode = "read";
        //we need to set the schemaname that will be used for open
        entity._schemaname = schemaName;
        entity = getPersistenceService().read( entity );
        
        //we need to reset the schema name for update.
        entity._schemaname = schemaName;
        init();
        afterOpen();
        return null;
    }
    
    def edit() {
        mode = "edit";
        //we'll try to correct the entries like serailizers that are null;
        //check for serializer fields that are null and correct it
        def serFields = schema.fields.findAll{ it.serializer!=null };
        serFields.each {
            def val = EntityUtil.getNestedValue( entity, it.extname );
            if( val == null ) {
                EntityUtil.putNestedValue( entity, it.extname, [:] );
            }
        }        
        entity = new DataMap( entity );
        afterEdit();
        return null;
    }
    
    def unedit() {
        mode = "read";
        entity.unedit();
        entity = entity.data();
        loadData();
        return null;
    }
    
    def save() {
        if(!_inited_) throw new Exception("This workunit is not inited. Please call open or create action");
       
        if(!MsgBox.confirm('You are about to save this record. Proceed?')) return null;
        
        if( mode == 'create' ) {
            beforeSave("create");
            entity._schemaname = schemaName;
            entity = getPersistenceService().create( entity );
        }
        else {
            beforeSave("update");
            //extract from the DataMap. Right now we'll use the pure data first
            //we'll change this later to diff.
            entity = entity.data(); 
            entity._schemaname = schemaName;
            getPersistenceService().update( entity );
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
        Modal.show("crudform_debug:view", [schema:schema, data:e]);
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
        entity = getPersistenceService().read( entity );
        itemHandlers.values().each {
            it.reload();
        }
    }
    
    def reloadEntity() {
        if( caller.selectedItem ) {
            entity = caller.selectedItem;
            loadData();
            afterOpen();
        }
        if(invoker.properties.target == 'window') {
            subWindow.update( [title: getTitle() ] );
        }
    }
    
    /*************************************************************************
     * This part here is for item handlers.  
     **************************************************************************/
    private void buildItemHandlers() {
        itemHandlers.clear();
        if( schema.items ) {
            schema.items.each { item->
                def s = new SubItemEditorListModel( item, this );
                itemHandlers.put( item.name, s );
            }
        }
    }
    
    
    public List getColumns(String name, Map subSchema) {
        def cols = [];
        for( i in subSchema.fields ) {
            if( i.visible == 'false' ) continue;
            def c = [name: i.name, caption: i.caption];
            c.type = 'text';
            c.editable = true;
            cols << c;
        }
        return cols;
    }
    
    public Map createItem(String name, Map subSchema ) {
        def n = subSchema.ref;
        if(n.indexOf(":")>0) n = n.split(":")[1];
        return createData( n, subSchema );
    }

    public List fetchItems(String name, Map subSchema, def params ) {
        if( entity.get(name)==null ) entity.put(name, [] );
        def list = entity.get(name);
        afterFetchItems( name, list );
        return list;
    }
    
    public def openItem(String name,def item, String colName) {
        return null;
    }
    
    public boolean beforeColumnUpdate(String name, def item, String colName, def newItem) {
        return true;
    }
    
    public void afterColumnUpdate(String name, def item, String colName ) {;}
    
    
    public void addItem (String name, def item) {
        if( mode == 'read' ) return;
        entity.get(name).add( item );
    }
    
    public boolean beforeRemoveItem(String name, def item ) {
        return true;
    }
    
    public final void removeItem( String name, def item) {
        if( mode == 'read' ) return;
        if( !beforeRemoveItem(name, item) ) return;
        String dname = name +"::deleted";
        if( entity.get(dname) == null ) {
            entity.put(dname, []);
        }
        entity.get(dname).add( item );
        entity.get(name).remove( item );
    }

    
    //For managing small combo like lists....
    def listTypeHandler = { n->
        def fld = schema.fields.find{ it.name == n };    
        if( fld?.lov!=null ) {
            return LOV.get( fld.lov.toUpperCase() )*.key;
        }
        else if(fld.ref!=null) {
            try {
                String nn = fld.ref;
                if(nn.indexOf(".")>0) nn = nn.split(":")[1];
                def m = [_schemaname:nn];
                m._limit =100;
                m._start = 0;
                return  qryService.getList( m );
            }
            catch(e) {
                e.printStackTrace();
            }
        }
        return [];
    }
    def listTypes = new ListTypeMap(listTypeHandler);
    
}

public class ListTypeMap extends HashMap {
    def handler;
    public ListTypeMap( def h ) {
        handler = h;
    }
    public def get( def k ) {
        return handler(k);
    }
}

