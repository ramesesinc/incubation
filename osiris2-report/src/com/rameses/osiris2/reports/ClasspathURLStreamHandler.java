/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.reports;

import com.rameses.util.URLStreamHandler;
import java.net.URL;

/**
 *
 * @author wflores 
 */
public class ClasspathURLStreamHandler extends URLStreamHandler {

    private final static String KEY_NAME = "classpath";
    
    public String getProtocol() {
        return KEY_NAME; 
    }

    public int getIndex() {
        return -1; 
    }

    public URL getResource(String spath) { 
        if ( ReportUtil.isDeveloperMode()) { 
            return ReportUtil.factory.getResource(spath);
        } else { 
            return null; 
        } 
    }    
}
