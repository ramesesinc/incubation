/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.sql.SqlUnitCache;
import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaRelation;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlQuery;
import com.rameses.osiris3.sql.SqlUnit;
import com.rameses.util.ValueUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell
 */
public final class EntityManagerProcessor {
   
    private boolean debug;
    private SqlDialect sqlDialect;
    
    private SqlContext sqlContext;
    
    public EntityManagerProcessor(SqlContext sqc, SqlDialect stmt ) {
        this.sqlContext = sqc;
        this.sqlDialect = stmt;
    }
    
    public void executeUpdate( SqlDialectModel sqm, Map baseData, Map vars  ) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit( sqm, sqlDialect );
        SqlExecutor exec = sqlContext.createExecutor(squ);
        exec.setVars(vars);
        for( Object s : squ.getParamNames()) {
            exec.setParameter(s.toString(), baseData.get(s));
        }
        if( debug ) {
            System.out.println("-> "+squ.getStatement());
            for( Object s : squ.getParamNames()) {
                System.out.println("param->"+ s + "=" + baseData.get(s));
            }
            System.out.println("vars");
            for(Object o: vars.entrySet()) {
                Map.Entry me = (Map.Entry)o;
                System.out.println(me.getKey()+"="+me.getValue());
            }
        }
        exec.execute();
    }
    
    public SqlQuery createQuery( SqlDialectModel sqm, Map params, Map vars  ) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit( sqm, sqlDialect );
        SqlQuery qry = sqlContext.createQuery(squ);
        if( sqm.getStart()>0 || sqm.getLimit()>0) {
            params.put("_start", sqm.getStart());
            params.put("_limit", sqm.getLimit());
        }
        qry.setVars(vars);
        for( Object s : squ.getParamNames()) {
            qry.setParameter(s.toString(), params.get(s));
        }
        if( debug ) {
            System.out.println("-> "+squ.getStatement());
            for( Object s : squ.getParamNames()) {
                System.out.println("param->"+ s + "=" + params.get(s));
            }
            System.out.println("vars");
            for(Object o: vars.entrySet()) {
                Map.Entry me = (Map.Entry)o;
                System.out.println(me.getKey()+"="+me.getValue());
            }
        }
        return qry;
    }
    
    //returns a string statement
    public SqlUnit getSqlUnit( SqlDialectModel sqm ) throws Exception {
        return SqlUnitCache.getSqlUnit( sqm, sqlDialect );
    }
    
    //returns a string statement
    public String buildStatement( SqlDialectModel sqm ) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit( sqm, sqlDialect );
        return squ.getStatement();
    }
   
    //**************************************************************************
    //CREATE METHOD
    //**************************************************************************
    //This is the main entry point for create. Everything top lvel is done here.
    //Top level operations include: 
    //1. Filling up of ids and keys except the relationship keys
    //2. Verifying information. verify has a drill down action
    public Map create( EntityManagerModel model, Map data ) throws Exception {
        SchemaView svw = model.getSchemaView();
        Map<String, SqlDialectModel> sqlModelMap = new LinkedHashMap();
        SqlDialectModelBuilder.buildCreateSqlModels(svw, sqlModelMap );
        //build main data
        create( svw, data, sqlModelMap, model.getVars() );
        return data;
    }
    
    public void create(SchemaView svw, Map rawData, Map<String, SqlDialectModel> sqlModelMap, Map vars )  throws Exception {
        //this is very critical. make sure you get this correct. This is the
        //cause of unmapped fields.flatten data so it can be easily managed
        Map baseData = EntityDataUtil.prepareDataForInsert( svw, rawData );
        createBase( svw, baseData, sqlModelMap, vars );
        createOneToOneLinks(svw, baseData, sqlModelMap, vars);
        createOneToManyLinks(svw, rawData, sqlModelMap, vars);
    }

    /**
     * This simply creates the base element including all its extends
     */ 
    public void createBase( AbstractSchemaView svw, Map data, Map<String, SqlDialectModel> sqlModelMap, Map vars ) throws Exception {
        //insert all extends first
        if( svw.getExtendsView()!=null ) {
            createBase( svw.getExtendsView(), data, sqlModelMap, vars );
        }
        //insert the base model
        SqlDialectModel sqlModel = sqlModelMap.get(svw.getName());
        executeUpdate(sqlModel, data, vars);
    }
    
    public void createOneToOneLinks( AbstractSchemaView vw, Map data,  Map<String, SqlDialectModel> modelMap, Map vars ) throws Exception {
        if( vw.getExtendsView() !=null) {
            createOneToOneLinks( vw.getExtendsView(), data, modelMap, vars );
        }
        for( AbstractSchemaView lnkVw: vw.getOneToOneViews() ) {
            createBase( lnkVw, data, modelMap, vars  );
            String n = lnkVw.getName()+":one--to-one-update";  
            SqlDialectModel updateModel = modelMap.get( n );
            if( updateModel == null  ) {
                updateModel = SqlDialectModelBuilder.buildOneToOneUpdateLinkedId( lnkVw );
                modelMap.put( n, updateModel );
            }
            if( updateModel == null ) 
                throw new Exception("SqlProcessor.create error. sql update for one to one relationship not found");
            executeUpdate(updateModel, data, vars);
        }
    }
    
    public void createOneToManyLinks(AbstractSchemaView vw, Map rawData, Map<String, SqlDialectModel> modelMap, Map vars )  throws Exception {
        if( vw.getExtendsView() !=null) {
            createOneToManyLinks( vw.getExtendsView(), rawData, modelMap, vars );
        }
        for( SchemaRelation sr: vw.getElement().getOneToManyRelationships() ) {
            Object d = EntityDataUtil.getNestedValue(sr.getName(), rawData );
            if( d != null  ) {
                SchemaView svw = sr.getLinkedElement().createView();
                if( !modelMap.containsKey( sr.getLinkedElement().getName() )) {
                    SqlDialectModelBuilder.buildCreateSqlModels(svw, modelMap);
                }
                List list = (List)d;
                for(Object o : list ) {
                    if(! (o instanceof Map )) continue;
                    create( svw, (Map)o, modelMap, vars );
                }
            }
        }    
    }
    
    //**************************************************************************
    //UPDATE METHOD
    //The key idea here is to find first all the affected tables. 
    //We can do this by parsing first the finder and where and updated fields.
    //After these tables are determined, we update each table using this as the 
    //where statement
    //**************************************************************************
    public Map update( EntityManagerModel model, Map odata ) throws Exception {
        if(odata==null || odata.size()==0  ) 
            throw new Exception("update error. data must have at least one value");
        if( model.getFinders().size()==0 && model.getWhereElement()==null ) 
            throw new Exception("update error. finder or where must be specified");
        
        final SchemaView svw = model.getSchemaView();
        
        //flatten first the data to make it easier to manipulate. Then check fields 
        //we will need to include.
        
        Map data = EntityDataUtil.prepareDataForUpdate( svw, odata );        
                
        Map<AbstractSchemaView, SqlDialectModel> modelMap =  SqlDialectModelBuilder.buildUpdateSqlModels(model,data );
        Map params = new HashMap();
        params.putAll( data );
        params.putAll( model.getFinders() );
        params.putAll( model.getWhereParams() );
        
        Map vars = model.getVars();
        
        for(SqlDialectModel sqlModel: modelMap.values()) {
            executeUpdate(sqlModel, params, vars);
        }
        return odata;
    }
    
    //**************************************************************************
    //QUERY METHOD
    //The query method returns queries from the database
    //**************************************************************************
    public List fetchList(EntityManagerModel model) throws Exception {
        SqlDialectModel sqlModel =  SqlDialectModelBuilder.buildSelectSqlModel(model);
        Map parms = new HashMap();
        parms.putAll( EntityDataUtil.flatten(model.getFinders(), "_"));
        parms.putAll( model.getWhereParams() );
        
        Map vars = model.getVars();
        SqlQuery sqlQry = createQuery(sqlModel,parms, vars);
        sqlQry.setFetchHandler(new DataMapFetchHandler(model.getSchemaView()));
        return sqlQry.getResultList();
    }
    
    public Map fetchFirst(EntityManagerModel model) throws Exception {
        SqlDialectModel sqlModel =  SqlDialectModelBuilder.buildSelectSqlModel(model);
        Map parms = new HashMap();
        parms.putAll( EntityDataUtil.flatten(model.getFinders(), "_"));
        parms.putAll( model.getWhereParams() );
        Map vars = model.getVars();
        SqlQuery sqlQry = createQuery(sqlModel,parms, vars);
        sqlQry.setFetchHandler(new DataMapFetchHandler(model.getSchemaView()));
        return (Map)sqlQry.getSingleResult();
    }
    
    /***************************************************************************
     * DELETE
     * Check first the context element and do the ff:
     * 1. get context element
     * 2. check if it has extends, one to many and one to one links
     * 3. if true, create a select statement based on primary keys and stack it 
     *     on top of each element found
     **************************************************************************/ 
    public void delete(EntityManagerModel entityModel) throws Exception {
        
        SchemaElement elem = entityModel.getElement();
        Map parms = new HashMap();
        parms.putAll( EntityDataUtil.flatten(entityModel.getFinders(), "_"));
        parms.putAll( entityModel.getWhereParams() );
        
        Map vars = entityModel.getVars();
        boolean hasLinks = false;
        if( elem.getExtendedElement() !=null ||
            elem.getOneToManyRelationships().size() >0 ||
            elem.getOneToOneRelationships().size() > 0 ) {
            hasLinks = true;
        }    
        
        if(hasLinks) {
            SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectPrimaryKeys(entityModel);
            //createQuery( sqlModel, parms );
            SqlUnit squ = this.getSqlUnit(sqlModel);
            for( SchemaRelation sr: elem.getOneToManyRelationships()) {
                deleteCascade( sr.getLinkedElement(), sqlModel, sr );
            }
        }
        else {
            SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSimpleDeleteSqlModel(entityModel);
            executeUpdate( sqlModel, parms, vars );
        }
    }
    
    private void deleteCascade( SchemaElement elem, SqlDialectModel parentModel, SchemaRelation sr ) {
        
        boolean hasLinks = false;
        if( elem.getExtendedElement() !=null ||
            elem.getOneToManyRelationships().size() >0 ||
            elem.getOneToOneRelationships().size() > 0 ) {
            hasLinks = true;
        }    
        
        if(hasLinks) {
            //form the ff. statement
            //SELECT objid FROM entityid WHERE parentid = ::parent
//            EntityModel2 emodel = new EntityModel2(elem);
//            for( SimpleField sf: elem.getPrimaryKeys() ) {
//                if(i++>0) 
//                emodel.setSelectfieldExpr( sf.getName() );
//            }
            
            
            //SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectPrimaryKeys(emodel);
            
            //createQuery( sqlModel, parms );
            /*
            SqlUnit squ = this.getSqlUnit(sqlModel);
            for( SchemaRelation sr: elem.getOneToManyRelationships()) {
                EntityModel2 eModel = new EntityModel2(sr.getLinkedElement());
                for(RelationKey rk: sr.getRelationKeys()) {
                    System.out.println("target->"+ rk.getTarget() );
                    System.out.println("field->"+ rk.getField());
                }
                //SqlDialectModel sqlModel2 = SqlDialectModelBuilder.buildSimpleDeleteSqlModel(eModel);
            }
            */ 
        }
        else {
            //SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSimpleDeleteSqlModel(entityModel);
            //executeUpdate( sqlModel, parms, vars );
        }
        
        for(RelationKey rk: sr.getRelationKeys()) {
            System.out.println("target->"+ rk.getTarget() );
            System.out.println("field->"+ rk.getField());
        }
    }
    
    
    public void deleteNullifyOneToOne( AbstractSchemaView vw, Map parms,  Map<String, SqlDialectModel> sqlModelMap  ) throws Exception {
        System.out.println("update keys set id to nullify");
    }

    public void deleteBase( AbstractSchemaView vw, Map parms,  Map<String, SqlDialectModel> sqlModelMap, Map vars  ) throws Exception {
        SqlDialectModel sqlModel = sqlModelMap.get(vw.getName());
        executeUpdate( sqlModel, parms, vars );
        if( vw.getExtendsView() !=null ) {
            deleteBase( vw.getExtendsView(), parms, sqlModelMap, vars );
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    
}
