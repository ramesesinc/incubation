/*
 * DefaultLookupCodeBean.java
 *
 * Created on April 29, 2013, 4:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.lookup;

import com.rameses.rcp.common.AbstractListModel;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.LookupFieldModel;
import com.rameses.rcp.common.LookupModel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class DefaultLookupCodeBean extends LookupModel   
{
    private LookupFieldModel fieldModel;
    private Object selectedEntity;
    
    public DefaultLookupCodeBean(LookupFieldModel fieldModel) {
        this.fieldModel = fieldModel;
    }

    public final AbstractListModel getListHandler() { return this; } 
    
    public Object getSelectedEntity() { return this.selectedEntity; } 
    public void setSelectedEntity(Object selectedEntity) {  
        this.selectedEntity = selectedEntity; 
    }
    
    public List fetchList(Map params) {  
        return fieldModel.fetchList(params);  
    } 

    public Column[] getColumns() {
        return fieldModel.getColumns(); 
    }
}
