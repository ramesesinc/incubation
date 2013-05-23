import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;
/*
 * TestGetService.java
 * JUnit based test
 *
 * Created on April 12, 2013, 4:03 PM
 */

/**
 *
 * @author Elmo
 */
public class TestGetService extends TestCase {
    
    public TestGetService(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public interface PersonnelServiceIntf {
        Map open(Map p); 
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        Map map = new HashMap();
        map.put( "app.context", "default");
        map.put( "app.host", "localhost:8070");
        map.put( "app.cluster", "osiris3");
        ScriptServiceContext ctx = new ScriptServiceContext(map);
        PersonnelServiceIntf c = ctx.create( "PersonnelService" , PersonnelServiceIntf.class  );
        
        Map p = new HashMap();
        p.put("idno", "6");
        Map result = c.open(p);
        System.out.println(result.get("firstname"));
        System.out.println(result.get("lastname"));
        
    }

}
