package et.mds.test.redistovo;

import java.util.Set;

import redis.clients.jedis.Jedis;
import et.mds.bc.JedisPools;

/**
 * 类描述：封装基于jedis常用redis方法， packaging common redis method for java base on jedis.
 * 
 * @version: 1.0
 * @author: ZXH
 * @version: 2014-6-19 上午9:52:55
 */
public class RedisCallForJava {

	/**
	 * put the string value as value of the key.
	 */
	public static Boolean put(String key, String value) {
		String ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.set(key, value);
		JedisPools.getInstance().returnResource(jedis);
		return ret.equals("OK");
	}

	/**
	 * put the string value as value of the key And set a timeout on the
	 * specified key. After the timeout the key will be automatically deleted by
	 * the server.
	 * */
	public static Boolean put(String key, String value, int seconds) {
		String ret = null;
		long retexp = 0;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.set(key, value);
		retexp = jedis.expire(key, seconds);
		JedisPools.getInstance().returnResource(jedis);
		return ret.equals("OK") && retexp == 1;

	}

	/**
	 * Get the value of the specified key. If the key does not exist null is
	 * returned.
	 */
	public static String get(String key) {
		String ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.get(key);
		JedisPools.getInstance().returnResource(jedis);
		return ret;
	}

	/**
	 * Remove the specified keys. If a given key does not exist no operation is
	 * performed for this key. The command returns the number of keys removed.
	 */
	public static Boolean remove(String... keys) {
		Long ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.del(keys);
		JedisPools.getInstance().returnResource(jedis);
		return ret == keys.length;
	}

	/**
	 * Add the specified member to the set value stored at key. If member is
	 * already a member of the set no operation is performed. If key does not
	 * exist a new set with the specified member as sole member is created. If
	 * the key exists but does not hold a set value an error is returned.
	 */
	public static Boolean sPut(String key, String... value) {
		Long ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.sadd(key, value);
		JedisPools.getInstance().returnResource(jedis);
		return ret == 1;
	}

	/**
	 * Remove the specified member from the set value stored at key. If member
	 * was not a member of the set no operation is performed. If key does not
	 * hold a set value an error is returned.
	 * <p>
	 * Time complexity O(1)
	 * 
	 * @param key
	 * @param value
	 * @return Boolean reply, specifically: ture if the new element was removed
	 *         false if the new element was not a member of the set
	 */
	public static Boolean sRemove(String key, String... value) {
		Long ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.srem(key, value);
		JedisPools.getInstance().returnResource(jedis);
		return ret == 1;
	}

	/**
	 * Return all the members (elements) of the set value stored at key.
	 */
	public static Set<String> sGet(String key) {
		Set<String> ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.smembers(key);
		JedisPools.getInstance().returnResource(jedis);
		return ret;
	}
	
	/** 
	 * @Title: synBegin 
	 * @Description: 同步方法开始。before synchronized  
	 * @param key
	 * @return      
	 */
	private static Jedis synBegin(String key) {
		Jedis jedis = JedisPools.getInstance().getResource();
		Long lock = (long) 0;
		while (lock != 1) {
			lock = jedis.setnx("lock" + key, "lockvalue");
			if (lock == 1) {
				jedis.expire("lock" + key, 1);// 超时删除1s
				break;
			}
		}
		return jedis;
		
	}
	
	/** 
	 * @Title: synEnd 
	 * @Description: 同步方法结束。End synchronized
	 * @param jedis
	 * @param key      
	 */
	private static void synEnd(Jedis jedis,String key){
		jedis.del("lock" + key);
		JedisPools.getInstance().returnResource(jedis);
	}
	
	
	
	
	
}
