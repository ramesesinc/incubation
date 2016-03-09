package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;

public class MdiFormModel  extends CrudFormModel {
    
    boolean isAllowEdit() {return false; }    
    boolean isSaveAllowed() { return false; }
    boolean isUndoAllowed() { return false; }
    boolean isCancelEditAllowed() { return false; }
   
}