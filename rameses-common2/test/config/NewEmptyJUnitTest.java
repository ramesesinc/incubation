/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on March 1, 2013, 2:53 PM
 */

package config;

import com.rameses.io.StreamUtil;
import java.io.File;
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
    public void test1() throws Exception {
        File file = new File("c:/temp/test-conf.txt"); 
        String str = StreamUtil.toString( file.toURI().toURL().openStream() ); 
        
        System.getProperties().put("gdx.host", "192.168.254.10:9070"); 
        
        int startidx = 0;
        StringBuilder buff = new StringBuilder();
        while ( true ) {
            int idx0 = str.indexOf("${", startidx); 
            if ( idx0 < 0 ) break; 
            
            int idx1 = str.indexOf("}", idx0); 
            if ( idx1 < 0 ) break; 
            
            buff.append(str.substring(startidx, idx0)); 
            
            String skey = str.substring(idx0+2, idx1); 
            Object objval = System.getProperty( skey ); 
            if (objval == null) objval = System.getenv( skey ); 
            
            if (objval == null) { 
                buff.append(str.substring(idx0, idx1+1)); 
            } else { 
                buff.append( objval );  
            } 
            
            startidx = idx1 + 1; 
        }
        
        if ( startidx < str.length()) {
            buff.append(str.substring(startidx)); 
        }
        System.out.println( buff );
    }
}
