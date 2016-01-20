/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SimpleField;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManagerModel {
    
    private SchemaElement element;
    private String action;
    private Map data;
    private String includeFields;
    private String excludeFields;
    private Map finders = new HashMap();
    private String selectFields;
    private List<FilterCriteria> filters = new ArrayList();
    private long start;
    private long limit;
    private List<OrderField> orderFields = new ArrayList();
    
    public SchemaElement getElement() {
        return element;
    }

    public void setElement(SchemaElement element) {
        this.element = element;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(String includeFields) {
        this.includeFields = includeFields;
    }
    
    
    public void addFinders(Map f) {
        finders.putAll(f);
    }

    public String getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(String selectFields) {
        this.selectFields = selectFields;
    }

    public String getExcludeFields() {
        return excludeFields;
    }
    
    public List<OrderField> getOrderFields() {
        return orderFields;
    }
    
    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    //this is used when scanning the selected fields used for queries
    public SelectFields buildSelectFields() {
        SelectFields sf = new SelectFields();
        sf.addFields(this.selectFields);
        return sf;
    }
    
    public SelectFields buildFinderFields() {
        SelectFields sf  = new SelectFields();
        //loop thru the finders
        for( Object k: finders.keySet() ) {
            sf.addFields( k.toString() );
        }
        return sf;
    }
    
    public Map getFinderByPrimaryKey() {
        Map map = new HashMap();
        for( SimpleField sf: this.element.getSimpleFields() ) {
            if( sf.isPrimary() ) {
                map.put( sf.getName(), data.get(sf.getName()) );
            }
        }
        return map;
    }
    
    public Map getFinders() {
        return finders;
    }
    
    public List<FilterCriteria> getFilters() {
        return this.filters;
    }
    
    public void addFilter( FilterCriteria fc ) {
        filters.add(fc);
    }
    
    private String correctFieldName(String s, StreamTokenizer st ) throws Exception {
        int i = st.nextToken();
        if( i == '_' || i == '.' ) {
            s += (char)i;
            int j = st.nextToken(); 
            if(j != st.TT_WORD ) {
                st.pushBack();
                return s;
            }
            return correctFieldName( s+st.sval, st);
        }
        else {
            st.pushBack();
            return s;
        }
    }
    
    public void addFilter( String cond, Map params ) {
        try {
            FilterCriteria fc = new FilterCriteria();
            fc.setData(params);
            StringBuilder sb = new StringBuilder();
            
            StreamTokenizer st = new StreamTokenizer(new StringReader(cond));
            int i = 0;
            while ((i = st.nextToken()) != st.TT_EOF) {
                if (i == st.TT_WORD) {
                    String v = st.sval;
                    //eat all fields with underscores
                    if (v.toUpperCase().matches("CASE|WHEN|THEN|END|IS|LIKE|AND|OR")) {
                        sb.append(" " + v + " ");
                        continue;
                    } 
                    v = correctFieldName( v, st );
                    //this is a field. we will add markers so that it can be easily replaceable
                    sb.append(" @@["+v +"] ");
                    fc.addField(v);
                }
                else if (i == st.TT_NUMBER) {
                    sb.append( st.nval );
                } 
                else if( i == '@') {
                    //this is a function
                    st.nextToken();
                    String funcName = st.sval;
                    st.nextToken(); //should be the open parens
                    sb.append( "@" + funcName + "(");
                }
                else if( i == ',' ) {
                    //used if there are functions
                    sb.append(",");
                }
                else if( i == ':') {
                    st.nextToken();
                    sb.append( "$P{" + correctFieldName(st.sval,st) + "}" );
                }
                else if( i == '\'') {
                    sb.append( "'" + st.sval + "'" );
                }
                else {
                    sb.append( (char)i );
                }
            }
            
            fc.setExpr( sb.toString() );
            addFilter(fc);
            
        } catch (Exception e) {
            throw new RuntimeException("Error in EntityManager.where " + e.getMessage());
        }
    }
    
    /***
    * Loop thru the fields in the map including the nested loops
    */ 
    private void fetchFields( Map d, String prefix, List<String> fNames ) {
        for( Object k: d.entrySet() ) {
            Map.Entry me = (Map.Entry)k;
            if( me.getValue() instanceof Map ) {
                String pref = me.getKey().toString();
                if( prefix !=null ) pref = prefix + "_" + pref;
                fetchFields( (Map)me.getValue(), pref, fNames );
            }
            else {
                String fName = me.getKey().toString();
                if( prefix !=null ) fName = prefix + "_" + fName;
                fNames.add( fName );
            };
        }
    }
    
    public boolean buildIncludeFieldsForUpdate() {
        List<String> fieldNames = new ArrayList();
        fetchFields( data, null, fieldNames );
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for( String str: fieldNames ) {
            if( first ) first = false;
            else sb.append("|");
            sb.append( str );
        }
        //include also the 
        this.includeFields = null;
        String iflds = sb.toString();
        if(iflds.trim().length()>0) {
            this.includeFields =   iflds; 
        }
        return this.includeFields != null;
    }
    
    private void mergeData( Map d, Map target ) {
        if(d == null) return;
        for(Object k: d.entrySet() ) {
            Map.Entry me = (Map.Entry)k;
            String fldName = me.getKey().toString();
            Object targetValue = target.get(fldName);
            Object val = me.getValue();
            if( val instanceof Map  ) {
                if(targetValue == null ) {
                    targetValue = new HashMap();
                    target.put(fldName, targetValue);
                    mergeData( (Map)val,  (Map)targetValue );
                }
                else if( targetValue instanceof Map ) {
                    mergeData( (Map)val,  (Map)targetValue );
                }
                else {
                    //if targetValue is not a map, we will override it.
                    target.put(fldName, val);
                }
            }
            else {
                target.put(fldName, val);
            }
        }
    }
    
    public Map getAllData() {
        Map map = new HashMap();
        mergeData( data, map );
        mergeData( finders, map );
        for( FilterCriteria fc: this.filters ) {
            mergeData( fc.getData(),  map );
        }
        return map;
    }
    
    //this clears all the finders, data and parameters. 
    //called after every execution 
    public void clear() {
        action = null;
        Map data = null;
        includeFields = null;
        excludeFields = null;
        finders = new HashMap();
        selectFields = null;
        filters = new ArrayList();        
    }
    
    private void fetchFields( SchemaElement elem, List<Map> cols, boolean extended ) {
        SchemaManager sm = elem.getSchema().getSchemaManager();
        if( elem.getExtends()!=null ) {
            SchemaElement extElem = sm.getElement(elem.getExtends());
            fetchFields( extElem, cols, true );
        }
        for( SimpleField sf: elem.getSimpleFields()) {
            if(sf.isPrimary() && extended) continue;
            cols.add(sf.toMap());
        }
        for( ComplexField cf: elem.getComplexFields()) {
            if( cf.getSerializer()!=null) {
                cols.add(cf.toMap());
            }
            else {
                String joinType = cf.getJoinType();
                if(joinType.equals("one-to-many")) continue;
                String ref = cf.getRef();
                Map cm = cf.toMap();
                SchemaElement refElem = sm.getElement(elem.getExtends());
                fetchSchema( refElem, cm );
                cols.add(cm);
            }
        }
    }
    
    private void fetchSchema( SchemaElement elem, Map xschema ) {
        List<Map> cols = new ArrayList();
        xschema.put("columns", cols);
        fetchFields(elem, cols, false);
    }
    
    
    public Map getSchema() {
        Map _schema = new HashMap();
        fetchSchema( element, _schema );
        return _schema;
    }

    
    //This is used for storing the cached model.
    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append( element.getSchema().getName()+":");
        sb.append( element.getName()+":"+action+";" );
        if( !action.equals("create") ) {
            if(selectFields!=null) {
                sb.append( ":select=" + selectFields+";" );
            }
            if( this.includeFields!=null ) {
                sb.append( ":inc=" + includeFields+";" );
            }
            if( this.excludeFields!=null ) {
                sb.append( ":exc=" + excludeFields+";" );
            }
            if( this.finders.size()>0) {
                sb.append("finders:");
                for(Object mm: this.finders.keySet()) {
                    sb.append( mm.toString() + ";" );
                }
            }
            if( this.filters.size()>0) {
                sb.append("filters:");
                for(FilterCriteria fc:this.filters) {
                    sb.append(fc.getExpr()+";");
                }
            }
            if( this.orderFields.size()>0) {
                sb.append("order:");
                for(OrderField ff:this.orderFields) {
                    sb.append(ff.toString()+";");
                }
            }
            if( this.getStart()>=0 && this.getLimit()>0 ) {
                sb.append("paging:true;");
            }
        }
        return sb.toString();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
    
    public void addOrderField( String name, String direction ) {
        OrderField o = new OrderField();
        o.setName(name);
        if(direction!=null) o.setDirection(direction);
        this.orderFields.add(o);
    }
    
    public SelectFields buildOrderFields() {
        SelectFields sf  = new SelectFields();
        //loop thru the finders
        for( OrderField k: orderFields ) {
            sf.addFields( k.getName() );
        }
        return sf;
    }
    
}
