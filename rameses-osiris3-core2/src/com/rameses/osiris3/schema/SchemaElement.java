/*
 * Schema.java
 *
 * Created on August 12, 2010, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.rameses.osiris3.persistence.JoinTypes;

public class SchemaElement implements Serializable {
    
    private Schema schema;
    private String name;
    private List<SchemaField> fields = new ArrayList();
    private Map<String,SchemaField> fieldMap = new Hashtable();
    private Map properties = new HashMap();
    
    private List<SimpleField> simpleFields;
    private List<ComplexField> complexFields;
    private List<SimpleField> primaryKeys;
    
    private List<ComplexField> serializedFields;
    
    private SchemaRelation extendedRelationship;
    private List<SchemaRelation> oneToManyRelationships;
    private List<SchemaRelation> oneToOneRelationships;
    private List<SchemaRelation> manyToOneRelationships;
    
    //only one inverse join. reserved for parent
    private List<SchemaRelation> inverseRelationships;
    
    /** Creates a new instance of Schema */
    public SchemaElement(Schema schema) {
        this.schema = schema;
    }
    
    /**
     * method accessed by the parser.
     */
    public void addSchemaField(SchemaField fld) {
        fields.add( fld );
        if( fld.getName()!=null ) {
            fieldMap.put(fld.getName(), fld);
        }  
        fld.setElement(this);
    }
    
    public Map getProperties() {
        return properties;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Schema getSchema() {
        return schema;
    }
    
    public List<SchemaField> getFields() {
        return fields;
    }
    
    
    public SchemaField getField(String name) {
        return fieldMap.get(name);
    }
    
    public List<SimpleField> getSimpleFields() {
        if(simpleFields==null) {
            simpleFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof SimpleField) simpleFields.add((SimpleField) sf);
            }
        }
        return simpleFields;
    }
    
    public List<SimpleField> getPrimaryKeys() {
        if(primaryKeys==null) {
            primaryKeys = new ArrayList();
            for(SimpleField sf: this.getSimpleFields()) {
                if(!sf.isPrimary()) continue;
                primaryKeys.add(sf);
            }
        }
        return primaryKeys;
    }
    
    public List<ComplexField> getComplexFields() {
        if(complexFields==null) {
            complexFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof ComplexField) complexFields.add((ComplexField) sf);
            }
        }
        return complexFields;
    }
    
    public List<ComplexField> getSerializedFields() {
        if(serializedFields==null) {
            serializedFields = new ArrayList();
            for( ComplexField cf : getComplexFields() ) {
                if(cf.getSerializer()==null) continue;
                if(cf.getSerializer().trim().length()==0) continue;
                serializedFields.add(cf);
            }
        }
        return serializedFields;
    }
    
    public Object getProperty(String name) {
        return this.properties.get( name );
    }
    
    public String getExtends() {
        String ext = (String) this.properties.get("extends");
        if( ext == null ) return null;
        if( ext.indexOf(":")<=0) return ext + ":" + ext;
        return ext;
    }
    
    public String getTablename() {
        return (String) this.properties.get("tablename");
    }
    
    public String getAdapter() {
        return (String) this.properties.get("adapter");
    }
    
    public SchemaElement getExtendedElement() {
        if(this.getExtends()==null) return null;
        return schema.getSchemaManager().getElement(this.getExtends());
    }
    
    /**
     * The schema view represents the complete instance of the schema including 
     * the links and the join tables. 
     * @return 
     */
    private final static Object lock = new Object();
    private SchemaView schemaView;
    public SchemaView createView() {
        synchronized(lock) {
            if( schemaView == null ) {
                schemaView = new SchemaView(this);
                fetchAllFields( schemaView, schemaView, null );
            }
        }
        return schemaView;
    }
    
    /**
     * 
     * @param rootVw - the main view
     * @param lsvw - the immediate view that relates to the field. 
     */
    private void fetchAllFields(SchemaView rootVw, AbstractSchemaView currentVw, String prefix) {
        for( SimpleField sf: this.getSimpleFields() ) {
            rootVw.addField(new SchemaViewField(sf, rootVw, currentVw));
        }
        for( ComplexField cf: this.getSerializedFields() ) {
            SchemaViewField vf = new SchemaViewField(cf, rootVw, currentVw);
            vf.setSerialized(true);
            rootVw.addField(vf);
        }
        
        SchemaElement extElement = this.getExtendedElement();
        if( extElement!=null ) {
            String n = extElement.getName();
            LinkedSchemaView targetVw = new LinkedSchemaView( n, extElement, rootVw, currentVw, JoinTypes.EXTENDED, true, prefix );
            //loop on primary keys. This assumes that it has the same order as the extended element.
            int isrc = this.getPrimaryKeys().size();
            int itgt = extElement.getPrimaryKeys().size();
            if( isrc!=itgt ) {
                throw new RuntimeException( "Error on fetchAllFields. extends is ignored because the primary keys do not match. " + extElement.getName() );
            }
            for( int i=0; i<isrc; i++) {
                SimpleField _sf = this.getPrimaryKeys().get(i);
                SimpleField _tf = extElement.getPrimaryKeys().get(i);
                SchemaViewRelationField rf = new SchemaViewRelationField(_sf, rootVw, currentVw, _tf, targetVw);    
                targetVw.addRelationField(rf);
            }
            currentVw.setExtendsView(targetVw);
            extElement.fetchAllFields(rootVw, targetVw, prefix );
        }
        
        List<SchemaRelation> relList = new ArrayList();
        relList.addAll( this.getOneToOneRelationships() );
        relList.addAll( this.getManyToOneRelationships() );
        //extract all fields related.
        for( SchemaRelation sr: relList  ) {
            if( sr.getJointype().equals(JoinTypes.INVERSE)) continue;
            SchemaElement targetElem = sr.getLinkedElement();
            LinkedSchemaView targetVw = new LinkedSchemaView(sr.getName(), targetElem, rootVw, currentVw, sr.getJointype(), sr.isRequired(), prefix  );
            
            for( RelationKey rk: sr.getRelationKeys() ) {
                SimpleField tf = (SimpleField)sr.getLinkedElement().getField(rk.getTarget());
                if( tf == null ) 
                    throw new RuntimeException("SchemaElement.fetchAllFields error. Target field not found");
                if(! (tf instanceof SimpleField) ) 
                    throw new RuntimeException("SchemaElement.fetchAllFields error. Target field must be a simple field");
                
                //build the simple field
                SimpleField sf = new SimpleField();
                sf.setElement(currentVw.getElement());
                sf.setName(rk.getField());
                sf.setFieldname(rk.getField());
                sf.setType( tf.getType() );
                SchemaViewRelationField rf = new SchemaViewRelationField(sf, rootVw, currentVw,tf, targetVw);
                rootVw.addField( rf );
                if( sr.getJointype().equals(JoinTypes.ONE_TO_ONE) ) {
                    currentVw.addOneToOneView( targetVw );
                }
                else {
                    currentVw.addManyToOneView( targetVw );
                }
                targetVw.addRelationField(rf);
            };
            targetElem.fetchAllFields(rootVw, targetVw, targetVw.getName());
        }
        
        // Process the inverse relationship
        List<SchemaRelation> inverseList = getInverseRelationships();
        for( SchemaRelation ir : inverseList ) {
            SchemaElement targetElem = ir.getLinkedElement();
            JoinLink jl = new JoinLink(targetElem, ir.getName());
            jl.setRequired(ir.isRequired());
            jl.setJoinType(jl.getJoinType());
            jl.setRelationKeys(ir.getRelationKeys());
            rootVw.addInverseJoin(jl);
        }
        
        //load the one to many relationships
        for( SchemaRelation sr: this.getOneToManyRelationships() ) {
            rootVw.addOneToManyLink(new OneToManyLink(sr.getName(), prefix, this, sr));
        }
        
    }    
    
    private void buildRelations(String joinType, List schemaRelations) {
        for(ComplexField cf: this.getComplexFields()) {
            if(cf.getJoinType()==null ) continue;
            if(!cf.getJoinType().toLowerCase().equals(joinType) ) continue;
            String ref = cf.getRef();
            if(ref==null || ref.trim().length()==0) {
                System.out.println("SchemaElement.buildRelations warning. ref not specified");
                continue;
            }
            SchemaElement elem = this.schema.getSchemaManager().getElement(ref);
            SchemaRelation sr = new SchemaRelation(this, cf);
            sr.setLinkedElement(elem);
            schemaRelations.add(sr);
        }
    }
    
    public List<SchemaRelation> getOneToManyRelationships() {
        if( oneToManyRelationships == null ) {
            oneToManyRelationships = new ArrayList();
            buildRelations( JoinTypes.ONE_TO_MANY, oneToManyRelationships );
        }
        return oneToManyRelationships;
    }
    
    public List<SchemaRelation> getOneToOneRelationships() {
        if( oneToOneRelationships == null ) {
            oneToOneRelationships = new ArrayList();
            buildRelations( JoinTypes.ONE_TO_ONE, oneToOneRelationships );
        }
        return oneToOneRelationships;
    }
    
    public List<SchemaRelation> getManyToOneRelationships() {
        if( manyToOneRelationships == null ) {
            manyToOneRelationships = new ArrayList();
            buildRelations( JoinTypes.MANY_TO_ONE, manyToOneRelationships );
        }
        return manyToOneRelationships;
    }
    
    public boolean hasExtends() {
        return (this.getExtends()!=null && this.getExtends().trim().length()>0);
    }

    public List<SchemaRelation> getInverseRelationships() {
        if( inverseRelationships == null ) {
            inverseRelationships = new ArrayList();
            buildRelations( JoinTypes.INVERSE, inverseRelationships );
        }
        return inverseRelationships;
    }
    
    
}
