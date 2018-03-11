package com.rameses.osiris2.report;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

public class SchemaListComponent extends ComponentBean  {
    
    def ui; // set by the XComponentPanel

    @Service("QueryService")
    def queryService;
    
    @Service("PersistenceService")
    def persistenceService;
    
    String schemaName;
    String customFilter;
    String hiddenCols;
    String orderBy;
    String groupBy;
    String actionContext;
    String menuContext;

    boolean allowCreate;
    boolean allowOpen;
    boolean allowDelete;
    int rows = 20;

    def query;    
    def selectedItem;
    
    def _handler; 
    def _schema;    
    def searchText;

    def getSchema() {
        if ( _schema == null ) {
            def map = [ name: schemaName ]; 
            _schema = persistenceService.getSchema( map );
            _schema.name = schemaName;
        }
        return _schema; 
    }
    
    void search() {
        listModel.reload();
    }
    
    void setSelectedItem( o ) {
        this.selectedItem = o; 
        if ( ui && ui.name ) { 
            ui.setProperty( ui.name, o );  
            ui.notifyDepends( ui.name );  
        } 
    }
    
    boolean isMultiSelectEnabled() {
        return (ui ? ui.isMultiSelect() : false); 
    }
    
    def listModel = [
        getRows: {
            return rows;
        },
        isMultiSelect: {
            return isMultiSelectEnabled(); 
        }, 
        isPagingEnabled: {
            return true;
        },
        fetchList: { o-> 
            if ( !schemaName ) return []; 
            
            def m = [:];
            m.putAll( o ); 
            m._schemaname = schemaName;
            
            def colnames = [];  
            if ( hiddenCols ) colnames << hiddenCols;

            //def cols = listModel.getColumnList(); 
            //if ( cols ) colArr = cols.collect{ it.name } 

            if ( colnames ) m.select = colnames.join(",");
            
            if ( customFilter ) { 
               if ( query == null ) query = [:]; 
               m.where = [ customFilter, query ]; 
            }
            if(searchText) {
                m.search
            }
            if( orderBy ) m.orderBy = orderBy;
            if( groupBy ) m.groupBy = groupBy; 
            return queryService.getList( m );
        },
        onOpenItem : {o, colName ->
            return openImpl( o );
        }, 
        onRemoveItem: { o-> 
            if ( _handler?.beforeRemoveItem ) _handler.beforeRemoveItem( o );  
            
            removeItem();
            
            if ( _handler?.afterRemoveItem ) _handler.afterRemoveItem( o );  
        }, 
        isColumnEditable: { o, name-> 
            if ( _handler?.isColumnEditable ) { 
                return _handler.isColumnEditable( o, name );  
            }
            return true; 
        }, 
        beforeColumnUpdate: { o, name, newValue->  
            if ( _handler?.beforeColumnUpdate ) { 
                return _handler.beforeColumnUpdate( o, name, newValue );  
            } 
            return true; 
        },
        afterColumnUpdate: { o, name-> 
            if ( _handler?.afterColumnUpdate ) { 
                return _handler.afterColumnUpdate( o, name );  
            } 
        }
    ] as EditorListModel; 
    
    void setHandler( o ) { 
        if ( o instanceof Map ) {
            o.load = { listModel.load(); }
            o.refresh = { listModel.refresh(); }
            o.reload = { listModel.reload(); }
            o.reloadAll = { listModel.reloadAll(); }
            o.refreshSelectedItem = { listModel.refreshSelectedItem(); } 

            o.getSelectedValue = {
                if ( isMultiSelectEnabled()) {
                    return listModel.getSelectedValue(); 
                } else { 
                    return selectedItem; 
                }
            } 
            o.selectedValue = { return o.getSelectedValue(); }
        } 
        
        _handler = o; 
    } 
    
    def open() { 
        return openImpl( selectedItem );  
    }
    def openImpl( o ) {
        if(!allowOpen || !o) return null;

        if ( _handler?.beforeOpen ) _handler.beforeOpen( o );  
        return Inv.lookupOpener(schemaName+":open", [ entity: o ]);         
    }
    
    void removeEntity() {
        if(!allowDelete) return null;
        if(!selectedItem) throw new Exception("Please select an item to remove");
        if( !MsgBox.confirm("Are you srue you want to remove this item?")) return null;
        selectedItem._schemaname = schemaName;
        persistenceService.removeEntity( selectedItem );
        listModel.reload();
    } 
    
    public def create() {
        if(!allowCreate) return null; 
        
        def m = null; 
        if ( _handler?.createItem ) {  
            m = _handler.createItem(); 
        }
        if ( m == null ) m = [:]; 
        return Inv.lookupOpener(schemaName+":create", [defaultData: m] );
    } 
    
    void refresh() { 
        listModel.reload(); 
    }
    
    void showFilter() {
        MsgBox.alert('Not supported at this time'); 
    }
    
    def getFilterText() {
        return "";
    }
    
    void showInfo() {        
        Modal.show("debug:view", [schema: schema, data: selectedItem ]);
    }
    def showHelp() {
        return null; 
    }
}   
