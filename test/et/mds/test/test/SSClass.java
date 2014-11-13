package et.mds.test.test;

public class SSClass extends BaseClass {

	private String ss;

	public SSClass(int i) {
		ss = i+"";
		this.getAll(this);
		
	}
	
	public String getSs() {
		return ss;
	}

	public void setSs(String ss) {
		this.ss = ss;
	}

	public static void main(String[] args) {
		SSClass sc = new SSClass(1);
		//sc.getAll();
		SSClass sc1 = new SSClass(2);
		//sc.getAll();
		System.out.println(sc.getSs()+"------");
		System.out.println(sc1.getSs()+"------");
	}

}