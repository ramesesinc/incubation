/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.LinkedSchemaView;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewFieldFilter;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlDialectModel.WhereFilter;
import com.rameses.osiris3.sql.SqlUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dell
 */
public final class SqlDialectModelBuilder {

    /**
     * ***********************************************************************
     * BUILDER HELPERS
     * ***********************************************************************
     */
    public static WhereFilter buildWhereFilter(EntityManagerModel entityModel) {
        EntityManagerModel.WhereElement we = entityModel.getWhereElement();
        if (we == null) {
            return null;
        }
        String expr = correctExpr(entityModel.getWhereElement().getExpr());
        return new SqlDialectModel.WhereFilter(expr);
    }

    /*
     private static String buildWhereFieldMatchPattern( List<SqlDialectModel.WhereFilter> whereList ) {
     StringBuilder sb = new StringBuilder();
     int i = 0;
     for(SqlDialectModel.WhereFilter wf: whereList) {
     if(i++>0) sb.append("|");
     sb.append( wf.getFieldnamePattern() );
     }
     return sb.toString();
     }
     */
    private static String correctSelectFieldMatcher(String expr) {
        if(expr==null) return null;
        if( expr.equals("*") ) return ".*";
        String[] arr = expr.split(",");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(String s: arr) {
            if(i++>0) sb.append("|");
            s = s.trim().replace( ".", "_").replace( "_*", ".*" );
            sb.append(s);
        }
        return sb.toString();
    }

    
    private static String correctExpr(String expr) {
        return expr.replaceAll("\\s{1,}", " ").replaceAll("\\s{1,}(?=[,|\\(|\\)])", "").replaceAll("(?<=[,|\\(|\\)])\\s{1,}", "");
    }

