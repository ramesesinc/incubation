/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.models;

import com.rameses.rcp.common.EditorListModel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public class SubItemEditorListModel extends EditorListModel {
    
    private Map subSchema;
    private String name;
    private SubItemListener handler;
    private List cols;
    
    public SubItemEditorListModel(Map schema, SubItemListener listener) {
        this.subSchema = schema;
        this.handler = listener;
        this.name = schema.get("name").toString();
    }
    
    public Map createItem() {
        return handler.createItem( name, subSchema );
    }
    
    public List<Map> getColumnList() {
        if(cols==null) {
            cols = handler.getColumns(name, subSchema);
        }
        return cols;
    }
    
    public List fetchList(Map params) {
        return handler.fetchItems(name, subSchema, params);
    }
    
    protected void onAddItem(Object item) {
        handler.addItem(name, item);
    }        

    protected boolean onRemoveItem(Object item) {
        handler.removeItem(name, item);
        return true;
    } 
        
    protected Object onOpenItem(Object item, String columnName) {
        return handler.openItem(name, item, columnName);
    }

    protected boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
        return handler.beforeColumnUpdate(name, item, columnName, newValue);
    }

    protected void afterColumnUpdate(Object item, String columnName) {
        handler.afterColumnUpdate(name, item, columnName);
    }
    
}
