/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.sql.dialect.functions.mysql.DATE_ADD;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public class TestFunction extends TestCase {

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testCreate() throws Exception {
        DATE_ADD d = new DATE_ADD();
        d.addParam("NOW()");
        d.addParam("-1");
        d.addParam("DAY");
        System.out.println(d.toString());
    }

    
}
