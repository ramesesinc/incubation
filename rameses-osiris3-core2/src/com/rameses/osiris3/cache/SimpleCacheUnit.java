/*
 * SimpleCacheUnit.java
 *
 * Created on February 11, 2013, 11:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Elmo
 * This is the holder of the actual cache. This is used for the default cache in checking expiry
 */
public class SimpleCacheUnit {
    
    private Date expiry;
    
    //timeout in seconds. default is 30 seconds
    private int timeout;
    private Object value;
    
    public SimpleCacheUnit(Object data, int timeout) {
        if(timeout==0) timeout = 30;
        this.value = data;
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.SECOND, timeout );
        this.expiry = cal.getTime();
    }

    public boolean isExpired() {
        Date now = new Date();
        return now.after( expiry );
    }

    public Object getValue() {
        return value;
    }
    
}
