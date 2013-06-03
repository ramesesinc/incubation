package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PageListModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicListController extends PageListModel 
{    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    private com.rameses.rcp.framework.Binding binding;
    

    private Object selectedEntity;
    private List formActions;
    
    
    public abstract Column[] getColumns();
    public abstract List fetchList(Map params); 
    
    
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
        
    public final AbstractListDataProvider getListHandler() { return this; } 
    
    public com.rameses.rcp.framework.Binding getBinding() { return binding; } 
    
    public String getTitle() { return invoker.getCaption(); } 
    
    public Object getSelectedEntity() { return selectedEntity; }
    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity;
    }    
              
    public String getFormTarget() { return "popup"; }    
            
    public Opener getQueryForm() { return null; }    
        
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" Action Methods ">        

    public void search() {
        load(); 
    }    
    
    public Object open() throws Exception { 
        return null; 
    }
    
    public Object onOpenItem(Object o, String columnName) 
    {
        try
        {
            setSelectedEntity(o);
            if (o == null) return null;
            
            return open(); 
        }
        catch(Exception ex) 
        {
            MsgBox.err(ex); 
            return null; 
        } 
    }         
      
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Form and Extended Actions ">  
    
    public List getFormActions() 
    { 
        if (formActions == null)
        {
            formActions = new ArrayList();
            try { 
                formActions.addAll(lookupActions("formActions")); 
            } catch(Exception ex) {;} 
        }
        return formActions;
    } 
    
    public List getNavActions() { 
        return new ArrayList(); 
    }
    
    protected List lookupActions(String type)
    {
        return InvokerUtil.lookupActions(type, new InvokerFilter() {
            public boolean accept(com.rameses.osiris2.Invoker o) { 
                return o.getWorkunitid().equals(invoker.getWorkunitid()); 
            }
        }); 
    }
        
    // </editor-fold>
       
}
