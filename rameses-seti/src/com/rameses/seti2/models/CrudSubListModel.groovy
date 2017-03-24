package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.*;
        
/**
* used for lists under sections
**/
public class CrudSubListModel extends CrudListModel {
        
    def getMasterEntity() {
        return caller.entity;
    }

    void beforeQuery( def qry ) {
        def listFilter = getListFilter();
        if(listFilter) {
            qry.findBy = getListFilter();
        }
    }
    
    public def getListFilter() {
        String parentid = invoker?.properties?.parentid;
        if(parentid) {
            def m = [(parentid): getMasterEntity().objid];
            return m;
        }
        return null;
    }
    
}
