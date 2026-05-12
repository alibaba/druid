package com.alibaba.druid.bvt.spring;

import com.alibaba.druid.support.ibatis.SqlMapClientWrapper;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class SqlMapExecutorWrapperTest {
    private ClassPathXmlApplicationContext context;

    @BeforeEach
    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("com/alibaba/druid/pool/ibatis/spring-config-ibatis.xml");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void test_wrap() throws Exception {
        SqlMapClientImpl client = (SqlMapClientImpl) context.getBean("master-sqlMapClient");
        assertNotNull(client);

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
        assertNotNull(error);
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
