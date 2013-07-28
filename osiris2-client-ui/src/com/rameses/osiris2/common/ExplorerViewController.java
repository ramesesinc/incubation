package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TreeNodeModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExplorerViewController 
{     
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    protected com.rameses.rcp.framework.Binding binding;

    private String serviceName;    
    private Node selectedNode;
    private Object selectedItem;
    private Opener folderOpener;
    private Opener leafOpener;
    
    private String DEFAULT_FOLDER_OPENER = "explorer-folder:open";
    
           
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
    
    public String getTitle() { 
        return (invoker == null? null: invoker.getCaption());
    }
    
    public Node getSelectedNode() { return selectedNode; }
    public void setSelectedNode(Node selectedNode) { 
        this.selectedNode = selectedNode;
    }
        
    public String getServiceName() { return null; } 
    public String getFolderOpener() { return DEFAULT_FOLDER_OPENER; } 

    public boolean isRootVisible() { return false; }
    public String getIcon() { return "Tree.closedIcon"; } 
    
    public Object getNodeModel() { 
        return getTreeNodeModel(); 
    }
    
    public List<Map> getNodeList(Map params) {
        return getService().getNodeList(params);
    }
    
    public Opener getOpener() { 
        if (this.selectedNode == null) return null; 
        
        if (this.selectedNode.isLeaf())
            return leafOpener;
        else 
            return folderOpener;         
    } 
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" events ">
    
    private ExplorerViewModelImpl treeHandler = new ExplorerViewModelImpl(); 
    
    public Object openFolder(Node node) {
        if (node == null) return null;
        
        if (folderOpener == null) { 
            Map params = new HashMap(); 
            params.put("treeHandler", treeHandler); 
            folderOpener = InvokerUtil.lookupOpener(getFolderOpener(), params); 
        }
        
        if (!node.hasItems()) node.reloadItems(); 
        
        if (treeHandler.getListHandler() != null) 
            treeHandler.getListHandler().refresh(true);  
        
        if (binding != null) binding.refresh("subform");         

        return null; 
    } 
    
    public Object openLeaf(Node node) {
        String opener = (String) node.getProperties().get("opener");
        if (opener == null) 
            throw new NullPointerException("Please provide an opener for this node"); 
        
        Map params = new HashMap(); 
        params.put("treeHandler", treeHandler); 
        leafOpener = InvokerUtil.lookupOpener(opener, params);
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
        ExplorerViewController root = ExplorerViewController.this;
        private Opener opener;
        
        public boolean isRootVisible() { return root.isRootVisible(); } 
        
        public String getIcon() { return root.getIcon(); }
        
        public List<Map> getNodeList(Node node) {
            Map params = new HashMap(); 
            params.put("root", (node.getParent() == null));
            params.put("caption", node.getCaption()); 
            params.put("item", node.getItem()); 
            return root.getNodeList(params);
        }
        
        public Object openFolder(Node node) {
            return root.openFolder(node); 
        }

        public Object openLeaf(Node node) {
            return root.openLeaf(node);
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ExplorerViewService helper and utilities ">
    
    private ExplorerViewService service;
    
    protected ExplorerViewService getService()
    {
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new RuntimeException("No service name specified"); 
            
        if (service == null) {
            service = (ExplorerViewService) InvokerProxy.getInstance().create(name, ExplorerViewService.class);
        }
        return service;
    } 
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ExplorerViewModelImpl (class) ">
    
    private class ExplorerViewModelImpl implements ExplorerViewModel 
    {
        ExplorerViewController root = ExplorerViewController.this; 
        private AbstractListDataProvider listHandler;
                
        public AbstractListDataProvider getListHandler() { return listHandler; }
        public void setListHandler(AbstractListDataProvider listHandler) {
            this.listHandler = listHandler; 
        }
        
        public Node getSelectedNode() {
            return root.getTreeNodeModel().getSelectedNode(); 
        } 
        
        public ExplorerViewService getService() { 
            return root.getService();
        } 
    }
    
    // </editor-fold>
}
