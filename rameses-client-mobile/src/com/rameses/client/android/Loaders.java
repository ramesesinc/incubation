/*
 * Loaders.java
 *
 * Created on January 22, 2014, 2:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import com.rameses.client.interfaces.AppLoader;
import com.rameses.client.interfaces.AppLoaderCaller;
import com.rameses.util.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author compaq
 */
public final class Loaders 
{
    private static List<AppLoader> LOADERS = new ArrayList(); 
    
    public static synchronized void register(AppLoader loader) {
        if (loader == null) return;
        if (!LOADERS.contains(loader)) LOADERS.add(loader); 
    }
    
    static synchronized void load(ClassLoader classLoader) {
        Loaders l = new Loaders(classLoader);
        l.loadImpl();
        l.activate();
    }
    
    private ClassLoader classLoader;
    private List<AppLoader> loaders;
    private CallerImpl callerImpl;
    
    private Loaders(ClassLoader classLoader) {
        this.classLoader = classLoader; 
    }
    
    private void loadImpl() { 
        List<AppLoader> list = new ArrayList(); 
        list.addAll(LOADERS); 
        
        Iterator itr = Service.providers(AppLoader.class, classLoader); 
        while (itr.hasNext()) {
            AppLoader loader = (AppLoader) itr.next(); 
            list.add(loader); 
        }
        //sort according to its index
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                int idx1 = ((AppLoader) o1).getIndex();
                int idx2 = ((AppLoader) o2).getIndex();
                if (idx1 < idx2) return -1;
                else if (idx1 > idx2) return 1;
                else return 0;                
            }
            
            public boolean equals(Object obj) {
                return (obj instanceof AppLoader); 
            }
        }); 
        loaders = list;
    } 
    
    private void activate() { 
        callerImpl = new CallerImpl(); 
        callerImpl.resume(); 
    } 
    
    private class CallerImpl implements AppLoaderCaller
    {
        Loaders root = Loaders.this;
        int index = 0;
        
        public void resume() { 
            AppLoader current = null;
            try { 
                current = root.loaders.get(index); 
            } catch(Throwable t) {;} 

            if (current == null) return;
            
            index += 1;
            current.setCaller(this); 
            current.load();
        }
    }
}
