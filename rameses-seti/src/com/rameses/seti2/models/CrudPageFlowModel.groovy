package com.rameses.seti2.models;

import com.rameses.osiris2.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;
import com.rameses.rcp.framework.ClientContext;
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
        return querySvc;
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
        init();
        return super.start(name);
    }

}