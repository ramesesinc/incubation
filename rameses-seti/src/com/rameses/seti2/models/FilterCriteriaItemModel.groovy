package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
class FilterCriteriaItemModel {
        
    @Caller
    def caller;

    def fieldList;

    def entry = [:];
    def operatorList;
    def datatype;
    int fieldCount;
            
    void init() {
        fieldList = caller.fieldList;
        setFieldData();
    }
            
    void setFieldData() {
        if( !entry.field ) return;
        datatype = entry.field.type;
        if(datatype==null) datatype = "string";
        if( datatype == "integer" || datatype == "decimal" ) {
            operatorList = caller.numberOperators;
        }
        else if( datatype == "date" || datatype=="timestamp") {
            println "date operators";
            operatorList = caller.dateOperators;
        }
        else if( datatype == "boolean" ) {
            operatorList = caller.booleanOperators;
        }
        else {
            operatorList = caller.stringOperators;
        }
    }

    @PropertyChangeListener
    def l = [
        "entry.field" : { o->  
            entry.operator = null;
            entry.value = null;
            entry.value1 = null;
            setFieldData(); 
        }
    ]
            
    void addField() { 
        caller.addField();
    }     
            
    void removeField() {
        caller.removeField( this.entry );
    }
                      
}