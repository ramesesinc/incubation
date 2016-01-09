/*
 * EntityManager.java
 *
 * Created on August 15, 2010, 1:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.common.UpdateChangeHandler;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaSerializer;
import com.rameses.osiris3.sql.FieldToMap;
import com.rameses.osiris3.sql.MapToField;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author elmo
 */
public class EntityManager {

    private SqlContext sqlContext;
    private SchemaManager schemaManager;
    private boolean debug;
    private boolean transactionOpen = false;
    //newly added. This is for the name of the schema
    private String schemaName;
    //if true, complex fields are separated by underscores.
    //example : person.firstname will be person_firstname in db
    private boolean resolveNested = true;
    private EntityManagerModel model = new EntityManagerModel();
    private EntityManagerInvoker invoker;
    
    public EntityManager(SchemaManager scm, SqlContext sqlContext, String schemaName) {
        this.sqlContext = sqlContext;
        this.schemaManager = scm;
        this.schemaName = schemaName;
    }

    public EntityManager(SchemaManager scm, SqlContext sqlContext) {
        this(scm, sqlContext, null);
    }

    public void setSqlContext(SqlContext ctx) {
        if (transactionOpen) {
            throw new RuntimeException("SqlContext cannot be set at this time because transaction is currently open");
        }
        this.sqlContext = ctx;
    }

    public SqlContext getSqlContext() {
        return this.sqlContext;
    }

    public Object create(Object data) {
        if (this.schemaName == null) {
            throw new RuntimeException(" for EntityManager.create error. Schema name must not be null");
        }
        return create(this.schemaName, data, true);
    }

    public Object create(String schemaName, Object data) {
        return create(schemaName, data, true);
    }

