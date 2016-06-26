package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
        
public class DynamicCrudFormModel extends CrudFormModel {
    
    def formControls = [];

    void afterInit() {
        buildFormInfos();
    }
    
    def formPanel = [
        getCategory: { key->
           return "";
        },
        updateBean: {name,value,item->
            item.bean.value = value;
        },
        getControlList: {
            return formControls;
        }
    ] as FormPanelModel;

    
    /*
    def sortInfos(sinfos) {
        def list = sinfos.findAll{it.lob?.objid==null && it.attribute.category==null}?.sort{it.attribute.sortorder};
        def catGrp = sinfos.findAll{it.lob?.objid==null && it.attribute.category!=null};
        if(catGrp) {
            def grpList = catGrp.groupBy{ it.attribute.category };
            grpList.each { k,v->
                v.sort{ it.attribute.sortorder }.each{z->
                    list.add( z );
                }
            }
        }
        list = list + sinfos.findAll{ it.lob?.objid!=null }?.sort{ [it.lob.name, it.attribute.sortorder] }; 
        return list; 
    }
    */

    /*
    def findValue( info ) {
        if(info.lob?.objid!=null) {
            def filter = existingInfos.findAll{ it.lob?.objid!=null };
            def m = filter.find{ it.lob.objid==info.lob.objid && it.attribute.objid == info.attribute.objid };
            if(m) return m.value;
        }
        else {
            def filter = existingInfos.findAll{ it.lob?.objid==null };
            def m = filter.find{ it.attribute.objid == info.attribute.objid };
            if(m) return m.value;
        }
        return null;
     }
     */
    
     def buildFormInfos() {
        formControls.clear();
        def infos = schema.fields;
        //infos = sortInfos( infos );
        for( x in infos ) {
            def i = FormControlUtil.createControl( x, entity );
            if(i==null) continue;
            formControls << i;
        }
     }
        
}