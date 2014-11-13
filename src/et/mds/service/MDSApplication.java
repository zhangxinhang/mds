package et.mds.service;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class MDSApplication extends Application {
	HashSet<Object> singletons = new HashSet<Object>();

	public MDSApplication() {
		singletons.add(new MDSHandel());
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
