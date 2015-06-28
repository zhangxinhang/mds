package et.mds.bc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import et.mds.vo.ReleaseVO;

import redis.clients.jedis.Jedis;

/**
 * 发布量核心算法。
 * 
 * 创建日期：(2014-2-28)
 * 
 * @author zxh
 * 
 */
public class HandelReleaseChange {
	private static Logger log = Logger.getLogger(HandelReleaseChange.class);
	private static DecimalFormat df = new DecimalFormat("#0.0000");// 格式化数据
	static {
		log.info("初始化数据");
		InitPlaceOrder.initPlaceOrder();
	}

	/**
	 * 单个产品的处理方法。
	 * 
	 * 创建日期：(2014-3-10)
	 * 
	 * @author zxh
	 * @param key
	 *            :标识; count:数量 ;isadd：true + false -; isNegative
	 *            可下单量是否可为负（主要针对其它出入库单据，减少可用量造成）; isNoKeyToAdd
	 *            是否在不存在是添加此key.(主要针对入库新品在可下单量中不存在的情况) expire 设置添加key的超时删除时间
	 * @return false:错误，true 正确
	 */
	public static boolean modifiDiffQuantityHandel(String key, Double count,
			Boolean isAdd, Boolean isNegative, Boolean isNoKeyToAdd,
			int expire, Jedis jedis) {
		boolean flag = true;
		if (jedis.exists(key)) {// 判断可下单量中是否存在此产品
			if (isAdd) {
				jedis.set(key,
						df.format(Double.parseDouble(jedis.get(key)) + count));
			} else {
				if (Double.parseDouble(jedis.get(key)) >= count || isNegative) {
					jedis.set(
							key,
							df.format(Double.parseDouble(jedis.get(key))
									- count));
				} else {
					// 数量不足
					flag = false;
				}
			}
		} else {
			if (isNoKeyToAdd) {
				if(isAdd){//对于新增key值的正负值判断。
					jedis.set(key, df.format(count));
				}else{
					jedis.set(key, df.format(0-count));
				}
			} else {
				flag = false;
			}
		}
		//定时删除无用缓存数据
		if (expire > 0) {
			jedis.expire(key, expire);
		}
		return flag;

	}

	/**
	 * 方法描述：算法核心并发控制，core algorithm concurreny control
	 * 
	 * @param:
	 * @return: boolean
	 * @author: ZXH
	 * @version: 2014-6-20 上午9:45:50
	 */
	public static boolean modifiDiffQuantity(String key, Double count,
			Boolean isAdd, Boolean isNegative, Boolean isNoKeyToAdd, int expire) {
		boolean flag = false;
		Jedis jedis = JedisPools.getInstance().getResource();
		Long lock = (long) 0;
		while (lock != 1) {
			lock = jedis.setnx("lock" + key, "lockvalue");
			if (lock == 1) {
				jedis.expire("lock" + key, 1);// 超时删除1s
				break;
			} else {
				// TODO can detele
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					log.error("线程阻断", e);
				}
			}
		}
		flag = modifiDiffQuantityHandel(key, count, isAdd, isNegative,
				isNoKeyToAdd, expire, jedis);
		jedis.del("lock" + key);
		JedisPools.getInstance().returnResource(jedis);
		return flag;
	}

	/**
	 * 订单批量处理方法。
	 * 
	 * 创建日期：(2014-2-28)
	 * 
	 * @author zxh
	 * @param SuperVO
	 *            []：产品数组 、keyFields 查询主键的集合（或为产品ID加库存组织ＩＤ）、countField 数量计算字段、
	 *            isadd：true + false -
	 * @return ArrayList ret 批量执行正确：ret[0]:true。错误ret[0]:false
	 *         ret[1]:未正确处理的产品的当前差异量的集合。
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList modifiDiffQuantitySuperVOS(ReleaseVO[] vos,
			String second_key, Boolean isadd, Boolean isNegative,
			Boolean isNoKeyToAdd, int expire) {

		ArrayList<ReleaseVO> backOrderPro = new ArrayList<ReleaseVO>();// 临时放置已完成的产品，用于回退。
		ArrayList<ReleaseVO> orderPro = new ArrayList<ReleaseVO>();// 存储下单量不足的产品，用于返回
		ArrayList ret = new ArrayList();
		for (int i = 0; i < vos.length; i++) {

			if (modifiDiffQuantity(getTrueKey(vos[i], second_key),
					Double.parseDouble(vos[i].getValue()), isadd, isNegative,
					isNoKeyToAdd, expire)) {
				backOrderPro.add(vos[i]);
			} else {
				orderPro.add(vos[i]);
			}
		}

		if (backOrderPro.size() == vos.length) {
			ret.add(true);
		} else {
			ret.add(false);
			// 回退已减的数
			for (int i = 0; i < backOrderPro.size(); i++) {
				modifiDiffQuantity(getTrueKey(backOrderPro.get(i), second_key),
						Double.parseDouble(backOrderPro.get(i).getValue()),
						!isadd, isNegative, isNoKeyToAdd, expire);
			}
			// 将产品当前的可下单量返回。
			for (int i = 0; i < orderPro.size(); i++) {
				orderPro.get(i)
						.setRet_msg(
								getDiffQuantity(getTrueKey(orderPro.get(i),
										second_key)));
			}

			ret.add(orderPro);
		}
		return ret;

	}

	/**
	 * 删除key值。
	 * 
	 * 
	 * */
	@SuppressWarnings("unused")
	private static boolean removeKey(String key) {
		Jedis jedis = JedisPools.getInstance().getResource();
		Boolean flag = jedis.del(key) != null;
		JedisPools.getInstance().returnResource(jedis);
		return flag;

	}

	/**
	 * 获取真实主键，即组合模式的主键值。
	 * */
	private static String getTrueKey(ReleaseVO vo, String senond_key) {
		return senond_key + ":" + vo.getMain_key();
	}

	/**
	 * 
	 * 查询单个产品当前可下单量
	 * 
	 * */
	private static String getDiffQuantity(String key) {
		Jedis jedis = JedisPools.getInstance().getResource();
		double ret = 0;
		if (jedis.exists(key)) {
			if (Double.parseDouble(jedis.get(key)) > 0) {
				ret = Double.parseDouble(jedis.get(key));
			}
		}
		JedisPools.getInstance().returnResource(jedis);
		return df.format(ret);
	}

	/**
	 * 
	 * 查询一组产品当前可下单量
	 * 
	 * 
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getDiffQuantityArr(ArrayList<String> proArr) {
		ArrayList ret = new ArrayList();
		for (String pro_id : proArr) {
			String[] pro = new String[2];
			pro[0] = pro_id;
			pro[1] = getDiffQuantity(pro_id);
			ret.add(pro);
		}
		return ret;
	}

	public static void main(String[] args) {
	}
}
