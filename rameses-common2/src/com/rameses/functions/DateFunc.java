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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    
    public static long yearsDiff( Date startDate, Date endDate) {
        if (startDate == null || endDate == null ) 
            return 0;
        
        long m = monthsDiff(startDate, endDate);
        if (m <= 0)
            return 0;
        return m % 12;
    }
    
    public static long daysDiff( Date startDate, Date endDate ) {
        return DateUtil.diff(startDate, endDate, Calendar.DATE);
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
    
    public static int getQtrMonth( int qtr ) {
        switch(qtr) {
            case 1: 
                return Calendar.JANUARY;
            case 2: 
                return Calendar.APRIL; 
            case 3: 
                return Calendar.JULY; 
            default: 
                return Calendar.OCTOBER;
        }
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
    
    public static int getMonth( Date dt ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.MONTH)+1;
    }

    public static int getYear( Date dt ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.YEAR);
    }
    
    public static int getDay( Date dt ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DATE);
    }
    
    public static Date getDate( int year, int month, int day ) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DATE, day);
        cal.set( Calendar.HOUR_OF_DAY, 0  );
        cal.set( Calendar.MINUTE, 0  );
        cal.set( Calendar.SECOND, 0  );
        cal.set( Calendar.MILLISECOND, 0  );
        return cal.getTime();
    }
    
    public static Date getDayAdd( Date dt, int days ) {
        return DateUtil.add(dt, days + "d");
    }

    public static Date getMonthAdd( Date dt, int months ) {
        return DateUtil.add(dt, months + "M");
    }
    
    public static int getDayOfWeek(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getFindNextWorkDay(Date dt, List holidays) {
        int dow = getDayOfWeek(dt);
        int add_days = 0;
        if( dow == 1 ) add_days = 1;
        else if( dow == 7 ) add_days = 2;
        Date d = getDayAdd(dt, add_days);
        if(holidays!=null && holidays.size()>0) {
            for( Object v: holidays ) {
                if( v instanceof Date ) {
                    Date testDate = (Date)v;
                    if( testDate.equals(d) ) d = getDayAdd(d, 1);
                }
            }
        }
        return d;
    }

    public static Date formatDate(Object dt,String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        String sdt = null;
        if( dt instanceof Date ) {
            sdt = df.format((Date)dt);
        }
        else {
            sdt = dt.toString();
        }
        try {
            return df.parse(sdt);
        }
        catch(Exception e) {
            System.out.println("error  " + e.getMessage());
            return null;
        }
    }
    
}
