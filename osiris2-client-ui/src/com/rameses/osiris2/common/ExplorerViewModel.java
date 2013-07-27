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
import com.rameses.rcp.common.Node;
import java.util.List;

/**
 *
 * @author wflores
 */
public interface ExplorerViewModel {

    AbstractListDataProvider getListHandler();
    void setListHandler(AbstractListDataProvider listHandler); 
    
    Node getSelectedNode();    
}
