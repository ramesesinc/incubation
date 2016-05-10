package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
        
public class CrudLookupModel extends CrudListModel implements SimpleLookupDataSource {

    def onselect;
    LookupSelector selector;
    
    boolean _first_search = false;
    public void setSearchText(String s) {
        super.setSearchText(s);
        if(!_first_search ) {
            search();
            _first_search = true;
        }
    }
    
    void setSelector(LookupSelector s) {
        this.selector = s;
    }
    
    def doOk() { 
        def selobj = listHandler.getSelectedValue(); 
        if ( !selobj ) throw new Exception("Please select an item"); 
        
        if ( onselect ) onselect( selobj );
        else if ( selector ) selector.select( selobj );
        return "_close"; 
    } 
    
    def doCancel() {
        return "_close";
    }
    
}    