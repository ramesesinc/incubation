/*
 * MessageObject.java
 *
 * Created on May 14, 2014, 1:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author wflores
 */
public class MessageObject 
{
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
            byte[] bytes = new Base64CoderImpl().decode(encdata.toCharArray());
            Object[] datas = (Object[]) toObject(bytes); 
            MessageObject mo = new MessageObject();
            mo.setConnectionId(datas[0] == null? null: datas[0].toString()); 
            mo.setGroupId(datas[1] == null? null: datas[1].toString()); 
            
            String sdata = (datas[2] == null? null: datas[2].toString()); 
            if (sdata == null || sdata.trim().length() == 0) return mo;
            
            byte[] decbytes = null; 
            try { 
                decbytes = new Base64CoderImpl().decode(sdata.toCharArray());
            } catch(IllegalArgumentException iae) {
                mo.setData(datas[2]); 
            } 
            
            if (decbytes != null) {
                mo.setData(toObject(decbytes)); 
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
        
        Object[] datas = new Object[]{ connectionid, groupid, data };
        byte[] bytes = toByteArray(datas); 
        char[] chars = new Base64CoderImpl().encode(bytes); 
        String str = PROTOCOL + new String(chars); 
        return str.getBytes(); 
    }
    
    private byte[] toByteArray(Object value) { 
        if (value == null) return null;
        
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream(); 
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value); 
            return baos.toByteArray();
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { oos.close(); }catch(Throwable t){;} 
            try { baos.close(); }catch(Throwable t){;} 
        }
    } 
    
    private Object toObject(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null; 
        
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes); 
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            try { ois.close(); }catch(Throwable t){;} 
            try { bais.close(); }catch(Throwable t){;} 
        }        
    }
}

