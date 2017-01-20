package com.rameses.filemgmt.models;

import com.rameses.seti2.models.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
public class FileNewModel  {
    
    @Service("PersistenceService")
    def persistenceSvc;
    
    def handler;
    def entity = [_schemaname:'sys_file'];    
    def fileTypes = ["jpg","png","doc","pdf","docx"];
    
    def doOk() {
        if(!handler) throw new Exception("handler must be set");
        entity = persistenceSvc.create( entity );
        handler( entity );
        return "_close";
    }

    def doCancel() {
        return "_close";
    }
    
    
}