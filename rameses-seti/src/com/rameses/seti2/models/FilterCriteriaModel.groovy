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
        if(handler) handler.clear();
        addField();
        binding?.refresh();
        return "_close";
    }
            
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
        def z = controlList.last();
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