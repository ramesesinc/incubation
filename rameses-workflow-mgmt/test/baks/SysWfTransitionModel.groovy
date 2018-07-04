package com.rameses.wf.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;
import com.rameses.seti2.models.*;

public class SysWfTransitionModel extends CrudFormModel {
    
    def props = [:];

    void afterOpen() {
        MsgBox.alert( entity );
        props.putAll( entity.properties );
    }
    
    void beforeSave( def m ) {
        MsgBox.alert( m );
        entity.properties = props;
    }
    
}