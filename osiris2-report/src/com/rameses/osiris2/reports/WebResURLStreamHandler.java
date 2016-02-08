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
public class WebResURLStreamHandler extends URLStreamHandler {

    private URLStreamHandler handler; 
    
    private URLStreamHandler getHandler() {
        if (handler == null) {
            handler = new ReportURLStreamHandlerFactory().getHandler( getProtocol() ); 
        } 
        return handler; 
    } 
    
    public String getProtocol() {
        return "webresource";
    }

    public URL getResource(String spath) {
        URLStreamHandler h = getHandler(); 
        if ( h == null ) {
            return null; 
        } else {
            return h.getResource( spath ); 
        }
    }    
}
