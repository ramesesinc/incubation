/*
 * EditorListModel.java
 *
 * Created on May 15, 2013, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.rcp.common.EditorListSupport.TableEditorHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class EditorListModel extends AbstractListDataProvider implements EditorListSupport.TableEditor 
{
    private List DEFAULT_LIST = new ArrayList();
    
    public List fetchList(Map params) { return DEFAULT_LIST; }    
    
    public boolean isAllowAdd() { return true; }     
    public Object createItem() { return new HashMap(); }     
    
    public Object createItem(String columnName) {
        return createItem(); 
    } 
    
    protected void validateItem(Object item) {}    
    protected void validate(ListItem li) {
        if (li != null) validateItem(li.getItem()); 
    }
    
    protected void addItem(Object item) {}
    protected void onAddItem(Object item) {
        addItem(item);
    }
    
    protected void onUpdateItem(Object item) {
    }
    
    protected boolean onRemoveItem(Object item) { 
        return false; 
    } 
    
    protected void commitItem(Object item) {} 
    protected void onCommitItem(Object item) {
        commitItem(item); 
    }

    public boolean isColumnEditable(Object item, String columnName) { 
        return true;
    }
    
    protected boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
        return true; 
    } 
    
    protected void afterColumnUpdate(Object item, String columnName) {}
    protected void onColumnUpdate(Object item, String columnName) {
        afterColumnUpdate(item, columnName); 
    }
    
    public final void refreshEditedCell() {
        refreshCurrentEditor(); 
    }
    public final void refreshCurrentEditor() {
        if (editorSupport != null) editorSupport.refreshCurrentEditor(); 
    }
    
    public final boolean hasUncommittedData() {
        return (editorSupport == null? false: editorSupport.hasUncommittedData()); 
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" TableEditor implementation ">

    private TableEditorHandlerImpl editorHandler;
    private EditorListSupport editorSupport;
    
    public TableEditorHandler getTableEditorHandler() { 
        if (editorHandler == null) { 
            editorHandler = new TableEditorHandlerImpl(); 
        } 
        return editorHandler; 
    } 

    public TableModelSupport getTableModelSupport() {
        return tableModelSupport; 
    }
    
    public void setEditorListSupport(EditorListSupport editorSupport) {
        this.editorSupport = editorSupport; 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" TableEditorHandler implementation ">
    
    private class TableEditorHandlerImpl implements EditorListSupport.TableEditorHandler 
    {
        EditorListModel root = EditorListModel.this;

        public boolean isAllowAdd() {
            return root.isAllowAdd(); 
        }
        
        public Object createItem(String columnName) {
            return root.createItem( columnName ); 
        }
        
        public void validate(ListItem li) {
            root.validate(li);
        }

        public void onAddItem(Object item) {
            root.onAddItem(item);
        }
        
        public void onUpdateItem(Object item) {
            root.onUpdateItem(item); 
        }        

        public void onCommitItem(Object item) { 
            root.onCommitItem(item);
        } 

        public boolean onRemoveItem(Object item) {
            return root.onRemoveItem(item);
        }

        public boolean isColumnEditable(Object item, String columnName) {
            return root.isColumnEditable(item, columnName); 
        }

        public boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
            return root.beforeColumnUpdate(item, columnName, newValue);
        }

        public void onColumnUpdate(Object item, String columnName) {
            root.onColumnUpdate(item, columnName); 
        }
    } 
    
    // </editor-fold>
}
