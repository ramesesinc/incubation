/*
 * ExplorerViewModel.java
 *
 * Created on July 26, 2013, 2:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Node;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public interface ExplorerListViewModel {

    AbstractListDataProvider getListHandler();
    void setListHandler(AbstractListDataProvider listHandler); 
    
    Node getSelectedNode(); 
    
    Map createParam(Node node);    
    
    List<Map> getColumnList(Map params);     
    
    List getList(Map params); 
    
    List<Action> lookupActions(String invokerType);
}
