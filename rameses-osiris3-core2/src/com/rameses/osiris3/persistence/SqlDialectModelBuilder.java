/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaField;
import com.rameses.osiris3.schema.SimpleField;

import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlDialectModel.Criteria;
import com.rameses.osiris3.sql.SqlDialectModel.Field;
import com.rameses.osiris3.sql.SqlDialectModel.Relationship;
import com.rameses.osiris3.sql.SqlDialectModel.RelationshipKey;
import com.rameses.osiris3.sql.SqlDialectModel.Table;
import java.util.List;

/**
 *
 * @author dell
 */
public class SqlDialectModelBuilder {

    public static SqlDialectModelBuilder instance = new SqlDialectModelBuilder();

    public static SqlDialectModelBuilder getInstance() {
        return instance;
    }

    private SqlDialectModel.Field createFieldFromSimpleField(SimpleField sf) {
        SqlDialectModel.Field f = new SqlDialectModel.Field();
        f.setFieldname(sf.getFieldname());
        f.setName(sf.getName());
        f.setAlias(f.getName());
        f.setPrimary(sf.isPrimary());
        return f;
    }

    //this is applicable only to serializable fields.
    private SqlDialectModel.Field createFieldFromComplexField(ComplexField cf) {
        SqlDialectModel.Field f = new SqlDialectModel.Field();
        f.setFieldname(cf.getFieldname());
        f.setName(cf.getName());
        f.setAlias(cf.getName());
        return f;
    }

    //this is applicable only to serializable fields.
    //this should be done. we have to do this otherwise it wont get the proper embedded object
    private SqlDialectModel.Field createFieldFromRelationKey(RelationKey rk, String embeddedPrefix) {
        SqlDialectModel.Field f = new SqlDialectModel.Field();
        f.setFieldname(rk.getField());
        
        String fName = rk.getTarget();
        if( embeddedPrefix !=null )fName = embeddedPrefix+"_"+fName;
        f.setName(fName);
        f.setAlias(rk.getTarget());
        return f;
    }

    private SqlDialectModel.Table createTable(SchemaElement elem) {
        return new SqlDialectModel.Table(elem.getTablename(), elem.getName());
    }

    private Relationship createRelationshipFromComplexField(ComplexField cf, Table fromTable, String inverseKey) {
        SchemaElement subElement = cf.getElement().getSchema().getSchemaManager().getElement(cf.getRef());
        String joinType = cf.getJoinType();
        Relationship subRel = new Relationship();
        String jType = "INNER";
        if (joinType.matches("one-to-one") && !cf.isRequired()) {
            jType = "LEFT";
        }
        subRel.setJoinType(jType);
        Table tbl = createTable(subElement);
        tbl.setAlias(cf.getName());
        subRel.setJoinTable(tbl);
        for (RelationKey rk : cf.getRelationKeys()) {
            //subRel. add key here
            RelationshipKey relKey = new SqlDialectModel.RelationshipKey();
            Field fromFld = new SqlDialectModel.Field();
            
            //apply inverseKey. If supplied, use it instead of from
            String refFld = rk.getField();
            if( inverseKey!=null && inverseKey.trim().length()>0 ) refFld = inverseKey;
            
            fromFld.setFieldname(refFld);
            fromFld.setName(refFld);
            fromFld.setAlias(refFld);
            //set the from part
            relKey.setFromKey(fromFld);
            relKey.setFromTable(fromTable);

            //set the to part
            Field toFld = new SqlDialectModel.Field();
            toFld.setFieldname(rk.getTarget());
            
            toFld.setName(rk.getTarget());   
            toFld.setAlias(rk.getTarget());
            relKey.setToTable(tbl);
            relKey.setToKey(toFld);
            subRel.addKey(relKey);
        }
        return subRel;
    }

