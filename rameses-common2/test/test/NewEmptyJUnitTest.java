/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.functions.DateFunc;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author dell
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        System.out.println("Date " + new Date());
        System.out.println("today is " + Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        System.out.println("thu is " + Calendar.SATURDAY);
        Date d = DateFunc.getDate(2016, 1, 1);
        System.out.println( DateFunc.getMonthAdd(d, 1) );
        //System.out.println("date is " + d);
        //System.out.println("next date ->"+DateFunc.getFindNextWorkDay(d, null));
    }
}
