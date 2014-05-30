/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.common.AsyncRequest;

/**
 *
 * @author Elmo
 */
public interface XAsyncConnection  {
    
    void register(String id) throws Exception;
    void unregister(String id) throws Exception;
    Object poll(String id) throws Exception;
    void push(String id, Object data) throws Exception;
    void submitAsync( AsyncRequest ar );
    
}
