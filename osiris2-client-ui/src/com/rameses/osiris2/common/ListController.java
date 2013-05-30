package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.common.Opener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ListController extends BasicListController implements ListModelHandler 
{    
    
    private ListService service;
    private List formActions;
    private List navActions;
    private Map query = new HashMap(); 
        
    public abstract String getEntityName();
    public abstract String getServiceName();
   
    
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
            
    public Map getQuery() { return query; }
          
    public String getFormTarget() { return "popup"; }    
    
    public Opener getQueryForm() 
    {
        Opener o = new Opener();
        o.setOutcome("queryform");
        return o;
    }    
    
    // </editor-fold>    
 
    // <editor-fold defaultstate="collapsed" desc=" Options ">    
    
    public boolean isShowNavActions() { return true; } 
    public boolean isShowFormActions() { return true; } 
    public boolean isAllowCreate() { return true; } 
    public boolean isAllowOpen() { return true; } 
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Action Methods ">        
    
    public Opener create() throws Exception 
    {
        Map params = new HashMap();
        params.put("listModelHandler", this);
        
        Opener o = InvokerUtil.lookupOpener(getEntityName()+":create", params);
        o.setTarget(getFormTarget()); 
        return o;
    }
    
    public Object open() throws Exception 
    {
        Map params = new HashMap();
        params.put("entity", getSelectedEntity()); 
        params.put("listModelHandler", this);
        
        Opener o = InvokerUtil.lookupOpener(getEntityName()+":open", params);
        o.setTarget(getFormTarget()); 
        return o;
    }    
    
    public Object close() {
        return "_close"; 
    }
    
    public void reset() 
    {
        query.clear(); 
        search(); 
    }
    
    public void search() {
        load(); 
    }    
          
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Form and Navigation Actions ">  
    
    public List getNavActions() {
        if (navActions == null) 
        {
            if (!isShowNavActions()) return null;
            
            navActions = new ArrayList();
            navActions.add(createAction("moveFirstPage", "", "images/navbar/first.png", null, '\u0000', null, true)); 
            navActions.add(createAction("moveBackPage",  "", "images/navbar/previous.png", null, '\u0000', null, true)); 
            navActions.add(createAction("moveNextPage",  "", "images/navbar/next.png", null, '\u0000', null, true)); 
            navActions.add(createAction("moveLastPage",  "", "images/navbar/last.png", null, '\u0000', null, true)); 
        } 
        return navActions; 
    }
    
    public List getFormActions() {
        if (formActions == null) 
        {
            if (!isShowFormActions()) return null;
            
            formActions = new ArrayList();
            formActions.add(createAction("close", "Close", "images/toolbars/cancel.png", "ctrl C", 'c', null, true)); 
            
            if (isAllowCreate())
                formActions.add(createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', null, true));       
            
            if (isAllowOpen())
            {
                Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null}", true);
                a.getProperties().put("depends", "selectedEntity");
                formActions.add(a); 
            }
            
            formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true)); 
            
            List extActions = InvokerUtil.lookupActions("formActions", new InvokerFilter() {
                public boolean accept(com.rameses.osiris2.Invoker o) { 
                    return o.getWorkunitid().equals(invoker.getWorkunitid()); 
                }
            });
            formActions.addAll(extActions); 
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
    
    
    public final List fetchList(Map m) {
        return getService().getList(m); 
    }
    
    protected ListService getService()
    {
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new RuntimeException("No service name specified"); 
            
        if (service == null) {
            service = (ListService) InvokerProxy.getInstance().create(name, ListService.class);
        }
        return service;
    } 

    /*
     *  ListControllerHandler implementation
     */ 
    public void handleInsert(Object data) 
    {
        if (data == null) return;
        
        int idx = -1; 
        try { 
            idx = getListItems().indexOf(getSelectedItem()); 
        } catch(Exception ign) {;} 
        
        List dataList = getDataList();
        if (idx >= 0 && idx < dataList.size()) { 
            dataList.add(idx, data); 
        } 
        else {
            dataList.add(data); 
            idx = dataList.size()-1;
        }
        
        refresh(); 
        setSelectedItem(idx); 
    }
    
    public void handleUpdate(Object data) 
    {
        ListItem item = getSelectedItem(); 
        if (item == null) return;
        if (data != null) 
        {
            List dataList = getDataList();
            if (item.getIndex() >= 0 && item.getIndex() < dataList.size()) 
            {
                dataList.set(item.getIndex(), data); 
                item.loadItem(data);
                refreshSelectedItem(); 
            } 
        }
    }

    public void handleRemove(Object data) 
    {
        if (getSelectedEntity() == null) return;
        
        int idx = getListItems().indexOf(getSelectedItem()); 
        if (idx >= 0) 
        {
            getDataList().remove(idx); 
            refresh();
        }
    }
    
}
