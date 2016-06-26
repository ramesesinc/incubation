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
public class TestCreate extends AbstractTestCase {

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

    private Map createContact(String type, String value) {
        Map map = new HashMap();
        map.put("type", type);
        map.put("value", value);
        return map;
    }
    
    private Map buildCreateData() {
        Map data = new HashMap();
        //data.put("objid", "ENT000001");
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
        data.put("address2", "capitol 3");

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
        
        List contacts = new ArrayList();
        contacts.add(createContact("PHONE", "999-000-9871"));
        contacts.add(createContact("MOBILE", "09173870196"));
        data.put("contactinfos", contacts);
        
        Map info = new HashMap();
        info.put("age", 24);
        info.put("sss", "11267899");
        data.put("info", info);
        
        return data;
    }

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testCreate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.create(buildCreateData());
            }
        });
    }

    
}
