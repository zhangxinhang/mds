package et.mds.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import et.mds.bc.HandelReleaseChange;
/**
 * 
 * 初始化数据，使得tomcat在第一次加载就同步数据。
 * 
 * */
public class InitOnStart implements ServletContextListener  {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		new HandelReleaseChange();
	}

}