    //finds all field matches
    private static String findAffectedFieldPattern(String expr, SchemaView svw) {
        Set<String> set = new HashSet();
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(expr.getBytes());
            StreamTokenizer st = new StreamTokenizer(is);
            st.wordChars('_', '_');
            st.ordinaryChar(' ');
            int i = 0;
            while ((i = st.nextToken()) != st.TT_EOF) {
                if (i == st.TT_WORD) {
                    String v = st.sval;
                    SchemaViewField vf = svw.getField(v);
                    if (vf != null) {
                        set.add(vf.getExtendedName());
                    }
                }
            }
            StringBuilder s1 = new StringBuilder();
            i = 0;
            for (String s : set) {
                if (i++ > 0) {
                    s1.append("|");
                }
                s1.append(s);
            }
            return s1.toString();
        } catch (Exception ign) {
            //do nothing
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {;
            }
        }
    }

    public static String findWhereAffectedFieldPattern(EntityManagerModel entityModel, SchemaView vw) {
        EntityManagerModel.WhereElement we = entityModel.getWhereElement();
        if (we == null) {
            return "";
        }
        return findAffectedFieldPattern(correctExpr(we.getExpr()), vw);
    }

    public static String findFindersAffectedFieldPattern(EntityManagerModel entityModel, SchemaView vw) {
        return EntityDataUtil.stringify(entityModel.getFinders());
    }

    
    public static String findSelectAffectedFieldPattern(EntityManagerModel entityModel, SchemaView vw) {
        String expr = entityModel.getSelectExpr();
        if( expr == null ) return null;
        return findAffectedFieldPattern(expr, vw);
    }

    public static String findOrderAffectedFieldPattern(EntityManagerModel entityModel, SchemaView vw) {
        String expr = entityModel.getOrderExpr();
        if( expr == null ) return null;
        return findAffectedFieldPattern(expr, vw);    
    }
    
    /*
    public static String findUpdateAffectedFieldPattern(EntityManagerModel entityModel, SchemaView vw) {
        return findAffectedFieldPattern(entityModel.getSelectExpr(), vw);
    }
    */ 

    /**
     * ***********************************************************************
     * CREATE
     * ***********************************************************************
     */
    public static void buildCreateSqlModels(SchemaView svw, final Map<String, SqlDialectModel> sqlModelMap) {
        //basic create statement
        svw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                if (!vf.isInsertable()) {
                    return false;
                }
                AbstractSchemaView vw = vf.getView();
                SqlDialectModel model = sqlModelMap.get(vw.getName());
                if (model == null) {
                    model = new SqlDialectModel();
                    model.setAction("create");
                    model.setTablealias(vw.getName());
                    model.setTablename(vw.getTablename());
                    //model.setSchemaView(vw);
                    sqlModelMap.put(vw.getName(), model);
                }
                model.addField(vf);
                return false;
            }
        });
    }

    public static SqlDialectModel buildOneToOneUpdateLinkedId(final AbstractSchemaView targetVw) {
        SchemaView svw = targetVw.getRootView();
        final List<SchemaViewField> primKeys = new ArrayList();
        List<SchemaViewField> flds = svw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                if (vf.isBaseField() && vf.isPrimary()) {
                    primKeys.add(vf);
                }
                if (!(vf instanceof SchemaViewRelationField)) {
                    return false;
                }
                SchemaViewRelationField svrf = (SchemaViewRelationField) vf;
                if (!svrf.getTargetJoinType().equals(JoinTypes.ONE_TO_ONE)) {
                    return false;
                }
                AbstractSchemaView tvw = svrf.getTargetView();
                if (!tvw.equals(targetVw)) {
                    return false;
                }
                return true;
            }
        });
        if (flds.size() == 0) {
            return null;
        }

        Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        joinedViews.addAll(targetVw.getParent().getJoinPaths());
        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);

        SqlDialectModel model = new SqlDialectModel();
        model.setAction("update");
        model.setTablealias(targetVw.getParent().getName());
        model.setTablename(targetVw.getParent().getTablename());

        for (SchemaViewField sf : flds) {
            model.addField(sf);
            model.getFieldMap().put(sf.getName(), sf);
        }

        model.setFinderFields(primKeys);
        model.setJoinedViews(jvList);
        return model;
    }

    /**
     * ***********************************************************************
     * UPDATE
     * ***********************************************************************
     */
    public static Map<AbstractSchemaView, SqlDialectModel> buildUpdateSqlModels(EntityManagerModel entityModel, Map data) {

        final SchemaView svw = entityModel.getSchemaView();
        //final HashSet<SchemaViewField> whereSet = new LinkedHashSet();
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        final LinkedHashSet<String> uniqueNames = new LinkedHashSet();
        final List<SchemaViewField> vfinders = new ArrayList(); //fields used by finders

        //build the finders
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, svw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, svw);
        final Map<String, SchemaViewField> fieldMap = new HashMap();

        final String fieldMatch =  EntityDataUtil.stringify(data);
        
        //build the where
        final Map<AbstractSchemaView, SqlDialectModel> modelMap = new HashMap();
        svw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                String extName = vf.getExtendedName();
                if (!uniqueNames.add(extName)) {
                    return false;
                }

                AbstractSchemaView avw = svw;
                if (vf.getView() != null) {
                    avw = vf.getView();
                }

                boolean add_joined_view = false;
                //check if you will add in list. if primary do not add bec. we should not update prim keys
                if (fieldMatch!=null && extName.matches(fieldMatch)) {
                    boolean test = false;
                    if (vf.isUpdatable()) {
                        test = true;
                    }
                    if (test) {
                        SqlDialectModel sqlModel = modelMap.get(avw);
                        if (sqlModel == null) {
                            sqlModel = new SqlDialectModel();
                            //sqlModel.setSchemaView(svw);
                            sqlModel.setAction("update");
                            sqlModel.setTablename(avw.getElement().getTablename());
                            sqlModel.setTablealias(avw.getElement().getName());
                            modelMap.put(avw, sqlModel);
                        }
                        sqlModel = modelMap.get(avw);
                        sqlModel.addField(vf);
                        add_joined_view = true;
                    }
                }
                
                //check if in finders
                if (extName.matches(finderMatch)) {
                    vfinders.add(vf);
                    add_joined_view = true;
                }
                //check if in where filters
                if (extName.matches(whereMatch)) {
                    add_joined_view = true;
                }
                //add joined view if teh field exists in field, finder or where
                if (add_joined_view) {
                    joinedViews.addAll(avw.getJoinPaths());
                    fieldMap.put(vf.getExtendedName(), vf);
                }
                return false;
            }
        });

        //before attaching the views we sort it first according to the join order
        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);
        //attach the where and finders in each sql model
        for (SqlDialectModel sqlModel : modelMap.values()) {
            sqlModel.setFieldMap(fieldMap);
            sqlModel.setFinderFields(vfinders);
            sqlModel.setWhereFilter(buildWhereFilter(entityModel));
            sqlModel.setJoinedViews(jvList);
            sqlModel.setSubqueries(entityModel.getSubqueries());
        }
        return modelMap;
    }

    /**
     * **********************************************************************
     * SELECT
    ***********************************************************************
     */
    
    public static SqlDialectModel buildSelectSqlModel(final EntityManagerModel entityModel) {
        SchemaView svw = entityModel.getSchemaView();

        //field matches
        String testMatch = correctSelectFieldMatcher(entityModel.getSelectFields());
        if( testMatch == null && entityModel.getSelectExpr()==null ) {
            testMatch = ".*";
        }    
        final String fieldMatch1 = testMatch;
        final String fieldMatch2 = findSelectAffectedFieldPattern(entityModel, svw);
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, svw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, svw);   //buildWhereFieldMatchPattern( whereList );
        final String orderMatch = findOrderAffectedFieldPattern(entityModel, svw);
        
        //temporary data holders
        final HashSet<String> uniqueNames = new HashSet();
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        final List<SchemaViewField> vfinders = new ArrayList(); //fields used by finders
        final Map<String, SchemaViewField> fieldMap = new HashMap();

        final StringBuilder fieldList = new StringBuilder();
        
        svw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                String extName = vf.getExtendedName();
                if (!uniqueNames.add(extName)) {
                    return false;
                }
                boolean add_joined_view = false;
                if( fieldMatch1!=null && extName.matches(fieldMatch1)) {
                    if(fieldList.length()>0) fieldList.append(",");
                    fieldList.append(extName);
                    //we need to do this because the select fields are already specified as an expression
                    if( !extName.equals(vf.getFieldname()) ) {
                        fieldList.append( " AS [" + extName + "]" );
                    }
                    add_joined_view = true;
                }
                if (fieldMatch2!=null && extName.matches(fieldMatch2)) {
                    add_joined_view = true;
                }
                //check if in finders
                if (finderMatch!=null && extName.matches(finderMatch)) {
                    vfinders.add(vf);
                    add_joined_view = true;
                }
                //check if in where filters
                if (whereMatch!=null && extName.matches(whereMatch)) {
                    add_joined_view = true;
                }
                //check if in order expression
                if (orderMatch!=null && extName.matches(orderMatch)) {
                    add_joined_view = true;
                }
                
                //add joined view if teh field exists in field, finder or where
                if (add_joined_view) {
                    joinedViews.addAll(vf.getView().getJoinPaths());
                    fieldMap.put(extName, vf);
                }
                return false;
            }
        });
        
        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);

        //build the field expression
        if( entityModel.getSelectExpr()!=null  ) {
            if( fieldList.length() > 0  ) fieldList.append( "," );
            fieldList.append( entityModel.getSelectExpr() );
        }
        
        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("select");
        sqlModel.setTablename(svw.getTablename());
        sqlModel.setTablealias(svw.getName());
        
        sqlModel.setSelectExpression(fieldList.toString());
        sqlModel.setFieldMap(fieldMap);
        sqlModel.setFinderFields(vfinders);
        sqlModel.setJoinedViews(jvList);
        sqlModel.setWhereFilter(buildWhereFilter(entityModel));
        sqlModel.setSubqueries(entityModel.getSubqueries());
        
        sqlModel.setStart(entityModel.getStart());
        sqlModel.setLimit(entityModel.getLimit());
        sqlModel.setOrderExpr( entityModel.getOrderExpr() );
        return sqlModel;
    }

    /**
     * **********************************************************************
     * DELETE STATEMENTS This is a statement to delete only one table based on a
     * single element
    **********************************************************************
     */
    public static SqlDialectModel buildSimpleDeleteSqlModel(EntityManagerModel entityModel) {

        final SchemaView vw = entityModel.getSchemaView();
        final List<SchemaViewField> vfinders = new ArrayList(); //fields used by finders
        final LinkedHashSet<String> uniqueNames = new LinkedHashSet();
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();

        //build the finders
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, vw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, vw);
        final Map<String, SchemaViewField> fieldMap = new HashMap();
        //build the where
        vw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                String extName = vf.getExtendedName();
                if (!uniqueNames.add(extName)) {
                    return false;
                }
                AbstractSchemaView avw = vf.getView();
                boolean add_joined_view = false;
                //check if in finders
                if (extName.matches(finderMatch)) {
                    vfinders.add(vf);
                    add_joined_view = true;
                }
                //check if in where filters
                if (extName.matches(whereMatch)) {
                    add_joined_view = true;
                }
                //add joined view if teh field exists in field, finder or where
                if (add_joined_view) {
                    joinedViews.addAll(avw.getJoinPaths());
                    fieldMap.put(extName, vf);
                }
                return false;
            }
        });

        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);

        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("delete");
        sqlModel.setFieldMap(fieldMap);
        sqlModel.setTablename(vw.getTablename());
        sqlModel.setTablealias(vw.getName());
        sqlModel.setJoinedViews(jvList);
        sqlModel.setFinderFields(vfinders);
        sqlModel.setWhereFilter(buildWhereFilter(entityModel));
        sqlModel.setSubqueries(entityModel.getSubqueries());
        return sqlModel;
    }

    /**
     * **********************************************************************
     * facilities for the delete
    ************************************************************************
     */
    public static SqlDialectModel buildSelectPrimaryKeys(EntityManagerModel entityModel) {
        SchemaView vw = entityModel.getSchemaView();
        final List<SchemaViewField> vfinders = new ArrayList(); //fields used by finders
        final LinkedHashSet<String> uniqueNames = new LinkedHashSet();
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        //build the finders
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, vw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, vw);
        final Map<String, SchemaViewField> fieldMap = new HashMap();

        List<SchemaViewField> flds = vw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                String extName = vf.getExtendedName();
                if (!uniqueNames.add(extName)) {
                    return false;
                }

                boolean add_joined_view = false;
                boolean add_field = false;
                if (vf.isPrimary() && vf.isBaseField()) {
                    add_field = true;
                }

                if (extName.matches(finderMatch)) {
                    vfinders.add(vf);
                    add_joined_view = true;
                }
                if (extName.matches(whereMatch)) {
                    add_joined_view = true;
                }
                //add joined view if teh field exists in field, finder or where
                if (add_joined_view) {
                    AbstractSchemaView vw = vf.getView();
                    if (vw instanceof LinkedSchemaView) {
                        joinedViews.addAll(vf.getView().getJoinPaths());
                    }
                    fieldMap.put(extName, vf);
                }
                return add_field;
            }
        });

        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);

        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("select");
        sqlModel.setFieldMap(fieldMap);
        sqlModel.setTablename(vw.getTablename());
        sqlModel.setTablealias(vw.getName());
        sqlModel.setJoinedViews(jvList);
        sqlModel.setFinderFields(vfinders);
        sqlModel.setWhereFilter(buildWhereFilter(entityModel));
        return sqlModel;
    }

    /**
     * *
     * sample output: WHERE parentid IN ( SELECT objid FROM entityindividual
     * WHERE objid=$P{objid} )
     */
    public static SqlDialectModel buildSelectSubquery(EntityManagerModel entityModel, SqlUnit squ) {
        return null;
    }
}
