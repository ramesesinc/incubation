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
import com.rameses.seti2.models.*;

/****
* This facility only extracts only a portion of the data. 
*/
public class ChangeInfoListModel extends CrudSubListModel {
   
    def onOpenHandler = { o,colName ->
        if(colName == "oldvalue") {
            Modal.show( "debug_subinfo:view", [data: o.oldvalue]);
        }
        else if( colName == "newvalue" ) {
            Modal.show( "debug_subinfo:view", [data: o.newvalue]);
        }
        else if( colName == "remarks" ) {
            MsgBox.alert(o.remarks);
        }        
    }
    
    
}
        