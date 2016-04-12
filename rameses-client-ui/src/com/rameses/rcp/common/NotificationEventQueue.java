/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores 
 */
public class NotificationEventQueue {
    
    public List<MessageItem> items = new ArrayList(); 
    
    public void add( String message, int count ) {
        add( message, count, null ); 
    }

    public void add( String message, int count, Object userobj ) {
        if ( message == null ) return; 
        
        MessageItem mi = new MessageItem(); 
        mi.userObject = userobj; 
        mi.message = message;
        mi.count = count; 
        items.add( mi ); 
    } 
    
    public int size() {
        return items.size(); 
    }
    
    public void clear() { 
        items.clear(); 
    } 
    
    public MessageItem get( int index ) {
        if ( index >= 0 && index < size() ) {
            return items.get( index ); 
        } else {
            return null; 
        }
    }
    
    public class MessageItem {
        private Object userObject; 
        private String message;
        private int count; 
        
        public String getMessage() { return message; } 
        public Object getUserObject() { return userObject; }
        public int getCount() { return count; } 
    }
}
