package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.ChangeLog;
import com.rameses.rcp.annotations.Invoker;
import java.util.ArrayList;
import java.util.List;


public class FormController {
    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @ChangeLog
    private com.rameses.rcp.framework.ChangeLog changeLog; 
    
    @Binding
    private com.rameses.rcp.framework.Binding binding;
    
    
    private List formActions;
    
    public FormController() {
    }
 
    public String getTitle() {
        return invoker.getCaption();
    } 
    
    public List getFormActions() 
    {
        if (formActions == null) 
        {
            formActions = new ArrayList();
            try { formActions.addAll(lookupActions("formActions")); } catch(Exception ign){;} 
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
}