    private Relationship createRelationshipFromExtends(SchemaElement element, Table fromTable) {
        String ext = element.getExtends();
        SchemaElement extElem = element.getSchema().getSchemaManager().getElement(ext);
        Relationship rel = new Relationship();
        Table toTable = createTable(extElem);
        rel.setJoinTable(toTable);
        rel.setJoinType("INNER");
        for (SimpleField sf : element.getSimpleFields()) {
            if (sf.isPrimary()) {
                RelationshipKey relKey = new RelationshipKey();
                Field fromFld = new Field();
                fromFld.setAlias(sf.getName());
                fromFld.setName(sf.getName());
                fromFld.setFieldname(sf.getFieldname());
                relKey.setFromKey(fromFld);
                relKey.setFromTable(fromTable);

                Field toFld = new Field();
                toFld.setAlias(sf.getName());
                toFld.setName(sf.getName());
                toFld.setFieldname(sf.getFieldname());
                relKey.setToKey(toFld);
                relKey.setToTable(toTable);
                rel.addKey(relKey);
            }
        }
        return rel;
    }

    private void setBaseTable(EntityManagerModel eModel, SqlDialectModel model) {
        SqlDialectModel.Table table = createTable(eModel.getElement());
        model.setBaseTable(table);
    }

    //this methid collects all fields in the base table.
    private void collectFieldsForPersistence(EntityManagerModel eModel, SqlDialectModel model) {
        SchemaElement elem = eModel.getElement();
        String includeFields = eModel.getIncludeFields();
        String excludeFields = eModel.getExcludeFields();
        String action = eModel.getAction();

        boolean includePrimKeys = (action.equals("create")) ? true : false;
        for (SchemaField sf : elem.getFields()) {
            if (includeFields != null && !sf.getName().matches(includeFields)) continue;
            if (excludeFields != null && sf.getName().matches(excludeFields)) continue;   
                
            if (sf instanceof SimpleField) {
                SimpleField ssf = (SimpleField) sf;
                if (ssf.isPrimary() && !includePrimKeys) continue;
                model.addField(createFieldFromSimpleField(ssf));
            } 
            else if (sf instanceof ComplexField) {
                ComplexField cf = (ComplexField) sf;
                if (cf.getSerializer() != null) {
                    model.addField(createFieldFromComplexField(cf));
                } 
                else {
                    String joinType = cf.getJoinType();
                    if( joinType.equals("many-to-one") 
                        || (joinType.equals("one-to-one")&& action.equals("update")) ) {
                        for (RelationKey rk : cf.getRelationKeys()) {
                            model.addField(createFieldFromRelationKey(rk, cf.getName()));
                        }
                    }
                }
            }
        }    
    }
    
    private void collectFields(FieldCollector fc, SchemaElement element, SqlDialectModel.Table table,
            SqlDialectModel model, Relationship relationship, SelectFields selFld, String embeddedPrefix) {
        //add the relationship if any...
        for (SchemaField f : element.getFields()) {
            if (f instanceof SimpleField) {
                SimpleField sf = (SimpleField) f;
                //do not display primary keys if it is extended. If it is extended embeddedPrefix is null
                if (relationship != null && sf.isPrimary() && embeddedPrefix==null ) {
                    continue;  
                }
                if (selFld.hasSimpleField(sf.getName()) == false) {
                    continue;
                }
                Field ff = createFieldFromSimpleField(sf);
                ff.setTable(table);
                ff.setEmbeddedPrefix(embeddedPrefix);
                fc.add(ff, f);
            } else if (f instanceof ComplexField) {
                ComplexField cf = (ComplexField) f;
                if (cf.getSerializer() != null) {
                    if (!selFld.hasSimpleField(cf.getName())) {
                        continue;
                    }
                    Field ff = createFieldFromComplexField(cf);
                    ff.setTable(table);
                    ff.setEmbeddedPrefix(embeddedPrefix);
                    fc.add(ff, f);
                } 
                else {
                    SelectFields selFldSubset = selFld.getComplexField(f.getName());
                    if( selFldSubset == null ) continue;
                    //applied only for one-to-one or many-to-one because one to many is a separate fetch
                    String joinType = cf.getJoinType();
                    if( joinType == null ) continue;
                    if( joinType.matches("one-to-many"))continue;
                    
                    String ref = cf.getRef();
                    SchemaElement subElement = element.getSchema().getSchemaManager().getElement(ref);
                    //check first in the parent element if the complex field exists
                    boolean testExist = selFldSubset.checkExistField(subElement);
                    if(!testExist) continue;
                    String inverseKey = cf.getInversekey();
                    
                    Relationship subRel = createRelationshipFromComplexField(cf, table, inverseKey);
                    model.addRelationship(subRel);
                    String pref = cf.getName();
                    if (embeddedPrefix != null) {
                        pref = embeddedPrefix + "_" + pref;
                    }
                    collectFields(fc, subElement, subRel.getJoinTable(), model, subRel, selFldSubset, pref);
                }
            }
        }
        if (element.getExtends() != null) {
            String ext = element.getExtends();
            SchemaElement extElem = element.getSchema().getSchemaManager().getElement(ext);
            boolean hasFields = selFld.checkExistField(extElem);
            if (hasFields) {
                Relationship rel = createRelationshipFromExtends(element, table);
                model.addRelationship(rel);
                collectFields(fc, extElem, rel.getJoinTable(), model, rel, selFld, null);
            }
        }
    }
    
