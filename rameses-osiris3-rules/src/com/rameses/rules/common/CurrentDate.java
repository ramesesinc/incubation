/*
 * CurrentDate.java
 *
 * Created on October 22, 2013, 6:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Elmo
 */
public class CurrentDate {
    
    private Date date;
    private int year;
    private int qtr;
    private int month;
    private int hour;
    private int minute;
    private int second;
    private int day;
    private int dayOfWeek;  //Mon,Tue,Wed,Thu,Fri
    
    
    /** Creates a new instance of CurrentDate */
    public CurrentDate() {
        this( new Date());
    }
    
    public CurrentDate(Date d) {
        this.date = d;
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        month = cal.get( Calendar.MONTH ) + 1;
        day = cal.get( Calendar.DATE );
        year = cal.get( Calendar.YEAR );
        hour = cal.get( Calendar.HOUR );
        minute = cal.get( Calendar.MINUTE );
        second = cal.get( Calendar.SECOND );
        if( month >= 1 && month <= 3 ) qtr = 1;
	else if( month >= 4 && month <= 6 ) qtr = 2;
	else if( month >= 7 && month <= 9 ) qtr = 3;
        else qtr = 4;
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    }

    public Date getDate() {
        return date;
    }

    public int getYear() {
        return year;
    }

    public int getQtr() {
        return qtr;
    }

    public int getMonth() {
        return month;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getDay() {
        return day;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }
    
    
}
