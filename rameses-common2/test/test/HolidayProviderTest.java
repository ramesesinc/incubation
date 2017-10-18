/*
 * AlphaNumRandomTest.java
 * JUnit based test
 *
 * Created on December 17, 2013, 12:59 PM
 */
package test;

import com.rameses.functions.DateFunc;
import com.rameses.util.HolidayProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class HolidayProviderTest extends TestCase {

    public HolidayProviderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHoliday() throws Exception {
        HolidayProvider hp = new HolidayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date d = sdf.parse("2017-10-24");
        d = DateFunc.getFindNextWorkDay(d,hp,-1);
        System.out.println("day is " + d);

    }

    private class HolidayList implements HolidayProvider {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public boolean exists(Date date) {
            try {
                if (date.equals(sdf.parse("2017-10-20"))) {
                    return true;
                }
                if (date.equals(sdf.parse("2017-10-23"))) {
                    return true;
                }
                if (date.equals(sdf.parse("2017-10-24"))) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
             
        }
    }
}
