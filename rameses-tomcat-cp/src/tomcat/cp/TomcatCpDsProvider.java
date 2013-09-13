/*
 * TomcatCpDSProvider.java
 *
 * Created on September 13, 2013, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tomcat.cp;

import com.rameses.osiris3.data.AbstractDataSource;
import com.rameses.osiris3.data.DsProvider;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
/**
 *
 * @author Elmo
 */
public class TomcatCpDsProvider implements DsProvider{
    
    
    public AbstractDataSource createDataSource(String name, Map map) {
        try {
            return new TomcatCPDataSource(name,map);
        } finally {
        }
    }
    
    public class TomcatCPDataSource extends AbstractDataSource {
        
        private DataSource datasource;
        
        public TomcatCPDataSource(String name, Map map) {
            init(map);
        }
        
        public void init(Map map) {
            super.init(map);
            try {
                PoolProperties p = new PoolProperties();
                p.setUrl(getUrl());
                p.setDriverClassName(getDriverClass());
                p.setUsername(getUser());
                p.setPassword(getPwd());
                
                p.setJmxEnabled(true);
                p.setTestWhileIdle(false);
                p.setTestOnBorrow(true);
                p.setValidationQuery("SELECT 1");
                p.setTestOnReturn(false);
                p.setValidationInterval(30000);
                p.setTimeBetweenEvictionRunsMillis(30000);
                
                int maxActive = 75;
                if( map.containsKey("maxActive")) {
                    maxActive = Integer.parseInt( map.get("maxActive")+"");
                }
                p.setMaxActive(maxActive);
                p.setMaxIdle(75);
                p.setInitialSize(10);
                p.setMaxWait(10000);
                p.setRemoveAbandonedTimeout(60);
                p.setMinEvictableIdleTimeMillis(30000);
                p.setMinIdle(10);
                p.setLogAbandoned(true);
                p.setRemoveAbandoned(true);
                p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
                datasource = new DataSource();
                datasource.setPoolProperties(p);
            }
            catch(RuntimeException re) {
                throw re;
            }
            catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        protected Connection createConnection(String username, String pwd) throws SQLException {
            return datasource.getConnection();
        }
        
        public void destroy() {
            datasource.close();
        }
        
    }
}
