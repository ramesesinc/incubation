/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import com.rameses.osiris3.schema.SchemaViewFieldFilter.ExtendedNameViewFieldFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell
 */
public class SchemaView extends AbstractSchemaView {
    
    private HashMap<String,SchemaViewField> fieldSet = new HashMap();
    private List<SchemaViewField> fields = new ArrayList();
    
    //this contains the flattened out one to one relations
    private List<SchemaRelation> oneToOneRelations;
    private List<SchemaRelation> manyToOneRelations;
    private List<SchemaRelation> oneToManyRelations;
    
    
    SchemaView(SchemaElement elem) {
        super(elem.getName(), elem);
        super.setRootView(this);
    }

    //if true the name is not a duplicate. If false field already exists
    boolean addField( SchemaViewField vw ) {
        fields.add(vw);
        if( !fieldSet.containsKey(vw.getExtendedName()) ) {
            fieldSet.put( vw.getExtendedName(), vw );
            return true;
        }
        return false;
    }
    
    public List<SchemaViewField> getFields() {
        return fields;
    }
 
    
    private List<SchemaViewField> _allFields;
    public List<SchemaViewField> findAllFields() {
        if(_allFields == null ) {
            _allFields = findAllFields(".*");
        }
        return _allFields;
    }
    
    public List<SchemaViewField> findAllFields( String fieldNames ) {
        ExtendedNameViewFieldFilter mf = new ExtendedNameViewFieldFilter(fieldNames);
        return findAllFields(mf);
    }

    /**
     * findAll returns all fields that match the criteria. Duplicates will not be
     * included
     * @param filter - specify a FindFilter to limit the results
     * @return 
     */
    public List<SchemaViewField> findAllFields( SchemaViewFieldFilter filter ) {
        List<SchemaViewField>  results = new ArrayList();
        for(SchemaViewField vw: getFields()) {
            if( filter.accept(vw) ) {
                results.add(vw);
            }
        }
        return results;
    }
    
    public boolean fieldExists( String name ) {
        return fieldSet.containsKey(name);
    }
    
    public SchemaViewField getField(String name) {
        name = name.replace(".", "_");
        return fieldSet.get(name);
    }
    
    /***
     * This is the method being used to get the schema. ignore the schemaview field
     */ 
    public Map getSchema() {
        Map map = new HashMap();
        map.put("name", this.getName());
        List<Map> flds = new ArrayList();
        for( SchemaViewField vf: this.findAllFields() ) {
            if(vf instanceof SchemaViewRelationField ) {
                SchemaViewRelationField svrf = (SchemaViewRelationField)vf;
                //if(svrf.getJoinType()!=null && !svrf.getJoinType().equals(JoinTypes.MANY_TO_ONE ))  continue;
                Map fld = new HashMap();
                fld.put( "name", svrf.getTargetView().getName() );
                fld.put( "ref", svrf.getTargetView().getElement().getName() );
                fld.put( "jointype", svrf.getTargetJoinType() );
                flds.add( fld );
            }
            else {
                Map fld = new HashMap();
                fld.putAll( vf.getSchemaField().toMap() );
                fld.put("name", vf.getMapname());
                fld.put("extname", vf.getExtendedName());
                flds.add( fld );
            }
        }
        map.put("columns", flds);
        //load one to many children.
        if( this.getElement().getOneToManyRelationships().size() > 0  ) {
            List list = new ArrayList();
            for( SchemaRelation sr: this.getElement().getOneToManyRelationships() ) {
                Map m = new HashMap();
                m.put("name", sr.getName());
                m.put("ref", sr.getRef());
                m.put("required", sr.isRequired() );
                
                //okay we need to get the schema element columns:
                SchemaElement subElement = this.getElement().getSchema().getSchemaManager().getElement(sr.getRef());
                Map subSchema = subElement.createView().getSchema();
                m.put( "columns", subSchema.get("columns"));
                m.put( "items", subSchema.get("items"));
                list.add(m);
            }
            map.put( "items", list );
        }
        
        return map;
        
    }    


   
}
