package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleConstraintListValueHandler extends RuleConstraintHandler {

    def operatorList = [
        [caption:"is any of the ff.", symbol:"matches"],
        [caption:"not exist in", symbol:"not matches"],
        [caption:"is null", symbol:"==null"],
        [caption:"not null", symbol:"!=null"],
    ];

    
}