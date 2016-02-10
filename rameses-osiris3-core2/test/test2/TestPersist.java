/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

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
public class TestPersist extends TestCase {

    private SqlManager sqlManager;
    private SchemaManager schemaManager;
    private MockConnectionManager cm;
    private EntityManager em;
    
    public TestPersist(String testName) {
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

    private Map buildCreateData() {
        Map data = new HashMap();
        data.put("objid", "ENT000001");
        data.put("firstname", "elmo");
        data.put("lastname", "nazareno");
        data.put("name", "nazareno, elmo");
        data.put("entityno", "123456");
        data.put("state", "ACTIVE");
        data.put("type", "INDIVIDUAL");

        Map brgy = new HashMap();
        brgy.put("objid", "BRGY0001");
        brgy.put("name", "POBLACION");

        Map addr = new HashMap();
        //addr.put("objid", "ADDR1");
        addr.put("text", "18 orchid st capitol site");
        addr.put("street", "street 18");
        addr.put("barangay", brgy);
        data.put("address", addr);

        Map created = new HashMap();
        created.put("objid", "EMN");
        created.put("username", "elmo nazareno");
        data.put("createdby", created);

        Map edited = new HashMap();
        edited.put("objid", "WVF");
        edited.put("username", "worgie flores");
        data.put("modifiedby", edited);
        data.put("billaddress", addr);

        List ids = new ArrayList();
        ids.add(createId("1287787", "Drivers License"));
        ids.add(createId("981288", "SSS"));
        data.put("ids", ids);
        
        Map info = new HashMap();
        info.put("age", 24);
        info.put("sss", "11267899");
        data.put("info", info);
        
        return data;
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
    
     // TODO add test methods here. The name must begin with 'test'. For example:
    public void ztestCreate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.create(buildCreateData());
            }
        });
    }
   
    public void ztestUpdateExpr() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map map = new HashMap();
                map.put("dtcreated", "{NOW()}");
                map.put("name", "{CONCAT(firstname,',++cross ',lastname)}");
                em.find(getFinder()).update(map);
            }
        });
    }

    public void ztestSimpleUpdate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map d = new HashMap();
                d.put("objid", "ENT000001");
                Map addr = new HashMap();
                addr.put("street", "ZZY 1072 dawis");
                addr.put("text", "ZZY 1072 dawis tabunok talisay city");
                d.put("address", addr);
                em.update( d );
            }
        });
    }
    
    public void ztestUpdate() throws Exception {
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
                //m.put("createdby", created);
                m.put("address", addr);
                m.put("createdby", created);
                m.put("modifiedby", modified);

                Map whereMap = new HashMap();
                whereMap.put("entityno", "123456");
                em.where("entityno=:entityno", whereMap).update(m);
            }
        });
    }
    
    public void ztestRead() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map map = new HashMap();
                map.put( "objid", "ENT000001");
                Map d = (Map)em.read(map);
                for( Object m: d.entrySet() ) {
                    Map.Entry me = (Map.Entry)m;
                    System.out.println(me.getKey()+"="+ (me.getValue()==null?"": me.getValue().getClass()));
                }
                System.out.println(d);
            }
        });   
    }

    public void ztestSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                //em.select("address.barangay.name,address_barangay_city:{'cebu city'}, name:{ CONCAT(lastname, ', ', firstname) }, today: {NOW()}");
                em.select( "stat:{ CASE WHEN state=:state THEN '1' ELSE '0' END }" );
                
                em.sort("lastname ASC, firstname ASC");
                //List list = em.where(" 1=1 ").limit(100).list();
                List list = em.find(getFinder()).list();
                printList(list);
            }
        });   
    }

    public void ztestDelete() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                //em.setName("barangay").find(getFinder()).delete();
                Map finder = new HashMap();
                finder.put("objid", "ID-518bd1bd:152bf58a4c3:-7fff");
                em.setName("id").find(finder).delete();
            }
        }); 
    }
    
    public void ztestGroupBy() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.select("maxname:{MAX(lastname)}, entityno");
                List list = em.find(getFinder()).sort("firstname DESC,lastname, entityno").groupBy("entityno, address.barangay.objid, yr:{ YEAR(dtcreated) }").list();
                printList(list);
            }
        });
    }
    
    public void testDelete1() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.setName("barangay");
                Map m = new HashMap();
                m.put("objid", "TEMP");
                em.find( m ).delete();
            }
        });
    }
    
}
