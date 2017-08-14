/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.report;

import java.util.ArrayList;
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
    
    private List<FieldProperty> rowGroups;
    private List<FieldProperty> columnGroups;
    private List<FieldProperty> measureGroups;

    public CrosstabReport() {
        rowGroups = new ArrayList();
        columnGroups = new ArrayList();
        measureGroups = new ArrayList();
    }
    
    public List<FieldProperty> getRowGroups() { 
        return rowGroups; 
    } 
    public FieldProperty addRowGroup( String name ) {
        return addRowGroup( name, null ); 
    }
    public FieldProperty addRowGroup( String name, String caption ) {
        FieldProperty fp = findFieldProperty( getRowGroups(), name ); 
        if ( fp == null ) { 
            fp = new FieldProperty( name );
            getRowGroups().add( fp ); 
        } 
        fp.setCaption( caption ); 
        return fp; 
    } 
    
    public List<FieldProperty> getColumnGroups() { 
        return columnGroups; 
    } 
    public FieldProperty addColumnGroup( String name ) {
        return addColumnGroup( name, null ); 
    }
    public FieldProperty addColumnGroup( String name, String caption ) {
        FieldProperty fp = findFieldProperty( getColumnGroups(), name ); 
        if ( fp == null ) { 
            fp = new FieldProperty( name );
            getColumnGroups().add( fp ); 
        } 
        fp.setCaption( caption ); 
        return fp; 
    }        

    public List<FieldProperty> getMeasures() { 
        return measureGroups; 
    }
    public FieldProperty addMeasure( String name ) {
        return addMeasure( name, null ); 
    }
    public FieldProperty addMeasure( String name, String caption ) {
        FieldProperty fp = findFieldProperty( getMeasures(), name ); 
        if ( fp == null ) { 
            fp = new FieldProperty( name );
            getMeasures().add( fp ); 
        } 
        fp.setCaption( caption ); 
        return fp; 
    } 
    
    private FieldProperty findFieldProperty( List<FieldProperty> fields, String name ) { 
        if ( name == null ) return null;
        
        for (int i=0; i<fields.size(); i++) { 
            FieldProperty fp = fields.get(i);
            if (name.equals( fp.getName())) {
                return fp;
            }
        }
        return null; 
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

    public FieldProperty getRowGroup( String name ) {
        if ( name == null ) return null; 

        for ( FieldProperty fp : getRowGroups() ) {
            if ( name.equals(fp.getName())) {
                return fp; 
            }
        }
        return null;
    }
    public FieldProperty getColumnGroup( String name ) {
        if ( name == null ) return null; 

        for ( FieldProperty fp : getColumnGroups() ) {
            if ( name.equals(fp.getName())) {
                return fp; 
            }
        }
        return null;
    }  
    public FieldProperty getMeasureGroup( String name ) {
        if ( name == null ) return null; 

        for ( FieldProperty fp : getMeasures() ) {
            if ( name.equals(fp.getName())) {
                return fp; 
            }
        }
        return null;
    }  
    
    public class FieldProperty {
        
        private String name; 
        private String caption;
        private String alignment;
        private String headerAlignment;
        private String pattern;
        
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
        
        public String getPattern() { return pattern; } 
        public void setPattern( String pattern ) {
            this.pattern = pattern; 
        }
    }
}
