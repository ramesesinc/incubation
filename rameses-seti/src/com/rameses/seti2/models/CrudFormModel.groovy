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
        
public class CrudFormModel extends AbstractCrudModel implements SubItemListener {
        
    def adapter;
    def entity;
    def mode;
    def itemHandlers = [:];     //holder for all specific item handlers
    
    def barcodeid;              //include the barcodeid so it will be uniform to all.
    def findBy;                 //this is also included for opening via barcode.
    def refid;                  //this is also used for opening. If there is no entity passed
    
     //used for mdi forms.
    def selectedSection;
    def sections;
    
    @Script("ListTypes")
    def listTypes;
    
    String getFormType() {
        return 'form';
    }
    
    public def getEntityContext() {
        return entity;
    }

    boolean isCloseOnSave() { 
        def c = workunit?.info?.workunit_properties?.closeOnSave;
        if ( c ) {
            return "true".equals(c); 
        } else { 
            return false; 
        }
    }
    boolean isCreateAllowed() { 
        if( mode != 'read') return false;
        return super.isCreateAllowed();
    }
     
    boolean isDeleteAllowed() { 
        if( mode != 'read') return false;
        return super.isDeleteAllowed();
    }
                
    boolean isEditAllowed() { 
        if( mode !='read') return false;
        return super.isEditAllowed();
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

    public void afterInit(){;}
    public void afterCreate(){;}
    public void beforeOpen(){;}
    public void afterOpen(){;}
    public void afterEdit(){;}
    public void afterSave(){;}
    
    
    public void beforeSave(def mode){;}
    public void afterCreateData(String name, def data){;}
    
    //override for items
    public void afterFetchItems(String name, def data){;}
    
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
            sections = Inv.lookupOpeners(getSchemaName() + ":section",[:]);
        } 
        catch(Exception ex){;}
    }
    
    boolean _inited_ = false;
    
    void init() {
        if( !schemaName )
            throw new Exception("Please provide a schema name. Put it in workunit schemaName or override the getSchemaName()");
        if( _inited_ ) return;
        schema = getPersistenceService().getSchema( [name: schemaName, adapter: adapter]  );
        listTypes.init( schema );

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
        if( pageExists("create")) return "create";
        return null;
    }

    public def getBarcodeFieldname() {
        return null;
    }
    
    public def openBarcode() {
        if( !barcodeid ) 
            throw new Exception("Open barcode error! barcodeid is not specified" );
        def key = getBarcodeFieldname();
        if( !key ) throw new Exception("Open barcode error! Please override (def)getBarcodeFieldname method" );
        findBy = [:];
        findBy.put(key, barcodeid);
        return open();
    }
    
    /**************************************************************************
     * This method is an alternative to openining a record. If specified
     **************************************************************************/
    public final def buildFindByForOpenByRefid() {
        if( !schema ) throw new Exception("buildKeysForOpenByRefid error. There is no schema built yet!");
        def primKey = schema.fields.find{ it.primary == true }?.name;
        findBy = [:];
        findBy.put(primKey, refid);
        //initialize also the entity bec. there is no entity in this instance
    }
    
    def open() {
        mode = "read";
        init();
        if( !entity ) entity = [:];
        //we need to set the schemaname that will be used for open
        if( refid ) {
            buildFindByForOpenByRefid();
        }
        if( findBy !=null ) {
            entity.findBy = findBy;
        }
        entity._schemaname = schemaName;
        if( debug ) entity.debug = debug;
        beforeOpen();
        entity = getPersistenceService().read( entity );
        
        
        //we need to reset this so it can be used again.
        findBy = null;  
        //we need to reset the schema name for update.
        entity._schemaname = schemaName;
        afterOpen();
        if( pageExists("view")) return "view";
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
        if( pageExists("edit")) return "edit"; 
        return 'default';
    }
    
    def unedit() {
        mode = "read";
        entity.unedit();
        entity = entity.data();
        loadData();
        if( pageExists("view")) return "view";
        return 'default';
    }
    
    def save() {
        if(!_inited_) throw new Exception("This workunit is not inited. Please call open or create action");
       
        if(!MsgBox.confirm('You are about to save this record. Proceed?')) return null;
        
        if( mode == 'create' ) {
            entity._schemaname = schemaName;
            beforeSave("create");
            entity = getPersistenceService().create( entity );
        }
        else {
            //extract from the DataMap. Right now we'll use the pure data first
            //we'll change this later to diff.
            entity = entity.data(); 
            entity._schemaname = schemaName;
            beforeSave("update");
            getPersistenceService().update( entity );
            loadData();
        }
        afterSave();
        mode = "read";
        try {
            caller?.refresh();
        }
        catch(ign){;}
        
        if (isCloseOnSave()) return '_close';
        
        if( pageExists("view")) return "view";
        return "default";
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
        Modal.show("debug:view", [schema:schema, data:e]);
    }
    
    /*************************************************************************
     * Navigation Controls
     **************************************************************************/
    boolean getShowNavigation() {
        //println "show navigation";
        try { 
            if( !caller?.listHandler ) return false; 
        } catch(Throwable t) {;} 
        
        return (mode == 'read');
    }
    
    void moveUp() {
        caller.listHandler.moveBackRecord();
        reloadEntity();
        sections?.each {
            try { it.controller.codeBean.reload(); }catch(e){;}
        }        
    }

    void moveDown() {
        caller.listHandler.moveNextRecord();
        reloadEntity();
        sections?.each {
            try { it.controller.codeBean.reload(); }catch(e){;}
        }
    }
    
    void loadData() {
        entity._schemaname = schemaName;
        entity = getPersistenceService().read( entity );
        itemHandlers.values().each {
            it.reload();
        }
        binding?.refresh();
    }
    
    def reloadEntity() {
        if( caller?.selectedItem !=null ) {
            entity = caller.selectedItem;
            loadData();
            afterOpen();
        }
        updateWindowProperties(); 
    }
    
    /*************************************************************************
     * This part here is for item handlers.  
     **************************************************************************/
    //overridable list events:
    public def openItem(String name,def item, String colName) { 
        return null;
    }
    public boolean beforeColumnUpdate(String name, def item, String colName, def newItem) { return true;}
    public void afterColumnUpdate(String name, def item, String colName ) {;}
    public void beforeAddItem(String name, def item ) {;}
    public void afterAddItem(String name, def item ) {;}
    
    boolean isColumnEditable(String name, Object item, String columnName) {
        return (mode != 'read');
    }
    
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
    
    
    public void addItem (String name, def item) {
        beforeAddItem(name, item);
        if( mode == 'read' ) return;
        entity.get(name).add( item );
        afterAddItem(name, item);
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

}


