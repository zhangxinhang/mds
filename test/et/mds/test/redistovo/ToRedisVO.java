package et.mds.test.redistovo;

import java.lang.reflect.Field;

import redis.clients.jedis.Jedis;
import et.mds.bc.JedisPools;

/**
 * 类描述：
 * 
 * @version: 1.0
 * @author: ZXH
 * @version: 2014-6-19 上午9:43:28
 */
public abstract class ToRedisVO {


	/**
	 * return the VO primarykey value
	 * */
	public abstract String getPrimaryKey();

	/**
	 * return the vo primarykey field
	 * */
	public abstract String getPrimaryKeyField();

	public void initObjFromRedis() {
		Field[] fields = this.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getName().equals(this.getPrimaryKeyField())) {
					continue;
				}
				field.set(this, getValueOfField(field.getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * return the redis key of VO field. default class's
	 * name:primarykey:fieldname
	 * */
	private String getKeyOfField(String fieldName) {
		return this.getClass().getSimpleName() + ":" + this.getPrimaryKey()
				+ ":" + fieldName;

	}

	/**
	 * return the redis value of VO field.
	 * */
	public String getValueOfField(String fieldName) {
		String value = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		value = jedis.get(getKeyOfField(fieldName));
		JedisPools.getInstance().returnResource(jedis);
		return value;
	}

	/**
	 * set value to redis and vo
	 * */
	public Boolean setValueOfField(String fieldName, String fieldValue) {
		String ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.set(getKeyOfField(fieldName), fieldValue);
		JedisPools.getInstance().returnResource(jedis);
		try {
			Field field = this.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(this, fieldValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret.equals("OK") ? true : false;

	}

	/**
	 * set value to redis
	 * */
	private Boolean setValueOfFieldToRedis(String fieldName, String fieldValue) {
		String ret = null;
		Jedis jedis = JedisPools.getInstance().getResource();
		ret = jedis.set(getKeyOfField(fieldName), fieldValue);
		JedisPools.getInstance().returnResource(jedis);
		return ret.equals("OK") ? true : false;

	}

	/**
	 * set vo to redis from instance
	 * */
	public void saveVOToRedis() {
		Field[] fields = this.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getName().equals(this.getPrimaryKeyField())) {
					continue;
				}
				setValueOfFieldToRedis(field.getName(), field.get(this)
						.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
