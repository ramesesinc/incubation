package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.CallbackHandler;
import com.rameses.rcp.common.Column;
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
        
    public String getEntityName() {
        throw new RuntimeException("Please provide entity name");
    }
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

    public Column[] getColumns() { return null; }

    public List<Map> getColumnList() {
        return getService().getColumns( new HashMap() ); 
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
        Opener o = InvokerUtil.lookupOpener(getEntityName()+":create", createOpenerParams());
        o.setTarget(getFormTarget()); 
        return o;
    }
    
    public Object open() throws Exception 
    {
        Map params = createOpenerParams();
        params.put("entity", getSelectedEntity()); 
        
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
    
    private Map createOpenerParams() 
    {
        Map handlers = new HashMap();
        handlers.put("saveCreate", new InsertRefreshHandler());
        handlers.put("saveUpdate", new UpdateRefreshHandler());
        
        Map params = new HashMap();
        params.put("listModelHandler", this);
        params.put("refreshHandler", handlers); 
        return params;
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
            
            List extActions = lookupActions("formActions");
            formActions.addAll(extActions);
            extActions.clear(); 
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
    
    
    public List fetchList(Map m) {
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
    // <editor-fold defaultstate="collapsed" desc=" ListControllerHandler implementation "> 
    
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" refresh handler support "> 
    
    public class InsertRefreshHandler implements CallbackHandler
    {
        public Object call() { 
            throw new NullPointerException("Please feed the data when invoking this method");
        }

        public Object call(Object arg) { 
            return call(new Object[]{ arg }); 
        }

        public Object call(Object[] args) { 
            Object data = args[0];
            handleInsert(data);
            return null;
        } 
    }
    
    public class UpdateRefreshHandler implements CallbackHandler
    {
        public Object call() { 
            throw new NullPointerException("Please feed the data when invoking this method");
        }

        public Object call(Object arg) { 
            return call(new Object[]{ arg }); 
        }

        public Object call(Object[] args) { 
            Object data = args[0];
            handleUpdate(data);
            return null;
        } 
    }    
    
    // </editor-fold>
}
