package com.rameses.rulemgmt.constraint;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleConstraintLOVHandler extends RuleConstraintListValueHandler {

    @PropertyChangeListener
    def listener = [
        "constraint.usevar": { o->
            if(o == 1 ) {
                constraint.listvalue = null;
                buildVarList();
            }
            else {
                constraint.var = null;
            }
        }
    ]

    def showLookup() {
        return InvokerUtil.lookupOpener( "lov:lookup", [
            key: field.lovname,
            multiSelect: true,
            multiSelectHandler: { o->
                return (constraint.listvalue?.find{ it == o.key }!=null);
            },
            afterSelectItem: { o-> 
                if ( !o ) return; 
                
                def fk = o.data?.key; 
                if ( constraint.listvalue == null ) {
                    constraint.listvalue = []; 
                } 
                
                def ofk = constraint.listvalue.find{ it == fk }
                if ( o.selected==true ) {
                    if ( ofk == null ) {
                        constraint.listvalue << fk; 
                    } 
                } else if ( ofk ) { 
                    constraint.listvalue.remove( fk );  
                } 
            }, 
            onselect: { o->
                constraint.listvalue = [];
                o.each {
                    constraint.listvalue << it ;
                }
                binding.refresh("selection");
            }
        ]);
    }
    
    String getSelection() {
        if( constraint.listvalue != null ) {
            return constraint.listvalue.join(",");
        }
        return "";
    }

}