package com.rameses.enterprise.treasury.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.seti2.models.*;

class AFReceiptNewModel extends CrudPageFlowModel {
    
    boolean withrequest = false;
    def afrequest;
    
    def txnTypes;
    def reqtype = null;
    def selectedItem;
    
    void create() {
        entity = [:];
        entity.items = [];
        afrequest = null;
        itemListHandler.reload();
    }
    
    void initNew() {
        if(afrequest!=null) {
            def req = persistenceService.read( [_schemaname:'afrequest', objid: afrequest.objid ] );
            entity.request = [objid:req.objid , reqno: req.reqno];
            entity.issueto = req.requester;
            req.items.each {
                def m = [:];
                m.item = it.item;
                m.unit = it.unit;
                m.qtyrequested = it.qty;
                m.qtyissued = 0;  
                m.txntype = 'COLLECTION';
                entity.items << m;
            }
            txnTypes = ["PURCHASE"];
        }
        else {
            txnTypes = ["PURCHASE", "BEGIN_BALANCE"]
        }
    }

    //the only lookup here is for the request because the other request os for purchase (af receipt)
    public def getLookupRequest() {
        return Inv.lookupOpener( "afrequest_purchase:lookup", [:] );
    }
    
    public def getUnitList() {
        return selectedItem.item.units;
    }
    
    def itemListHandler = [
        fetchList : { o->
            return entity.items;
        },
        onAddItem: { o->
            entity.items << o;
        },
        onRemoveItem: { o->
            entity.items.remove(o);
        },
        onColumnUpdate: { o,colName->
            if(colName=="qtyreceived" && afrequest!=null ) {
                if( o.qtyreceived > o.qtyrequested ) throw new Exception("Qty received must be less than qty requested");
            }
        }
    ] as EditorListModel;
    
    void saveDraft() {
       saveCreate(); 
       def op = Inv.lookupOpener( "afreceipt:open", [entity: [objid: entity.objid ]] );
       Inv.invoke( op );
       MsgBox.alert('Draft saved');
    }
    
}    