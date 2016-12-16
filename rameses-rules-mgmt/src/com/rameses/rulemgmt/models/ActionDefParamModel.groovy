package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.reports.*;
import com.rameses.seti2.models.*;
import java.rmi.server.UID;
        
class ActionDefParamModel {
    
    def entity;
    def handler;
    
    def handlerTypes = [
        "lov", "lookup", "var", "expression", "message", "boolean", "string"
    ]

    @PropertyChangeListener
    def listener = [
        "entity.handler": { o->
            switch(o) {
                case "decimal" : 
                    entity.vardatatype = "decimal";
                    entity.datatype = "decimal";
                    break;
                case "integer" : 
                    entity.vardatatype = "integer";
                    entity.datatype = "integer";
                    break;
                case "string" : 
                    entity.vardatatype = "string";
                    entity.datatype = "string";
                    break;
                case "boolean" : 
                    entity.vardatatype = "boolean";
                    entity.datatype = "boolean";
                    break;
                default:
                    entity.vardatatype = null;
                    entity.datatype = null;
            }
        }
    ]
     
    def getOpener() {
        if(!entity.handler) return null;
        return new Opener( outcome: entity.handler );
    }
    
    def doCancel() {
        return "_close";
    }
    
    def doOk() {
        if(!handler) throw new Exception("Please indicate handler")
        handler( entity );
        return "_close";
    }
    
}