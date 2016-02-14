package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
class FilterCriteriaModel {
        
    @Binding
    def binding;
        
    def cols;
    def handler;
            
    def controlList = [];
    def formControls = [
        getControlList: {
            return controlList;
        }
    ] as FormPanelModel;

    def selectedField;
    def fieldList; 
    def criteriaList = []; 
                                                                                    
    void init() {
        fieldList = cols.findAll{ it.indexed == 'true' };
        if(!fieldList)
        throw new Exception("Please define at least one indexed column in the schema");
                
        controlList.clear();
        if(criteriaList) {
            controlList.addAll( criteriaList );
        }
        else {
            addField();
        }      
    }                
               
    def doCancel() {
        return "_close";
    }                
                
    def doOk() {
        if(handler) handler(controlList);
        return "_close";
    }                      
            
    def clearFilter() {
        controlList.clear();
        if(handler) handler(controlList);
        return "_close";
    }
            
    def stringOperators = [
        [caption: "equals", key:"="],
        [caption: "like", key:"LIKE"],
    ];
            
    def numberOperators = [
        [caption: "greater than", key:">"],
        [caption: "less than", key:"<"],
        [caption: "greater than or equal to", key:">=" ],
        [caption: "less than or equal to", key:"<="],
        [caption: "equal to", key:"="],
        [caption: "between", key:"BETWEEN"],
    ];
            
    def lookupOperators = [
        [caption: "is any of the ff.", key: "IN"],
        [caption: "not in any of the ff.", key:"NOT IN"],
    ];
            
    def dateOperators = [
        [caption: "equals", key: "="],
        [caption: "on or before", key: "<="],
        [caption: "before", key:"<"],
        [caption: "on or after", key:">="],
        [caption: "after", key:">"],
        [caption: "between", key:"BETWEEN"],
    ];
            
    def booleanOperators = [
        [caption: "is true", key: "=true"],
        [caption: "is false", key: "=false"],
    ];
            
    void addField() {
        String h = "criteria:item";
        def m = [type:'subform', handler:h, showCaption:false ];
        m.entry = [index:getFieldIndex()+1];
        m.properties = [entry: m.entry];
        controlList << m;  
        if(binding!=null) binding.refresh();                  
    }
            
    int getFieldIndex() {
        return controlList.size();
    }
            
    void removeField(def entry) {
        def z = controlList.find{ it.entry == entry };
        controlList.remove(z);
        //recalc the index
        int fldIndex = 0;
        for( o in controlList ) {
            fldIndex++;
            o.entry.index = fldIndex;
        }
        if(binding!=null) binding.refresh();    
    }
            
}