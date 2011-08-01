package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.Properties;

import javax.security.auth.callback.PasswordCallback;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class PasswordCallbackTest extends TestCase {

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        TestPasswordCallback passwordCallback = new TestPasswordCallback();
        dataSource.setPasswordCallback(passwordCallback);

        Connection conn = dataSource.getConnection();
        conn.close();

        Assert.assertEquals(dataSource.getUrl(), passwordCallback.getUrl());
        Assert.assertEquals(dataSource.getConnectProperties(), passwordCallback.getProperties());
    }

    public static class TestPasswordCallback extends PasswordCallback {

        private static final long serialVersionUID = 1L;

        private String            url;
        private Properties        properties;

        public TestPasswordCallback(){
            super("test", false);
        }

        public TestPasswordCallback(String prompt, boolean echoOn){
            super(prompt, echoOn);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

    }
}
