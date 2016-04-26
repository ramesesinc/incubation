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
public class ChangeInfoModel extends CrudFormModel {
    
    @Service("ChangeInfoService")
    def changeInfoService;
    
    @Caller
    def caller;
    
    def info = [:];

    public void afterInit() {
        def arr = invoker.properties.fields?.split(",");
        arr?.each {
            def s = it.trim();
            schema?.fields.findAll{it.name.matches(s) }.each { f->
                def v = EntityUtil.getNestedValue( entity, f.extname );
                EntityUtil.putNestedValue( info, f.extname, v  );
            }
        };
        schema?.fields.findAll{it.primary==true && it.source == getSchemaName() }.each {
            def v = EntityUtil.getNestedValue( entity, it.extname );
            EntityUtil.putNestedValue( info, it.extname, v  );
        };
    }
    
    def doOk() {
        if(!_inited_) throw new Exception("Please run init first");
        def m = [info:info];
        m.info._schemaname = getSchemaName();
        changeInfoService.update( m );
        if(caller!=null) {
            caller.loadData();
        }
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        