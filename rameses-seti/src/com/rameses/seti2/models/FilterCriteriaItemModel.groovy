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
            
    boolean hasCallerProperty( property ) {
        if ( caller == null ) return false; 
        return caller.metaClass.hasProperty(caller, property ); 
    }
    boolean hasCallerMethod( property ) {
        if ( caller == null ) return false; 
        return caller.metaClass.respondsTo(caller, property ); 
    }
    
    void init() {
        fieldList = []; 
        
        if ( hasCallerProperty('fieldList')) {
            fieldList = caller.fieldList;
        } 
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
        if ( hasCallerMethod('addField')) {
            caller.addField();
        } 
    }     
            
    void removeField() { 
        if ( hasCallerMethod('removeField')) { 
            caller.removeField( this.entry ); 
        } 
    }
         
    
    void lookupList() { 
        if ( entry.field.list ) {
            showKeyValueList(entry.field.list, true, h );
        } 
        else if ( entry.field.handler ) {
            Modal.show( entry.field.handler, [onselect: h]);
        } 
        else if ( entry.field.schemaname ) { 
            def cols = entry.field.cols;
            def lkey = entry.field.lookupkey;
            def lval = entry.field.lookupvalue;
            if(lkey==null || lval == null ) throw new Exception("Provide a lookupkey and lookupvalue in "+ entry.field.name);
            def m = [schemaName: entry.field.schemaname]; 
            m.onselect = { o->
                entry.value = [];
                for(xx in o) {
                    entry.value << [key: xx[(lkey)],  value: xx[(lval)] ];
                }
                entry.displayvalue = entry.value*.value.join(",");
                return null;
            }
            m.multiSelect = true;
            m.selectColNames = lkey+","+lval;
            Modal.show('dynamic_schema_lookup', m ); 
        }
    }
    
    def showKeyValueList( def list, boolean multiSelect, def onselect ) {
        def listhandler = [
            getColumnList: {
                return [
                    [name: 'value', caption:'']
                ];
            }, 
            fetchList: {
                return list; 
            }, 
            isMultiSelect: {
                return multiSelect; 
            }
        ] as BasicListModel; 

        Modal.show('simple_list_lookup', [listHandler: listhandler, onselect: onselect]); 
    }
    
    void showInfo() {
        showKeyValueList( entry.value, false, {o->;} );
    }
    
}