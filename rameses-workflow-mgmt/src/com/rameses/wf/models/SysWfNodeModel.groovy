package com.rameses.wf.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;
import com.rameses.seti2.models.*;

public class SysWfNodeModel extends CrudFormModel {

    def selectedItem;
    
    public void afterAddItem(String name, def item ) {
        item.processname = entity.processname;
        item.parentid = entity.name;
    }

    def transitionList = [
        fetchList: { o->
            def m = [_schemaname:'sys_wf_transition'];
            m.findBy = [ processname:entity.processname, parentid:entity.name];
            m.orderBy = "idx";
            return queryService.getList( m );
        }
    ] as BasicListModel;

    void addTransition() {
        def p = [parentid:entity.name, processname: entity.processname];
        Modal.show( "sys_wf_transition:create", [defaultData: p ]);
        transitionList.reload();
    }
    
    void editTransition() {
        if(!selectedItem) throw new Exception("Please select a transition first");
        Modal.show( "sys_wf_transition:open", [entity: selectedItem ]);
        transitionList.reload();
    }

    def removeTransition() {
        if(!selectedItem) throw new Exception("Please select a transition first");
        if(!MsgBox.confirm("You are about to remove this entry. Proceed?")) return;
        selectedItem._schemaname = 'sys_wf_transition';
        persistenceService.removeEntity(selectedItem);
        transitionList.reload();
    }

}