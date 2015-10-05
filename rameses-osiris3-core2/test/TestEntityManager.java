/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.rameses.osiris3.data.MockConnectionManager;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.persistence.SelectFields;
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
public class TestEntityManager extends TestCase {
    
    private MockConnectionManager cm;
    private EntityManager em;

    public TestEntityManager(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        SimpleDataSource sd = new SimpleDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sample", "root", "1234");
        SqlManager sqlm = SqlManager.getInstance();
        cm = new MockConnectionManager();
        SqlContext sc =  sqlm.createContext( cm.getConnection("main", sd) );
        sc.setDialect(new MySqlDialect());
        em = new EntityManager(SchemaManager.getInstance(), sc);
        em.setDebug(true);
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public Map createData() {
        Map orgtype = new HashMap();
        orgtype.put("objid", "I");
        orgtype.put("name", "INDIVIDUAL");
        
        Map m = new HashMap();
        m.put("name", "elmo" );
        m.put("entityno", "ABX124" );
        m.put("type", orgtype );
        //m.put("typeid", "I" );
        
        m.put("lastname", "NAZARENO" );
        m.put("firstname", "ELMO" );
        
        Map bar = new HashMap();
        bar.put("objid", "B1");
        bar.put("name", "POBLACION");

        Map addr = new HashMap();
        addr.put("street", "18 orchid st.");
        addr.put("city", "cebu");
        addr.put("addresstype", "local");
        addr.put("province", "cebu");
        addr.put("barangay", bar);
        addr.put("type", "ADDR");
        addr.put("text", "18 orchid st. capitol site cebu city" );
        
        
        //List list = new ArrayList();
        //list.add( addr );
        //m.put("addresses", list);
        m.put("address", addr);
        return m;
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testInsert() throws Exception {
        try {
            Map data = createData();
            data.put("objid", "N0002");
            em.create("entityindividual", data);
            cm.commit();
            System.out.println("ok");
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            cm.close();
        }
    }
    
    public void xtestRead1() throws Exception {
        try {
            Map data = new HashMap();
            data.put("objid", "N0001");
            Map r = (Map)em.read("entityindividual", data);
            System.out.println(r);
            for( Object o : r.entrySet() ) {
                Map.Entry me = (Map.Entry)o;
                System.out.println(me.getKey()+"="+me.getValue());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public void xtestUpdate() throws Exception {
        try {
            Map data = new HashMap();
            data.put( "objid", "N0001");
            data.put( "name", "elmoxi NAZARENI" );
            data.put( "lastname", null );
            em.update("entityindividual", data);
            cm.commit();
            System.out.println("ok updated");
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            cm.close();
        }
    }
    
    public void xtestDelete() throws Exception {
        try {
            Map data = new HashMap();
            data.put("objid", "N0001");
            em.delete("entityindividual", data);
            cm.commit();
            System.out.println("ok deleted");
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            cm.close();
        }
    }
    
    public void ztestHashSet() {
        String s = "firstname,lastname,billto.name,billto.address.text,billto.address.street,billto.address.barangay.text";
        SelectFields sf = new SelectFields();
        sf.addFields(s);
        System.out.println("select fields ->"+ sf);
    }
    
    public void ZtestSelect() throws Exception {
        try {
            Map params = new HashMap();
            params.put("objid", "N0002");
            //params.put("typeid", "I");
            //,type.objid,type.name
            em.setName("entityindividual");
            List list = em.select("*,address.*").find(params).list();//   .where("type.objid=:typeid", params).list();
            for(Object o: list) {
                System.out.println("item is " + o);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    
}
