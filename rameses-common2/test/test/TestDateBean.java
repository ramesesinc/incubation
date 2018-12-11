/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.util.DateBean;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class TestDateBean extends TestCase {
    
    public TestDateBean(String testName) {
        super(testName);
    }

    public void test0() throws Exception {
        DateBean b = new DateBean("2018-01-01");
        System.out.println(b.getDate());
        System.out.println(b.getMonthEnd());
    }
}
