/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.image;

import com.rameses.util.Base64Cipher;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores
 */
public class ThumbnailListModel extends AbstractListModel {

    private final Object LOCKED = new Object();
    
    private Object source; 
    private ArrayList<ThumbnailItem> list;
    
    public ThumbnailListModel() {
        list = new ArrayList(); 
    }
    
    void setSource( Object source ) {
        this.source = source;
    }
    
    public int getSize() { 
        return list.size();
    }

    public Object getElementAt(int index) { 
        try {
            return list.get(index); 
        } catch(Throwable t) {
            return null; 
        }
    } 
    
    public void clear() { 
        synchronized( LOCKED ) {
            list.clear(); 
        }
    }
    
    public void add( Map data ) { 
        synchronized (LOCKED) {
            if ( data == null ) return;

            Object caption = data.get("caption");
            Object image = data.get("image");         

            ThumbnailItem item = new ThumbnailItem();
            item.setCaption( caption == null? "": caption.toString()); 
            item.setIcon( resolveImage( image )); 
            item.setData( data ); 
            list.add( item ); 
        } 
    } 
    
    public void remove( int index ) {
        synchronized (LOCKED) { 
            try { 
                list.remove( index ); 
                fireIntervalRemoved( source, index, index);
            } catch(Throwable t) {;} 
        } 
    }
    
    public void fireRemoveItemEvent( Object source, int index0, int index1 ) {
        fireIntervalRemoved(source, index0, index1);
    }
    public void fireAddItemEvent( Object source, int index0, int index1 ) {
        fireIntervalAdded(source, index0, index1);
    }
    
    private Base64Cipher base64;
    private Base64Cipher getBase64Cipher() {
        if (base64 == null) {
            base64 = new Base64Cipher(); 
        }
        return base64; 
    }
    
    private ImageIcon resolveImage( Object value ) {
        if ( value instanceof Image ) {
            return new ImageIcon((Image) value); 
        } else if ( value instanceof byte[] ) {
            return new ImageIcon((byte[]) value);
        } else if ( value instanceof String && getBase64Cipher().isEncoded(value.toString()) ) {
            Object o = getBase64Cipher().decode(value.toString()); 
            return resolveImage( o ); 
        } else {
            return null; 
        }
    }
}
