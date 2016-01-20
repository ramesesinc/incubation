package com.rameses.rulemgmt;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*
import com.rameses.rulemgmt.*;
import java.rmi.server.*;

class RuleConditionController  {

    @Service("RuleMgmtService")
    def service;

    @Binding
    def binding;

    def rule;
    def entity;
    def constraintControls = []; 
    def fact;
    def savehandler;
    def mode;

    void initCreate() {
        mode = "create";
        fact = service.findFact( fact ); 
        entity = [vars:[], constraints: []];
        entity.parentid = rule.objid;
        entity.rulename = rule.name;
        entity.objid = "RCOND" + new UID();
        entity.fact = fact;
        entity.pos = rule.conditions.size();
        if( fact.defaultvarname ) entity.varname = fact.defaultvarname;
    }

    void create() {
        initCreate();
        //immediately load all required fields in the fact.
        def reqs = fact.fields.findAll{ it.required == 1 };
        reqs.each { fld->
            def constraint = [objid:"RCONST"+new UID(), pos:entity.constraints.size() ];
            constraint.field = fld;
            entity.constraints << constraint;
        }
        entity.constraints.each {c->
            addConstraintControl(c);
        }
    }

    void open() {
        mode = "open";
        //build up the constraints
        entity.constraints.each {c->
            addConstraintControl(c);
        }
    }

    /*****************************************************************
    * constraints
    *****************************************************************/
    void addConstraintControl(def constraint) {
        def field = constraint.field;
        def m = [:];
        m.objid = constraint.objid;
        m.fieldname = field.name;
        m.caption = field.title;
        m.type = "subform";
        m.properties = [:];
        m.properties.condition = entity;
        m.properties.constraint = constraint;
        m.properties.field = field;
        m.properties.removehandler = { x-> 
            def g = constraintControls.find{ it.objid == x.objid };
            constraintControls.remove(g);
            if(entity._deleted_constraints==null) entity._deleted_constraints = [];
            entity._deleted_constraints << x;
            binding.refresh();
        }
        def h = field.handler;
        if(!field.handler) h = field.datatype;
        m.handler = "ruleconstraint:handler:"+h;
        constraintControls << m;
    }

    def getAvailableFields() {
        return fact.fields.findAll{it.required!=1};
    }

    def addConstraint() {
        def fieldList = getAvailableFields();
        return InvokerUtil.lookupOpener("rulecondition:selectfield",[
            fieldList : fieldList,
            onselect: {o->
                def constraint = [objid:"RCONST"+new UID(), pos:entity.constraints.size() ];
                constraint.field = o;
                entity.constraints << constraint;
                addConstraintControl(constraint);
                binding.refresh( "constraintControls" );
            }
        ]);
    }

    def doOk() {
        service.saveCondition(entity);
        if(mode=="create") {
            rule.conditions << entity;
        }    
        if(savehandler) savehandler(rule);
        return "_exit";
    }

    def doCancel() {
        return "_exit";
    }

}