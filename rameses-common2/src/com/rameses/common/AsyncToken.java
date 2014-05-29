/*
 * AsyncHandler.java
 *
 * Created on October 24, 2010, 1:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.common;

import java.io.Serializable;

public class AsyncToken implements Serializable{
    
    private String id;
    private String connection;
    
    
    public AsyncToken(String id, String connection) {
        this.id =id;
        this.connection = connection;
    }

    public String getId() {
        return id;
    }

    public String getConnection() {
        return connection;
    }

}
