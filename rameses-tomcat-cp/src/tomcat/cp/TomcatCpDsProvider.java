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
public class TomcatCpDsProvider implements DsProvider 
{
    public AbstractDataSource createDataSource(String name, Map map) {
        try {
            return new TomcatCPDataSource(name,map);
        } catch(Throwable t) {
            t.printStackTrace();
            return null; 
        } finally { 
            
        }
    }
    
    public class TomcatCPDataSource extends AbstractDataSource 
    {
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
                p.setTestOnReturn(false); 
                p.setTestOnBorrow(true); 
                
                String validationQuery = null; 
                if (map.containsKey("validationQuery")) {
                    Object ov = map.get("validationQuery");
                    validationQuery = (ov == null? null: ov.toString()); 
                } 
                if (validationQuery == null || validationQuery.trim().length() == 0) {
                    validationQuery = "SELECT 1"; 
                }
                p.setValidationQuery(validationQuery);
                
                int validationInterval = 30000;
                if (map.containsKey("validationInterval")) {
                    Object ov = map.get("validationInterval");
                    if (ov != null) validationInterval = Integer.parseInt(ov.toString()); 
                }                 
                p.setValidationInterval(validationInterval); 
                
                int timeBetweenEvictionRunsMillis = 30000;
                if (map.containsKey("timeBetweenEvictionRunsMillis")) {
                    Object ov = map.get("timeBetweenEvictionRunsMillis");
                    if (ov != null) timeBetweenEvictionRunsMillis = Integer.parseInt(ov.toString()); 
                } 
                p.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
                
                int initialSize = 5;
                if (map.containsKey("initialSize")) {
                    Object ov = map.get("initialSize");
                    if (ov != null) initialSize = Integer.parseInt(ov.toString()); 
                } 
                p.setInitialSize(initialSize); 

                int minIdle = initialSize;
                if (map.containsKey("minIdle")) {
                    Object ov = map.get("minIdle");
                    if (ov != null) minIdle = Integer.parseInt(ov.toString()); 
                }                 
                p.setMinIdle(minIdle); 
                
                int minEvictableIdleTimeMillis = 60000;
                if (map.containsKey("minEvictableIdleTimeMillis")) {
                    Object ov = map.get("minEvictableIdleTimeMillis");
                    if (ov != null) minEvictableIdleTimeMillis = Integer.parseInt(ov.toString()); 
                } 
                p.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis); 
                
                int maxActive = 100;
                if (map.containsKey("maxActive")) {
                    Object ov = map.get("maxActive");
                    if (ov != null) maxActive = Integer.parseInt(ov.toString()); 
                } 
                p.setMaxActive(maxActive);
                
                int maxIdle = maxActive;
                if (map.containsKey("maxIdle")) {
                    Object ov = map.get("maxIdle");
                    if (ov != null) maxIdle = Integer.parseInt(ov.toString()); 
                }                                 
                p.setMaxIdle(maxIdle);
                
                int maxWait = 30000;
                if (map.containsKey("maxWait")) {
                    Object ov = map.get("maxWait");
                    if (ov != null) maxWait = Integer.parseInt(ov.toString()); 
                } 
                p.setMaxWait(maxWait);
                
                //database connection pool leak settings
                p.setRemoveAbandoned(true);
                p.setLogAbandoned(true);

                int removeAbandonedTimeout = 300;
                if (map.containsKey("removeAbandonedTimeout")) {
                    Object ov = map.get("removeAbandonedTimeout");
                    if (ov != null) removeAbandonedTimeout = Integer.parseInt(ov.toString()); 
                }                 
                p.setRemoveAbandonedTimeout(removeAbandonedTimeout); 
                //
                //
                p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
                datasource = new DataSource();
                datasource.setPoolProperties(p); 
                System.out.println("#######################################");
                System.out.println("# TomcatCPDataSource datasource config:");
                System.out.println("#######################################");
                System.out.println(" validationQuery="+p.getValidationQuery());
                System.out.println(" validationInterval="+p.getValidationInterval());
                System.out.println(" timeBetweenEvictionRunsMillis="+p.getTimeBetweenEvictionRunsMillis());
                System.out.println(" initialSize="+p.getInitialSize());
                System.out.println(" minIdle="+p.getMinIdle());
                System.out.println(" minEvictableIdleTimeMillis="+p.getMinEvictableIdleTimeMillis());
                System.out.println(" maxActive="+p.getMaxActive());
                System.out.println(" maxIdle="+p.getMaxIdle());
                System.out.println(" maxWait="+p.getMaxWait());
                System.out.println(" removeAbandonedTimeout="+p.getRemoveAbandonedTimeout());
                System.out.println(" ");
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception e) {
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
