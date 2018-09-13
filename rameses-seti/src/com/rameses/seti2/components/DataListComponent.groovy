package com.rameses.seti2.components;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.common.Action
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

public class DataListComponent extends ComponentBean  {
    
    def ui; // set by the XComponentPanel

    String actionContext;
    String menuContext;

    int rows = 20;

    def stylerule; 
    def selectedItem;
    
    def _handler; 
    def items;
    def formActionContext;

    public def getFormActions() {
        if( !formActionContext ) return [];
        
        def list = []; 
        try {
            def actionProvider = ClientContext.currentContext.actionProvider; 
            list = actionProvider.getActionsByType( formActionContext, callerBinding.controller );
            list.each{ 
                it.properties.put('Action.Bean', callerBinding.bean); 
            }
        }
        catch(Throwable t) {
            //do nothing 
        } finally {
            return list; 
        }
    }
    
    void setSelectedItem( o ) {
        this.selectedItem = o; 
        if ( ui && ui.name ) { 
            ui.setProperty( ui.name, o );  
            ui.notifyDepends( ui.name );  
        } 
    }

    def listModel = [
        getRows: {
            return rows;
        },
        fetchList: { o-> 
            return getValue(items);
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
        },
        getContextMenu: { item, name-> 
            if ( _handler?.getContextMenu ) { 
                return _handler.getContextMenu( item, name );  
            } else {
                return null; 
            } 
        }, 
        callContextMenu: { item, menuitem-> 
            def outcome = null; 
            if ( _handler?.callContextMenu ) { 
                 outcome = _handler.callContextMenu( item, menuitem );  
            } 
            return outcome; 
	}
    ] as EditorListModel; 
    
    def open() { 
        return openImpl( selectedItem );  
    }
    
    void removeEntity() {
        if(!selectedItem) throw new Exception("Please select an item to remove");
        if( !MsgBox.confirm("Are you srue you want to remove this item?")) return null;
        listModel.reload();
    } 
    
    void refresh() { 
        listModel.reload(); 
    }
    
    void showInfo() {        
        Modal.show("debug:view", [schema: schema, data: selectedItem ]);
    }

    def showHelp() {
        return null; 
    }
    
    void fire() {
        MsgBox.alert( "parent bean is " + getValue("entity") );
    }
}   

