package com.rameses.filemgmt.models;

import com.rameses.seti2.models.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
public class FileListModel extends CrudListModel {
    
    def create() {
        def h = { o->
            def k = Inv.lookupOpener("sys_file:open", [entity: o] );
            binding.fireNavigation(k);
        };
        return Inv.lookupOpener("sys_file:new", [handler:  h] );
    }
    
}