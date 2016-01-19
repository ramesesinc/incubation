package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleConstraintLookupHandler extends RuleConstraintListValueHandler {

    @Service("RuleMgmtService")
    def service;

    def varList = [];

    void buildVarList() {
        try {
            varList = service.findAllVarsByType( [ruleid:condition.parentid, datatype:field.vardatatype, pos: condition.pos ] ).collect{  
                [objid: it.objid, name: it.name]
            };
        }
        catch(e) {
            println "error in building varlist ->" + e.message;
        }
    }
    
    void init() {
        if( constraint.usevar == 1 ) buildVarList();
    }

    @PropertyChangeListener
    def listener = [
        "constraint.usevar": { o->
            if(o == 1 ) {
                constraint.listvalue = null;
                buildVarList();
            }
            else {
                constraint.var = null;
            }
        }
    ]

    def showLookup() {
        if( !field.lookupkey || !field.lookupvalue || !field.lookuphandler )
            throw new Exception( "Please specify a lookup key, value and handler in the definition" )
        def m = [:];
        
        m.lookupHandler = field.lookuphandler;
        m.lookupKey = field.lookupkey;
        m.lookupValue = field.lookupvalue;
        m.items = [];
        if(constraint.listvalue !=null) {
            m.items.addAll( constraint.listvalue );
        }    
        m.handler = { o->
            constraint.listvalue = o;
            binding.refresh("selection");
        }
        return InvokerUtil.lookupOpener( "ruleconstraint:multilookup", m );
    }
    
    String getSelection() {
        if( constraint.listvalue != null ) {
            return constraint.listvalue*.value.join(",");
        }
        return "";
    }

}