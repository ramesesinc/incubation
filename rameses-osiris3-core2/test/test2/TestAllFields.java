/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewFieldFilter;
import com.rameses.osiris3.schema.SchemaViewFieldFilter.ExtendedNameViewFieldFilter;
import com.rameses.osiris3.sql.SqlDialectModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 *
 * @author dell
 */
public class TestAllFields extends TestCase {

    public TestAllFields(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void displayFields(String pattern) {

        SchemaManager sm = SchemaManager.getInstance();
        SchemaElement elem = sm.getElement("entityindividual");
        SchemaView svw = elem.createView();
        Set<AbstractSchemaView> set = new LinkedHashSet();
        ExtendedNameViewFieldFilter hf = new SchemaViewFieldFilter.ExtendedNameViewFieldFilter(pattern, true);
        for (SchemaViewField vf : svw.findAllFields(hf)) {
            if (vf.isPrimary() && !vf.isBaseField()) {
                continue;
            }
            set.add(vf.getView());
            System.out.println(vf);
        }

        LinkedHashSet<AbstractSchemaView> joinPaths = new LinkedHashSet<AbstractSchemaView>();
        for (AbstractSchemaView vw : set) {
            joinPaths.addAll(vw.getJoinPaths());
        }

        List<AbstractSchemaView> arr = new ArrayList(Arrays.asList(joinPaths.toArray()));
        Collections.sort(arr);
        for (AbstractSchemaView vw : arr) {
            System.out.println("-------------------------------");
            System.out.println(vw);
        }
        /*
         System.out.println("parent of barangay: " + vf.getView().getName());
         List<AbstractSchemaView> list = vf.getView().getJoinPaths();
         */
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    public void stestInfo() throws Exception {
        displayFields("address_barangay_pin");
    }

    /**
     * The objective is to group first by view, select only fields that are
     * marked as insertable
     *
     * @throws Exception
     */
    public void ztestForCreate() throws Exception {
        SchemaManager sm = SchemaManager.getInstance();
        SchemaElement elem = sm.getElement("entityindividual");
        SchemaView svw = elem.createView();
        final Map<AbstractSchemaView, SqlDialectModel> map = new HashMap();
        svw.findAllFields( new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                if( !vf.isInsertable() ) return false;
                AbstractSchemaView vw = vf.getView();
                SqlDialectModel model = map.get(vw);
                if( model==null) {
                    model = new SqlDialectModel();
                    model.setAction("create");
                    model.setTablealias(vw.getName());
                    model.setTablename(vw.getTablename());
                    //model.setSchemaView(vw);
                    map.put( vw, model);
                }
                model.addField(vf);
                return false;
            }
        });
        
        for( Object o: map.entrySet() ) {
            Map.Entry<AbstractSchemaView,SqlDialectModel> me = (Map.Entry)o;
            //if( vw.isExtendedView() && numNested == 0) {
                System.out.println("--------------------------------------");
                System.out.println(me.getKey().getName() + " insert into " + me.getKey().getTablename() );
                System.out.println("--------------------------------------");
                for(  SchemaViewField vf:  me.getValue().getFields() ) {
                    System.out.println(vf.getTablename()+"."+vf.getFieldname() + "   =   " + vf.getExtendedName() );
                }
                System.out.println("--------------------------------------");
            //}
        }
    }
    
    public void testForUpdate() throws Exception {
        final String updateFields = "";
        final String finderFields = "";
        final String whereFields = "";
        
        final String matchAll = updateFields + "|" + finderFields + "|" + whereFields;
        
        SchemaManager sm = SchemaManager.getInstance();
        SchemaElement elem = sm.getElement("entityindividual");
        SchemaView svw = elem.createView();
        final Map<AbstractSchemaView, SqlDialectModel> map = new HashMap();
        final List<SchemaViewField>  finderList = new ArrayList();
        svw.findAllFields( new SchemaViewFieldFilter() {
            public boolean accept(SchemaViewField vf) {
                if( !vf.isUpdatable() ) return false;
                String extName = vf.getExtendedName();
                if( !extName.matches(matchAll) ) return false;
                AbstractSchemaView vw = vf.getView();
                SqlDialectModel model = map.get(vw);
                if( model == null ) {
                    model = new SqlDialectModel();
                    model.setAction("update");
                    model.setTablealias(vw.getName());
                    model.setTablename(vw.getTablename());                    
                    //model.setSchemaView(vw);
                    map.put( vw, model );
                }
                if( extName.matches(updateFields) ) {
                    model.addField(vf);
                }
                if( extName.matches(finderFields) ) {
                    finderList.add(vf);
                }
                //add the where elements
                return false;
            }
        });
        
        //finders
        //where
        
    }
    
}
