package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.reports.*;
import com.rameses.seti2.models.*;
import java.rmi.server.UID;
import com.rameses.rulemgmt.developer.*;
        
class ActionDefModel extends CrudFormModel {
    
    @Service("RuleActionDefService")
    def devService;
    
    def varStatus;
    def selectedParam;
    
    def paramModel = [
        fetchList: { o->
            return  entity.params;
        }
    ] as BasicListModel;
    
    public void initNewData() {
        entity = [:];
        entity.params  = [];
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
        entity.params = RuleDevUtil.shiftPos( entity.params, pos );
        if(pos>0) pos = pos-1;
        paramModel.setSelectedItem(pos);
        paramModel.reload();
    }

    def shiftDown() {
        int pos = varStatus.index + 1;
        entity.params = RuleDevUtil.shiftPos( entity.params, pos );
        if(pos >= entity.params.size()) pos = entity.params.size()-1;
        paramModel.setSelectedItem(pos);
        paramModel.reload();
    }
    
    void addParam() {
        def h = { o->
            entity.params << o;
        }
        def p = [objid: "ACTPARAM"+new UID()];
        p.sortorder = 0;
        Modal.show( "sys_rule_actiondef_param:create", [entity: p, handler: h] )
    }

    def editParam() {
        if(!selectedParam) throw new Exception("Please select a param");
        def h = { o->
            def h = entity.params.find{ it.objid == o.objid };
            h.putAll(o);
        }
        def p = [:];
        p.putAll( selectedParam );
        Modal.show( "sys_rule_actiondef_param:edit", [entity: p, handler: h] )
    }

    void removeParam() {
        if(MsgBox.confirm("You are about to remove this entry. Continue?")) {
            entity.params.remove( selectedParam );
            if(!entity._deleted_params) entity._deleted_params = [];
            entity._deleted_params << selectedParam;
        }
    }
          
    public def save() {
        def e = entity;
        if( mode == 'edit' ) {
            e = entity.data(); 
        }
        e.name = entity.actionname;
        e.objid = entity.actionclass;
        e.params.each {
            it.objid = e.objid + "." + it.name.replace("-","_");
        }
        devService.save( e );
        entity = e;
        MsgBox.alert("Record saved");
        mode = 'read';
        return null;
    }
    
    public def copyAction() {
        def h = { txt->
            devService.copy( [oldid:entity.objid, newid: txt ] ); 
            entity.objid = txt;
            reloadEntity();
            MsgBox.alert("Copy successful" );
            return "_close";
        }
        Modal.show( "text:prompt", [title: "Enter new action class for copy", text: entity.actionclass, handler:h] );
    }
    
    def refactor() {
        def h = { txt->
            if(!MsgBox.confirm("You are about to refactor/rename the class and will affect all rules associated with this. Proceed?")) return null; 
            devService.refactor( [oldid:entity.objid, newid: txt ] );
            entity.objid = txt;
            open();
            return "_close";
        }
        Modal.show( "text:prompt", [title: "Enter new action class", text: entity.actionclass, handler:h] );
    }

    def updateId() {
        devService.refactor( [oldid:entity.objid, newid: entity.actionclass ] ); 
        entity.objid = entity.actionclass;
        open();
        return null;
    }

    def merge() {
        def h = { txt->
            if(!MsgBox.confirm("This will transfer all links to the new target class. Check first if has similar parameters before executing. Proceed?")) return null;
            devService.merge( [oldid:entity.objid, newid: txt ] ); 
            entity.objid = txt;
            open();
            return "_close";
        }
        Modal.show( "text:prompt", [title: "Enter target action class for merge", text: entity.actionclass, handler:h] );
    }


    
}