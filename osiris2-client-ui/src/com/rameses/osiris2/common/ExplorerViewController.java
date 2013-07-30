/*
 * ExplorerViewController.java
 *
 * Created on July 28, 2013, 10:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Caller;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ExplorerViewController extends ListController 
{
    @Caller
    private Object caller; 
    
    private List formActions;

    public Opener getQueryForm() { return null; }     
    public String getEntityName() { return null; } 
    public Object getCaller() { return caller; }
    
    public String getServiceName() {
        ExplorerListViewModel handler = getTreeHandler();
        String svcname = (handler==null? null: handler.getServiceName()); 
        if (svcname != null) return svcname;
        
        throw new NullPointerException("Please specify serviceName"); 
    }
    
    public Node getSelectedNode() { 
        ExplorerListViewModel handler = getTreeHandler();
        return (handler==null? null: handler.getSelectedNode()); 
    }
    
    public Object getSelectedNodeItem() {
        ExplorerListViewModel handler = getTreeHandler();
        return (handler==null? null: handler.getSelectedNodeItem()); 
    } 
    
    public String getTitle() {
        Node selNode = getSelectedNode(); 
        return (selNode == null? null: selNode.getCaption()); 
    } 
    
    public Column[] getColumns() {
        List<Map> list = getColumnList();
        if (list == null || list.isEmpty()) 
            return getDefaultColumns();
        else 
            return super.getColumns(); 
    }   
    
    public List<Map> getColumnList() {
        Map params = new HashMap();
        String stag = getTag();
        if (stag != null) params.put("_tag", stag);
        
        Object item = getSelectedNodeItem();
        if (item instanceof Map) params.putAll((Map) item); 
        
        return getService().getColumns(params);
    }
    
    private Column[] getDefaultColumns() {
        return new Column[]{ 
            new Column("caption", "Folder")
        };
    }

    public List fetchList(Map params) {
        String stag = getTag();
        if (stag != null) params.put("_tag", stag);
        
        Object item = getSelectedNodeItem();
        if (item instanceof Map) params.putAll((Map) item); 
        
        return getService().getList(params); 
    }
    
    protected ListService getService()
    {
        ExplorerListViewModel handler = getTreeHandler();
        ExplorerListViewService svc = (handler==null? null: handler.getService());
        if (svc != null) return svc; 
        
        return super.getService(); 
    }     
    
    // <editor-fold defaultstate="collapsed" desc=" Action Methods ">        
    
    public Opener create() throws Exception 
    {
        String type = getNodeType();
        if (type == null || type.length() == 0) type = getDefaultNodeType();

        Map params = new HashMap();
        params.put("treeHandler", getTreeHandler());
        params.put("selectedNode", getSelectedNode()); 
        params.put("selectedNodeItem", getSelectedNodeItem()); 
        Opener opener = InvokerUtil.lookupOpener("explorer-"+type+":create", params); 
        resolveTargetFromOpener(opener);
        return opener;
    }
    
    public Object open() throws Exception 
    {
        String type = getNodeType();
        if (type == null || type.length() == 0) type = getDefaultNodeType();
        
        Map params = new HashMap();
        params.put("treeHandler", getTreeHandler());
        params.put("selectedNode", getSelectedNode()); 
        params.put("selectedNodeItem", getSelectedNodeItem());         
        Opener opener = InvokerUtil.lookupOpener("explorer-"+type+":open", params);
        resolveTargetFromOpener(opener);
        return opener;        
    }  
    
    private void resolveTargetFromOpener(Opener opener) {
        if (opener == null) return;
        
        String target = opener.getTarget()+"";
        if (!target.matches("window|popup|process|_window|_popup|_process")) {
            opener.setTarget("popup"); 
        }
    }
    
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" Form and Navigation Actions ">  

    public List getFormActions() {
        if (formActions == null) {
            formActions = new ArrayList();
            formActions.add(createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', null, true));       

            Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null}", true);
            a.getProperties().put("depends", "selectedEntity");
            formActions.add(a); 

            ExplorerListViewModel handler = getTreeHandler();
            if (handler != null) handler.getNodeActions(formActions);
        } 
        return formActions; 
    }

    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ExplorerListViewModel implementation "> 
    
    private ExplorerListViewModel treeHandler;
    
    public ExplorerListViewModel getTreeHandler() { 
        if (treeHandler != null) return treeHandler; 
        
        Object o = getCaller();
        if (o instanceof ExplorerListViewModel) 
            return (ExplorerListViewModel) o;
        else 
            return null; 
    } 
    
    public void setTreeHandler(ExplorerListViewModel treeHandler) {
        this.treeHandler = treeHandler; 
    }
    
    public String getDefaultNodeType() {
        ExplorerListViewModel handler = getTreeHandler();
        return (handler == null? null: handler.getType()); 
    }
    
    public String getNodeType() {
        Node node = getSelectedNode();
        return (node == null? null: node.getPropertyString("type")); 
    }    
        
    // </editor-fold> 
}
