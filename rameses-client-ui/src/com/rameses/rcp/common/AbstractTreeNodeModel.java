package com.rameses.rcp.common;

import java.util.List;
import java.util.Map;

public abstract class AbstractTreeNodeModel 
{
    private static final long serialVersionUID = 1L;
    private TreeNodeModel.Provider provider;
    private Node selectedNode;
    
    public abstract Node[] fetchNodes(Node node);
    
    public void setProvider(TreeNodeModel.Provider provider) {
        this.provider = provider; 
    } 
    
    public boolean isRootVisible() { return true; } 
    
    public Node getRootNode() {
        return new Node("root", "All");
    }
    
    public Node getSelectedNode() 
    {
        if (provider == null) 
            throw new NullPointerException("No provider implementation found"); 
        
        return provider.getSelectedNode(); 
    } 

    public Object openLeaf(Node node) { return null; }    
    public Object openFolder(Node node) { return null; }
    
    public Object openSelected() 
    {
        if ( selectedNode == null ) return null;
        
        if ( selectedNode.isLeaf() )
            return openLeaf( selectedNode );
        
        return openFolder( selectedNode );
    }
       
    public Object onChangeNode(Node node) { 
        return null; 
    } 
    
    public final Node findNode(NodeFilter filter) 
    {
        if (provider == null) 
            throw new NullPointerException("No provider implementation found"); 
        
        return provider.findNode(filter);
    }
    
    public final List<Node> findNodes(NodeFilter filter) 
    {
        if (provider == null) 
            throw new NullPointerException("No provider implementation found"); 
        
        return provider.findNodes(filter);
    }
    
    
    
    public static interface Provider 
    {
        Node getSelectedNode(); 
        
        Node findNode(NodeFilter filter);        
        List<Node> findNodes(NodeFilter filter);
    }
    
}
