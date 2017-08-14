
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Elmo Nazareno
 */
public class TestSetRedis {
    
    public static void main(String[] args) throws Exception {
        String host = "107.21.113.74";
        //String host = "localhost";
        JedisPool pool = new JedisPool(new JedisPoolConfig(), host);
        Jedis jedis = pool.getResource();
        jedis.set("rameses.foo", "rameses.bar");
        //jedis.expire("rameses.foo", 10);
        pool.destroy();
    }
    
    
    
}
