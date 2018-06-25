package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.reports.*;
import com.rameses.seti2.models.*;
import java.rmi.server.UID;
import com.rameses.rulemgmt.developer.*;
        
class FactModel extends CrudFormModel {
    
    @Service("RuleFactService")
    def devService;
    
    def varStatus;
    def selectedField;
    
    def fieldModel = [
        fetchList: { o->
            return  entity.fields;
        }
    ] as BasicListModel;
    
    public void initNewData() {
        entity = [:];
        entity.fields  = [];
    }
    
    public def fetchEntityData() {
        return devService.find(entity); 
    }

    /*
    public def edit() {
        mode = "edit";
        return null;
    }
    */
    
    def shiftUp() {
        int pos = varStatus.index;
        entity.fields = RuleDevUtil.shiftPos( entity.fields, pos );
        if(pos>0) pos = pos-1;
        fieldModel.setSelectedItem(pos);
        fieldModel.reload();
    }

    def shiftDown() {
        int pos = varStatus.index + 1;
        entity.fields = RuleDevUtil.shiftPos( entity.fields, pos );
        if(pos >= entity.fields.size()) pos = entity.fields.size()-1;
        fieldModel.setSelectedItem(pos);
        fieldModel.reload();
    }
    
    void addField() {
        def h = { o->
            entity.fields << o;
        }
        def p = [:];
        p.sortorder = 0;
        Modal.show( "sys_rule_fact_field:create", [entity: p, handler: h] )
    }

    def editField() {
        if(!selectedField) throw new Exception("Please select a param");
        def h = { o->
            def h = entity.fields.find{ it.objid == o.objid };
            h.putAll(o);
        }
        def p = [:];
        p.putAll( selectedField );
        Modal.show( "sys_rule_fact_field:edit", [entity: p, handler: h] )
    }

    void removeField() {
        if(MsgBox.confirm("You are about to remove this entry. Continue?")) {
            entity.fields.remove( selectedField );
            if(!entity._deleted_fields) entity._deleted_fields = [];
            entity._deleted_fields << selectedField;
        }
    }
          
    public def save() {
        def e = entity;
        if( mode == 'edit' ) {
            e = entity.data(); 
        }
        e.name = entity.factclass;
        e.objid = e.name;
        e.fields.each {
            it.objid = e.objid + "." + it.name;
        }
        devService.save( e );
        entity = e;
        MsgBox.alert("Record saved");
        mode = 'read';
        return null;
    }
    
    public def copyFact() {
        def p = MsgBox.prompt( "Enter new fact class" );
        if(!p) return;
        devService.copy( [oldid:entity.objid, newid: p ] ); 
        MsgBox.alert("Copy successful" );
    }
    
    def refactor() {
        def p = MsgBox.prompt( "Enter new fact class" );
        if(!p) return;
        if(!MsgBox.confirm("You are about to refactor/rename the class and will affect all rules associated with this. Proceed?")) return false;
        devService.refactor( [oldid:entity.objid, newid: p ] ); 
        return "_close";
    }

    def updateId() {
        devService.refactor( [oldid:entity.objid, newid: entity.factclass ] ); 
        return "_close";
    }
    
}