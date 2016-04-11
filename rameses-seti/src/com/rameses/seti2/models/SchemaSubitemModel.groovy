package com.rameses.seti2.models;

import com.rameses.rcp.common.EditorListModel;
import java.util.List;
import java.util.Map;


/**
 * @author dell
 * This is mainly used for displaying subitems for an entity.
 * The schema name here refers to the parent schema.
 */
public class SchemaSubitemModel extends EditorListModel {
    
    String schemaname;
    def entity;
    
    SubItemListener handler;
    String cols;
    
    public SubItemEditorListModel(String schemaName, def entity) {
        this.schemaname = schemaName;
        this.entity = entity;
    }
    
    public Map createItem() {
        return handler.createItem( name, subSchema );
    }
    
    public List<Map> getColumnList() {
        def cols = [];
        for( i in subSchema.fields ) {
            if( i.visible == 'false' ) continue;
            def c = [name: i.name, caption: i.caption];
            c.type = 'text';
            c.editable = true;
            cols << c;
        }
        return cols;
        
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
    
    public boolean isColumnEditable(Object item, String columnName) { 
        return handler.isColumnEditable( name, item, columnName ); 
    }
}