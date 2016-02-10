/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.persistence.SelectFieldsTokenizer.Token;
import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.LinkedSchemaView;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewFieldFilter;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlDialectModel.Field;
import com.rameses.osiris3.sql.SqlDialectModel.WhereFilter;
import com.rameses.osiris3.sql.SqlUnit;
import com.rameses.util.ValueUtil;
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
    /*
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
    */ 
    
    private static String correctExpr(String expr) {
        return expr.replaceAll("\\s{1,}", " ").replaceAll("\\s{1,}(?=[,|\\(|\\)])", "").replaceAll("(?<=[,|\\(|\\)])\\s{1,}", "");
    }

    private static interface FindFieldFromExprHandler {
        void handle( SchemaViewField  vf );
    }
    
    //finds all field matches
    private static String findAffectedFieldPattern(String expr, SchemaView svw, FindFieldFromExprHandler h) {
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
                        if(h!=null) h.handle(vf);
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
        return findAffectedFieldPattern(correctExpr(we.getExpr()), vw, null);
    }

    public static String findFindersAffectedFieldPattern(EntityManagerModel entityModel, SchemaView vw) {
        return DataUtil.stringifyMapKeys(entityModel.getFinders());
    }

    
    //This is mostly used for inline expressions for field like select and update
    private static Set<AbstractSchemaView> findJoinedViewsFromExprForUpdate( String expr, SchemaView vw ) {
        final Set set = new LinkedHashSet();
        findAffectedFieldPattern(correctExpr(expr), vw, new FindFieldFromExprHandler() {
            public void handle(SchemaViewField vf) {
                set.addAll( vf.getView().getJoinPaths() );
            }
        });        
        return set;
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
                model.addField(createSqlField(vf));
                return false;
            }
        });
    }

    private static SqlDialectModel.Field createSqlField(SchemaViewField vf) {
        SqlDialectModel.Field f = new SqlDialectModel.Field();
        f.setName( vf.getName() );
        f.setTablename(vf.getTablename());
        f.setTablealias(vf.getTablealias());
        f.setExtendedName(vf.getExtendedName());
        f.setFieldname(vf.getFieldname());
        f.setPrimary(vf.isPrimary());
        f.setInsertable(vf.isInsertable());
        f.setUpdatable(vf.isUpdatable());
        f.setSerialized(vf.isSerialized());
        f.setBasefield(vf.isBaseField());
        return f;
    }
    
    public static SqlDialectModel buildOneToOneUpdateLinkedId(final AbstractSchemaView targetVw) {
        SchemaView svw = targetVw.getRootView();
        final List<Field> primKeys = new ArrayList();
        List<SchemaViewField> flds = svw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                if (vf.isBaseField() && vf.isPrimary()) {
                    primKeys.add(createSqlField(vf));
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
            Field f = createSqlField(sf);
            model.addField(f);
            model.getFieldMap().put(sf.getName(), f);
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
    
    public static Map<AbstractSchemaView, SqlDialectModel> buildUpdateSqlModels(EntityManagerModel entityModel, final Map data) {

        final SchemaView svw = entityModel.getSchemaView();
        //final HashSet<SchemaViewField> whereSet = new LinkedHashSet();
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        final LinkedHashSet<String> uniqueNames = new LinkedHashSet();
        final List<Field> vfinders = new ArrayList(); //fields used by finders

        //build the finders
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, svw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, svw);
        final Map<String, Field> fieldMap = new HashMap();

        final String fieldMatch =  DataUtil.stringifyMapKeys(data);
        
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
                if ( fieldMatch!=null && extName.matches(fieldMatch)) {
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
                        
                        //check the data if it is an expression
                        SqlDialectModel.Field sqlF = createSqlField(vf);
                        try {
                            Object val = DataUtil.getNestedValue(data, extName);
                            if(val!=null && (val instanceof String)) {
                                String t = val.toString().trim();
                                if(t.startsWith("{") && t.endsWith("}")) {
                                    String texpr = t.substring(1, t.length()-1);
                                    sqlF.setExpr( texpr );
                                    joinedViews.addAll(findJoinedViewsFromExprForUpdate( texpr, svw ));
                                }
                            }
                        }
                        catch(Exception ign){;}
                        sqlModel.addField(sqlF);
                        add_joined_view = true;
                    }
                }
                
                //check if in finders
                if (extName.matches(finderMatch)) {
                    vfinders.add(createSqlField(vf));
                    add_joined_view = true;
                }
                //check if in where filters
                if (extName.matches(whereMatch)) {
                    add_joined_view = true;
                }
                //add joined view if teh field exists in field, finder or where
                if (add_joined_view) {
                    joinedViews.addAll(avw.getJoinPaths());
                    fieldMap.put(vf.getExtendedName(), createSqlField(vf));
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

        List<Field> selectFieldList = new ArrayList();
        List<Field> groupFieldList = null;  //there might be none
        List<Field> orderFieldList = null;  //there might be none
        
        boolean includePrimary = true;
        boolean hasGroup = false;
        final Map<String, Field> fieldMap = new HashMap();
        
        String fldExpr = ValueUtil.isEmpty(entityModel.getSelectFields())?".*":entityModel.getSelectFields();
        List<Token> fieldMatchList = SelectFieldsTokenizer.tokenize(fldExpr);
        
        if( !ValueUtil.isEmpty(entityModel.getGroupByExpr() )) {
            List<Token> groupList = SelectFieldsTokenizer.tokenize(entityModel.getGroupByExpr());
            for( Token t: groupList) {
                t.setInGroup(true);
                fieldMatchList.add(t);
            }
            includePrimary = false;
            groupFieldList = new ArrayList();
            hasGroup = true;
        }
        
        if( !ValueUtil.isEmpty(entityModel.getOrderExpr())) {
            List<Token> orderTokenList = SelectFieldsTokenizer.tokenize(entityModel.getOrderExpr());
            for( Token t: orderTokenList ) {
                t.setInOrder(true);
                if(hasGroup) t.setInGroup(true);
                fieldMatchList.add(t);
            }
            orderFieldList = new ArrayList();
        }
        
        //temporary data holders
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        
        //put the found list here
        List<SchemaViewField> allFields = svw.findAllFields(".*");
        
        
        //loop each token until all matches passed. Each token represents a select view
        for( Token t: fieldMatchList ) {
            if(t.hasExpr()) {
                //for expressed fields.
                SqlDialectModel.Field sf = new SqlDialectModel.Field();
                sf.setExtendedName(t.getAlias());
                sf.setExpr( t.getExpr() );
                if( !t.isInOrder()) {
                    selectFieldList.add( sf );
                }
                else {
                    sf.setSortDirection(t.getSortDirection());
                    orderFieldList.add(sf);
                }
                if( t.isInGroup() ) {
                    groupFieldList.add(sf);
                }
                findAffectedFieldPattern(correctExpr(sf.getExpr()), svw, new FindFieldFromExprHandler() {
                    public void handle(SchemaViewField vf) {
                        joinedViews.addAll( vf.getView().getJoinPaths() );
                        fieldMap.put( vf.getExtendedName(), createSqlField(vf) );
                    }
                });        
            }
            else {
                //for normal fields.
                for( SchemaViewField vf: allFields) {
                    String extName = vf.getExtendedName();
                    
                    boolean add_fld = false;
                    boolean passNext = false;

                    if( includePrimary && vf.isPrimary() && vf.isBaseField() ) {
                        add_fld = true;
                    }
                    
                    if( !t.hasExpr() && extName.equals(t.getFieldMatch()) ) {
                        add_fld = true;
                        passNext = true;
                    }
                    else if(!t.hasExpr() && extName.matches(t.getFieldMatch())) {
                        add_fld = true;
                    }
                    if (add_fld) {
                        
                        SqlDialectModel.Field sf = createSqlField(vf);
                        if(!t.isInOrder()) {
                            if(!selectFieldList.contains(sf)) selectFieldList.add( sf );
                        }
                        else {
                            if(!orderFieldList.contains(sf)) {
                                sf.setSortDirection(t.getSortDirection());
                                orderFieldList.add(sf);
                            }
                        }
                        if( t.isInGroup() ) {
                            if(!groupFieldList.contains(sf)) {
                                groupFieldList.add(sf);
                            }
                        }
                        joinedViews.addAll(vf.getView().getJoinPaths());
                        if(!fieldMap.containsKey(extName)) {
                            fieldMap.put(extName, sf);
                        }
                        if(passNext) break; //move to the next token
                    }
                }
            }
        }
        
        //loop on the other fields in where, order and groupBy
        final Set<Field> vfinders = new HashSet(); //fields used by finders
        String finderMatch = findFindersAffectedFieldPattern(entityModel, svw);
        String whereMatch = findWhereAffectedFieldPattern(entityModel, svw);   //buildWhereFieldMatchPattern( whereList );
        for( SchemaViewField vf: allFields) {
            String extName = vf.getExtendedName();
            boolean add_joined_view = false;
            boolean add_finder = false;
            if (finderMatch!=null && extName.matches(finderMatch)) {
                add_finder = true;
                add_joined_view = true;
            }
            //check if in where filters
            if (whereMatch!=null && extName.matches(whereMatch)) {
                add_joined_view = true;
            }
            //add joined view if teh field exists in field, finder or where
            if (add_joined_view) {
                SqlDialectModel.Field sf = createSqlField(vf);
                if(add_finder) vfinders.add(sf);
                joinedViews.addAll(vf.getView().getJoinPaths());
                fieldMap.put(extName, sf);
            }
        }
        
        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);
        
        List<Field> finderList = new ArrayList(Arrays.asList(vfinders.toArray()));

        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("select");
        sqlModel.setTablename(svw.getTablename());
        sqlModel.setTablealias(svw.getName());
        
        sqlModel.setFields(selectFieldList);
        sqlModel.setFieldMap(fieldMap);
        sqlModel.setFinderFields(finderList);
        sqlModel.setJoinedViews(jvList);
        sqlModel.setWhereFilter(buildWhereFilter(entityModel));
        sqlModel.setSubqueries(entityModel.getSubqueries());
        
        sqlModel.setStart(entityModel.getStart());
        sqlModel.setLimit(entityModel.getLimit());
        sqlModel.setOrderFields( orderFieldList );
        sqlModel.setGroupFields(groupFieldList);
        return sqlModel;
    }

    /**
     * **********************************************************************
     * DELETE STATEMENTS This is a statement to delete only one table based on a
     * single element
    **********************************************************************
     */
    public static SqlDialectModel buildDeleteSqlModel(EntityManagerModel entityModel) {

        SchemaView svw = entityModel.getSchemaView();
        final List<Field> vfinders = new ArrayList(); //fields used by finders
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        //build the finders
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, svw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, svw);
        final Map<String, Field> fieldMap = new HashMap();
        
        for( SchemaViewField vf: svw.findAllFields()) {
            String extName = vf.getExtendedName();
            boolean add_joined_view = false;
            boolean add_finder = false;
            if (extName.matches(finderMatch)) {
                add_finder = true;
                add_joined_view = true;
            }
            if (extName.matches(whereMatch)) {
                add_joined_view = true;
            }
            //add joined view if teh field exists in field, finder or where
            if (add_joined_view) {
                Field sqlF = createSqlField(vf);
                if(add_finder) {
                    vfinders.add(sqlF);
                }
                AbstractSchemaView vw = vf.getView();
                if (vw instanceof LinkedSchemaView) {
                    joinedViews.addAll(vf.getView().getJoinPaths());
                }
                fieldMap.put(extName, sqlF);
            }            
        }
        
        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);

        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("delete");
        sqlModel.setFieldMap(fieldMap);
        sqlModel.setTablename(svw.getTablename());
        sqlModel.setTablealias(svw.getName());
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
        final List<Field> vfinders = new ArrayList(); //fields used by finders
        final LinkedHashSet<String> uniqueNames = new LinkedHashSet();
        final Set<AbstractSchemaView> joinedViews = new LinkedHashSet();
        //build the finders
        final String finderMatch = findFindersAffectedFieldPattern(entityModel, vw);
        final String whereMatch = findWhereAffectedFieldPattern(entityModel, vw);
        final Map<String, Field> fieldMap = new HashMap();

        final List<SqlDialectModel.Field> fieldList = new ArrayList();
        
        vw.findAllFields(new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                String extName = vf.getExtendedName();
                if (!uniqueNames.add(extName)) {
                    return false;
                }

                boolean add_joined_view = false;
                if (vf.isPrimary() && vf.isBaseField()) {
                    SqlDialectModel.Field sf = createSqlField(vf);
                    if(!fieldList.contains(sf)) fieldList.add(sf);
                }

                if (extName.matches(finderMatch)) {
                    vfinders.add(createSqlField(vf));
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
                    fieldMap.put(extName, createSqlField(vf));
                }
                return false;
            }
        });

        List<AbstractSchemaView> jvList = new ArrayList(Arrays.asList(joinedViews.toArray()));
        Collections.sort(jvList);

        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("select");
        sqlModel.setFieldMap(fieldMap);
        sqlModel.setTablename(vw.getTablename());
        sqlModel.setFields(fieldList);
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
