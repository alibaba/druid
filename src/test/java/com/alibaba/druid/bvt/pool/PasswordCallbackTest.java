package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import javax.security.auth.callback.PasswordCallback;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class PasswordCallbackTest extends TestCase {

    public void test_0 () throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setPasswordCallback(new TestPasswordCallback());
        
        Connection conn = dataSource.getConnection();
        conn.close();
    }

    public static class TestPasswordCallback extends PasswordCallback {
        public TestPasswordCallback() {
            super ("test", false);
        }

        public TestPasswordCallback(String prompt, boolean echoOn){
            super(prompt, echoOn);
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

    }
}
