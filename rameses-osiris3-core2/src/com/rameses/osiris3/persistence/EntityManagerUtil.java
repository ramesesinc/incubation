/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaField;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlQuery;
import com.rameses.osiris3.sql.SqlUnit;
import com.rameses.util.ObjectSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell
 */
public class EntityManagerUtil {
    
    private static Map<String, SqlUnitModel> sqlUnits = new HashMap();
    private SqlContext sqlContext;
    private SqlDialect dialect;
    private SqlDialectModelBuilder builder = new SqlDialectModelBuilder();
            
    private boolean debug = false;
    
    public EntityManagerUtil( SqlContext ctx, boolean debug) {
        this.dialect = ctx.getDialect();
        this.sqlContext = ctx;
        this.debug = debug;
        if( this.dialect == null )
            throw new RuntimeException("PersistenceUtil init error. No dialect defined");
    }
    
    private void exec( SqlUnit squ, EntityManagerModel eModel ) throws Exception {
        SqlExecutor sqe = sqlContext.createExecutor(squ);
        SchemaElement elem = eModel.getElement();
        Map allData = eModel.getAllData();
        for( Object n: squ.getParamNames() ) {
            String sname = n.toString();
            Object val = DataUtil.getData(allData, sname);
            //find also from finders 
            
            SchemaField sf = elem.getField(sname);
            //check first the data type. we have to correct it in its proper format. 
            if( sf !=null && ( sf instanceof ComplexField ) ) {
                ComplexField cf = (ComplexField)sf;
                if( cf.getSerializer()!=null ) {
                    ObjectSerializer sr = ObjectSerializer.getInstance();
                    val = sr.toString(val);
                }
            } 
            sqe.setParameter(sname, val);
        }
        if(debug) {
            System.out.println(sqe.getStatement());
            int i = 0;
            for(Object s: sqe.getParameterNames()) {
                System.out.println("param->"+s+ "="+ sqe.getParameterValues().get(s) );
            }
        }
        sqe.execute();
    }
    
    private Object execQuery( SqlUnit squ, EntityManagerModel eModel, List cols, boolean singleResult ) throws Exception {
        SqlQuery sq = sqlContext.createQuery(squ);
        Map allData = eModel.getAllData();
        for( Object n: squ.getParamNames() ) {
            String sname = n.toString();
            sq.setParameter(sname, DataUtil.getData(allData, sname));
        }
        if(eModel.getStart()>=0 && eModel.getLimit()>0) {
            sq.setParameter("_start", eModel.getStart());
            sq.setParameter("_limit", eModel.getLimit());
        }
        if(debug) {
            System.out.println(sq.getStatement());
            int i = 0;
            for(Object s: sq.getParameterNames()) {
                System.out.println("param->"+s+ "="+ sq.getParameterValues().get(s) );
            }
        }
        //if( vars!=null ) se.setVars(vars); 
        if( singleResult ) {
            DataMapFetchHandler dm = new DataMapFetchHandler(cols);
            sq.setFetchHandler(dm);
            return sq.getSingleResult();
        }
        else {
            DataListFetchHandler dh = new DataListFetchHandler(cols);
            sq.setFetchHandler(dh);
            return sq.getResultList();
        }
    }
    
    public static class SqlUnitModel {
        private SqlUnit sqlUnit;
        private SqlDialectModel model;
        private List columns;
        public SqlUnitModel( SqlUnit u, SqlDialectModel m, List cols) {
            this.sqlUnit = u;
            this.model = m;
            this.columns = cols;
        }
        public SqlUnit getSqlUnit() {
            return sqlUnit;
        }
        public SqlDialectModel getModel() {
            return model;
        }
        public List getColumns() {
            return columns;
        }
    }
    
