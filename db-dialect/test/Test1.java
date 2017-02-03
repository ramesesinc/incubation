import com.rameses.sql.dialect.functions.mssql.MONTH_DIFF;
import junit.framework.*;
/*
 * Test1.java
 * JUnit based test
 *
 * Created on April 30, 2012, 8:33 PM
 */

/**
 *
 * @author Elmo
 */
public class Test1 extends TestCase {
    
    public Test1(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    /*
    public void testHello() 
    {
        String sql = getSql("query5.sql");

        MsSqlDialect d = new MsSqlDialect();
        String res = d.getPagingStatement(sql, 0, 10, new String[]{"ugm.objid"});
        System.out.println( res );
    }
    
    private String getSql(String name) {
        StringWriter w = new StringWriter();
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(name);
            int i = -1;
            while( (i=is.read())!= -1 ) {
                w.write(i);
            }
            w.flush();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try{ is.close(); }catch(Exception e){}
        }
        return w.toString();
    }
    */
    
    
    public void testFunction() {
        MONTH_DIFF g = new MONTH_DIFF();
        g.addParam("B1");
        g.addParam("B2");
        System.out.println(g.toString());
    }
}
