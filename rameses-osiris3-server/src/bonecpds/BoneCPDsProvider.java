/*
 * BoneCPDsProvider.java
 *
 * Created on January 14, 2013, 11:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bonecpds;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.rameses.osiris3.data.AbstractDataSource;
import com.rameses.osiris3.data.DsProvider;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class BoneCPDsProvider implements DsProvider {
    
    public AbstractDataSource createDataSource(String name, Map map) {
        return new BoneCPDataSource(name,map);
    }
    
    
    public class BoneCPDataSource extends AbstractDataSource {
        
        private BoneCP connectionPool = null;
        private BoneCPConfig config;
        public BoneCPDataSource(String name, Map map) {
            init(map);
        }
        
        public void init(Map map) 
        {
            super.init(map);
            try 
            {
                Class.forName(getDriverClass());
                config = new BoneCPConfig();
                config.setJdbcUrl(getUrl());
                config.setUsername(getUser());
                config.setPassword(getPwd());
                config.setMinConnectionsPerPartition(getMinPoolSize());
                config.setMaxConnectionsPerPartition(getMaxPoolSize());
                config.setPartitionCount(1);
                connectionPool = new BoneCP(config);                
            }
            catch(RuntimeException re) {
                throw re;
            }
            catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        
        protected Connection createConnection(String username, String pwd) throws SQLException {
           return connectionPool.getConnection();
        }
        
        public void destroy() {
            System.out.println("shutting down BoneCPDataSource");
            connectionPool.shutdown();
        }
    }
    
}
