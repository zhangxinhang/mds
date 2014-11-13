package et.mds.bc;

import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPools {
	private static JedisPool pool = null;
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("redis");
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(bundle
				.getString("redis.pool.maxActive")));
		config.setMaxIdle(Integer.valueOf(bundle
				.getString("redis.pool.maxIdle")));
		config.setMaxWaitMillis(Long.valueOf(bundle
				.getString("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf(bundle
				.getString("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(bundle
				.getString("redis.pool.testOnReturn")));
		pool = new JedisPool(config, bundle.getString("redis.ip"),
				Integer.valueOf(bundle.getString("redis.port")));
	}

	public static JedisPool getInstance() {
		return pool;
	}

	public static void main(String[] args) {

		Jedis tt = JedisPools.pool.getResource();
		/*
		 * tt.set("foo" + "-bar", "bar"); System.out.println(tt.get("foo-bar"));
		 * tt.flushDB(); System.out.println(tt.get("foo-bar")); tt.keys(""); //
		 * tt.del(tt.keys(""));
		 * 
		 * tt.hset("", "key1", "1"); tt.hset("", "key2", "1"); tt.hset("",
		 * "key3", "1"); System.out.println(tt.hgetAll(""));
		 */
		String value = tt.set("test", "123");
		System.out.println(value.equals("OK"));

	}
}
