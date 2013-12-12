/*
 * LogicalFunc.java
 *
 * Created on May 21, 2013, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.functions;

import com.rameses.util.DateUtil;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Elmo
 */
public final class DateFunc {
    
    public static long monthsDiff( Date startMonth, Date endMonth ) {
        int m1 = startMonth.getYear() * 12 + startMonth.getMonth();
        int m2 = endMonth.getYear() * 12 + endMonth.getMonth();
        return m2 - m1;
    }
    
    public static long daysDiff( Date startMonth, Date endMonth ) {
        return DateUtil.diff(startMonth, endMonth, Calendar.DATE);
    }
    
    public static Date startQtrDate( int year, int qtr ) {
        Calendar cal = Calendar.getInstance();
        int month = 0;
        switch(qtr) {
            case 1: month = Calendar.JANUARY; break;
            case 2: month = Calendar.APRIL; break;
            case 3: month = Calendar.JULY; break;
            default: month = Calendar.OCTOBER;
        }
        cal.set( year, month, 1,  0, 0  );
        return cal.getTime();
    }
    
    public static Date endQtrDate( int year, int qtr ) {
        Calendar cal = Calendar.getInstance();
        int month = 0;
        switch(qtr) {
            case 1: month = Calendar.MARCH; break;
            case 2: month = Calendar.JUNE; break;
            case 3: month = Calendar.SEPTEMBER; break;
            default: month = Calendar.DECEMBER;
        }
        cal.set( year, month, 1,  0, 0  );
        return monthEnd(cal.getTime());
    }
    
    public static Date monthEnd( Date dt ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int d = cal.getActualMaximum( Calendar.DAY_OF_MONTH );
        cal.set( Calendar.DAY_OF_MONTH, d );
        return cal.getTime();
    }
    
}