    private SqlUnitModel getSqlUnitCache( EntityManagerModel eModel) throws Exception {
        String id = eModel.getId();
        //System.out.println("id is " + id);
        SqlUnitModel sqm = sqlUnits.get(id);
        if( sqm == null ) {
            String action = eModel.getAction();
            if( action.equals("create")) {
                SqlDialectModel model = builder.buildCreateModel(eModel);
                SqlUnit squ = dialect.getCreateSqlUnit(model);
                sqm = new SqlUnitModel(squ, model,null);
            }
            else if( action.equals("update") ) {
                SqlDialectModel model = builder.buildUpdateModel(eModel);
                SqlUnit squ = dialect.getUpdateSqlUnit(model);
                sqm = new SqlUnitModel(squ, model,null);
            }
            else if( action.equals("delete") ) {
                SqlDialectModel model = builder.buildDeleteModel(eModel);
                SqlUnit squ = dialect.getDeleteSqlUnit(model);
                sqm = new SqlUnitModel(squ, model,null);
            }
            else if( action.equals("read")) {
                List columns = new ArrayList();
                SqlDialectModel model = builder.buildSelectModel(eModel, columns);
                SqlUnit squ = dialect.getSelectSqlUnit(model);
                sqm = new SqlUnitModel(squ, model,columns);
                
            }
            else if( action.equals("select")) {
                List columns = new ArrayList();
                SqlDialectModel model = builder.buildSelectModel(eModel, columns);
                SqlUnit squ = dialect.getSelectSqlUnit(model);
                sqm = new SqlUnitModel(squ, model, columns);
            }
            sqlUnits.put(id, sqm);
        }    
        return sqm;
    }
    
    /***
     * merge used to be the update method before.
     * read the data first before applying the changes
     */ 
    public Map merge(Map data, SchemaElement elem ) throws Exception {
        throw new Exception("Merge not yet supported");
    }
    
    public Map create(EntityManagerModel eModel ) throws Exception {
        SchemaElement elem = eModel.getElement();
        SchemaManager sm = elem.getSchema().getSchemaManager();
        
        if( elem.getExtends()!=null ) {
            String ext = elem.getExtends();
            SchemaElement extObj = sm.getElement(ext);
            if( extObj==null) throw new Exception("Error PersistenceUtil.create. Extends "+ext + " not found");
            EntityManagerModel extModel = new EntityManagerModel();
            extModel.setData(eModel.getData());
            extModel.setAction("create");
            extModel.setElement(extObj);
            create( extModel );
        }
        
        //build the model first before passing to dialect
        
        SqlUnitModel squ = getSqlUnitCache(eModel);
        exec(squ.getSqlUnit(), eModel);
        
        //execute also if the element has children
        List<ComplexField> oneToOne = new ArrayList();
        List<ComplexField> oneToMany = new ArrayList();
        List<ComplexField> manyToOne = new ArrayList();
        
        for( ComplexField cf: elem.getComplexFields() ) {
            if( cf.getSerializer() !=null ) continue;
            String joinType = cf.getJoinType();
            if( joinType == null || joinType.trim().length() == 0 ) continue;
            if( cf.getRef()==null) {
                throw new Exception("PersistenceUtil.create error. Ref not found for complex field "+cf.getName());
            }
            if( joinType.matches("one-to-one")) oneToOne.add(cf);
            if( joinType.matches("one-to-many")) oneToMany.add(cf);
            if( joinType.matches("many-to-one")) manyToOne.add(cf);
        };

        //save each field
        for(ComplexField cf: oneToOne) {
            SchemaElement itemElem = sm.getElement(cf.getRef());
            //feed the parent's objid into the item
            Map item =(Map)DataUtil.getData( eModel.getData(), cf.getName() );
            
            //we also need to populate the keys from the parent to the linked table before saving...
            /*
            for( RelationKey rk: cf.getRelationKeys()) {
                SchemaUtil.putData(item, rk.getTarget(), SchemaUtil.getData(eModel.getData(), rk.getField()) );
            }
            */
            
            //create the related element first.
            EntityManagerModel itemModel = new EntityManagerModel();
            itemModel.setElement(itemElem);
            itemModel.setAction("create");
            itemModel.setData(item);
            create( itemModel );

            Map newData = new HashMap();
            //get the related fields from the relationships
            for( RelationKey rk: cf.getRelationKeys() ) {
                DataUtil.putData(newData, rk.getField(), DataUtil.getData(item, rk.getTarget())  );
            }
            Map finders = new HashMap();
            for( SimpleField sf: elem.getSimpleFields() ) {
                if(sf.isPrimary()) {
                    DataUtil.putData(finders, sf.getName(), DataUtil.getData(eModel.getData(), sf.getName()));
                }
            }
            EntityManagerModel em = new EntityManagerModel();
            em.setElement(elem);
            em.setAction("update");
            em.setData(newData);
            em.addFinders( finders );
            update( em );
            //we will update the data here...
            //throw new Exception("the inversekey of linked element is " + SchemaUtil.getData(item, inversekey) );
        }
        
        //one to many
        for(ComplexField cf: oneToMany) {
            SchemaElement itemElem = sm.getElement(cf.getRef());
            //feed the parent's objid into the item
            List<Map> items = DataUtil.getDataList( eModel.getData(), cf.getName() );
            EntityManagerModel itemModel = new EntityManagerModel();
            itemModel.setElement(itemElem);
            itemModel.setAction("create");
            for( Map m : items ) {
                itemModel.setData(m);
                create( itemModel );
            }
        }
        return eModel.getData();
    }
    
