/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import com.rameses.osiris3.schema.SchemaViewFieldFilter.ExtendedNameViewFieldFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dell
 */
public class SchemaView extends AbstractSchemaView {
    
    private HashMap<String,SchemaViewField> fieldSet = new HashMap();
    private List<SchemaViewField> fields = new ArrayList();
    
    //this contains the flattened out one to many relations
    private List<OneToManyLink> oneToManyLinks;
    
    SchemaView(SchemaElement elem) {
        super(elem.getName(), elem);
        super.setRootView(this);
    }

    //if true the name is not a duplicate. If false field already exists
    
    
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
    public static interface SchemaFilter {
        boolean accept( String type, Map m );
    }
    
    public Map getSchema() {
        return getSchema(new SchemaFilter() {
             public boolean accept(String type, Map m) {
                 if(type.equals("field"))
                    return true;
                 else
                    return false;    
            }
        }, new HashSet());
    }
    
    public Map getSchema( String name ) {
        final String n = name.replace(',', '|'); 
        return getSchema(new SchemaFilter() {
             public boolean accept(String type, Map m) { 
                 Object o = m.get("extname");
                 return ( o != null && o.toString().matches(n) );
            }
        }, new HashSet());
    }
    
    public Map getSchema(SchemaFilter filter, Set<SchemaRelation> repeating) {
        Map map = new HashMap();
        map.put("name", this.getName());
        List<Map> flds = new ArrayList();
        for( SchemaViewField vf: this.findAllFields() ) {
            if(vf instanceof SchemaViewRelationField ) {
                SchemaViewRelationField svrf = (SchemaViewRelationField)vf;
                //if(svrf.getJoinType()!=null && !svrf.getJoinType().equals(JoinTypes.MANY_TO_ONE ))  continue;
                Map fld = new HashMap();
                fld.put( "name", svrf.getTargetView().getName() );
                fld.put( "source", vf.getSchemaField().getElement().getName());
                fld.put( "ref", svrf.getTargetView().getElement().getName() );
                fld.put( "jointype", svrf.getTargetJoinType() );
                if( filter.accept("field", fld) ) {
                    flds.add( fld );
                }
            }
            else {
                Map fld = new HashMap();
                fld.putAll( vf.getSchemaField().toMap() );
                fld.put( "source", vf.getSchemaField().getElement().getName());
                fld.put("name", vf.getMapname());
                fld.put("extname", vf.getExtendedName());
                if( filter.accept("field", fld) ) {
                    flds.add( fld );
                }
            }
        }
        map.put("fields", flds);
        //load one to many children.
        if(this.getOneToManyLinks()!=null && this.getOneToManyLinks().size()>0) {
            List list = new ArrayList();
            for( OneToManyLink oml: this.getOneToManyLinks() ) {
                SchemaRelation sr = oml.getRelation();
                if( repeating.contains(sr) ) continue;
                repeating.add( sr ) ;
                Map m = new HashMap();
                m.put("name",oml.getName());
                m.put("prefix", oml.getPrefix());
                m.put("ref", sr.getRef());
                m.put("required",sr.isRequired() );
                //okay we need to get the schema element columns:
                SchemaElement subElement = this.getElement().getSchema().getSchemaManager().getElement(sr.getRef());
                Map subSchema = subElement.createView().getSchema(filter, repeating);
                m.put( "fields", subSchema.get("fields"));
                list.add(m);
            }
            map.put( "items", list );
        }
        return map;
    }    

    public List<OneToManyLink> getOneToManyLinks() {
        return oneToManyLinks;
    }
    
    
    boolean addField( SchemaViewField vw ) {
        fields.add(vw);
        if( !fieldSet.containsKey(vw.getExtendedName()) ) {
            fieldSet.put( vw.getExtendedName(), vw );
            return true;
        }
        return false;
    }
    
    public void addOneToManyLink( OneToManyLink vw ) {
        if(oneToManyLinks==null) oneToManyLinks = new ArrayList();
        oneToManyLinks.add(vw);    
    }
    
    
}
