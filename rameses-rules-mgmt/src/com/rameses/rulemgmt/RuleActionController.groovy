package com.rameses.rulemgmt;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.rulemgmt.*;
import java.rmi.server.*;

class RuleActionController  {
        
    @Service("RuleMgmtService")
    def service;

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
        actionDef.params.each { 
            def actionParam = [:];
            actionParam.objid = "RULACT"+ new UID();
            actionParam.param = it;
            actionParam.actiondefparam = it;
            entity.params << actionParam;
            addParamControl(actionParam);
        }
    }

    void open() {
        mode = "edit";
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

}