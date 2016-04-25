package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
        
public class PopupAction extends Action { 
    
    def binding;
    def obj;
    
    def execute() {
        if( getName().startsWith("_")) { 
            binding.fireNavigation( getName() );  
            return getName(); 
            
        } else {
            return obj.invokeMethod(getName(), null);
        }
    }
    
}
