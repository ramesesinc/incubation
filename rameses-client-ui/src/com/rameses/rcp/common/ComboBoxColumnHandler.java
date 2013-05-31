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
public class ComboBoxColumnHandler extends Column.TypeHandler implements PropertySupport.ComboBoxPropertyInfo
{   
    private Object items;
    private String itemKey;
    private String expression;
    
    public ComboBoxColumnHandler(){
    } 
    
    public ComboBoxColumnHandler(Object items, String itemKey, String expression) 
    {
        this.items = items;
        this.itemKey = itemKey;
        this.expression = expression; 
    }
    
    public String getType() { return "combobox"; }
    
    public String getExpression() { return expression; }
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getItemKey() { return itemKey; }
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public Object getItems() { return items; }
    public void setItems(Object items) { 
        this.items = items;
    }
    
}