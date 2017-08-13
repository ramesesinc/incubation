
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
public class TestRemoveRedis {
    
    public static void main(String[] args) throws Exception {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        Jedis jedis = pool.getResource();
        jedis.del("rameses.foo");
        //jedis.expire("rameses.foo", 10);
        pool.destroy();
    }
    
    
    
}
