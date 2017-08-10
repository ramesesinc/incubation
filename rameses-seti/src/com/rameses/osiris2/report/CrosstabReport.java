/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.report;

import java.util.List;

/**
 *
 * @author wflores
 */
public class CrosstabReport extends SimpleTableReport {
    
    private FieldProperty rowGroup;
    private FieldProperty colGroup;
    private FieldProperty measure;
    private String orientation;
        
    public String getRowGroup() { 
        return (rowGroup == null ? null : rowGroup.getName()); 
    }
    public void setRowGroup(String name) {
        if ( name == null ) { 
            this.rowGroup = null; 
        } else { 
            this.rowGroup = new FieldProperty(name); 
        }
    }
    
    public String getColumnGroup() { 
        return (colGroup == null ? null : colGroup.getName()); 
    } 
    public void setColumnGroup( String name ) {
        if ( name == null ) { 
            this.colGroup = null; 
        } else { 
            this.colGroup = new FieldProperty(name); 
        }
    }

    public String getMeasure() { 
        return (measure == null ? null : measure.getName()); 
    }
    public void setMeasure( String name ) {
        if ( name == null ) { 
            this.measure = null; 
        } else { 
            this.measure = new FieldProperty(name); 
        }
    }
    
    public String getOrientation() { return orientation; } 
    public void setOrientation( String orientation ) {
        this.orientation = orientation; 
    }    
    
    public final String getPreferredOrientation() {
        String s = getOrientation(); 
        if ( s == null ) return "Portrait"; 
        else if ( s.equalsIgnoreCase("landscape")) return "Landscape"; 
        else return "Portrait"; 
    }        
    
    public final ReportColumn getRowField() {
        return findColumn( getRowGroup() ); 
    }
    public final ReportColumn getColumnField() {
        return findColumn( getColumnGroup() ); 
    }  
    public final ReportColumn getMeasureField() {
        return findColumn( getMeasure() ); 
    } 
    
    public FieldProperty getFieldProperty( String name ) {
        if ( name == null ) return new FieldProperty( name );  
        if ( name.equals( getRowGroup())) return rowGroup; 
        else if ( name.equals( getColumnGroup())) return colGroup; 
        else if ( name.equals( getMeasure())) return measure;
        else return new FieldProperty( name ); 
    }
    
    public class FieldProperty {
        
        private String name; 
        private String caption;
        private String alignment;
        private String headerAlignment;
        
        public FieldProperty( String name ) {
            this.name = name; 
        }
        
        public String getName() { return name; } 
        
        public String getCaption() { return caption; } 
        public void setCaption( String caption ) {  
            this.caption = caption;
        }
        
        public String getAlignment() { return alignment; } 
        public void setAlignment( String alignment ) {
            this.alignment = alignment; 
        }
        
        public String getHeaderAlignment() { return headerAlignment; } 
        public void setHeaderAlignment( String headerAlignment ) {
            this.headerAlignment = headerAlignment; 
        }
    }
}
