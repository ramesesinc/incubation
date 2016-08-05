package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
        
public class DynamicCrudFormModel extends CrudFormModel {
    
    def formControls = [];
    int _captionWidth;
    
    void afterInit() {
        if( workunit?.info?.workunit_properties?.captionWidth ) {
            _captionWidth = workunit?.info?.workunit_properties?.captionWidth.toString().toInteger();
        }
        buildFormInfos();
    }
    
    def formPanel = [
        updateBean: {name,value,item->
            item.bean.value = value;
        },
        getControlList: {
            return formControls;
        }
    ] as FormPanelModel;

    public void initControl( def o ) {
        //do nothing
    }
    
    
    def buildFormInfos() {
        formControls.clear();
        def infos = schema.fields;
        for( x in infos ) {
            def i = FormControlUtil.createControl( x, entity );
            if(i==null) continue;
            if(_captionWidth!=null) {
                i.captionWidth = _captionWidth;
            }
            initControl(i);
            formControls << i;
        }
    }
        
}