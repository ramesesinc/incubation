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
import java.text.*;

public final class FormControlUtil  {

    /**
     * fld is the schema field. Entity is the object
     */
    public static def createControl( def fld, def entity ) {
        return createControl(fld, entity, null);
    };
        
    public static def createControl( def fld, def entity, String contextName ) {
        String cname = (contextName==null)?"entity":contextName;
        
        if( fld.primary && fld.visible.toString() != 'true' ) return null;
        if ( fld.visible.toString() == 'false' ) return null; 
        def i = [
            caption: (!fld.caption)?fld.name:fld.caption, 
            name:cname+'.'+fld.name,
        ];
        
        if(fld.category) i.categoryid = fld.category;
        if(fld.width) i.width = fld.width.toString().toInteger();
        
        def dtype = fld.type;
        if(!dtype) dtype = fld.datatype;
        if( fld.ui ) dtype = fld.ui; 
        if(!dtype) dtype = 'text';
        i.type = dtype;
        if (fld.required) i.required = fld.required; 
        
        boolean enabled = true;
        if(fld.editable!=null && fld.editable.toBoolean() == false ) {
            enabled = false;
        }
        else if(fld.updatable!=null && (fld.updatable=="false" || fld.updatable==false)) {
            enabled = false;
        }
        
        if(!enabled) {
            i.type = "label";
            i.expression = "#{"+i.name+"}";
            //right align decimal
            if(fld.type == 'decimal') {
                i.horizontalAlignment = javax.swing.SwingConstants.RIGHT;
            }
            else if( fld.type == 'integer') {
                i.horizontalAlignment = javax.swing.SwingConstants.CENTER;
            }
            //center align integer
        }
        else if( fld.lov ) {
            i.type = "combo";
            i.dynamic = true;
            i.items = "lov."+fld.lov;
        };
        else if( fld.ref ) {
            i.type = "lookup";
            i.handler = fld.ref + ":lookup";
            i.expression = fld.expression;
        };
        
        //copy other properties make sure not to override them. 
        fld.each {k,v->
            if(!i.containsKey(k)) {
                i.put(k, v);
            }
        }
        
        return i;
    }
    
    
}
        