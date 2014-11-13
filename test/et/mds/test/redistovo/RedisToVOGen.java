package et.mds.test.redistovo;



public class RedisToVOGen extends ToRedisVO {

	private String key;
	private String value;

	@Override
	public String getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.key;
	}
	@Override
	public String getPrimaryKeyField() {
		// TODO Auto-generated method stub
		return "key";
	}
	public static RedisToVOGen getInstanceOfKey(String key) {
		RedisToVOGen rtvt = new RedisToVOGen(key);
		rtvt.initObjFromRedis();
		return rtvt;
	}

	public RedisToVOGen() {

	}

	public RedisToVOGen(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	

	public static void main(String[] args) {
		RedisToVOGen ttvt = new RedisToVOGen("test");
		ttvt.setValueOfField("value", "zhang131243");
		RedisToVOGen ttvt1 = RedisToVOGen.getInstanceOfKey("test");
		System.out.println(ttvt.getValue());
	}

	
}
