package et.mds.test.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class SentinelTest {

	@Test
	public void test() {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(1);
		config.setBlockWhenExhausted(false);
		Set<String> sentinels = new HashSet<String>();
		sentinels.add(new HostAndPort("192.168.0.36", 26379).toString());
		JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels,
				config, 10000000);
		Jedis jedis = pool.getResource();

		Jedis jedis2 = null;

		try {
			jedis.set("hello", "jedis");
			Transaction t = jedis.multi();
			t.set("hello", "world");
			pool.returnResource(jedis);

			jedis2 = pool.getResource();

			assertTrue(jedis == jedis2);
			assertEquals("jedis", jedis2.get("hello"));
		} catch (JedisConnectionException e) {
			if (jedis2 != null) {
				pool.returnBrokenResource(jedis2);
				jedis2 = null;
			}
		} finally {
			if (jedis2 != null)
				pool.returnResource(jedis2);

			pool.destroy();
		}

	}

	public static void main(String[] args) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(1);
		config.setBlockWhenExhausted(false);
		Set<String> sentinels = new HashSet<String>();
		sentinels.add(new HostAndPort("192.168.0.36", 26379).toString());
		JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels,
				config, 10000000);
		Jedis jedis = pool.getResource();
		jedis.set("hello", "world");
		System.out.println(jedis.get("hello"));
		pool.destroy();
	}
}
