package com.rameses.rulemgmt.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.rulemgmt.*;
import java.rmi.server.*;

class RuleActionController  {
        
    @Service("RuleMgmtService")
    def service;

    @Binding
    def binding;

    def rule;
    def actionDef;
    def entity;     //action.
    def paramControls = [];
    def savehandler;
    def mode;

    void create() {
        mode = "create";
        entity = [objid:'RACT'+new UID(), params:[]];
        entity.parentid = rule.objid;
        entity.rulename = rule.name;
        actionDef = service.findActionDef(actionDef);
        entity.actiondef = actionDef;
        /*
        actionDef.params.each { 
            def actionParam = [:];
            actionParam.objid = "RULACT"+ new UID();
            actionParam.param = it;
            actionParam.actiondefparam = it;
            entity.params << actionParam;
            addParamControl(actionParam);
        }
        */
    }

    void open() {
        mode = "edit";
        actionDef = service.findActionDef(entity.actiondef);
        entity.params.each {
            addParamControl(it);
        }
    }

    void addParamControl(def actionParam) {
        def actionParamDef = actionParam.actiondefparam;
        def m = [:];
        m.objid = actionParam.objid;
        m.param = actionParamDef;
        m.caption = actionParamDef.title;
        m.type = "subform";

        m.properties = [:];
        m.properties.action = entity;
        m.properties.actionParam = actionParam;
        m.properties.actionParamDef = actionParamDef;
        m.properties.rule = rule;
        
        def h = actionParamDef.handler;
        if(!h) h = actionParamDef.datatype;
        m.handler = "ruleaction:handler:"+h;
        paramControls << m;
    }
            
    def doOk() {
        service.saveAction( entity );
        if(mode=="create") {
            rule.actions << entity;
        }   
        if(savehandler) savehandler(rule);
        return "_exit";
    }
    
    def doCancel() {
        return "_exit";
    }

    void upgrade() {
        def list = [];
        def newActionDef = service.findActionDef(entity.actiondef);
        paramControls.clear();
        newActionDef.params.each { pp->
            def z = entity.params.find{ it.actiondefparam.objid == pp.objid };
            if( z ) {
                list << z;
                addParamControl(z);
            }
            else {
                def actionParam = [:];
                actionParam.objid = "RULACT"+ new UID();
                actionParam.param = pp;
                actionParam.actiondefparam = pp;
                list << actionParam;
                addParamControl(actionParam);
            }
        }
        entity.params = list;
    }


    def getAvailableFields() {
        return actionDef.params; //.findAll{it.required!=1};
    }

    def addParameter() {
        def fieldList = getAvailableFields();
        return InvokerUtil.lookupOpener("rule:selectfield",[
            fieldList : fieldList,
            onselect: {o->
                def actionParam = [:];
                actionParam.objid = "RULACT"+ new UID();
                actionParam.param = o;
                actionParam.actiondefparam = o;
                entity.params << actionParam;
                addParamControl(actionParam);
                binding.refresh( "paramControls" );
            }
        ]);
    }

    void removeParam(def param) {
        entity.params.remove(param);
        paramControls.clear();
        entity.params.each {
            addParamControl(it);
        }
        binding.refresh();
    }
    
}