package com.alibaba.druid.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.druid.util.JdbcUtils;

public class FilterManager {
	private static final ConcurrentHashMap<String, String> aliasMap = new ConcurrentHashMap<String, String>();

	static {
		try {
			Properties filterProperties = loadFilterConfig();
			for (Map.Entry<Object, Object> entry : filterProperties.entrySet()) {
				String key = (String) entry.getKey();
				if (key.startsWith("druid.filters.")) {
					String name = key.substring("druid.filters.".length());
					aliasMap.put(name, (String) entry.getValue());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String getFilter(String alias) {
		return aliasMap.get(alias);
	}

	public static Properties loadFilterConfig() throws IOException {
		Properties filterProperties = new Properties();

		loadFilterConfig(filterProperties, ClassLoader.getSystemClassLoader());
		loadFilterConfig(filterProperties, Thread.currentThread().getContextClassLoader());

		return filterProperties;
	}

	private static void loadFilterConfig(Properties filterProperties, ClassLoader classLoader) throws IOException {
		for (Enumeration<URL> e = classLoader.getResources("META-INF/druid-filter.properties"); e.hasMoreElements();) {
			URL url = e.nextElement();

			Properties property = new Properties();

			InputStream is = null;
			try {
				is = url.openStream();
				property.load(is);
			} finally {
				JdbcUtils.close(is);
			}

			filterProperties.putAll(property);
		}
	}
}
