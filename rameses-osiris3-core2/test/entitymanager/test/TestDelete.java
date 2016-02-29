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
import java.util.Map;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public class TestDelete extends TestCase {

    private SqlManager sqlManager;
    private SchemaManager schemaManager;
    private MockConnectionManager cm;
    private EntityManager em;
    private SqlContext sqlc;
    
    public TestDelete(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        sqlManager = SqlManager.getInstance();
        schemaManager = SchemaManager.getInstance();
        sqlc = createContext();
        em = new EntityManager(schemaManager, sqlc, "entityindividual");
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
    
    private Map getFinder() {
        Map map = new HashMap();
        map.put("entityno", "123456");
        //map.put("state", "ACTIVE");
        return map;
    }

    public void testDelete()  throws Exception {
        try {
            em.find(getFinder());
            em.delete();
            cm.commit();
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            cm.close();
        }
    }
    
    
}
