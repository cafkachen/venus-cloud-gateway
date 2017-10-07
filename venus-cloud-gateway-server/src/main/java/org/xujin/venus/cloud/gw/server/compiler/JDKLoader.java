package org.xujin.venus.cloud.gw.server.compiler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.config.FilterModel;
import org.xujin.venus.cloud.gw.server.constant.Constants;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;
import org.xujin.venus.cloud.gw.server.filter.dynamic.FilterRegistry;

/**
 * 
 * @author xujin
 *
 */
public class JDKLoader {
	private static Logger logger = LoggerFactory.getLogger(JDKLoader.class);
	private final static JDKLoader instance = new JDKLoader();

	public static JDKLoader getInstance() {
		return instance;
	}

	public JDKRegistry getJdkRegistry() {
		return jdkRegistry;
	}

	private final JDKRegistry jdkRegistry = JDKRegistry.getInstance();
	private final ConcurrentHashMap<String, Long> filterClassLastModified = new ConcurrentHashMap<>();

	private JDKLoader() {
	}

	public Class<?> getFilterClass(String className) {
		return jdkRegistry.get(className.toUpperCase());
	}

	public boolean putFilterClass(File file) throws IOException {
		String[] nameParts = file.getName().split("\\.");
		if (!nameParts[1].toUpperCase().equals("JAVA")) {
			logger.error("{} : is not a java filter!", file.getName());
			return false;
		}
		String sName = nameParts[0].toUpperCase();
		if (filterClassLastModified.get(sName) != null
				&& (file.lastModified() != filterClassLastModified.get(sName))) {
			logger.debug("reloading filter" + sName);
			jdkRegistry.remove(sName);
		}
		Class<?> filterClass = jdkRegistry.get(sName);
		if (filterClass == null) {
			String code = FileUtils.readFileToString(file);
			org.xujin.venus.cloud.gw.server.compiler.Compiler compiler = new JdkCompiler();
			Class<?> clazz = compiler.compile(code, this.getClass().getClassLoader());
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				jdkRegistry.put(sName, clazz);
				filterClassLastModified.put(sName, file.lastModified());
				return true;
			}
		}
		logger.info(" {} java file has not changed!", file.getName());
		return false;
	}

	public boolean updateJAVAFilter(File file, FilterType type, FilterModel filterModel) {
		boolean putSuccess = false;
		try {
			putSuccess = putFilterClass(file);
			if (putSuccess) {
				FilterRegistry.INSTANCE.update(file, type, filterModel);
			}
		}
		catch (Exception e) {
			logger.error("update java Filter " + file.getName() + " failed!", e);
			return false;
		}
		return true;
	}

	public void deleteFiles(String directory) throws IOException {
		List<File> aFiles = getFiles(directory);
		Iterator<File> fileIterator = aFiles.iterator();
		while (fileIterator.hasNext()) {
			File file = fileIterator.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}

	private List<File> getFiles(String path) {
		List<File> list = new ArrayList<>();
		File directory = new File(path);
		if (!directory.isDirectory()) {
			throw new RuntimeException(
					directory.getAbsolutePath() + " is not a valid directory ");
		}
		File[] aFiles = directory.listFiles((dir, name) -> {
			return name.endsWith(".java");
		});
		if (aFiles != null) {
			list.addAll(Arrays.asList(aFiles));
		}
		return list;
	}

	public boolean clear() {
		jdkRegistry.removeAll();
		filterClassLastModified.clear();
		String groovyDirectory = Constants.CONFIG_PATH + "/plugin/filters";
		try {
			deleteFiles(groovyDirectory);
		}
		catch (IOException e) {
			logger.error("delete java class failed!");
			return false;
		}

		return true;
	}

}
