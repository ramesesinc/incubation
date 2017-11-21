/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.remote;

import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public interface RemoteScriptCacheServiceInterface {
    public Map getInfo( String name );
    public List listServiceNames(String name);
}
