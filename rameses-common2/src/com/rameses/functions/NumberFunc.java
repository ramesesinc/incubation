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
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author Elmo
 */
public final class NumberFunc {
    
    public static int fixed( Object o ) {
        BigDecimal bd = new BigDecimal(o.toString());
        return bd.intValue();
    }
    
    public static BigDecimal roundToTen( Object value ) {
        BigDecimal bd = new BigDecimal( value.toString() );
        if( bd.doubleValue() < 5 ) {
            return new BigDecimal("0.0");
        }
        else if( bd.doubleValue()  < 10 )
            return new BigDecimal("10.0");
        else {
            DecimalFormat df = new DecimalFormat( "#0.00000000");
            String snum = df.format(bd.doubleValue());
            int i = snum.indexOf(".");
            i = (i == 1 ? 2 : i);
            MathContext mc = new MathContext(i-1, RoundingMode.HALF_UP);
            return bd.round(mc);
        }
    }
    
    public static BigDecimal round( Object amount ) {
        return round(amount, 2 );
    }
    
    public static BigDecimal round( Object amount, int scale ) {
        if( amount == null || amount.toString().length() == 0) 
            return new BigDecimal("0.0");
        
        if ( amount instanceof Number ) {
            DecimalFormat df = new DecimalFormat( "0.00000000");    
            amount = df.format(amount);
        }
        
        BigDecimal bd = new BigDecimal(amount.toString());
        return bd.setScale(scale, RoundingMode.HALF_UP);
    }    
    
}
