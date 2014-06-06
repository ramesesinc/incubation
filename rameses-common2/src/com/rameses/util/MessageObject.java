/*
 * MessageObject.java
 *
 * Created on May 14, 2014, 1:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class MessageObject 
{
    private final static String MSG_OBJECT_KEY = "2460ae670e45f5c00e10a05eecf98137";
    
    public final static String PROTOCOL = "MessageObject://";    
    
    private String connectionid;
    private String groupid;
    private Object data;
    
    public MessageObject() {
    }
    
    public String getConnectionId() { return connectionid; } 
    public void setConnectionId(String connectionid) {
        this.connectionid = connectionid; 
    }
    
    public String getGroupId() { return groupid; } 
    public void setGroupId(String groupid) {
        this.groupid = groupid; 
    }
    
    public Object getData() { return data; } 
    public void setData(Object data) {
        this.data = data; 
    }
    
    protected void init() {
        connectionid = null;
        groupid = null;
        data = null; 
    }
    
    public boolean isValid(String encdata) {
        return (encdata != null && encdata.startsWith(PROTOCOL)); 
    }
    
    public MessageObject decrypt(byte[] bytes, int offset, int length) { 
        if (bytes == null) return new MessageObject(); 
        
        return decrypt(new String(bytes, offset, length)); 
    }
    
    public MessageObject decrypt(String encdata) { 
        if (encdata == null || encdata.length() == 0) { 
            return new MessageObject(); 
        }

        if (encdata.startsWith(PROTOCOL)) { 
            encdata = encdata.replaceFirst(PROTOCOL, ""); 
            Base64Cipher cipher = new Base64Cipher(); 
            Map map = (Map) cipher.decode(encdata); 
            Object omsgkey = map.get("MSG_OBJECT_KEY"); 
            Object oconnid = map.get("connectionid"); 
            Object ogroupid = map.get("groupid"); 
            Object odata = map.get("data"); 
            if (omsgkey == null || !MSG_OBJECT_KEY.equals(omsgkey.toString())) {
                odata = map; 
            }

            MessageObject mo = new MessageObject();             
            mo.setConnectionId(oconnid == null? null: oconnid.toString()); 
            mo.setGroupId(ogroupid == null? null: ogroupid.toString()); 
            if (odata == null || odata.toString().length() == 0) return mo;
            
            try { 
                mo.setData( cipher.decode(odata) ); 
            } catch(IllegalArgumentException iae) {
                mo.setData(odata); 
            } 
            return mo; 
        } 

        throw new IllegalStateException("failed to decrypt message caused by invalid headers");         
    }
    
    public byte[] encrypt() {
        String connectionid = getConnectionId();
        if (connectionid == null || connectionid.trim().length() == 0) 
            throw new NullPointerException("MessageObject requires a connectionid");
        
        String groupid = getGroupId();
        if (groupid == null || groupid.trim().length() == 0) 
            throw new NullPointerException("MessageObject requires a groupid");        
        
        Object data = getData();
        if (data == null) data = new HashMap(); 
        
        Map map = new HashMap();
        map.put("MSG_OBJECT_KEY", MSG_OBJECT_KEY); 
        map.put("connectionid", connectionid);
        map.put("groupid", groupid);
        map.put("data", data);
        String str = PROTOCOL + new Base64Cipher().encode(map); 
        return str.getBytes(); 
    }
}

