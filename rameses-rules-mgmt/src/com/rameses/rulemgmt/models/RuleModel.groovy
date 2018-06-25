package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.rulemgmt.*;
import com.rameses.util.*;
import java.rmi.server.*;
        
class RuleModel extends PageFlowController {

    @Binding
    def binding;

    @Service("RuleMgmtService")
    def service;

    @Script("Template")
    def template;

    @Caller
    def caller;

    def entity;
    def rulegroups;
    def mode;
    def savehandler;

    @Service("QueryService")
    def querySvc;

    def create() {
        mode = "create";
        entity = [salience:50000, conditions:[],actions:[]];
        entity.objid = "RUL"+new UID();
        entity.ruleset = caller.query.ruleset;
        entity.rulegroup = caller.query.rulegroup;
        entity.noloop = 1;

        //temporary solution to retrieve the domain
        def p = [_schemaname:'sys_ruleset'];
        p.findBy = [ name: caller.query.ruleset ];
        p.select = "domain";
        def z = querySvc.findFirst( p );
        entity.domain = z.domain;

        rulegroups = caller.rulegroups;
        return super.start("new")
    }

    def open() {
        mode = "read"; 
        entity = service.open( entity ); 
        rulegroups = caller.rulegroups; 
        if( entity.state == 'DEPLOYED' ) { 
            return super.start("open-deployed"); 
        } else { 
            return super.start("open"); 
        } 
    }

    /**********************************
    * override methods
    **********************************/
    def openRecord( params ) { 
        entity = service.open( params ); 
        return signal('route');
    } 
    boolean isNavActionVisible() {
        return (mode == 'read'); 
    } 



    /**********************************
    * condition
    **********************************/
    def addCondition() {
        return InvokerUtil.lookupOpener("rule:selectfact", [
            rule:entity,
            savehandler: { o->
                binding.refresh();
            }
        ] );
    }

    def editCondition(o) {
        def c = entity.conditions.find{ it.objid == o.objid };
        def fact = service.findFact( [objid: c.fact.objid]);
        def d = fact.handler;
        if(!d) {
            d = "default";
            if(fact.dynamic == 1) d = "dynamic";
        }
        return InvokerUtil.lookupOpener("rulecondition:"+ d +":open", [
            rule:entity,
            fact: fact, 
            entity:c,
            savehandler: { x->
                binding.refresh("ruleHtml");
            }
        ] );
    }

    void removeCondition(o) {
        if( MsgBox.confirm("You are about to remove this condition. Continue?")) {
            try {
                service.removeCondition( [objid: o.objid] );
                def c = entity.conditions.find{ it.objid == o.objid };
                entity.conditions.remove( c );
                binding.refresh("ruleHtml");
            }
            catch(e) {
                MsgBox.err(e);
            }
        }
    }

    /**********************************
    * actions
    **********************************/
    def addAction() {
        return InvokerUtil.lookupOpener("rule:selectactiondef", [
            rule:entity,
            savehandler: { o->
                binding.refresh("ruleHtml");
            }
        ]);
    }

    def editAction(o) {
       def a = entity.actions.find{ it.objid == o.objid }; 
       def h = "default";
       return InvokerUtil.lookupOpener("ruleaction:" + h + ":open", [
            rule:entity,
            entity:a,
            savehandler: { x->
                binding.refresh("ruleHtml");
            }
        ] );               
    }

    void removeAction(o) {
       if( MsgBox.confirm("You are about to remove this action. Continue?")) {
            try {
                service.removeAction( [objid: o.objid] );
                def a = entity.actions.find{ it.objid == o.objid };
                entity.actions.remove( a );
                binding.refresh("ruleHtml");
            }
            catch(e) {
                MsgBox.err(e);
            }
        }
    }

    def getRuleHtml() {
        return template.render( "html/rule_html", [rule: entity, editable:true] );
    }

    void createNew() {
         entity = service.create(entity);
         mode = "read";
         caller.reload();
    }

    void deploy() {
        service.deploy( [objid: entity.objid] );
        entity.state = 'DEPLOYED';
    }

    void undeploy() {
        service.undeploy( [objid: entity.objid] );
        entity.state = 'APPROVED';
    }

    void edit() {
        mode = "edit";
    }

    void cancelEdit() {
        mode = "read";
    }

    void saveUpdate() {
        service.update(entity);
        mode = "read";
        caller.reload();
    }

    def viewRule() {
        def ruleText = service.viewRuleText( [objid: entity.objid] );
        return InvokerUtil.lookupOpener( "rule:ruletext", [ruleText:ruleText] );
    }

    def removeRule() {
        service.removeEntity(entity);
        caller.reload();
    }

    void reloadInfo() {
        entity = service.open( entity );
        binding.refresh();
    }

    def copyRule() { 
        def params = [rulegroups: rulegroups, ruleinfo: entity]; 
        return Inv.lookupOpener('sysrule:copy', params);
    } 

    def transferRuleset() {
        def h = { o->
            o.objid = entity.objid;
            service.transferRuleset( o );
            caller.reload();
        };
        return Inv.lookupOpener("sys_rulegroup:lookup", [domain: entity.domain, handler: h] );
    }
}