/*
 * ComboBoxColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class ComboBoxColumnHandler extends ColumnHandler
{   
    public ComboBoxColumnHandler(){
    } 
    
    public ComboBoxColumnHandler(String expression, Object items, String itemKey) 
    {
        setExpression(expression);
        setItems(items);
        setItemKey(itemKey); 
    }
    
    public String getExpression() { 
        return (String) get("expression"); 
    }
    public void setExpression(String expression) {
        put("expression", expression); 
    }

    public String getItemKey() { 
        return (String) get("itemKey"); 
    }
    public void setItemKey(String itemKey) {
        put("itemKey", itemKey); 
    }

    public Object getItems() { return get("items"); }
    public void setItems(Object items) { put("items", items); }
    
}
