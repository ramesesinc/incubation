/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import com.rameses.osiris3.schema.SchemaViewFieldFilter.ExtendedNameViewFieldFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    
    

   
}
