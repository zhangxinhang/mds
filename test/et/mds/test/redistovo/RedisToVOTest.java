package et.mds.test.redistovo;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class RedisToVOTest {

	@Test
	public void test() {
		RedisToVOGen ttvt = new RedisToVOGen();
		ttvt.setKey("zhang");
		ttvt.setValue("xinghang");
		ttvt.saveVOToRedis();
	}
	@Test
	public void testget() {
		RedisToVOGen ttvt = RedisToVOGen.getInstanceOfKey("zhang1d");
		System.out.println(ttvt.getKey()+":"+ttvt.getValue());
	}
	
	@Test
	public void testRedisCallGetSet(){
		Assert.assertTrue(RedisCallForJava.put("zhang", "xinhang"));
		Assert.assertEquals(RedisCallForJava.get("zhang"),"xinhang");
		Assert.assertTrue(RedisCallForJava.put("zhang1", "xinhang",10));
		Set<String> set = new HashSet<String>();
		set.add("zhang");
		set.add("zhang1");
		String[] strarr = set.toArray(new String[set.size()]);
		Assert.assertTrue(RedisCallForJava.remove(strarr));
		
	}
	
	@Test
	public void testRedisCallSput(){
		
		Assert.assertTrue(RedisCallForJava.sPut("zhang", "xinhang","loop"));
		Set<String> set  =  RedisCallForJava.sGet("zhang");
		for(String s:set){
			System.out.println(s);
		}
	}
		
	
}
