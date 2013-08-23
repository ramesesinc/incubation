/*
 * ImageIconSupport.java
 *
 * Created on July 8, 2013, 9:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import com.rameses.rcp.util.ControlSupport;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores
 */
public class ImageIconSupport 
{
    private static ImageIconSupport instance;
    
    public static ImageIconSupport getInstance() 
    {
        if (instance == null) instance = new ImageIconSupport(); 
        
        return instance; 
    }
    
    private Map<String,Image> cache = new HashMap(); 
            
    private ImageIconSupport() {
    }
    
    public synchronized void removeIcon(String path) 
    {
        if (path == null || path.length() == 0) return;
        
        cache.remove(path);
    }
    
    public synchronized ImageIcon getIcon(String path) 
    {
        if (path == null || path.length() == 0) return null;
        
        Image image = cache.get(path); 
        if (image == null) 
        {                        
            try 
            {
                if (path.toLowerCase().startsWith("http://")) 
                    return new ImageIcon(new URL(path)); 
                
                byte[] bytes = ControlSupport.getByteFromResource(path);
                if (bytes == null) return null; 
                
                ImageIcon icon = new ImageIcon(bytes); 
                cache.put(path, icon.getImage());
                return icon;
            } 
            catch(Exception ex) {
                return null; 
            } 
        }
        return new ImageIcon(image); 
    }    
    
}
