package com.rameses.filemgmt.models;

import com.rameses.seti2.models.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
public class FileItemNewModel  {
    
    @Service("PersistenceService")
    def persistenceSvc;
    
    def handler;
    def file;
    def entity = [_schemaname:'sys_fileitem'];    
    
    def doOk() {
        if(!handler) throw new Exception("handler must be set");
        entity.parentid = file.objid;
        entity = persistenceSvc.create( entity );
        handler( entity );
        return "_close";
    }

    def doCancel() {
        return "_close";
    }
    
    
}