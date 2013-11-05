/*
 * OSPlatformIdentity.java
 *
 * Created on November 5, 2013, 3:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores
 */
final class OSPlatformIdentity 
{
    private static OSPlatformIdentity instance;
    
    public static OSPlatformIdentity getInstance() {
        if (instance == null) {
            instance = new OSPlatformIdentity();
        }
        return instance; 
    }
    
    private Map identity;
    private ImageIcon defaultIcon;
    
    private OSPlatformIdentity() {
        URL url = OSPlatformIdentity.class.getResource(".identity");
        if (url == null) throw new RuntimeException("Failed to load platform identity"); 
        
        load(url);
    }
    
    public Object get(Object key) {
        return identity.get(key); 
    }
    
    public ImageIcon getDefaultIcon() { 
        try { 
            if (defaultIcon == null) { 
                URL url = OSPlatformIdentity.class.getResource("icon/os2-icon.png"); 
                defaultIcon = new ImageIcon(url); 
            }
            return defaultIcon; 
        } catch(Throwable x) {
            return null; 
        } 
    }
    
    
    private void load(URL url) { 
        ObjectInputStream ois = null;        
        InputStream inp = null;
        try {
            inp = url.openStream(); 
            ois = new ObjectInputStream(inp);
            Object o = ois.readObject();
            identity = (Map) CipherUtil.decode((Serializable)o);
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try { ois.close(); } catch(Exception ign){;}
            try { inp.close(); } catch(Exception ign){;}
        }
    }
}
