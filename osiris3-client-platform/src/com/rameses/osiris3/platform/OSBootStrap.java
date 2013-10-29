/*
 * OSBootStrap.java
 *
 * Created on October 24, 2013, 9:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import javax.swing.UIManager;

/**
 *
 * @author wflores
 */
public final class OSBootStrap 
{
    public static void main(String[] args) throws Exception 
    {
        try {
            String plaf = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(plaf); 
        } catch(Throwable t) {;} 
        
        try { 
            OSManager osm = OSManager.getInstance();
            osm.init(); 
            osm.startUpdate(); 
        } catch(Throwable t) {  
            ErrorDialog.show(t); 
        } 
    }
    
    private OSBootStrap() {}    
}
