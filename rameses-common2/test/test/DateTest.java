/*
 * DateTest.java
 * JUnit based test
 *
 * Created on April 12, 2014, 8:27 PM
 */

package test;

import com.rameses.functions.DateFunc;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public void testFindNextWorkday() {
        Date d1 = java.sql.Date.valueOf("2016-12-31");
        List<Date> list = new ArrayList();
        list.add( java.sql.Date.valueOf("2017-03-05"));
        list.add( java.sql.Date.valueOf("2017-01-02"));
        list.add( java.sql.Date.valueOf("2017-01-03"));
        list.add( java.sql.Date.valueOf("2017-03-10"));
        list.add( java.sql.Date.valueOf("2017-01-04"));
        list.add( java.sql.Date.valueOf("2017-01-05"));
        list.add( java.sql.Date.valueOf("2017-01-06"));
        list.add( java.sql.Date.valueOf("2017-01-07"));
        list.add( java.sql.Date.valueOf("2017-01-09"));
        for( Date d: list ) {
            System.out.println(d);
        }
        
        Date d2 = DateFunc.getFindNextWorkDay(d1, list);
        System.out.println("New date is " + d2);
    }

}