    //Listing ....
    public List list() {
        try {
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            SchemaElement elem = schemaManager.getElement(schemaName);
            model.setElement(elem);
            model.setAction("select");
            List list = ps.getList(model);
            model.clear();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    //get first record
    public Map first() {
        try {
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            SchemaElement elem = schemaManager.getElement(schemaName);
            model.setElement(elem);
            model.setAction("select");
            Map map = ps.read(model);
            model.clear();
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    //this merges the data with existing data.
    public Object merge(String schemaName, Object data) {
        try {
            if (!(data instanceof Map)) {
                throw new Exception("EntityManager.create error. Data passed must be a map");
            }
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            SchemaElement elem = schemaManager.getElement(schemaName);
            Object retVal = ps.merge((Map) data, elem);
            model.clear();
            return retVal;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * if there are no records found, this function returns null
     */
    public Object read(Object data) {
        return read(this.schemaName, data, null);
    }
    
    public Object read(String schemaName, Object data) {
        return read(schemaName, data, null);
    }

    //read will fetch each data everytime, there are no joins, each 
    //table will be fetched individually and merged. Read also is dependent
    //on the primary keys
    public Object read(String schemaName, Object params, Map options) {
        try {
            if (!(params instanceof Map)) {
                throw new Exception("EntityManager.read error. Data passed must be a map");
            }
            this.schemaName = schemaName;
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            SchemaElement elem = schemaManager.getElement(this.schemaName);
            model.setElement(elem);
            model.setSelectFields("*");
            model.setAction("read");
            model.setData((Map) params);
            Object retVal = ps.read(model);
            model.clear();
            return retVal;
        } catch (Exception e) {
            System.out.println("error in read ->" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * we need to ensure first to read the record before updating.
     */
    public Object update(Object data) {
        return update(this.schemaName, data, null, null, true, true);
    }
    
    public Object update(String schemaName, Object data) {
        return update(schemaName, data, null, null, true, true);
    }

    public Object update(String schemaName, Object data, boolean validate) {
        return update(schemaName, data, null, null, validate, true);
    }

    public Object update(String schemaName, Object data, Map vars) {
        return update(schemaName, data, vars, null, true, true);
    }

    public Object update(String schemaName, Object data, UpdateChangeHandler h) {
        return update(schemaName, data, null, h, true, true);
    }

    //added version handling of changes during updates
    public Object update(String schemaName, Object data, Map vars, UpdateChangeHandler vhandler, boolean validate, boolean read) {
        try {
            SchemaElement elem = schemaManager.getElement(schemaName);
            this.schemaName = schemaName;
            model.setElement(elem);
            model.setAction("update");
            model.setData((Map) data);
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            model.buildIncludeFieldsForUpdate();    //this is to build only what will be updated.
            if (validate) {
                validate(elem, data, model.getIncludeFields(), null);
            }            
            Object retVal = (Map) ps.update(model);
            model.clear();
            return retVal;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    //special function to avoid reading the data before updating
    public Object updateImmediate(String schemaName, Object data) {
        return update(schemaName, data, null, null, true, false);
    }

    public void delete() {
        if (this.schemaName == null) {
            throw new RuntimeException("EntityManager.delete error. schemaName is required");
        }
        delete(this.schemaName, new HashMap());
    }

    public void delete(String schemaName) {
        delete(schemaName, model);
    }

    public void delete(String schemaName, Object data) {
        try {
            if (model == null) {
                throw new Exception("EntityManager.delete error. EntityManager model is null");
            }
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            SchemaElement elem = schemaManager.getElement(schemaName);
            this.schemaName = schemaName;
            model.setElement(elem);
            model.setAction("delete");
            model.setData((Map) data);
            ps.delete(model);
            model.clear();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * *
     * extended methods in the DefaultEntityManager
     */
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /*
     public Object createModel(String schemaName) {
     return schemaManager.createMap(schemaName);
     }
     */
    public void validate(String schemaName, Object data) {
        SchemaElement elem = schemaManager.getElement(schemaName);
        validate(elem, data, null, null);
    }

    public void validate(SchemaElement elem, Object data, String includeFields, String excludeFields) {
        try {
            ValidationResult vr = ValidationUtil.getInstance().validate(data, elem, includeFields, excludeFields);
            if (vr.hasErrors()) {
                throw new Exception(vr.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * add a map serializer also later.
     */
    public SchemaSerializer getSerializer() {
        return schemaManager.getSerializer();
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    /**
     * This is a generic save routine merged as one call. create = if true will
     * insert the record. update = if true will update the record. if
     * create=true and update=false, this is insert only. you get the picture.
     */
    public Object save(String schemaName, Object data) {
        return save(schemaName, data, null);
    }

    public Object save(String schemaName, Object data, Map vars) {
        return save(schemaName, data, true, true, vars);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update) {
        return save(schemaName, data, create, update, null, null, true);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update, Map vars) {
        return save(schemaName, data, create, update, vars, null, true);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update, boolean validate) {
        return save(schemaName, data, create, update, null, null, validate);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update, Map vars, UpdateChangeHandler vhandler, boolean validate) {
        if (create == true && update == true) {
            Object test = read(schemaName, data, vars);
            if (test == null || ((test instanceof Map) && ((Map) test).isEmpty())) {
                return create(schemaName, data, validate);
            } else {
                return update(schemaName, data, vars, vhandler, validate, true);
            }
        } else if (create == true && update == false) {
            return create(schemaName, data, validate);
        } else if (create == false && update == true) {
            Object test = read(schemaName, data, vars);
            if (test == null) {
                throw new RuntimeException("Record for update does not exist");
            }
            return update(schemaName, data, vars, vhandler, validate, true);
        } else {
            return data;
        }
    }

    public boolean isTransactionOpen() {
        return transactionOpen;
    }

    public Map mapToField(Map data) {
        return MapToField.convert(data, null);
    }

    public Map mapToField(Map data, String excludeFields) {
        return MapToField.convert(data, excludeFields);
    }

    public Map fieldToMap(Map data) {
        return fieldToMap(data, null);
    }

    public Map fieldToMap(Map data, String excludeFields) {
        return FieldToMap.convert(data, excludeFields);
    }

    public boolean isResolveNested() {
        return resolveNested;
    }

    public void setResolveNested(boolean convertComplex) {
        this.resolveNested = resolveNested;
    }

    //New methods added!
    private EntityManagerModel getModel() {
        return model;
    }

    public EntityManager setName(String name) {
        this.schemaName = name;
        model.clear();
        model.setElement(schemaManager.getElement(name));
        return this;
    }

    public EntityManager find(Map params) {
        model.addFinders(params);
        return this;
    }

    public EntityManager select(String cols) {
        model.setSelectFields(cols);
        return this;
    }

    public EntityManager where(String cond) {
        model.addFilter(cond, new HashMap());
        return this;
    }
    
    public EntityManager where(String cond,  Map params) {
        model.addFilter(cond, params);
        return this;
    }
    
    public EntityManager limit(long start, long limit) {
        model.setStart(start);
        model.setLimit(limit);
        return this;
    }
    
    public EntityManager setStart(long start) {
        model.setStart(start);
        return this;
    }
    
    public EntityManager setLimit(long limit) {
        model.setLimit(limit);
        return this;
    }
    
    public Map getSchema() {
        return getSchema(this.schemaName);
    }

    public Map getSchema(String name) {
        SchemaElement elem = schemaManager.getElement(name);
        model.setElement(elem);
        model.setSelectFields("*");
        return model.getSchema();
    }
    
    //we will have a separate implementation of the following because 
    //many applications are already using this. we will deprecate later
    
    public Object create(String schemaName, Object data, boolean validate) {
        try {
            if (!(data instanceof Map)) {
                throw new Exception("EntityManager.create error. Data passed must be a map");
            }
            SchemaElement elem = schemaManager.getElement(schemaName);
            this.schemaName = schemaName;
            model.setElement(elem);
            model.setData((Map) data);
            model.setAction("create");
            DataFillerUtil.fill(elem, (Map) data);
            if (validate) {
                validate(elem, data, null, null);
            }
            EntityManagerUtil ps = new EntityManagerUtil(sqlContext, debug);
            Object retVal = ps.create(model);
            model.clear();
            return retVal;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void setInvoker(EntityManagerInvoker inv) {
        this.invoker = inv;
    }

    
    //used by the data context. if starts with find, it returns single record. 
    //If it starts with get, it returns list
    public Object invokeSqlMethod( String methodName, Object args ) throws Exception {
        String finalMethodName = this.schemaName+":"+methodName;
        Map m = null;
        if( args == null ) {
            m = null;
        }
        else if(args instanceof Object[]) {
            Object[] ao = (Object[])args;
            if(! (ao[0] instanceof Map) ) 
                throw new Exception("Unrecognied parameter for invokeSqlMethod. Must be map or Object[]");
            m = (Map) ao[0] ;
        }
        else if (args instanceof Map)  {
             m = (Map)args;
        }
        else {
            throw new Exception("Unrecognied parameter for invokeSqlMethod. Must be map or Object[]");
        }
        if( methodName.startsWith("find") || methodName.startsWith("get") ) {
            SqlQuery sq = sqlContext.createNamedQuery( finalMethodName );    
            if(m!=null) {
                sq.setVars(m).setParameters(m);
                if(m.containsKey("_start")) {
                    int s = Integer.parseInt(m.get("_start")+"");
                    sq.setFirstResult( s );
                }
                if(m.containsKey("_limit")) {
                    int l = Integer.parseInt(m.get("_limit")+"");
                    sq.setMaxResults( l );
                }
            }
            if(methodName.startsWith("find"))
                return sq.getSingleResult();
            else 
                return sq.getResultList();
        }
        else {
            SqlExecutor sqe = sqlContext.createNamedExecutor( finalMethodName );    
            if(m!=null) {
                sqe.setVars(m).setParameters(m);
            }
            return sqe.execute();
        }
    }
    
    public SqlQuery createQuery(String sql) {
        return this.getSqlContext().createQuery(sql);
    }
    
    public SqlExecutor createExecutor(String sql) {
        return this.getSqlContext().createExecutor(sql);
    }

}
