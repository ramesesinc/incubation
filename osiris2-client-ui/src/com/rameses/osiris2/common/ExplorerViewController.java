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
    private ExplorerListViewModel treeHandler;
    private Node selectedNode;
    private List formActions;
    
    public String getServiceName() {
        ExplorerListViewModel handler = getTreeHandler();
        String svcname = (handler==null? null: handler.getServiceName()); 
        if (svcname != null) return svcname;
        
        throw new NullPointerException("Please specify serviceName"); 
    }
    public String getEntityName() { return null; } 
    
    public ExplorerListViewModel getTreeHandler() { return treeHandler; } 
    public void setTreeHandler(ExplorerListViewModel treeHandler) {
        this.treeHandler = treeHandler; 
    }
    
    public Opener getQueryForm() { return null; }    
    
    public Node getSelectedNode() { return selectedNode; }
    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode; 
    }
    
    public Object getSelectedNodeItem() {
        Node selNode = getSelectedNode(); 
        return (selNode == null? null: selNode.getItem()); 
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
        Node node = getSelectedNode();
        String type = (String) (node == null? null: node.getProperties().get("type")); 
        return InvokerUtil.lookupOpener("explorer-"+type+":create", new HashMap());
    }
    
    public Object open() throws Exception 
    {
        Node node = getSelectedNode();
        String type = (String) (node == null? null: node.getProperties().get("type")); 
        return InvokerUtil.lookupOpener("explorer-"+type+":open", new HashMap());
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
            
            List extActions = lookupActions("formActions");
            formActions.addAll(extActions);
            extActions.clear();  
            
            Node node = getSelectedNode();
            String type = (String) (node==null? null: node.getProperties().get("type"));
            List list = InvokerUtil.lookupActions(type+":formActions"); 
            if (list != null) {
                formActions.addAll(list); 
//                for (Object obj: list) {
//                    Opener o = (Opener)obj;
//                    formActions.add(new ActionOpener(o)); 
//                }
            } 
        } 
        return formActions; 
    }

    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc=" ActionOpener (class) "> 
    
    private class ActionOpener extends Action 
    {
        private Opener opener;
        
        ActionOpener(Opener opener) {
            this.opener = opener;
            setName(opener.getAction()); 
            setCaption(opener.getCaption()); 
        }
        
        public Object execute() { 
            String target = opener.getTarget()+"";
            if (!target.matches("window|popup|process")) {
                opener.setTarget("popup"); 
            }
            return opener; 
        }
    }
    
    // </editor-fold>
}
