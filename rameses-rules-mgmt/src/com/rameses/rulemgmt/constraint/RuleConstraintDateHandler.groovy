package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleConstraintDateHandler extends RuleConstraintHandler {


    @Service("RuleMgmtService")
    def service;

    def _varList;

    void init() { 
        println '** constraint ' 
        constraint.each{
            println '>> '+ it 
        }
        if(constraint.usevar == 1 ) buildVarList();
    }
    
    def getVarList() {
        println '** varList '
        println this._varList; 
        return this._varList; 
    }
    
    void buildVarList() { 
        println 'buildVarList..'
        this._varList = service.findAllVarsByType( [ruleid:condition.parentid, datatype:field.vardatatype, pos: condition.pos ] ).collect{  
            [objid: it.objid, name: it.name]
        };
    }

    @PropertyChangeListener
    def listener = [
        "constraint.usevar": { o-> 
            println 'uservar-> ' + o;
            if( o == 1) {
                constraint.datevalue = null;
                buildVarList();                
            }
            else {
                constraint.var = null;
            }
        }
    ]

    def operatorList = [
        [caption:"before", symbol:"<"],
        [caption:"on or before", symbol:"<="],
        [caption:"after", symbol:">"],
        [caption:"on or after", symbol:">="],
        [caption:"on", symbol:"=="],
    ];

    
}