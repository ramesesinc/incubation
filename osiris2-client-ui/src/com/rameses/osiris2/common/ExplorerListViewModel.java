/*
 * ExplorerViewModel.java
 *
 * Created on July 26, 2013, 2:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Node;
import java.util.List;

/**
 *
 * @author wflores
 */
public interface ExplorerListViewModel {

    String getServiceName();
    
    Node getSelectedNode(); 
    
    Object getSelectedNodeItem(); 
    
    ExplorerListViewService getService(); 
    
    List<Action> getNodeActions(); 
    List<Action> getNodeActions(List<Action> actions); 
}
