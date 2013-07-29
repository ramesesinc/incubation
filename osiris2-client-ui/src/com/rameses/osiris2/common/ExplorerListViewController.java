package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TreeNodeModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExplorerListViewController implements ExplorerListViewModel
{     
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    protected com.rameses.rcp.framework.Binding binding;

    private String name = "explorer";
    private String serviceName; 
    private Node selectedNode;
               
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
    
    public String getTitle() { 
        return (invoker == null? null: invoker.getCaption());
    }
   
    public String getName() { return name; } 

    public boolean isRootVisible() { return false; }
    
    public String getIcon() { return "Tree.closedIcon"; } 
    
    public Object getNodeModel() { 
        return getTreeNodeModel(); 
    }
    
    public List<Map> getNodes(Map params) {
        return getService().getNodes(params);
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" ExplorerListViewModel implementation ">  
    
    public String getServiceName() { return null; } 
    
    public Node getSelectedNode() { return selectedNode; }
    public void setSelectedNode(Node selectedNode) { 
        this.selectedNode = selectedNode;
    }
    
    public Object getSelectedNodeItem() {
        Node node = getSelectedNode(); 
        return (node == null? null: node.getItem()); 
    }
    
    private ExplorerListViewService service;    
    public ExplorerListViewService getService()
    {
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new RuntimeException("No service name specified"); 
            
        if (service == null) {
            service = (ExplorerListViewService) InvokerProxy.getInstance().create(name, ExplorerListViewService.class);
        }
        return service;
    } 
    
    public List<Action> lookupActions(String type) { 
        List<Action> actions = InvokerUtil.lookupActions(type, new InvokerFilter() {
            public boolean accept(com.rameses.osiris2.Invoker o) { 
                return o.getWorkunitid().equals(invoker.getWorkunitid()); 
            }
        }); 
        
        for (int i=0; i<actions.size(); i++) {
            Action newAction = actions.get(i).clone();
            actions.set(i, newAction);
        }
        return actions;  
    }  
    
    public List lookupOpeners(String type) { 
        return InvokerUtil.lookupOpeners(type, null, new InvokerFilter() {
            public boolean accept(com.rameses.osiris2.Invoker o) { 
                return o.getWorkunitid().equals(invoker.getWorkunitid()); 
            } 
        }); 
    }     
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" event handling ">
    
    private Opener openerObj;
    
    public Opener getOpenerObject() { return openerObj; } 
    
    public Object openFolder(Node node) {
        if (node == null) return null;
        
        Map params = new HashMap(); 
        params.put("treeHandler", this); 

        String invokerType = getName() + "-listview:open";
        openerObj = InvokerUtil.lookupOpener(invokerType, params); 
        if (binding != null) binding.refresh("subform");         

        return null; 
    } 
    
    public Object openLeaf(Node node) {
        if (node == null) return null;
        
        Map params = new HashMap(); 
        params.put("treeHandler", this); 

        String invokerType = getName() + "-listview:open";
        openerObj = InvokerUtil.lookupOpener(invokerType, params); 
        if (binding != null) binding.refresh("subform"); 

        return null;
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TreeNodeModel helper and utilities ">
    
    private TreeNodeModel nodeModel; 
    
    private final TreeNodeModel getTreeNodeModel() {
        if (nodeModel == null) {
            nodeModel = new TreeNodeModelImpl(); 
        }
        return nodeModel;
    }    
    
    private class TreeNodeModelImpl extends TreeNodeModel 
    {
        ExplorerListViewController root = ExplorerListViewController.this;
        
        public boolean isRootVisible() { return root.isRootVisible(); } 
        
        public String getIcon() { return root.getIcon(); }
        
        public List<Map> getNodeList(Node node) {
            Map params = new HashMap(); 
            Object item = node.getItem(); 
            if (item instanceof Map) 
                params.putAll((Map) item); 
            else 
                params.put("item", node.getItem()); 
            
            params.put("root", (node.getParent() == null));
            params.put("caption", node.getCaption()); 
            return root.getNodes(params);
        }
        
        public Object openFolder(Node node) {
            return root.openFolder(node); 
        }

        public Object openLeaf(Node node) {
            return root.openLeaf(node);
        }
    }
    
    // </editor-fold>    
}
