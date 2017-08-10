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
    int fieldCount;
            
    void init() {
        fieldList = caller.fieldList;
    }
     
           
    def stringOperatorList = [
        [caption: "like", key:"LIKE"],
        [caption: "equals", key:"="]
    ];
            
    def numberOperatorList = [
        [caption: "greater than", key:">"],
        [caption: "less than", key:"<"],
        [caption: "greater than or equal to", key:">=" ],
        [caption: "less than or equal to", key:"<="],
        [caption: "equal to", key:"="],
        [caption: "between", key:"BETWEEN"],
    ];
            
    def lookupOperatorList = [
        [caption: "is any of the ff.", key: "IN"],
        [caption: "not in any of the ff.", key:"NOT IN"],
    ];
            
    def dateOperatorList = [
        [caption: "equals", key: "="],
        [caption: "on or before", key: "<="],
        [caption: "before", key:"<"],
        [caption: "on or after", key:">="],
        [caption: "after", key:">"],
        [caption: "between", key:"BETWEEN"],
    ];
            
    def booleanOperatorList = [
        [caption: "is true", key: "=true"],
        [caption: "is false", key: "=false"],
    ];
    
    def getDatatype() {
        if( !entry.field ) return null;
        if ( entry.field.type == null ) return "string"; 
        return entry.field.type;
    }
            
    void addField() { 
        caller.addField();
    }     
            
    void removeField() {
        caller.removeField( this.entry );
    }
         
    
    void lookupList() { 
        def h = { o-> 
            MsgBox.alert(o);
            entry.value = o; 
            entry.displayvalue = o*.value.join(', '); 
        }
        
        if ( entry.field.list ) {
            def listhandler = [
                getColumnList: {
                    return [
                        [name: 'value', caption:'']
                    ];
                }, 
                fetchList: {
                    return entry.field.list; 
                }, 
                isMultiSelect: {
                    return true; 
                }
            ] as BasicListModel; 
            
            Modal.show('simple_list_lookup', [listHandler: listhandler, onselect: h]); 
        } else if ( entry.field.handler ) {
            Modal.show( entry.field.handler, [onselect: h]);
        } else if ( entry.field.schemaname ) { 
            Modal.show('dynamic_schema_lookup', [schemaName: entry.field.schemaname, multiSelect: true]); 
        }
    }
}