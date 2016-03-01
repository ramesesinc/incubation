/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.osiris3.data.MockConnectionManager;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.sql.SimpleDataSource;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlManager;
import com.rameses.sql.dialect.MsSqlDialect;
import com.rameses.sql.dialect.MySqlDialect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public class TestUpdate extends TestCase {

    private SqlManager sqlManager;
    private SchemaManager schemaManager;
    private MockConnectionManager cm;
    private EntityManager em;
    
    public TestUpdate(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        sqlManager = SqlManager.getInstance();
        schemaManager = SchemaManager.getInstance();
        em = new EntityManager(schemaManager, createContext(), "entityindividual");
        em.setDebug(true);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private String dialect = "mysql";
    //private String dialect = "mssql";
    
    private SqlContext createContext() throws Exception {
        cm = new MockConnectionManager();
        SimpleDataSource ds = null;
        SqlContext sqlc = null;
        if( dialect.equals("mysql")) {
            ds = new SimpleDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/testdb", "root", "1234");
            sqlc = sqlManager.createContext(cm.getConnection("main", ds));
            sqlc.setDialect(new MySqlDialect());
        }
        else {
            //SQL SERVER
            ds = new SimpleDataSource("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://127.0.0.1;DatabaseName=testdb", "sa", "1234");
            sqlc = sqlManager.createContext(cm.getConnection("main", ds));
            sqlc.setDialect(new MsSqlDialect());
        }

        return sqlc;

    }
    
    private Map createId(String idno, String type) {
        Map map = new HashMap();
        map.put("idno", idno);
        map.put("idtype", type);
        map.put("dateissued", java.sql.Date.valueOf("2014-01-01"));
        return map;
    }

     private Map createContact(String id, String type, String value) {
        Map map = new HashMap();
        map.put("objid", id);
        map.put("type", type);
        map.put("value", value);
        return map;
    }
     
    private Map buildUpdateData() {
        Map data = new HashMap();
        data.put("firstname", "elmo");
        data.put("lastname", "nazareno");
        data.put("entityno", "123456");
        data.put("state", "ACTIVOR");

        Map brgy = new HashMap();
        brgy.put("objid", "BRGY0001");
        brgy.put("name", "POBLACION");

        Map addr = new HashMap();
        //addr.put("objid", "ADDR1");
        addr.put("text", "18 orchid st capitol site");
        addr.put("street", "street 18");
        addr.put("barangay", brgy);
        data.put("address", addr);
        data.put("address2", "capitol tol");
        /*
         Map addr = new HashMap();
         //addr.put("text", "19 orchid st capitol site");
         addr.put("city", "cebu city");
         addr.put("province", "cebu province");
         addr.put("municipality", "dalaguete");
         data.put("address", addr);
         */
        return data;
    }

    private Map getFinder() {
        Map map = new HashMap();
        map.put("entityno", "123456");
        //map.put("state", "ACTIVE");
        return map;
    }

    private static interface ExecHandler {
        void execute() throws Exception;
    }
    
    private void exec( ExecHandler h  ) throws Exception {
        try {
            h.execute();
            cm.commit();
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            cm.close();
        }
    }
    
    private void printList(List list) {
        for(Object obj: list) {
            System.out.println(obj + " class:"+obj.getClass());
        }
    }
    
    
    public void testUpdate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map created = new HashMap();
                created.put("objid", "WVF");

                Map modified = new HashMap();
                modified.put("objid", "EMN");

                Map addr = new HashMap();
                addr.put("street", "1072 dawis");
                addr.put("text", "1072 dawis tabunok talisay city");

                //update info from other tables. 
                Map m = new HashMap();
                m.put("name", "{CONCAT(firstname,',--myname2--',lastname)}");
                //m.put("createdby", created);
                m.put("address", addr);
                m.put("createdby", created);
                m.put("modifiedby", modified);
                m.put("dtcreated", "{NOW()}");

                List items = new ArrayList();
                items.add( createContact("CTCT13c576c5:1533050dbd9:-7ffe", "XMOBILE", "NAZA1234" ) );
                m.put("contactinfos", items);
                
                Map whereMap = new HashMap();
                whereMap.put("entityno", "123456");
                em.where("entityno=:entityno", whereMap).update(m);
            }
        });
    }
    
}
