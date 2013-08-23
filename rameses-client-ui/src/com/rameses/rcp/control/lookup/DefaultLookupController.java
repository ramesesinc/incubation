/*
 * DefaultLookupController.java
 *
 * Created on April 29, 2013, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.lookup;

import com.rameses.rcp.common.LookupFieldModel;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.util.ControlSupport;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class DefaultLookupController extends UIController 
{
    private DefaultLookupCodeBean codeBean;
    private LookupFieldModel fieldModel;
    private Map info;     
    private String id;
    private String name;
    
    
    public DefaultLookupController(LookupFieldModel fieldModel) 
    {
        this.codeBean = new DefaultLookupCodeBean(fieldModel); 
        this.fieldModel = fieldModel;
        this.name = "default-lookup-controller-";
        this.id = this.name + new UID();
    }
    
    public UIController.View[] getViews() {
        return new UIController.View[] {
            new UIController.View("default", DefaultLookupPage.class.getName())
        }; 
    }

    public String getDefaultView() { return "default"; }

    public Object getCodeBean() { return this.codeBean; }

    public Object init(Map params, String action, Object[] actionParams) 
    {
        ControlSupport.setProperties( getCodeBean(), params );     
        if (action == null) 
            return null;
        else if (action.startsWith("_")) 
            return action.substring(1);
        else 
            return ControlSupport.invoke(getCodeBean(), action, null);
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return fieldModel.getTitle(); }
    public void setTitle(String title) {}

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public Map getInfo() 
    {
        if (info == null) 
        {
            Map map = new HashMap();
            map.put("module", "SYSTEM_DEFAULT"); 
            map.put("path", DefaultLookupController.class.getName()); 
            map.put("name", this.id); 
        }
        return info; 
    }
    
}
