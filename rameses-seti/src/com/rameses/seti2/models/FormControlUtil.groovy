package com.rameses.seti2.models;
 
import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;

public final class FormControlUtil  {

    /**
     * fld is the schema field. Entity is the object
     */
    public static def createControl( def fld, def entity ) {
        return createControl(fld, entity, null);
    };
        
    public static def createControl( def fld, def entity, String contextName ) {
        String cname = (contextName==null)?"entity":contextName;
        
        if( fld.primary && !fld.visible ) return null;
        def i = [
            caption: (!fld.caption)?fld.name:fld.caption, 
            name:cname+'.'+fld.name,
        ];
        def dtype = fld.type;
        if(!dtype) dtype = fld.datatype;
        if(!dtype) dtype = 'string';
        i.type = dtype;
        if(!fld.updatable || fld.updatable=="false") {
            i.type = "label";
            i.expression = "#{"+i.name+"}"
        }
        else if( fld.lov ) {
            i.type = "combo";
            i.dynamic = true;
            i.items = "listTypes."+fld.extname;
        };
        else if( fld.ref ) {
            i.type = "lookup";
            i.handler = fld.ref + ":lookup";
            i.expression = "#{"+cname+"."+fld.name+"}";
            if( fld.name.indexOf(".") > 0  ) {
                i.name = cname+"."+fld.name.substring(0, fld.name.lastIndexOf("."));
            }
        }
        else if(i.type == "boolean") {
            i.type = "subform";
            i.handler = "business_application:yesno";
            i.properties = [item:fld];
            i.required = fld.required;
            i.editable = (!fld.editable)?true:fld.editable;
        }
        else if(i.type == "string_array") {
            i.type = "combo";
            i.preferredSize = '150,20';
            i.itemsObject = fld.attribute.arrayvalues;
            i.required = fld.required;
            i.editable = (!fld.editable)?true:fld.editable;
        }
        else if( i.type == 'decimal' ) {
            i.preferredSize = '150,20';
            i.required = fld.required;
            i.editable = (!fld.editable)?true:fld.editable;
        }
        else if( i.type == "string" ) {
            i.type = "text";
            i.required = fld.required;
            i.editable = (!fld.editable)?true:fld.editable;
        }
        else if( i.type == "info") {
            i.type = "subform";
            i.properties = [item:i.bean];
            i.showCaption = false;
        }
        
        //COMBO BOXES
        return i;
    }
    
    
}
        