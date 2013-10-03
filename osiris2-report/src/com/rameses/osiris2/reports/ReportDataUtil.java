/*
 * ReportDataUtil.java
 *
 * Created on October 3, 2013, 12:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports;

import com.rameses.common.PropertyResolver;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class ReportDataUtil 
{
    private static ReportDataUtil instance; 
    
    public final static ReportDataUtil getInstance() {
        if (instance == null) {
            instance = new ReportDataUtil(); 
        }
        return instance; 
    }
    
    private Object resolveObject(Object obj) {
        if (obj instanceof ReportDataSource) {
            return ((ReportDataSource)obj).getSource();
        } else { 
            return obj; 
        } 
    }
    
    public BigDecimal toBigDecimal(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return null;
        
        if (obj instanceof BigDecimal) {
            return (BigDecimal)obj;
        } else { 
            return new BigDecimal(obj.toString());
        } 
    }
    
    public Integer toInteger(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return null;
        
        if (obj instanceof Integer) {
            return (Integer)obj;
        } else { 
            return new Integer(obj.toString());
        }
    }
    
    public Double toDouble(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return null;
        
        if (obj instanceof Double) {
            return (Double)obj;
        } else { 
            return new Double(obj.toString()); 
        } 
    }
    
    public boolean isEmpty(Object value) {
        Object obj = resolveObject(value);
        if (obj == null) return true; 
        
        if (obj instanceof Map) {
            return ((Map)obj).isEmpty(); 
        } else if (obj instanceof List) {
            return ((List)obj).isEmpty();
        } else { 
            return false; 
        } 
    }
    
    public Object ifNull(Object value, Object defaultValue) {
        Object obj = resolveObject(value);
        return (obj == null) ? defaultValue : obj;
    }
    
    public Object getValue(Object bean, String name) {
        try {
            if (bean == null) return null;
            
            Object obj = resolveObject(bean);
            if (obj == null) return null;
            
            return PropertyResolver.getInstance().getProperty(obj, name);
        } catch (Throwable ex) {
            System.out.println("ReportDataUtil.getValue: [ERROR_" + ex.getClass().getName() + "] " + ex.getMessage());
            return null;
        }
    }
    
    public BigDecimal getBigDecimal(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof BigDecimal) {
            return (BigDecimal)value;
        } else { 
            return new BigDecimal(value.toString()); 
        } 
    }
    
    public Integer getInteger(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof Integer) {
            return (Integer)value;
        } else { 
            return new Integer(value.toString()); 
        }
    }
    
    public String getString(Object bean, String name) {
        Object value = getValue(bean, name);
        return (value == null) ? null : value.toString();
    }
    
    public java.util.Date getDate(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof java.util.Date) {
            return (java.util.Date)value;
        } else { 
            return java.sql.Date.valueOf(value.toString()); 
        } 
    }
    
    public Timestamp getTimestamp(Object bean, String name) {
        Object value = getValue(bean, name);
        if (value == null) return null;
        
        if (value instanceof Timestamp) {
            return (Timestamp)value;
        } else { 
            return Timestamp.valueOf(value.toString()); 
        } 
    }
    
    private java.util.Date convertDate(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof java.util.Date) {
            return (java.util.Date)value;
        } 
        
        java.util.Date dt = null;
        try {
            dt = java.sql.Date.valueOf(value.toString());
        } catch (Throwable ign) {;}
        
        try {
            if (dt == null) dt = Timestamp.valueOf(value.toString());
        } catch (Throwable ign) {;}
        
        return dt;
    }
    
    public int getDaysDiff(Object dtfrom, Object dtto) {
        java.util.Date dt1 = convertDate(dtfrom);
        java.util.Date dt2 = convertDate(dtto);
        
        SimpleDateFormat YM = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        java.util.Date now = cal.getTime();
        if (dt2 == null) dt2 = now;
        
        cal.setTime(dt2);
        int year2 = cal.get(1);
        int month2 = cal.get(2);
        int day2 = cal.get(5);
        
        dt1 = java.sql.Date.valueOf(YM.format(dt1) + "-01");
        dt2 = java.sql.Date.valueOf(YM.format(dt2) + "-01");
        List results = new ArrayList();
        cal.setTime(dt1);
        while (true) {
            int year1 = cal.get(1);
            int month1 = cal.get(2);
            if (year1 > year2)
                break;
            if (year1 < year2) {
                results.add(new Integer(cal.getActualMaximum(5)));
            } else {
                if (month1 > month2)
                    break;
                if (month1 < month2)
                    results.add(new Integer(cal.getActualMaximum(5)));
                else {
                    results.add(new Integer(day2));
                }
            }
            cal.add(2, 1);
        }
        
        int numdays = 0;
        while (!results.isEmpty()) {
            numdays += ((Integer)results.remove(0)).intValue();
        }
        return numdays;
    }
    
    public int getYearsDiff(Object dtfrom, Object dtto) {
        java.util.Date dt1 = convertDate(dtfrom);
        java.util.Date dt2 = convertDate(dtto);
        
        SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        if (dt2 == null) dt2 = cal.getTime();
        
        dt1 = java.sql.Date.valueOf(YMD.format(dt1));
        dt2 = java.sql.Date.valueOf(YMD.format(dt2));
        int numyears = 0;
        cal.setTime(dt1);
        while (true) {
            cal.add(2, 12);
            
            java.util.Date dt = cal.getTime();
            if (dt.after(dt2))
                break;
            ++numyears;
        }
        return numyears;
    }
}
