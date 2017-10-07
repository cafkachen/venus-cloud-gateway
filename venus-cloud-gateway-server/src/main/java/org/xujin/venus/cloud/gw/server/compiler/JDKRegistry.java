package org.xujin.venus.cloud.gw.server.compiler;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author xujin
 *
 */
public class JDKRegistry {
    
	private static final JDKRegistry instance = new JDKRegistry();
    
    public static JDKRegistry getInstance() {
		return instance;
	}

	private static final ConcurrentMap<String, Class<?>> classes = new ConcurrentHashMap<>();

    private JDKRegistry() {

    }

    public void put(String key, Class<?> filter) {
        classes.put(key, filter);
    }

    public Class<?> get(String key) {
        return classes.get(key);
    }

    public void remove(String key) {
        classes.remove(key);
    }

    public int size() {
        return classes.size();
    }

    public void removeAll() {
        classes.clear();
    }
}
