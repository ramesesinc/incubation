
package com.rameses.osiris2.reports;

import com.rameses.common.PropertyResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class ReportDataSource implements JRRewindableDataSource {
    
    protected Iterator iterator;
    protected Object currentObject;
    
    private Object source;
    private PropertyResolver propertyResolver;
    private ReportDataSourceHelper helper;

    public ReportDataSource(Object source) {    
        setSource( source ); 
        propertyResolver = PropertyResolver.getInstance();
    }
    
    public ReportDataSourceHelper getHelper() {
        return helper; 
    }
       
    public Object getSource() { return source; } 
    public void setSource(Object src) { 
        this.source = src; 
        this.helper = new ReportDataSourceHelper( this.source );
        reset();
    }
    
    private void reset() { 
        Object src = getSource(); 
        if ( src == null ) { 
            iterator = (new ArrayList()).iterator();
        } else if( src instanceof Collection ) {
            iterator = ((Collection)src).iterator();
        } else {
            List l = new ArrayList();
            l.add( src );
            iterator = l.iterator();
        } 
    } 
    
    public void moveFirst() throws JRException {
        reset(); 
    }

    public boolean next() throws JRException {
        if( iterator.hasNext() ) {
            currentObject = iterator.next();
            return true;
        } else {
            return false;
        }
    }
    
    public Object getFieldValue(JRField jRField) throws JRException {
        Object field = null;
        String fieldName = null;
        try {
            fieldName = jRField.getName();
            field = propertyResolver.getProperty(currentObject, fieldName);
            
            if( jRField.getValueClass().isAssignableFrom( Collection.class) ) {
                return new ReportDataSource( field );
            } else { 
                return field;
            }
        } catch(Exception ex) {
            System.out.println("Error on field [" + fieldName  + "] caused by " + ex.getMessage());
            return null; 
        } 
    }  
}
