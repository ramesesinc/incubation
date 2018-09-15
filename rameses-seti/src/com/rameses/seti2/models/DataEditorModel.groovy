package com.rameses.seti2.models;
 
import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;

public class DataEditorModel extends DynamicForm {
    
    @Service("PersistenceService")
    def persistenceSvc;
    
    //entity is the original record
    def entity;
    
    def newValues;
    def oldValues;
    def remarks;
    def handler;
    boolean _inited_ = false;
    
    def message = "Fill in data to edit, specify remarks and click OK to commit changes";
    
    public String getSchemaName() {
        return workunit?.info?.workunit_properties?.schemaName;
    } 
    
    public String getTag() {
        return invoker.properties.tag;
    }

    void setFieldToEdit( String key ) {
        if( entity == null ) throw new Exception("Error setValue. entity must not be null");
        if( data == null ) throw new Exception("Error setValue. data must not be null");
        
        oldValues = entity.get(key);
        data.put(key, entity.get(key) );
    }
    
    //This is if you want hghly generic editing.
    
    void afterSave() {}
    
    void beforeInit() {;}
    void initData() {;}
    
    void fetchData(def fields) {
        throw new Exception("Please override fetchData( fields )");
    }
    void setDataForUpdate() {
        throw new Exception("Please override setDataForUpdate");
    }
    
    void init() {
        beforeInit();
        if( !entity ) entity = caller.entity; 
        data = [:];
        /*
        def schema = persistenceSvc.getSchema( [name: schemaName] );
        def primKeys = schema.fields.findAll{it.primary==true && it.source==schemaName}*.name;
        primKeys.each {k->
            data.put( k, entity.get(k));
        }
        data.put(key, entity.get( key ) );
        fields = [];
        fields.addAll( getDataFields() );
        */
        fields = [];
        fetchData(fields);
        buildFormInfos();
        _inited_ = true;
    }
    
    def doOk() {
        if(!_inited_) throw new Exception("Please invoke init action on thos workunit");
        setDataForUpdate();
        data._tag = tag;
        if(!data._schemaname ) data._schemaname = schemaName;
        persistenceSvc.update( data );
        if(handler) handler();
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        