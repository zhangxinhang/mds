package et.mds.bc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import et.mds.util.JdbcUtil;
import et.mds.vo.ReleaseVO;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class InitPlaceOrder {

	private static Logger log = Logger.getLogger(InitPlaceOrder.class);

	/**
	 * 初始化可下单量key-value对应值
	 * 
	 * */
	public static void initPlaceOrder() {
		Jedis jedis = JedisPools.getInstance().getResource();
		log.info("清除发布量历史数据");
		// MARK TODO 需考虑
		jedis.flushDB();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = JdbcUtil.getConnection();
			ps = conn.prepareStatement(getQuerySql());
			rs = ps.executeQuery();
			while (rs.next()) {
				jedis.set(
						rs.getString("organization_id") + ":"
								+ rs.getString("PRODUCE_ID"),
						rs.getString("count"));
			}

			ps.clearParameters();
			ps = conn.prepareStatement(getQueryFormulaSql());
			rs = ps.executeQuery();
			while (rs.next()) {
				String key = rs.getString("order_date")
						+ ":"
						+ rs.getString("stockgroup_id")+ ":"
								+ rs.getString("formula_name");
				jedis.set(key, rs.getString("quantity"));
				jedis.expire(key, 24 * 60 * 60);
			}

		} catch (SQLException e) {
			log.error("发布量初始化", e);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn, ps, rs);
			JedisPools.getInstance().returnResource(jedis);
		}

	}

	private static String getQueryFormulaSql() throws SQLException {
		String sql = "SELECT " + "	oi.ORDER_DATE, " + "	oi.STOCKGROUP_ID, "
				+ "	bpf.FORMULA_NAME, "
				+ "	sum(oib.PRODUCE_QUANTITY) quantity " + "	 " + "FROM "
				+ "	order_info oi "
				+ "JOIN order_info_b oib ON oib.ORDER_ID = oi.ORDER_ID "
				+ "JOIN bdp_produce bp ON bp.PRODUCE_ID = oib.PRODUCE_ID "
				+ "JOIN bdp_formula bpf ON bpf.FORMULA_ID = bp.FORMULA_ID "
				+ "WHERE " + "	oi.TYPE = 1 " + "AND oi.STATE != 3 "
				+ "AND oi.ORDER_DATE = SUBSTR(SYSDATE() FROM 1 FOR 10) "
				+ "GROUP BY " + "	bpf.FORMULA_ID, " + "	oi.STOCKGROUP_ID";
		log.debug(sql);
		return sql;
	}

	private static String getQuerySql() throws SQLException {
		// 当前库存+未完成订单分仓量-未完成订单量
		String sql = "SELECT "
				+ "	ord.produce_id, "
				+ "	ord.organization_id, "
				+ "	sum(ord.count) count "
				+ "FROM "
				+ "	( "
				+ "		SELECT "
				+ "			bp.PRODUCE_ID, "
				+ "			rso.organization_id organization_id, "
				+ "			sum(sc.usable_num) count "
				+ "		FROM "
				+ "			bdp_produce bp "
				+ "		JOIN st_currentstock sc ON sc.produce_id = bp.PRODUCE_ID "
				+ "		JOIN re_stock_organization rso ON rso.stordoc_id = sc.stordoc_id "
				+ "		AND bp.STATE = 0 "
				+ "		AND rso.type = 1 "
				+ "		GROUP BY "
				+ "			bp.PRODUCE_ID, "
				+ "			rso.organization_id "
				+ "		UNION ALL "
				+ "			SELECT "
				+ "				dbs.PRODUCE_ID, "
				+ "				oi.STOCKGROUP_ID organization_id, "
				+ "				sum(dbs.QUANTITY) "
				+ "			FROM "
				+ "				detail_balancestock dbs "
				+ "			JOIN order_info oi ON oi.ORDER_ID = dbs.ORDER_ID "
				+ "			WHERE "
				+ "				oi.STATE = 0 "
				+ "			AND dbs.type = 0 "
				+ "			GROUP BY "
				+ "				dbs.PRODUCE_ID, "
				+ "				oi.STOCKGROUP_ID "
				+ "			UNION ALL "
				+ "				SELECT "
				+ "					oib.PRODUCE_ID, "
				+ "					oi.STOCKGROUP_ID organization_id ,- sum(oib.PRODUCE_QUANTITY) "
				+ "				FROM " + "					order_info_b oib "
				+ "				JOIN order_info oi ON oi.ORDER_ID = oib.ORDER_ID "
				+ "				WHERE " + "					oi.STATE = 0 " + "				GROUP BY "
				+ "					oib.PRODUCE_ID, " + "					oi.STOCKGROUP_ID "
				+ "	) ord " + "GROUP BY " + "	ord.produce_id, "
				+ "	ord.organization_id";
		log.debug(sql);
		return sql;
	}

	/**
	 * 
	 * 检查当前发布量是否有错
	 * */
	public static Boolean checkError() {
		Jedis jedis = JedisPools.getInstance().getResource();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Boolean flag = true;
		try {
			conn = JdbcUtil.getConnection();
			ps = conn.prepareStatement(getQuerySql());
			rs = ps.executeQuery();
			while (rs.next()) {
				String key = rs.getString("organization_id") + ":"
						+ rs.getString("PRODUCE_ID");
				if (!rs.getString("count").equals(jedis.get(key))) {
					flag = false;
					log.error(key + "--实际量为：" + rs.getString("count")
							+ "--显示量为：" + jedis.get(key));
				}
			}
		} catch (SQLException e) {
			log.error("发布量检查", e);
			e.printStackTrace();
		} finally {
			JedisPools.getInstance().returnResource(jedis);
			JdbcUtil.close(conn, ps, rs);
		}
		return flag;

	}

	/**
	 * 通过库存组织初始化发布量
	 * 
	 * */

	public static void InitReleaseFromGroup(ReleaseVO[] rvo, String group) {
		Jedis jedis = JedisPools.getInstance().getResource();
		for (int i = 0; i < rvo.length; i++) {
			jedis.set(group + rvo[i].getMain_key(), rvo[i].getValue());
		}
		JedisPools.getInstance().returnResource(jedis);

	}

	public static void main(String[] args) {
		// initPlaceOrder();
		checkError();
	}
}