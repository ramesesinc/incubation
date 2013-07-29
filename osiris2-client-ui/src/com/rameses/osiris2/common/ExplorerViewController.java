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
public class ExplorerViewController extends BasicListController
{
    private List formActions;
    private ExplorerListViewModel treeHandler;
    
    public void init() { 
        ExplorerListViewModel handler = getTreeHandler();
        if (handler != null) {
            handler.setListHandler(this); 
        }
    }

    public ExplorerListViewModel getTreeHandler() { return treeHandler; } 
    public void setTreeHandler(ExplorerListViewModel treeHandler) {
        this.treeHandler = treeHandler; 
    }
    
    public Node getSelectedNode() {
        ExplorerListViewModel handler = getTreeHandler();
        return (handler == null? null: handler.getSelectedNode()); 
    }
    
    public Object getSelectedNodeItem() {
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null; 
        
        Node node = handler.getSelectedNode();
        return (node == null? null: node.getItem()); 
    }    
    
    public String getTitle() {
        ExplorerListViewModel handler = getTreeHandler();
        Node node = (Node) handler.getSelectedNode();
        return (node == null? null: node.getCaption()); 
    }    
    
    public Column[] getColumns() {
        List<Map> list = getColumnList();
        if (list == null || list.isEmpty()) 
            return getDefaultColumns();
        
        Column[] columns = new Column[list.size()];
        for (int i=0; i<list.size(); i++) {
            columns[i] = new Column(list.get(i)); 
        }
        return columns;
    }   
    
    public List<Map> getColumnList() {
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null;
        
        Node node = handler.getSelectedNode();
        return handler.getColumnList(handler.createParam(node));
    }
    
    private Column[] getDefaultColumns() {
        return new Column[]{ 
            new Column("caption", "Folder")
        };
    }

    public List fetchList(Map params) {
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null;
        
        Node node = handler.getSelectedNode();
        return handler.getList(handler.createParam(node));
    }

    public Object open() throws Exception { 
        Object item = getSelectedEntity(); 
        if (item == null) return null;
        
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null;
        
        Node node = handler.getSelectedNode(); 
        String type = (String) (node == null? null: node.getProperties().get("type")); 
        Map params = new HashMap();
        params.put("entity", item);
        params.put("node", node); 
        return InvokerUtil.lookupOpener("explorer-"+type+":open", params); 
    }
        
    public Object create() {
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null;
        
        Node node = handler.getSelectedNode(); 
        String type = (String) (node == null? null: node.getProperties().get("type")); 
        Map params = new HashMap();
        params.put("node", node); 
        return InvokerUtil.lookupOpener("explorer-"+type+":create", params);        
    }    
        
    // <editor-fold defaultstate="collapsed" desc=" Form and Navigation Actions ">  

    public List getFormActions() {
        if (formActions == null) {
            formActions = new ArrayList();
            formActions.add(createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', null, true));       

            Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null}", true);
            a.getProperties().put("depends", "selectedEntity");
            formActions.add(a); 
            
            ExplorerListViewModel handler = getTreeHandler();
            if (handler != null) { 
                List exts = handler.lookupActions("formActions"); 
                if (exts != null) formActions.addAll(exts); 
                
                Node node = handler.getSelectedNode();
                String type = (String) (node==null? null: node.getProperties().get("type"));
                List list = InvokerUtil.lookupOpeners(type+":formActions"); 
                if (list != null) {
                    for (Object obj: list) {
                        Opener o = (Opener)obj;
                        formActions.add(new ActionOpener(o)); 
                    }
                }
            }
        } 
        return formActions; 
    }
    
    private Action createAction(String name, String caption, String icon, String shortcut, char mnemonic, String visibleWhen, boolean immediate) 
    {
        Action a = new Action(name, caption, icon, mnemonic);
        if (visibleWhen != null) a.setVisibleWhen(visibleWhen); 
        if (shortcut != null) a.getProperties().put("shortcut", shortcut);    
        
        a.setImmediate( immediate );
        a.setShowCaption(true); 
        return a;
    }    
    
    // </editor-fold>    

    
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
}
