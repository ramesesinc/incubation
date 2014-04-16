/*
 * DateTest.java
 * JUnit based test
 *
 * Created on April 12, 2014, 8:27 PM
 */

package test;

import com.rameses.util.DateUtil;
import java.util.Date;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class DateTest extends TestCase {
    
    public DateTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        Date d1 = java.sql.Date.valueOf("2010-01-20");
        Date d2 = DateUtil.add( d1, "1M" );
        System.out.println(d2);
    }

}
