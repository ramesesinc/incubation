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
public class PopupMenuOpener_1 extends Opener {
    
    private List<Opener> openers = new ArrayList(); 
    
    public PopupMenuOpener_1() {
        super();
        setId("PopupMenu" + new UID()); 
    }
    
    public final String getTarget() { return "popupmenu"; }   

    public List<Opener> getOpeners() { return openers; } 
    
    public Opener getFirst() {
        return (openers.isEmpty()? null: openers.get(0)); 
    }
    
    public void removeAll() { openers.clear(); }
    
    public void add(Opener opener) {
        if (opener != null && !openers.contains(opener)) 
            openers.add(opener); 
    }
    
}
