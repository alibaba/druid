package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;

public class SpringFilterTest extends TestCase {

    public void test_spring() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("com/alibaba/druid/pool/spring-config-1.xml");

        DataSource dataSource = (DataSource) context.getBean("dataSource");
        Connection conn = dataSource.getConnection();
        conn.close();

        TestFilter filter = (TestFilter) context.getBean("test-filter");
        Assert.assertEquals(1, filter.getConnectCount());
        
        context.close();
    }

    public static class TestFilter extends FilterAdapter {

        private AtomicLong connectCount = new AtomicLong();

        @Override
        public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
            connectCount.incrementAndGet();
            return chain.connection_connect(info);
        }

        public long getConnectCount() {
            return connectCount.get();
        }
    }
}
