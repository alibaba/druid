package com.alibaba.druid.bvt.spring;

import java.sql.Connection;

import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.druid.support.ibatis.SqlMapClientWrapper;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;

public class SqlMapExecutorWrapperTest extends TestCase {

    private ClassPathXmlApplicationContext context = null;

    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("com/alibaba/druid/pool/ibatis/spring-config-ibatis.xml");
    }

    protected void tearDown() throws Exception {
        context.close();
    }

    public void test_wrap() throws Exception {
        SqlMapClientImpl client = (SqlMapClientImpl) context.getBean("master-sqlMapClient");
        Assert.assertNotNull(client);

        SqlMapClientWrapper wrapper = new SqlMapClientWrapper(client);
        wrapper.getClient();
        wrapper.startTransaction();
        wrapper.endTransaction();
        wrapper.startTransaction(Connection.TRANSACTION_NONE);
        wrapper.endTransaction();
        wrapper.setUserConnection(wrapper.getUserConnection());
        wrapper.getCurrentConnection();
        wrapper.getDataSource();
        wrapper.openSession().close();
        wrapper.openSession(wrapper.getCurrentConnection()).close();
        wrapper.getSession();
        wrapper.flushDataCache();
        Exception error = null;
        try {
            wrapper.flushDataCache(null);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        wrapper.startTransaction();
        wrapper.commitTransaction();
        
        wrapper.getMappedStatement("Sequence.getValue");
        
        wrapper.isEnhancementEnabled();
        wrapper.isLazyLoadingEnabled();
        
        wrapper.getSqlExecutor();
        
        wrapper.getDelegate();
        
        wrapper.getResultObjectFactory();
    }
}