    private Map createFinderFromPrimaryKeys(EntityManagerModel eModel) {
        Map finders = new HashMap();
        SchemaElement elem = eModel.getElement();
        Map data = eModel.getData();
        //loop each primary key to load the fields used for finders.
        for( SimpleField sf: elem.getSimpleFields() ) {
            if( sf.isPrimary() ) {
                Object d = data.get(sf.getName());
                finders.put(sf.getName(), d);
            }
        }
        return finders;
    }
    
    public Map update( EntityManagerModel eModel) throws Exception {
        SchemaElement elem = eModel.getElement();
        SchemaManager sm = elem.getSchema().getSchemaManager();
        //update the table first. 
        
        boolean hasAffectedFields = eModel.buildIncludeFieldsForUpdate();
        if( hasAffectedFields ) {
            Map finders = eModel.getFinders();
            if( finders.size() == 0 ) {
                finders.putAll( createFinderFromPrimaryKeys(eModel)) ;
            }
            SqlUnitModel squ = getSqlUnitCache(eModel);
            exec(squ.getSqlUnit(), eModel);
        }
        
        //cascade updates to the extends table if any.
        if( elem.getExtends()!=null ) {
            String ext = elem.getExtends();
            SchemaElement extObj = sm.getElement(ext);
            if( extObj==null) throw new Exception("Error PersistenceUtil.create. Extends "+ext + " not found");
            EntityManagerModel extModel = new EntityManagerModel();
            extModel.setAction("update");
            extModel.setData(eModel.getData());
            extModel.setElement(extObj);
            update(extModel);
        }
        return eModel.getData();        
    }
     
    
    /**
     * 
     * @param eModel
     * @return DataMap containing the data loaded. Normally this will read from the finders
     * but if none is provided, we will select the primary keys of the element and use that.
     */
    public Map read( EntityManagerModel eModel ) throws Exception {
        SchemaElement elem = eModel.getElement();
        SchemaManager sm = elem.getSchema().getSchemaManager();
        Map finders = eModel.getFinders();
        Map data = eModel.getData();
        if(finders.size()==0) {
            finders.putAll(createFinderFromPrimaryKeys(eModel));
        }
        SqlUnitModel squ = getSqlUnitCache(eModel);
        return (Map) execQuery(squ.getSqlUnit(), eModel, squ.getColumns(), true);
    }
    
    /**
     * Delete in order as follows:
     *      lowest child elements
     *      base table
     *      extended tables
     *      e.g. DELETE FROM child WHERE itemid = 
     *       (SELECT parent.objid FROM parent INNER JOIN root ON parent.parentid=root.objid WHERE root.objid=$P{objid} )
     * 
     * @param elem
     * @param data
     * @throws Exception 
     */
    public void delete( EntityManagerModel eModel ) throws Exception {
        SchemaElement elem = eModel.getElement();
        SchemaManager sm = elem.getSchema().getSchemaManager();

        Map finders = eModel.getFinders();
        Map data = eModel.getData();
        if(finders.size()==0) {
            finders.putAll(createFinderFromPrimaryKeys(eModel));
        }
        SqlUnitModel squ = getSqlUnitCache(eModel);
        exec(squ.getSqlUnit(), eModel);
    }
    
    public List getList(EntityManagerModel eModel ) throws Exception {
        SchemaElement elem = eModel.getElement();
        SchemaManager sm = elem.getSchema().getSchemaManager();
        SqlUnitModel squ = getSqlUnitCache(eModel);
        //System.out.println(squ.getSqlUnit().getStatement());
        List list = (List) execQuery(squ.getSqlUnit(), eModel, squ.getColumns(), false);
        return list;
    }
    
}
