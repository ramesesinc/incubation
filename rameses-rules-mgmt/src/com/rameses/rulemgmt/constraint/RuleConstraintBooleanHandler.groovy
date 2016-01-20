package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleConstraintBooleanHandler extends RuleConstraintHandler {

    def operatorList = [
        [caption:"is true", symbol:"== true"],
        [caption:"not true", symbol:"== false"],
    ];

    
}