package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaRelation;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlQuery;
import com.rameses.osiris3.sql.SqlUnit;
import com.rameses.osiris3.sql.SqlUnitCache;
import java.lang.String;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EntityManagerProcessor {

    private boolean debug = false;
    private SqlDialect sqlDialect;
    private SqlContext sqlContext;

    public EntityManagerProcessor(SqlContext sqc, SqlDialect stmt) {
        this.sqlContext = sqc;
        this.sqlDialect = stmt;
    }

    public void executeUpdate(SqlDialectModel sqm, Map baseData, Map vars) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
        SqlExecutor exec = this.sqlContext.createExecutor(squ);
        if (vars != null) {
            exec.setVars(vars);
        }
        for (Object o : squ.getParamNames()) {
            String s = o.toString();
            exec.setParameter(s, baseData.get(s));
        }

        if (this.debug) {
            System.out.println("-> " + squ.getStatement());
            for (Object o : squ.getParamNames()) {
                String s = o.toString();
                System.out.println("param->"+s+"="+baseData.get(s));
            }
            if ((vars != null) && (vars.size() > 0)) {
                System.out.println("vars");
                for (Object o: vars.entrySet()) {
                    Map.Entry me = (Map.Entry) o;
                    System.out.println(me.getKey()+"="+me.getValue());
                }
            }
        }
        exec.execute();
    }

    public SqlQuery createQuery(SqlDialectModel sqm, Map params, Map vars)
            throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
        SqlQuery qry = this.sqlContext.createQuery(squ);
        if ((sqm.getStart() > 0) || (sqm.getLimit() > 0)) {
            params.put("_start", Integer.valueOf(sqm.getStart()));
            params.put("_limit", Integer.valueOf(sqm.getLimit()));
        }
        if (vars != null) {
            qry.setVars(vars);
        }
        for (Object o: squ.getParamNames()) {
            String s = o.toString();
            qry.setParameter(s, params.get(s));
        }

        if (this.debug) {
            System.out.println("-> " + squ.getStatement());
            for (Object o : squ.getParamNames()) {
                String s = o.toString();
                System.out.println("param->"+s+"="+params.get(s));
            }
            if ((vars != null) && (vars.size() > 0)) {
                System.out.println("vars");
                for (Object o: vars.entrySet()) {
                    Map.Entry me = (Map.Entry) o;
                    System.out.println(me.getKey()+"="+me.getValue());
                }
            }
        }
        return qry;
    }

    public SqlUnit getSqlUnit(SqlDialectModel sqm) throws Exception {
        return SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
    }

    public String buildStatement(SqlDialectModel sqm) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
        return squ.getStatement();
    }

    public Map create(EntityManagerModel model, Map data)
            throws Exception {
        SchemaView svw = model.getSchemaView();
        Map sqlModelMap = new LinkedHashMap();
        SqlDialectModelBuilder.buildCreateSqlModels(svw, sqlModelMap);

        create(svw, data, sqlModelMap, model.getVars());
        return data;
    }

    public void create(SchemaView svw, Map rawData, Map<String, SqlDialectModel> sqlModelMap, Map vars) throws Exception {
        Map baseData = DataTransposer.prepareDataForInsert(svw, rawData);
        createBase(svw, baseData, sqlModelMap, vars);
        createOneToOneLinks(svw, baseData, sqlModelMap, vars);
        createOneToManyLinks(svw, rawData, sqlModelMap, vars);
    }

    public void createBase(AbstractSchemaView svw, Map data, Map<String, SqlDialectModel> sqlModelMap, Map vars) throws Exception {
        if (svw.getExtendsView() != null) {
            createBase(svw.getExtendsView(), data, sqlModelMap, vars);
        }

        SqlDialectModel sqlModel = (SqlDialectModel) sqlModelMap.get(svw.getName());
        executeUpdate(sqlModel, data, vars);
    }

    public void createOneToOneLinks(AbstractSchemaView vw, Map data, Map<String, SqlDialectModel> modelMap, Map vars) throws Exception {
        if (vw.getExtendsView() != null) {
            createOneToOneLinks(vw.getExtendsView(), data, modelMap, vars);
        }
        for (AbstractSchemaView lnkVw : vw.getOneToOneViews()) {
            createBase(lnkVw, data, modelMap, vars);
            String n = lnkVw.getName()+":one--to-one-update";
            SqlDialectModel updateModel = (SqlDialectModel) modelMap.get(n);
            if (updateModel == null) {
                updateModel = SqlDialectModelBuilder.buildOneToOneUpdateLinkedId(lnkVw);
                modelMap.put(n, updateModel);
            }
            if (updateModel == null) {
                throw new Exception("SqlProcessor.create error. sql update for one to one relationship not found");
            }
            executeUpdate(updateModel, data, vars);
        }
    }

    public void createOneToManyLinks(AbstractSchemaView vw, Map rawData, Map<String, SqlDialectModel> modelMap, Map vars) throws Exception {
        if (vw.getExtendsView() != null) {
            createOneToManyLinks(vw.getExtendsView(), rawData, modelMap, vars);
        }
        for (SchemaRelation sr : vw.getElement().getOneToManyRelationships()) {
            Object d = DataUtil.getNestedValue(rawData, sr.getName());
            if (d != null) {
                SchemaView svw = sr.getLinkedElement().createView();
                if (!modelMap.containsKey(sr.getLinkedElement().getName())) {
                    SqlDialectModelBuilder.buildCreateSqlModels(svw, modelMap);
                }
                List list = (List) d;
                for (Object o: list) {
                    if (o instanceof Map);
                    create(svw, (Map) o, modelMap, vars);
                }
            }
        }
    }

    public Map update(EntityManagerModel model, Map odata)  throws Exception {
        return update(model, odata, null);
    }

    public Map update(EntityManagerModel model, Map odata, Map updateParams) throws Exception {
        if ((odata == null) || (odata.size() == 0)) {
            throw new Exception("update error. data must have at least one value");
        }
        if ((model.getFinders().size() == 0) && (model.getWhereElement() == null)) {
            throw new Exception("update error. finder or where must be specified");
        }
        SchemaView svw = model.getSchemaView();
        Map data = DataTransposer.prepareDataForUpdate(svw, odata);
        Map<String, SqlDialectModel> modelMap = SqlDialectModelBuilder.buildUpdateSqlModels(model, data);
        Map params = new HashMap();
        params.putAll(data);
        params.putAll(model.getFinders());
        params.putAll(model.getWhereParams());
        if (updateParams != null) {
            params.putAll(updateParams);
        }
        Map vars = model.getVars();
        for (SqlDialectModel sqlModel : modelMap.values()) {
            executeUpdate(sqlModel, params, vars);
        }
        return odata;
    }

    public Map merge(EntityManagerModel model, Map data) throws Exception {
        Map m = update(model, data);
        return m;
    }

    public List fetchList(EntityManagerModel model) throws Exception {
        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
        Map parms = new HashMap();
        parms.putAll(DataTransposer.flatten(model.getFinders(), "_"));
        parms.putAll(model.getWhereParams());
        Map vars = model.getVars();
        SqlQuery sqlQry = createQuery(sqlModel, parms, vars);
        sqlQry.setFetchHandler(new DataMapFetchHandler(model.getSchemaView()));
        return sqlQry.getResultList();
    }

    public Map fetchFirst(EntityManagerModel model, int nestLevel) throws Exception {
        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
        Map parms = new HashMap();
        parms.putAll(DataTransposer.flatten(model.getFinders(), "_"));
        parms.putAll(model.getWhereParams());
        sqlModel.setStart(0);
        sqlModel.setLimit(1);
        Map vars = model.getVars();
        SqlQuery sqlQry = createQuery(sqlModel, parms, vars);
        sqlQry.setFetchHandler(new DataMapFetchHandler(model.getSchemaView()));
        Map result = (Map) sqlQry.getSingleResult();
        if (nestLevel > 0) {
            fetchSubItems(model, result, 1, nestLevel);
        }
        return result;
    }

    public void fetchSubItems(EntityManagerModel parentModel, Map parent, int level, int nestLevel) throws Exception {
        for (SchemaRelation sr : parentModel.getElement().getOneToManyRelationships()) {
            EntityManagerModel subModel = new EntityManagerModel(sr.getLinkedElement());
            for (RelationKey rk : sr.getRelationKeys()) {
                subModel.getFinders().put(rk.getTarget(), parent.get(rk.getField()));
            }
            subModel.setStart(0);
            subModel.setLimit(0);
            List list = fetchList(subModel);
            parent.put(sr.getName(), list);
        }
    }

    public void delete(EntityManagerModel entityModel) throws Exception {
        SchemaElement baseElement = entityModel.getElement();
        Map parms = new HashMap();
        parms.putAll(DataTransposer.flatten(entityModel.getFinders(), "_"));
        parms.putAll(entityModel.getWhereParams());
        Map vars = entityModel.getVars();
        SqlDialectModel model = SqlDialectModelBuilder.buildSelectKeysForDelete(entityModel);
        List list = createQuery(model, parms, vars).getResultList();
        for (Object o : list) {
            Map finders = (Map) o;
            deleteOneToMany(entityModel.getSchemaView(), finders);
            deleteSingle(entityModel.getSchemaView(), finders);
        }
    }
    
    private void deleteOneToMany(SchemaView svw, Map finders) throws Exception {
        SchemaElement parentElem = svw.getElement();
        for(SchemaRelation sr:parentElem.getOneToManyRelationships()) {
            //check if the linked element has relationships like one to one or one to many
            //we have to load each record in that case.
            Map subFinders = new HashMap();
            for(RelationKey rk: sr.getRelationKeys()) {
                Object val = DataUtil.getNestedValue(finders, rk.getField());
                subFinders.put( rk.getTarget(), val );
            }
            SchemaElement childElement = sr.getLinkedElement();
            if( childElement.getOneToManyRelationships().size()>0 && childElement.getOneToOneRelationships().size()>0) {
                EntityManagerModel entityModel = new EntityManagerModel(childElement);
                SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectKeysForDelete(entityModel);
                List list = createQuery(sqlModel, subFinders, null).getResultList();
                for (Object o : list) {
                    Map xfinders = (Map) o;
                    deleteOneToMany(entityModel.getSchemaView(), xfinders);
                    deleteSingle(entityModel.getSchemaView(), xfinders);
                }
            }
            else {
                //we'll simply delete the record based on its parentid
                EntityManagerModel model = new EntityManagerModel(childElement);
                model.getFinders().putAll(subFinders);
                deleteSimple( model );
            }
        };
    }

    private void deleteSingle(SchemaView svw, Map finders) throws Exception {
        Map fieldsToNullify = new HashMap();
        EntityManagerModel model = new EntityManagerModel(svw.getElement());
        
        Map<AbstractSchemaView, EntityManagerModel> toDeleteMap = new LinkedHashMap();
        Map<AbstractSchemaView, EntityManagerModel> toDeleteExtended = new LinkedHashMap();
        for (SchemaViewField vf : svw.getFields()) {
            String n = vf.getExtendedName();
            if ((vf.isPrimary()) && (vf.isBaseField())) {
                model.getFinders().put(n, finders.get(n));
            }
            else if (vf instanceof SchemaViewRelationField) {
                SchemaViewRelationField svf = (SchemaViewRelationField) vf;
                if (svf.getTargetJoinType().equals(JoinTypes.ONE_TO_ONE)) {
                    fieldsToNullify.put(svf.getFieldname(), "{NULL}");

                    AbstractSchemaView tgt = svf.getTargetView();
                    if (!toDeleteMap.containsKey(tgt)) {
                        EntityManagerModel em = new EntityManagerModel(tgt.getElement());
                        toDeleteMap.put(tgt, em);
                    }
                    EntityManagerModel em = (EntityManagerModel) toDeleteMap.get(tgt);
                    Object v = DataUtil.getNestedValue(finders, svf.getFieldname());
                    em.getFinders().put(svf.getTargetField().getName(), v);
                } 
            }
            else if( vf.isPrimary() && vf.getView().isExtendedView() ) {
                AbstractSchemaView tgtVw = vf.getView();
                if( !toDeleteExtended.containsKey(tgtVw) ) {
                    EntityManagerModel em = new EntityManagerModel(tgtVw.getElement());
                    toDeleteExtended.put( tgtVw, em );
                }
                EntityManagerModel em = (EntityManagerModel) toDeleteExtended.get(tgtVw);
                Object v = DataUtil.getNestedValue(finders, vf.getExtendedName());
                em.getFinders().put(vf.getExtendedName(), v);
            }
        }
        if (fieldsToNullify.size() > 0) {
            update(model, fieldsToNullify);
        }
        for (EntityManagerModel em : toDeleteMap.values()) {
            deleteSimple(em);
        }
        
        deleteSimple(model);
        
        for (EntityManagerModel em : toDeleteExtended.values()) {
            deleteSimple(em);
        }
    }

    private void deleteSimple(EntityManagerModel entityModel) throws Exception {
        SqlDialectModel model = SqlDialectModelBuilder.buildDeleteSqlModel(entityModel);
        executeUpdate(model, entityModel.getFinders(), null);
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public SqlDialectModel.SubQuery createSubQueryModel(EntityManagerModel model) {
        try {
            SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
            Map parms = new HashMap();
            parms.putAll(DataTransposer.flatten(model.getFinders(), "_"));
            parms.putAll(model.getWhereParams());
            SqlDialectModel.SubQuery sq = new SqlDialectModel.SubQuery();
            sq.setSqlModel(sqlModel);
            sq.getParams().putAll(parms);
            return sq;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}