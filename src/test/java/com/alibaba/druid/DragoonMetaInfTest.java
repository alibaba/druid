package com.alibaba.druid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import junit.framework.TestCase;

public class DragoonMetaInfTest extends TestCase {
	public void test_0 () throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> e = classLoader.getResources("META-INF/dragoon-filter.properties");
		while (e.hasMoreElements()) {
			URL url = e.nextElement();
			InputStream is = url.openStream();
			Properties properties = new Properties();
			try {
				properties.load(is);
			} finally {
				is.close();
			}
		}
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
