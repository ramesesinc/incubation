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

/*******************************************************************************
 * Limitation: There should only be 1 primary key
 *******************************************************************************/
public class DataEditorModel extends DynamicForm {
    
    @Service("ChangeInfoService")
    def changeInfoSvc;
    
    @Service("SchemaService")
    def schemaSvc;

    //entity is the original record
    def primKey;
    def newValues;
    def oldValues;
    def remarks;
    def handler;
    def _schema;
    boolean _inited_ = false;
    
    def message = "Fill in data to edit, specify remarks and click OK to commit changes";
    
    public String getSchemaName() {
        def _schemaname = invoker.properties.schemaName;
        if(_schemaname) return _schemaname;
        return workunit?.info?.workunit_properties?.schemaName;
    } 
    
    public String getLogSchemaName() {
        return workunit?.info?.workunit_properties?.logSchemaName;
    } 
    
    public String getTag() {
        return invoker.properties.tag;
    }
    
    //This is if you want hghly generic editing.
    
    void afterSave() {}
    
    void beforeInit() {;}
    void initData() {;}
    
    
    void setDataForUpdate() {
        throw new Exception("Please override setDataForUpdate");
    }
    
    public def getFormFields() {
        return null;
    }
    
    public def getEntity() {
        return caller.entity;
    }
    
    void buildFormFields() {
        fields = [];
        def fieldNames = invoker.properties.fields;
        if(!fieldNames) return;
        fieldNames.split(",").each { name->
            fields << _schema.fields.find{it.name == name.trim()};
        }
    }
    
    void init() {
        beforeInit();
        data = [:];
        _schema = schemaSvc.getSchema( [name: schemaName] );
        primKey = _schema.fields.find{it.primary==true && it.source==schemaName}.name;
        fields = getFormFields();
        if(fields == null) buildFormFields();
        fields.each {
            data.put(it.name, entity.get(it.name));
        }
        buildFormInfos();
        oldValues = MapBeanUtils.copy(data);
        _inited_ = true;
    }
    
    void validateEntry() {
        //do nothing. Throw exception if theres an error.
    }
    
    def doOk() {
        if(!_inited_) throw new Exception("Please invoke init action on thos workunit");
        if( oldValues == data )
            throw new Exception("No change has been made");
        
        validateEntry();
        def u = [:];
        u.putAll( data );
        
        def e = [:];
        e._tag = tag;
        e._schemaname = schemaName;
        e.putAll( u );
        e.put( primKey, entity.get( primKey ) );
        
        //build the log data
        def log = [_schemaname: getLogSchemaName() ];
        log.reftype = schemaName;
        log.refid = entity.get( primKey );
        log.remarks = remarks;
        log.oldvalue = oldValues;
        log.newvalue = u;
        log.action = "update";
        e._loginfo = log; 
        changeInfoSvc.save( e );
        if(handler) handler();
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        