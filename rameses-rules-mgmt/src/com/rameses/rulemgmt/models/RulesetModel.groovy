package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.osiris2.reports.*;
import com.rameses.seti2.models.*;
        
class RulesetModel extends CrudFormModel {
           
    def selectedFact;
    def selectedAction;
    
    def factListModel = [
        fetchList: {o->
            def m = [_schemaname: 'sys_rule_fact' ];
            m.where = [ 'ruleset.ruleset=:r', [r:entity.name]];
            return queryService.getList( m );
        }
    ] as BasicListModel;        

    def actionListModel = [
        fetchList: { o->
            def m = [_schemaname: 'sys_rule_actiondef' ];
            m.where = [ 'ruleset.ruleset=:r',[r: entity.name]];
            return queryService.getList( m );
        }
    ] as BasicListModel;        

    void addFact() {
        def h = { o->
            def m = [_schemaname: 'sys_ruleset_fact'];
            m.ruleset = entity.name;
            m.rulefact = o.objid;
            persistenceService.create( m );
            factListModel.reload();
        }
        Modal.show( "sys_rule_fact:lookup", [onselect:h] );
    }
    
    void removeFact() {
        if(!selectedFact) throw new Exception("Please select a fact");
        def m = [_schemaname: 'sys_ruleset_fact'];
        m.ruleset = entity.name;
        m.rulefact = selectedFact.objid;
        persistenceService.removeEntity( m );
        factListModel.reload();
    }

    void addAction() {
        def h = { o->
            def m = [_schemaname: 'sys_ruleset_actiondef'];
            m.ruleset = entity.name;
            m.rulefact = o.objid;
            persistenceService.create( m );
            actionListModel.reload();
        }
        Modal.show( "sys_rule_actiondef:lookup", [onselect:h] );
    }
    
    void removeAction() {
        if(!selectedAction) throw new Exception("Please select an action");
        def m = [_schemaname: 'sys_ruleset_actiondef'];
        m.ruleset = entity.name;
        m.rulefact = selectedAction.objid;
        persistenceService.removeEntity( m );
        actionListModel.reload();
    }


}