/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Elmo Nazareno
 */
public class DefaultHolidayProvider implements HolidayProvider {

    private HolidayProvider holidayProvider;
    private List<Date> holidays = new LinkedList();
    private List<Date> nonholidays = new LinkedList(); 
    
     /**
     * @return the holidayProvider
     */
    public HolidayProvider getHolidayProvider() {
        return holidayProvider;
    }

    /**
     * @param holidayProvider the holidayProvider to set
     */
    public void setHolidayProvider(HolidayProvider holidayProvider) {
        this.holidayProvider = holidayProvider;
    }
    
    public final boolean exists(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(sdf.format(date));
        } catch (Exception e) {
            //do nothing
        }
        
        //checked first if in holidays list
        if( holidays.contains(date)) {
            return true;
        }
        else if(nonholidays.contains(date)) {
            return false;
        }
        else {
            boolean b = isHoliday(date);
            if( b ) {
                holidays.add(date);
                return true;
            }
            else {
                nonholidays.add(date);
                return false;
            }
        }
        
    }

    public boolean isHoliday( Date date ) {
         if(holidayProvider == null ) 
                throw new RuntimeException("CachedHolidayProvider.error. HolidayProvider must not be null");
         return holidayProvider.exists(date);
    }
    
}
