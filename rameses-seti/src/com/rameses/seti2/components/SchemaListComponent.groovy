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
        
    boolean allowCreate;
    boolean allowOpen;
    boolean allowDelete;

    def query;    
    def handler; 
    def selectedItem;
        
    void setSelectedItem( o ) {
        this.selectedItem = o; 
        if ( ui && ui.name ) { 
            ui.setProperty( ui.name, o );  
            ui.notifyDepends( ui.name );  
        } 
    }
    
    def listModel = [
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
            
            if( orderBy ) m.orderBy = orderBy;
            if( groupBy ) m.groupBy = groupBy; 
            return queryService.getList( m );
        },
        onOpenItem : {o, colName ->
            if ( allowOpen ) { 
                if ( handler?.beforeOpen ) handler.beforeOpen( o );  
                return Inv.lookupOpener(schemaName+":open", [ entity: o ]);
            }
            return null;
        }, 
        onRemoveItem: { o-> 
            if ( handler?.beforeRemoveItem ) handler.beforeRemoveItem( o );  
            
            removeItem();
            
            if ( handler?.afterRemoveItem ) handler.afterRemoveItem( o );  
        }, 
        isColumnEditable: { o, name-> 
            if ( handler?.isColumnEditable ) { 
                return handler.isColumnEditable( o, name );  
            }
            return true; 
        }, 
        beforeColumnUpdate: { o, name, newValue->  
            if ( handler?.beforeColumnUpdate ) { 
                return handler.beforeColumnUpdate( o, name, newValue );  
            } 
            return true; 
        },
        afterColumnUpdate: { o, name-> 
            if ( handler?.afterColumnUpdate ) { 
                return handler.afterColumnUpdate( o, name );  
            } 
        }
    ] as EditorListModel; 
    
    void setHandler( o ) { 
        if ( o == null ) o = [:]; 
        
        this.handler = o; 
        if ( o instanceof Map ) {
            o.load = { listModel.load(); }
            o.refresh = { listModel.refresh(); }
            o.reload = { listModel.reload(); }
            o.reloadAll = { listModel.reloadAll(); }
            o.refreshSelectedItem = { listModel.refreshSelectedItem(); } 
        } 
    } 
    
    public void removeItem() {
        if(!allowDelete) return null;
        if(!selectedItem) throw new Exception("Please select an item to remove");
        if( !MsgBox.confirm("Are you srue you want to remove this item?")) return null;
        selectedItem._schemaname = schemaName;
        persistenceService.removeEntity( selectedItem );
        listModel.reload();
    } 
    
    public def create() {
        if(!allowCreate) return null;
        return Inv.lookupOpener(schemaName+":create" );
    } 
}   
