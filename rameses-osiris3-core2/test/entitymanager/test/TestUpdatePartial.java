/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell.
 */
public class TestUpdatePartial extends AbstractTestCase {

    public String getDialect() {
        return "mysql";
    }
    
    private Map createId(String idno, String type) {
        Map map = new HashMap();
        map.put("idno", idno);
        map.put("idtype", type);
        map.put("dateissued", java.sql.Date.valueOf("2014-01-01"));
        return map;
    }

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testUpdate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map finder = new HashMap();
                finder.put("entityno","123456" );
                
                List addedIds = new ArrayList();
                addedIds.add( createId("voters id", "98192189") );
                addedIds.add( createId("school id", "general") );
                Map m = createId("Drviers Licensia","999");
                m.put("objid", "ID668ec0db:1535133450e:-7ffd");
                addedIds.add( m );
                
                Map d = new HashMap();
                d.put("ids", addedIds);
                
                em.find(finder).update(d);
            }
        });
    }

    
}
