/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import java.util.Map;

/**
 *
 * @author wflores
 */
public class XNotificationProvider extends XConnectionProvider {

    @Override
    public String getProviderName() {
        return "notification"; 
    }

    @Override
    public XConnection createConnection(String name, Map conf) {
        return new XNotificationImpl(name, context, conf); 
    } 
}
