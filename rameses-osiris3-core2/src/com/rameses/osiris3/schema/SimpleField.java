/*
 * SchemaField.java
 *
 * Created on August 12, 2010, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author elmo
 */
public class SimpleField extends SchemaField implements SimpleFieldTypes {
    
    private String name;
    private boolean required;
    private String type;
    
    
    private Class dataTypeClass;
    
    /**
     * if this is provided, this will override the bean mapping.
     */
    private String mapfield;

    public SimpleField() {
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String dataType) {
        this.type = dataType;
        initDataTypeClass(this.type);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    
    private String _description;
    public String toString() {
        if(_description==null) {
            _description = (name==null ? "_unnamed" : name)  
            + (type!=null ? "[" + type + "]":" ")
            + ( required ? " required " : "" );
        }
        return _description;
    }

    public String getMapfield() {
        return mapfield;
    }

    public void setMapfield(String mapfield) {
        this.mapfield = mapfield;
    }

    public boolean isPrimary() {
        try {
            Object prim = super.getProperties().get("primary");
            if( prim == null ) return false;
            if(prim instanceof Boolean) return ((Boolean)prim).booleanValue();
            String s = prim + "";
            return Boolean.parseBoolean(s);
        }
        catch(Exception e) {
            return false;
        }
    }
    
    public String getFieldname() {
        String fieldName = (String)super.getProperties().get("fieldname");
        if(fieldName==null) return name;
        return fieldName;
    }
    
    public void setFieldname(String name) {
        super.getProperties().put("fieldname", name);  
    }
    
    
    private void initDataTypeClass(String dtype) {
        if( dtype== null || dtype.trim().length() == 0 ) {
            //if datatype not specified, the default is Object. 
            dtype = SimpleFieldTypes.OBJECT;
        }
        if( dtype.equalsIgnoreCase(SimpleFieldTypes.STRING) ) {
            dataTypeClass= String.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.TIMESTAMP) ) {
            dataTypeClass= Timestamp.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.DATE) ) {
            dataTypeClass= Date.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.DECIMAL) ) {
            dataTypeClass= BigDecimal.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.DOUBLE) ) {
            dataTypeClass= Double.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.INTEGER) ) {
            dataTypeClass= Integer.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.BOOLEAN) ) {
            dataTypeClass= Integer.class;
        } else if( dtype.equalsIgnoreCase(SimpleFieldTypes.LONG) ) {
            dataTypeClass= Long.class;
        } else {
            dataTypeClass = Object.class;
        }
    }
    
    public Class getDataTypeClass() {
        if( dataTypeClass==null) {
            initDataTypeClass( getType() );
        }
        return dataTypeClass;
    }
    
    //this verifies the data. Put the other values here.
    public void verify( Object val  )  throws Exception {
        if( isPrimary() ) return;   //for primary keys do nothing.
        if( val == null  ) {
            if( isRequired() ) {
                throw new Exception(  " is required.");
            }
        }
        else if( getDataTypeClass() == Object.class) {
            //do nothing...
        }
        else if( val.getClass() != getDataTypeClass()) {
            throw new Exception( " data type is incorrect.");
        }
        //verify all other things
        if( getDataTypeClass() == String.class) {
            String mask = (String)getProperty("mask");
            if(mask!=null){
                if(  !val.toString().matches(mask)) {
                    throw new Exception(  " value does not match mask pattern");
                }       
            }
        }
    }
    
    public Object getDefaultValue() {
        return null;
        //returns the default value specified.
    }
    
}