    public static interface FieldCollector {
        void add(Field field, SchemaField sf);
    }
    
    private void collectCriteria(EntityManagerModel eModel, final SqlDialectModel sqlModel) {
        //loop thru finders first
        FieldCollector fc = new FieldCollector() {
            public void add(Field field, SchemaField sf) {
                String fName = field.getName();
                if( field.getEmbeddedPrefix()!=null ) fName = field.getEmbeddedPrefix()+"_"+fName;
                String key = fName.trim().replace("_", ".");
                Criteria crit = new SqlDialectModel.Criteria();
                crit.addField(key, field);
                crit.setExpr( "@@[" + key + "]=$P{" + fName +"}" );
                sqlModel.addCriteria(crit);
            }
        };
        collectFields(fc, eModel.getElement(), sqlModel.getBaseTable(), sqlModel, null, eModel.buildFinderFields(), null);

        for( FilterCriteria fcr: eModel.getFilters() ) {
            final Criteria criteria = new SqlDialectModel.Criteria();
            criteria.setExpr( fcr.getExpr() );
            sqlModel.addCriteria(criteria);
            FieldCollector fc2 = new FieldCollector() {
                public void add(Field field, SchemaField sf) {
                    String fName = field.getName();
                    if( field.getEmbeddedPrefix()!=null ) fName = field.getEmbeddedPrefix()+"_"+fName;    
                    criteria.addField(fName.replace("_", "."), field);
                }
            };
            collectFields( fc2, eModel.getElement(), sqlModel.getBaseTable(), sqlModel, null, fcr.buildFields(), null );
        }
    }

    public SqlDialectModel buildCreateModel(EntityManagerModel eModel) throws Exception {
        SqlDialectModel sqlModel = new SqlDialectModel();
        setBaseTable(eModel, sqlModel);
        collectFieldsForPersistence(eModel, sqlModel);
        return sqlModel;
    }

    public SqlDialectModel buildUpdateModel(EntityManagerModel eModel) throws Exception {
        SqlDialectModel sqlModel = new SqlDialectModel();
        setBaseTable(eModel, sqlModel);
        collectFieldsForPersistence(eModel, sqlModel);
        collectCriteria( eModel, sqlModel );
        return sqlModel;
    }
    
    public SqlDialectModel buildSelectModel(EntityManagerModel eModel, final List columns) throws Exception {
        final SqlDialectModel sqlModel = new SqlDialectModel();
        setBaseTable(eModel, sqlModel);
        sqlModel.setStart(eModel.getStart());
        sqlModel.setLimit(eModel.getLimit());
        FieldCollector fc = new FieldCollector() {
            public void add(Field field, SchemaField sf) {
                sqlModel.addField(field);
                columns.add(sf.toMap());
            }
        };
        collectFields(fc, eModel.getElement(), sqlModel.getBaseTable(), sqlModel, null, eModel.buildSelectFields(), null);
        collectCriteria( eModel, sqlModel );
        return sqlModel;
    }

    public SqlDialectModel buildDeleteModel(EntityManagerModel eModel) throws Exception {
        SchemaElement elem = eModel.getElement();
        SqlDialectModel sqlModel = new SqlDialectModel();
        setBaseTable(eModel, sqlModel);
        collectCriteria( eModel, sqlModel );
        return sqlModel;
    }
}
