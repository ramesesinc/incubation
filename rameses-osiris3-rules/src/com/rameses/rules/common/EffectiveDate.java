/*
 * EffectiveDate.java
 *
 * Created on October 22, 2013, 1:06 PM
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
public class EffectiveDate {
    
    private Date date;
    private int numericDate;
    
    /** Creates a new instance of EffectiveDate */
    public EffectiveDate(Date date) {
        this.date = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        String yr = cal.get(Calendar.YEAR) +"";
        String mnth = (cal.get(Calendar.MONTH)+1)+"";
        String day = cal.get(Calendar.DAY_OF_MONTH)+"";
        if(mnth.trim().length()==1) mnth = "0"+mnth;
        if(day.trim().length()==1) day = "0"+day;
        numericDate = Integer.parseInt(yr+mnth+day);
    }

    public Date getDate() {
        return date;
    }
    
    public int getNumericDate() {
        return this.numericDate;
    }
    
}
