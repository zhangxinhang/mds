package et.mds.vo;



public class ReleaseVO {
	private String main_key;
	private String value;
	private String ret_msg;

	public ReleaseVO() {
		super();
	}

	public ReleaseVO(String main_key, String value, String ret_msg) {
		super();
		this.main_key = main_key;
		this.value = value;
		this.ret_msg = ret_msg;
	}

	public String getMain_key() {
		return main_key;
	}

	public void setMain_key(String main_key) {
		this.main_key = main_key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRet_msg() {
		return ret_msg;
	}

	public void setRet_msg(String ret_msg) {
		this.ret_msg = ret_msg;
	}

	public static void main(String[] args) {
	/*	ReleaseVO rvo = new ReleaseVO("pro1", "123", "");
		ArrayList<ReleaseVO> rarr = new ArrayList<ReleaseVO>();
		rarr.add(rvo);
		String str = JSONArray.fromObject(rarr).toString();
		System.out.println(str);
		ReleaseVO[] arr = (ReleaseVO[]) JSONArray.toArray(JSONArray.fromObject(str), ReleaseVO.class);
		System.out.println(arr.length);*/
	}
}
