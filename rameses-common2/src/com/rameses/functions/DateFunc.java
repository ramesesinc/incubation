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
import com.rameses.util.HolidayProvider;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
    
    /**************************************************************************
     * base line refers to the day comparison within the month.
     * if 0 then end of month
     * if 1 then begin of month
     * if any number that will be the day in a month. 
     * For example:  Feb 28, 2017 - Mar 31, 2017. 
     * If base line is 0 then correct to feb 28 - mar 31. 
     * If base line is 1 then correct to feb 1 - mar 31. diff :  1 month 
     * If base line is any day say 15, then feb 15 - mar 31. 
    ***************************************************************************/
    public static long monthsDiff( Date startMonth, Date endMonth, int baseLine ) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startMonth);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endMonth);
        int m1 = (cal1.get(Calendar.YEAR) * 12) + cal1.get(Calendar.MONTH);
        int m2 = (cal2.get(Calendar.YEAR) * 12) + cal2.get(Calendar.MONTH);
        long diff = m2 - m1;
        if( diff <= 0 ) return 0;

        int d1 = cal1.get(Calendar.DAY_OF_MONTH);
        int d2 = cal2.get(Calendar.DAY_OF_MONTH);

        //compare month ends
        if( baseLine == 0 ) {
            int daysInMonth2 = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
            if( d2 < daysInMonth2 ) diff = diff - 1;
        }   
        else if (baseLine == 1 ) {
            if( d1 > 1 ) diff = diff - 1;
        }
        else {
            if( d1 > baseLine ){
                diff = diff - 1;
            }
            else if( d2 < baseLine ) {
                diff = diff - 1;
            }
        }
        if(diff <0) diff = 0;
        return diff;
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

    /*************************************************************************
     * holidays must be sorted in order. 
     *************************************************************************/
    public static Date getFindNextWorkDay(Date dt) {
        return getFindNextWorkDay(dt, null, 1);
    }
    
    public static Date getFindNextWorkDay(Date dt, HolidayProvider holidayProvider) {
        return getFindNextWorkDay(dt, holidayProvider, 1 );
    }
    
    public static Date getFindNextWorkDay(Date dt, HolidayProvider holidayProvider, int direction) {
        try {
            int dir = (direction<0)? -1 : 1;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int dow = getDayOfWeek(dt);
            int add_days = 0;
            if(dir == 1 ) {
                if( dow == 1 ) add_days = 1;
                else if( dow == 7 ) add_days = 2;
            }
            else {
                if( dow == 1 ) add_days = -2;
                else if( dow == 7 ) add_days = -1;
            }
            Date d = getDayAdd(dt, add_days);
            if( holidayProvider !=null ) {
                if(holidayProvider.exists(d)) {
                    Date hd = getDayAdd(d, 1*dir);
                    d = getFindNextWorkDay( hd, holidayProvider, direction );
                }
            }
            return d;
        }
        catch(Exception e) {
            throw new RuntimeException("Error in getFindNextWorkday function " + e.getMessage());
        }
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
    
    public static String dateStringFormat(Date dt,String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.format(dt);
        }
        catch(Exception e) {
            System.out.println("error  " + e.getMessage());
            return null;
        }
    }
}
