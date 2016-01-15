/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.sql.SimpleDataSource;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlManager;
import com.rameses.sql.dialect.MySqlDialect;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author dell
 */
public class TestQuery extends TestCase {
    
    public TestQuery(String testName) {
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
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        SimpleDataSource sd = new SimpleDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/etracs25_kapalong", "root","1234");
        SqlContext sqlc = SqlManager.getInstance().createContext(sd);
        sqlc.setDialect(new MySqlDialect());
        EntityManager em = new EntityManager(SchemaManager.getInstance(), sqlc);
        em.setDebug(true);
        em.setName("revenueitem");
        em.select("objid,title,fund_code,fund_objid");
        Map m = new HashMap();
        m.put("fund_objid", "SEF");
        em.where(" fund_objid = :fund_objid ", m );
        em.sort("title", "desc").sort("objid");
        //em.where( "title LIKE 'A%'");
        em.setLimit(10);
        List list = em.list();
        for( Object o: list) {
            System.out.println(o);
        }
    }
}

