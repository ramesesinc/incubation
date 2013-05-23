/*
 * EntityUtilTest.java
 * JUnit based test
 *
 * Created on May 17, 2013, 2:40 PM
 */

package test;

import com.rameses.util.EntityUtil;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class EntityUtilTest extends TestCase {
    
    public EntityUtilTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        Map m = new HashMap();
        m.put("_fieldname_", "elmos");
        m.put(":permitee_firstname", "elmo");
        m.put(":permitee_objid", "BH0001");
        m.put(":permitee_lastname", "nazareno");
        m.put("permiteename", "1201020");
        m.put(":permitee_address_street", "orchid st.");
        m.put(":permitee_address_city", "cebu city");
        m.put("payer", new HashMap());
        System.out.println("original->"+m);
        Map target = EntityUtil.fieldToMap( m );
        System.out.println("converted->" + target);
        Map target2 = EntityUtil.mapToField( target, "payer" );
        System.out.println("final map to field->" +target2);
        
    }

}
