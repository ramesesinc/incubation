/*
 * TestSchema.java
 * JUnit based test
 *
 * Created on April 6, 2014, 8:08 PM
 */

package schema;

import com.rameses.osiris3.persistence.EntityManagerModel;
import com.rameses.osiris3.persistence.SqlDialectModelBuilder;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlDialectModel.Field;
import com.rameses.osiris3.sql.SqlUnit;
import com.rameses.sql.dialect.MySqlDialect;
import java.util.ArrayList;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestSchema1 extends TestCase {
    
    public TestSchema1(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        EntityManagerModel em = new EntityManagerModel();
        em.setLimit(1);
        SchemaManager sm = SchemaManager.getInstance();
        em.setElement(sm.getElement("businessvariable"));
        
        SqlDialectModelBuilder sb = new SqlDialectModelBuilder();
        SqlDialectModel sqlm = sb.buildSelectModel(em, new ArrayList());
        for(Field f: sqlm.getFields()) {
            System.out.println(f.getName());
        };
        MySqlDialect dialect = new MySqlDialect();
        SqlUnit squ = dialect.getReadSqlUnit(sqlm);
        System.out.println(squ.getStatement());
    }

}
