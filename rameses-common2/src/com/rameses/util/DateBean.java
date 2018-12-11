/*
 * DateFact.java
 *
 * Created on July 15, 2013, 8:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Elmo
 */
public class DateBean {
    
    private Date date;
    private Calendar cal;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    /** Creates a new instance of DateFact */
    public DateBean() {
        this(new Date());
    }
    
    public DateBean(Date d) {
        try {
            String s = df.format(d);
            this.date = df.parse(s);
            cal = Calendar.getInstance();
            cal.setTime( this.date );
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    public DateBean(String s) {
        try {
            this.date = df.parse(s);
            cal = Calendar.getInstance();
            cal.setTime( this.date );
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    public DateBean(Date d, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            String s = df.format(d);
            this.date = df.parse(s);
            cal = Calendar.getInstance();
            cal.setTime( this.date );
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    public DateBean(String s, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            this.date = df.parse(s);
            cal = Calendar.getInstance();
            cal.setTime( this.date );
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    public Date getDate() {
        return date;
    }
    
    
    public int getMonth() {
        return cal.get( Calendar.MONTH ) + 1;
    }
    
    public int getDay() {
        return cal.get( Calendar.DAY_OF_MONTH );
        //return cal.get( Calendar.DATE );
    }
    
    public int getYear() {
        return cal.get( Calendar.YEAR );
    }
    
    public int getHour() {
        return cal.get( Calendar.HOUR );
    }
    
    public int getMinute() {
        return cal.get( Calendar.MINUTE );
    }
    
    public int getSecond() {
        return cal.get( Calendar.SECOND );
    }
    
    public int getMillisecond() {
        return cal.get(Calendar.MILLISECOND);
    }
    
    public int getQtr() {
        int month = getMonth();
        if( month >= 1 && month <= 3 )return 1;
        else if( month >= 4 && month <= 6 ) return 2;
        else if( month >= 7 && month <= 9 ) return 3;
        else return 4;
    }
    
    public int getDayOfWeek() {
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    
    public Date getMonthEnd() {
        Date cloneDate = cal.getTime();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime( cloneDate );
        int ds = cal2.getActualMaximum( Calendar.DAY_OF_MONTH );
        cal2.set( Calendar.DAY_OF_MONTH, ds );
        return cal2.getTime();
    }
    
    public Date add( String interval ) {
        Date newDate = DateUtil.add( getDate(), interval ); 
        Calendar newCal = Calendar.getInstance();
        newCal.setTime( newDate );
        this.cal = newCal;
        this.date = newDate; 
        return getDate(); 
    }
    
    public String format( String pattern ) {
        return new SimpleDateFormat( pattern ).format( getDate() ); 
    }
}
