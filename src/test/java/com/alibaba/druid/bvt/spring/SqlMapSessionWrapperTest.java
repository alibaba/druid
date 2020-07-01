package com.alibaba.druid.bvt.spring;

import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.support.ibatis.SqlMapSessionWrapper;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;

public class SqlMapSessionWrapperTest extends TestCase {

    private ClassPathXmlApplicationContext context = null;

    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("com/alibaba/druid/pool/ibatis/spring-config-ibatis.xml");
    }

    protected void tearDown() throws Exception {
        context.close();
    }

    @SuppressWarnings("deprecation")
    public void test_wrap() throws Exception {
        SqlMapClientImpl client = (SqlMapClientImpl) context.getBean("master-sqlMapClient");
        Assert.assertNotNull(client);

        SqlMapSessionImpl session = new SqlMapSessionImpl(client);
        SqlMapSessionWrapper wrapper = new SqlMapSessionWrapper(client, session);

        wrapper.startTransaction();
        wrapper.commitTransaction();
        wrapper.getDataSource();
        wrapper.getCurrentConnection();
        wrapper.getUserConnection();
        wrapper.close();
    }
}
