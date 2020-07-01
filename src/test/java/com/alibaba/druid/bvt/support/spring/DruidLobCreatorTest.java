package com.alibaba.druid.bvt.support.spring;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.spring.DruidLobCreator;

public class DruidLobCreatorTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_lobCreator() throws Exception {
        DruidLobCreator lobCreator = new DruidLobCreator();

        Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement("select 1");
        lobCreator.setBlobAsBytes(ps, 1, new byte[0]);
        lobCreator.setBlobAsBinaryStream(ps, 2, new ByteArrayInputStream(new byte[0]), 0);
        lobCreator.setClobAsAsciiStream(ps, 3, new ByteArrayInputStream(new byte[0]), 0);
        lobCreator.setClobAsCharacterStream(ps, 4, new StringReader(""), 0);
        lobCreator.setClobAsString(ps, 5, "");
        ps.close();

        conn.close();
    }
}
