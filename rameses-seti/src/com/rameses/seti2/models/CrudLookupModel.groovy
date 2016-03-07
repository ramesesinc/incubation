package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
        
public class CrudLookupModel extends CrudListModel implements SimpleLookupDataSource {

    def onselect;
    String searchText;
    LookupSelector selector;
    
    void setSelector(LookupSelector s) {
        this.selector = s;
    }
    
    def doOk() {
        //if(!onselect) throw new Exception("Please specify an onselect");
        if(!selectedItem) throw new Exception("Please select an entity");
        if(onselect) onselect( selectedItem );
        else if(selector) selector.select( selectedItem );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}    