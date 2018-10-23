package com.rameses.osiris2.report;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;


public class FilterCriteriaComponent extends ComponentBean  {
    
    @Binding
    def binding;
    
    def handler;
    def selectedField;
    def fieldList; 
    def criteriaList = []; 
    def controlList = [];
    
    def formControls = [
        getControlList: {
            return controlList;
        }
    ] as FormPanelModel;

    void init() {
        fieldList = handler.getFieldList();
        if(!fieldList) {
            throw new Exception("Please specify a fieldList")
        }
        controlList.clear();
        if(criteriaList) {
            controlList.addAll( criteriaList );
        }
        else {
            addField();
        }      
    }                
               
    def clearFilter() {
        controlList.clear();
        handler.clear();
    }
     
            
    void addField() {
        String h = "criteria:item";
        def m = [type:'subform', handler:h, showCaption:false ];
        m.entry = [index:getFieldIndex()+1];
        m.properties = [entry: m.entry, caller : this];
        controlList << m;  
        formControls.reload();
        handler.add( m.entry );
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
        handler.removeItem();
        if(binding!=null) binding.refresh();    
    }
    
    
    
}   
