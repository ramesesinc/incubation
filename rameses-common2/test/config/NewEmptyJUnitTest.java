/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on March 1, 2013, 2:53 PM
 */

package config;

import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        ConfigProperties conf = new ConfigProperties("c:/osiris3/sample.conf");
        System.out.println(conf.getProperty( "app.conf" ));
        
        System.out.println(conf.getProperty( "mysql:host" ));
        System.out.println(conf.getProperty( "potsgresql:host" ));
        conf.put( "potsgresql:host", "newhost_postgresql" );
        conf.put( "potsgresql:password", "atari" );
        
        conf.put( "mysql:port", "3306" );
        
        conf.update();
        System.out.println("****************************");
        for( Object o: conf.getProperties( "potsgresql" ).values() ) {
            System.out.println(o);
        }
        
    }

}
