/*
 * PopupMenuOpener.java
 *
 * Created on August 5, 2013, 12:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class PopupMenuOpener extends Opener {
    
    private List<Opener> openers = new ArrayList(); 
    
    public PopupMenuOpener() {
        super();
        setId("PopupMenu" + new UID()); 
    }
    
    public final String getTarget() { return "popupmenu"; }   

    public List<Opener> getOpeners() { return openers; } 
    
    public void removeAll() { openers.clear(); }
    
    public void add(Opener opener) {
        if (opener != null && !openers.contains(opener)) 
            openers.add(opener); 
    }
    
}
