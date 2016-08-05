/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.sql.dialect.functions.mssql.DAY_DIFF;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public class TestFunction extends TestCase {

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testCreate() throws Exception {
        DAY_DIFF d = new DAY_DIFF();
        d.addParam("'2016-01-01'");
        d.addParam("NOW()");
        System.out.println(d.toString());
    }

    
}
