package com.rameses.filemgmt.components;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class FileUploadPanelModel extends ComponentBean {

    @Binding
    def binding;
    
    def handlerProxy; 
    def cache = [:]; 
    def self = this; 

    def selectedItem; 
    
    void setSelectedItem( item ) { 
        this.selectedItem = item; 
        if ( item == null ) return;
        if ( item.objid ) {
            def o = cache.get( item.objid ); 
            if ( o ) return; 
            
            o = handlerProxy.getItem( item ); 
            if ( o ) { 
                o.items?.each{ c-> 
                    c.filetype = o.filetype; 
                }
                item._cardname = 'view'; 
                cache.put( item.objid, o ); 
            } else { 
                item._cardname = 'blank';
                cache.remove( item.objid ); 
            } 
        } else {
            item._cardname = 'blank';
            cache.remove( item.objid ); 
        }
    } 
    
    def listHandler = [
        fetchList: {
            if ( handlerProxy ) {
                return handlerProxy.fetchList( it ); 
            } else {
                return null; 
            } 
        } 
    ] as ListPaneModel; 
    
    void addItem( Object item ) {
        listHandler.addItem( item ); 
    }

    def getCardName() { 
        if ( !selectedItem ) return 'blank'; 
        
        def o = selectedItem?._cardname; 
        return (o ? o : 'loading'); 
    }
    
    def getHeaderMessage() {
        if ( !selectedItem ) return " ";
        
        def o = cache.get( selectedItem.objid ); 
        def buff = new StringBuilder(); 
        buff.append('<html>');
        buff.append('<b>'+ selectedItem.title.toString() +'</b>');
        if ( o ) {
            buff.append('<br/>');
            buff.append('Posted By : '+ o.createdby?.name +'<br/>');
            buff.append('Date Posted : '+ o.dtcreated +'<br/>');
        }
        buff.append('</html>');
        return buff.toString(); 
    }
    
    def selectedThumbnail; 
    
    void setSelectedThumbnail( value ) {
        this.selectedThumbnail = value; 
    }
    
    def thumbnailListHandler = [
        fetchList: {
            if ( !selectedItem?.objid ) return null; 
            
            def o = cache.get( selectedItem.objid ); 
            return ( o ? o.items : null ); 
        }, 
        
        openItem: { o-> 
            if ( !o ) return null; 
            return Inv.lookupOpener('sys_fileitem:open', [ fileitem: o ]); 
        }
    ] as ThumbnailViewModel;     
}