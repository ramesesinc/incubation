package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleConstraintVarHandler  extends RuleConstraintHandler {

    @Service("RuleMgmtService")
    def service;

    def varList;
    def operatorList;

    void init() {
        varList = service.findAllVarsByType( [ruleid:condition.parentid, datatype:field.vardatatype, pos: condition.pos ] ).collect{  
            [objid: it.objid, name: it.name]
        };
        operatorList = [];
        if(varList) operatorList << [caption:"equals", symbol:"=="];
        operatorList << [caption:"is null", symbol:"== null"];
        operatorList << [caption:"not null", symbol:"!= null"];
    }

}