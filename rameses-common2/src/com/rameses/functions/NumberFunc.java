/*
 * NumberFunc.java
 *
 * Created on May 21, 2013, 4:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.functions;

import java.math.BigDecimal;

/**
 *
 * @author Elmo
 */
public final class NumberFunc {
    
    public static int fixed( Object o ) {
        BigDecimal bd = new BigDecimal(o.toString());
        return bd.intValue();
    }
    
}
