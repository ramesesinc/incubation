/*
 * AlphaNumRandomTest.java
 * JUnit based test
 *
 * Created on December 17, 2013, 12:59 PM
 */

package test;

import com.rameses.util.KeyGen;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class AlphaNumRandomTest extends TestCase {
    
    public AlphaNumRandomTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        int i = 0;
        Map map = new HashMap();
        while(true) {
            i++;
            String key = KeyGen.generateAlphanumKey(null,6);
            if(map.containsKey(key)) {
                break;
            }
            map.put( key, key );
        }
        System.out.println("loops " + i);    
    }

}
