/*
 * OSManager.java
 *
 * Created on October 24, 2013, 9:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import java.awt.Container;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

/**
 *
 * @author wflores
 */
final class OSManager 
{
    // <editor-fold defaultstate="collapsed" desc=" static methods "> 
    
    private static ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
    
    public static ClassLoader getOriginalClassLoader() {
        return originalClassLoader;
    } 

    private static OSManager instance;        
    public static OSManager getInstance() {
        if (instance == null) instance = new OSManager();

        return instance;
    } 

    // </editor-fold>
    
    private Object treeLock = new Object();
    private OSMainWindow osMainWindow;
    private OSPlatform osPlatform;
    private JMenuBar menubar;
    private Container toolbarView;
    private Container desktopView;
    private Container statusView;
    private Map<Object,Object> properties = new Hashtable<Object,Object>();
    //private NBHeaderBar headerBar = new NBHeaderBar();
    
    private OSManager() {
    }
    
    void init() {
        osMainWindow = new OSMainWindow();
        osPlatform = new OSPlatform(this);
    } 
    
    OSMainWindow getMainWindow() { return osMainWindow; }
    OSPlatform getPlatform() { return osPlatform; } 
    
    void reinitialize() {
        osMainWindow.reinitialize();
        osPlatform = new OSPlatform(this); 
        if (osAppLoader == null) {
            osMainWindow.setContent(new JLabel("")); 
        } else {
            osAppLoader.load(osPlatform); 
        }        
    }
    
    void startUpdate() {
        startUpdateImpl(false);
    }
    
    void retryUpdate() { 
        startUpdateImpl(true); 
    }
    
    private void startUpdateImpl(final boolean retry) {
        DownloadPanel pnl = null; 
        try { 
            pnl = new DownloadPanel(); 
            osMainWindow.setContent(pnl); 
            osMainWindow.show();
        } catch(Throwable t) {
            ErrorDialog.show(t); 
            t.printStackTrace();
        } 
        
        final DownloadPanel dpnl = pnl;
        Runnable runnable = new Runnable() {
            public void run() {
                if (retry) { 
                    try { Thread.sleep(1000); }catch(Throwable t){;} 
                } 
                
                dpnl.startDownload(); 
            } 
        }; 
        new Thread(runnable).start(); 
    }   
    
    private OSStartupWindow startupWindow;    
    OSStartupWindow getStartupWindow() {
        if (startupWindow == null) {
            startupWindow = new OSStartupWindow();
        }
        return startupWindow; 
    } 
    
    void closeStartupWindow() {
        if (startupWindow == null) return;

        startupWindow.dispose();
        startupWindow = null; 
    } 
    
    private OSAppLoader osAppLoader;
    OSAppLoader getAppLoader() { return osAppLoader; } 
    void setAppLoader(OSAppLoader osAppLoader) {
        this.osAppLoader = osAppLoader; 
        if (osAppLoader != null) osAppLoader.load(osPlatform); 
    }     
}
