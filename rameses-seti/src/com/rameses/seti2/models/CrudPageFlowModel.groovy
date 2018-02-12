package com.rameses.seti2.models;

import com.rameses.osiris2.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;
import com.rameses.rcp.framework.*;
import com.rameses.common.*;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;


public class CrudPageFlowModel extends PageFlowController {
    
    @Service("PersistenceService")
    def persistenceSvc;
    
    @Service("QueryService")
    def qryService;
    
    @Script("User")
    def userInfo;
    
    def schema;
    private String _schemaName_ ;
    def adapter;
    def entity;
    def itemHandlers = [:];     //holder for all specific item handlers
    def mode = "create";
    def barcodeid;
    def findBy;                 //this is also included for opening via barcode.
    def refid;                  //this is also used for opening. If there is no entity passed
    
    @Script("ListTypes")
    def listTypes;
    
    @Script("Lov")
    def lov;
    
    public void afterCreate(){;}
    public void  afterCreateData( _schemaname, map ){;}
    
    public def getPersistenceService() {
        return persistenceSvc;
    }
    
    public def getQueryService() {
        return qryService;
    }

    public String getSchemaName() {
        if( _schemaName_ )
            return _schemaName_;
        else    
            return workunit?.info?.workunit_properties?.schemaName;
    }
    
    @FormTitle
    String getFormTitle() {
        if( invoker.properties.formTitle ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formTitle,[entity:entity]);
        }
        if( invoker.caption ) {
            return invoker.caption;
        }
        return getSchemaName();
    }
    
    @FormId
    String getFormId() {
        if( invoker.properties.formId ) {
            return ExpressionResolver.getInstance().evalString(invoker.properties.formId,[entity:entity]);
        }
        return workunit.workunit.id;
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
        afterCreate();
    }
    
    public void afterInit(){
        initNewData();
    }
    
    
    boolean _inited_ = false;
    public void init() {
        if( !schemaName )
            throw new Exception("Please provide a schema name. Put it in workunit schemaName or override the getSchemaName()");
        if( _inited_ ) return;
        schema = getPersistenceService().getSchema( [name: schemaName, adapter: adapter]  );
        _inited_ = true;
        listTypes.init( schema );
        afterInit();
    }
    
    public void saveCreate() {
        entity._schemaname = getSchemaName();
        entity = getPersistenceService().create( entity );
    }
    
    public void saveUpdate() {
        entity._schemaname = getSchemaName();
        entity = getPersistenceService().update( entity )
    }
    
    def showMenu() {
        showMenu( inv );
    }
    
    def showMenu( inv ) {
        def menu = inv.properties.context;
        def op = new PopupMenuOpener();
        //op.add( new ListAction(caption:'New', name:'create', obj:this, binding: binding) );
        try {
            op.addAll( Inv.lookupOpeners(schemaName+":" + getFormType() + ":" + menu) );
        } catch(Throwable ign){;}
        if(menu=="menuActions") {
            op.add( new com.rameses.seti2.models.PopupAction(caption:'Close', name:'_close', obj:this, binding:binding) );
        }
        return op;
    }
    
    
    
    //information about the user
    public def getUser() {
        def app = userInfo.env;
        return [objid: app.USERID, name: app.NAME, fullname: app.FULLNAME, username: app.USER ];
    }
    
    
    public def start() {
        init();
        return super.start();
    }
    
    public def start(String name) {
        return super.start(name);
    }

    def create() {
        mode = "create";
        init();
        return super.start("create");
    }

    /**************************************************************************
     * This method is an alternative to openining a record. 
     **************************************************************************/
    /*
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
    
    public final def buildFindByForOpenByRefid() {
        if( !schema ) throw new Exception("buildKeysForOpenByRefid error. There is no schema built yet!");
        def primKey = schema.fields.find{ it.primary == true }?.name;
        findBy = [:];
        findBy.put(primKey, refid);
        //initialize also the entity bec. there is no entity in this instance
    }
    
    public def fetchEntityData() {
         return getPersistenceService().read( entity );
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
        entity.putAll(fetchEntityData());
        //we need to reset this so it can be used again.
        findBy = null;  
        //we need to reset the schema name for update.
        entity._schemaname = schemaName;
        afterOpen();
        if( pageExists("view")) return "view";
        return null;
    }
    */
    
}