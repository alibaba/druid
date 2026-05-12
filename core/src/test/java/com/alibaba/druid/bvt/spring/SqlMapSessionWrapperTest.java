package com.alibaba.druid.bvt.spring;

import com.alibaba.druid.support.ibatis.SqlMapSessionWrapper;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

public class SqlMapSessionWrapperTest {
    private ClassPathXmlApplicationContext context;

    @BeforeEach
    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("com/alibaba/druid/pool/ibatis/spring-config-ibatis.xml");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        context.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void test_wrap() throws Exception {
        SqlMapClientImpl client = (SqlMapClientImpl) context.getBean("master-sqlMapClient");
        assertNotNull(client);

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
