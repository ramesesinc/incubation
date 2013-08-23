/*
 * ServerLoader.java
 *
 * Created on March 27, 2013, 10:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.server;

import java.util.Map;

/**
 *
 * @author Elmo
 * 
 */
public interface ServerLoader {
    
    Map getInfo();
    void init(String baseUrl, Map info) throws Exception;
    void start() throws Exception;
    void stop() throws Exception;
}
