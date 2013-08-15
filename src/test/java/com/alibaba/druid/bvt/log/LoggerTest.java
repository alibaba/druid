package com.alibaba.druid.bvt.log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;

public class LoggerTest extends TestCase {

    private static java.security.ProtectionDomain DOMAIN;

    private ClassLoader                           contextClassLoader;
    private DruidDataSource                       dataSource;

    static {
        DOMAIN = (java.security.ProtectionDomain) java.security.AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
                return TestLoader.class.getProtectionDomain();
            }
        });
    }

    public static class TestLoader extends ClassLoader {

        private ClassLoader loader;

        private Set<String> definedSet = new HashSet<String>();

        public TestLoader(){
            super(null);
            loader = DruidDriver.class.getClassLoader();
        }

        public URL getResource(String name) {
            return loader.getResource(name);
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            return loader.getResources(name);
        }

        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.startsWith("java")) {
                return loader.loadClass(name);
            }

            if (definedSet.contains(name)) {
                return super.loadClass(name);
            }

            String resourceName = name.replace('.', '/') + ".class";
            InputStream is = loader.getResourceAsStream(resourceName);
            if (is == null) {
                throw new ClassNotFoundException();
            }
            try {
                byte[] bytes = Utils.readByteArray(is);
                this.defineClass(name, bytes, 0, bytes.length, DOMAIN);
                definedSet.add(name);
            } catch (IOException e) {
                throw new ClassNotFoundException(e.getMessage(), e);
            }
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Class<?> clazz = super.loadClass(name);

            return clazz;
        }
    }

    public void test_log() throws Exception {
        TestLoader classLoader = new TestLoader();

        Thread.currentThread().setContextClassLoader(classLoader);
        dataSource = new DruidDataSource();
        dataSource.setFilters("log");
        dataSource.setUrl("jdbc:mock:xx");
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    @Override
    protected void setUp() throws Exception {
        contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        JdbcUtils.close(dataSource);
    }
}
