/*
 * ExplorerViewerController.java
 *
 * Created on July 30, 2013, 6:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TreeNodeModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ExplorerViewerController {
    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    protected com.rameses.rcp.framework.Binding binding;
    
    private Opener openerObject;
    private ExplorerViewerService service;
    private String scheme = "explorer";
    private String serviceName;     
    private Node selectedNode;
    private Map<String,Opener> openers = new HashMap(); 
    
    public ExplorerViewerController() {
    }
    
    public Object getNodeModel() { 
        return getTreeNodeModel(); 
    }
    
    public Node getSelectedNode() { return selectedNode; } 
    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode; 
    }
    
    public String getServiceName() { return serviceName; }
    public String getScheme() { return scheme; } 
    
    public ExplorerViewerService getService()
    {
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new RuntimeException("No service name specified"); 
            
        if (service == null) {
            service = (ExplorerViewerService) InvokerProxy.getInstance().create(name, ExplorerViewerService.class);
        }
        return service;
    }     
    
    public Opener getOpenerObject() {
        return openerObject; 
    }
    
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
        ExplorerViewerController root = ExplorerViewerController.this;
        
        public boolean isRootVisible() { return false; } 
        
        public String getIcon() { return "Tree.closedIcon"; }
        
        public List<Map> getNodeList(Node node) {
            Map params = new HashMap(); 
            Object item = node.getItem(); 
            if (item instanceof Map) 
                params.putAll((Map) item); 
            else 
                params.put("item", node.getItem()); 
            
            params.put("root", (node.getParent() == null));
            params.put("caption", node.getCaption());             
            List<Map> nodes = getService().getNodes(params); 
            if (node.getParent() == null) {
                Map search = new HashMap();
                search.put("name", "search");
                search.put("caption", "Search"); 
                search.put("type", "search"); 
                if (nodes.isEmpty())
                    nodes.add(search);
                else 
                    nodes.add(0, search); 
            }
            return nodes; 
        }
        
        public Object openFolder(Node node) {
            if (node == null) return null;

            Opener opener = openers.get(node.getNodeType()); 
            if (opener == null) {
                String invokerType = root.getScheme() + ":" + node.getNodeType();
                try { 
                    opener = InvokerUtil.lookupOpener(invokerType, new HashMap());                     
                } catch(Throwable t) {
                    opener = InvokerUtil.lookupOpener("explorer-default-viewer", new HashMap());
                }
                openers.put(node.getNodeType(), opener);
            }
            
            ExplorerNodeViewer viewer = (ExplorerNodeViewer) opener.getHandle();
            viewer.setNode(node); 
            viewer.updateView();
            openerObject = opener;
            if (binding != null) binding.refresh("subform");         

            return null; 
        }

        public Object openLeaf(Node node) {
            if (node == null) return null;

            Opener opener = openers.get(node.getNodeType()); 
            if (opener == null) {
                String invokerType = root.getScheme() + ":" + node.getNodeType();
                try { 
                    opener = InvokerUtil.lookupOpener(invokerType, new HashMap());                     
                } catch(Throwable t) {
                    opener = InvokerUtil.lookupOpener("explorer-default-viewer", new HashMap());
                }
                openers.put(node.getNodeType(), opener);
            }
            
            ExplorerNodeViewer viewer = (ExplorerNodeViewer) opener.getHandle();
            viewer.setNode(node); 
            viewer.updateView();
            openerObject = opener;
            if (binding != null) binding.refresh("subform"); 
            
            return null; 
        }
    }
    
    // </editor-fold>        
}
