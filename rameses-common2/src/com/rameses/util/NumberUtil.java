/*
 * NumberUtil.java
 *
 * Created on February 18, 2011, 8:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public final class NumberUtil {
        
    private static DecimalFormat defaultFormat = new DecimalFormat("###0.00");
    
    public static BigDecimal formatDecimal(Number num, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return new BigDecimal( df.format(num)  );
    }
    
    public static BigDecimal round(Number num) {
        return new BigDecimal(  defaultFormat.format(num) );
    }
    
    public static BigDecimal round(double d) {
        return new BigDecimal( defaultFormat.format(d) );
    }
    
}
