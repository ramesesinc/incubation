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
* This facility only extracts only a portion of the data. 
*/
public class ChangeInfoModel  {
    
    @Service("ChangeInfoService")
    def changeInfoService;
    
    @Invoker
    def invoker;
    
    @Caller
    def caller;
    
    @Script("ListTypes")
    def listTypes;
    
    def handler;
    def fields;
    def entity;
    def schema;
    def info;
    boolean _inited_ = false;
    
    public def init() {
        if(!schema) throw new Exception("schema is required in ChangeInfoModel");
        if(!entity) throw new Exception("entity is required in ChangeInfoModel");
        if(!fields) {
            fields = invoker.properties.fields;
        }
        if(!fields) throw new Exception("fields is return required in ChangeInfoModel. Pass or specifiy in invoker");
        info = EntityUtil.clone(entity, fields);

        def primKeyMatch = schema.fields.findAll{ it.primary == true && it.source == schema.name }*.name.join("|");
        
        info.findBy = EntityUtil.clone( entity, primKeyMatch ); 
        listTypes.init( schema ); 
        
        _inited_ = true;
        
        def vw = invoker.properties.view;
        if ( !vw ) vw = "default";
        
        return vw;
    }
    
    def doOk() {
        if(!_inited_) throw new Exception("Please run init first");
        def m = [info:info];
        m.info._schemaname = schema.name;
        changeInfoService.update( m );
        if(handler) handler(m);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        