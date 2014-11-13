package et.mds.test.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.junit.Before;
import org.junit.Test;

import et.mds.bc.JedisPools;
import et.mds.vo.ReleaseVO;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JsonTest {
	
	@Before
	public void testbefore() {
	}
	
	
	@Test
	public void testjson() {
		Jedis jedis = JedisPools.getInstance().getResource();
		List<ReleaseVO> rvo = new ArrayList<ReleaseVO>();
		Map map = new HashMap();
		map.put("123", 456);
		map.put("124", 456);
		map.put("124", 456);
		for (int i = 0; i < 5; i++) {
			ReleaseVO vo = new ReleaseVO();
			vo.setMain_key("key"+i);
			vo.setRet_msg("msg"+i);
			vo.setValue("value"+i);
			//vo.setTestMap(map);
			rvo.add(vo);
		}
		String jsonStr = JSONArray.fromObject(rvo).toString();
		jedis.set("rvos", jsonStr);
		ReleaseVO[] arr = (ReleaseVO[]) JSONArray.toArray(JSONArray.fromObject(jedis.get("rvos")), ReleaseVO.class);
		//System.out.println(arr[1].getTestMap().get("124"));
	}
}
