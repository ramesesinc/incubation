/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.server;

import java.util.Vector;

/**
 *
 * @author wflores
 */
public final class ServerPID {
    
    private final static Object LOCK = new Object();
    private final static Vector list = new Vector();
    
    public static boolean isCleared() {
        return list.isEmpty(); 
    }
    
    public static void add(String name) {
//        synchronized (LOCK) {
//            if (name != null && !list.contains(name)) {
//                list.add(name); 
//            }
//        }
    }
    
    public static void remove(String name) {
        if (name != null) {
            list.remove(name); 
        }
    }
}
