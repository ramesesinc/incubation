/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.remote;

/**
 *
 * @author dell
 */
public interface RemoteScriptInvokerInterface {
    Object invoke(String serviceName, Object params);
}
