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
    def fileTypes = [ 
        [ objid: "jpg",  title: "JPEG image (*.jpg)" ],
        [ objid: "doc",  title: "Word Document (*.doc)" ],
        [ objid: "docx", title: "Word Document (*.docx)" ],
        [ objid: "pdf",  title: "PDF Document (*.pdf)" ],
        [ objid: "png",  title: "PNG image (*.png)" ]
    ];
    
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