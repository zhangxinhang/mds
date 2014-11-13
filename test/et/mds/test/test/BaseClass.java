package et.mds.test.test;

import java.lang.reflect.Field;

public class BaseClass {

	public String a;
	private int b;
	private final BaseClass bc = setBC();

	// public BaseClass() {
	// bc = setBC();
	// }

	private BaseClass setBC() {
		return this;
	}

	public void getAll(BaseClass bas) {
		Class clazz = bas.getClass();
		Field[] fields = clazz.getDeclaredFields();
		try {
			for(Field f:fields){
				
				System.out.println(bas.getClass().getSimpleName()+f.getName());
			}
			fields[0].setAccessible(true);
			fields[0].set(bas,"zhang"+fields[0].get(bas)+"test");
			fields[0].setAccessible(false);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}