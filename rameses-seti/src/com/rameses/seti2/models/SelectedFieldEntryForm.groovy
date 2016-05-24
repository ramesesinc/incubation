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

/****
* This displays selected fields for editing. 
* The fields, handler and info is initially passed
*    fields = field names for regex match
*    handler = handler when info is updated
*    entity = the original bean
*    schema = the schema map to check field properties
*****/
public class SelectedFieldEntryForm  {
    
    @Script("ListTypes")
    def listTypes;

    def handler;
    def fields;
    def schema;
    def entity;
    def formControls = [];
    
    public void init() {
        if(!fields) throw new Exception("fields is required");
        if(!entity) throw new Exception("entity is required");
        if(!handler) throw new Exception("handler is required");
        if(!schema) throw new Exception("schema is required");
        entity = EntityUtil.clone(entity, fields);
        def arr = fields.split(",");
        
        arr.each { t->
            def flds = schema.fields.findAll{ it.name.matches(t.trim()) };
            for( f in flds ) {
                def fc = FormControlUtil.createControl( f, entity );
                if(fc==null) continue;
                if( !formControls.find{ ff-> ff.name == fc.name } ) {
                    formControls << fc;
                }
            };
        };
        listTypes.init( schema );
    }
 
    def doOk() {
        handler( entity );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        