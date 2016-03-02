/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.osiris3.persistence.SubQueryModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell.
 */
public class TestSubQuery extends AbstractTestCase {

    public TestSubQuery() {
        super("TestSubQuery");
    }
    
    public String getDialect() {
        return "mysql";
    }
    
    private Map getFinder() {
        Map map = new HashMap();
        map.put("entityno", "123456");
        //map.put("state", "ACTIVE");
        return map;
    }

    public void testSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map joinKeys = new HashMap();
                joinKeys.put("objid", "objid");
                
                SubQueryModel sqm = em.setName("entityindividual_deleted").subquery("a");
                em.setName("entityindividual").select("entityno,firstname,lastname,a.voided").where("a.voided IS NULL").leftJoin(sqm, joinKeys);
                em.setDebug(true);
                List list = em.list();
                //List list = em.select( ".*" ).where("address2 = :addr2", map).list();
                printList(list);
                
                //Map m = em.select("count:{1}").where("1=1").first();
                //System.out.println(m);
            }
        });   
    }
    
    
}
