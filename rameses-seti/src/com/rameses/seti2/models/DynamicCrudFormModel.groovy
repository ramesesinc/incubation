package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
        
public class DynamicCrudFormModel extends CrudFormModel {
    
    def formControls = [];

    void init() {
        super.init();
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
            if( x.primary && !x.visible ) continue;
            def i = [
                caption:x.caption, 
                name:'entity.'+x.name,
                value: entity.get( x.name )
            ];
            //fix the datatype
            if( !x.type ) {
                i.type = "text";
            }
            else {
                i.type = x.type;
            }
            if( i.type == 'linked' ) {
                
            }
            /*
            if(i.type == "boolean") {
                i.type = "subform";
                i.handler = "business_application:yesno";
                i.properties = [item:x];
            }
            else if(i.type == "string_array") {
                i.type = "combo";
                i.preferredSize = '150,20';
                i.itemsObject = x.attribute.arrayvalues;
            }
            else if( i.type == 'decimal' ) {
                i.preferredSize = '150,20';
            }
            else if( i.type == "string" ) {
                i.type = "text";
            }
            else if( i.type == "info") {
                i.type = "subform";
                i.properties = [item:i.bean];
                i.showCaption = false;
            }
            */
            i.required = x.required;
            i.editable = (!x.editable)?true:x.editable;
            formControls << i;
        }
     }
        
}