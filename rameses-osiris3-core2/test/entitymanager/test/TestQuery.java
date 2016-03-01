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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public class TestQuery extends TestCase {

    private SqlManager sqlManager;
    private SchemaManager schemaManager;
    private MockConnectionManager cm;
    private EntityManager em;
    
    public TestQuery(String testName) {
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
    

    public void testSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map map = new HashMap();
                map.put("addr2", "%capitol%");
                em.select("today,fullname,address.barangay.name,address_barangay_city:{'cebu city'}, name:{ CONCAT(lastname, ',', firstname) }, today: {NOW()}");
                List list = em.where("address2 like :addr2 and 1=1", map ).limit(2).list();
                //List list = em.select( ".*" ).where("address2 = :addr2", map).list();
                printList(list);
                
                //Map m = em.select("count:{1}").where("1=1").first();
                //System.out.println(m);
            }
        });   
    }
    
    
}
