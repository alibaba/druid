package com.alibaba.druid.bvt.proxy;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.proxy.jdbc.*;
import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Created by wenshao on 12/07/2017.
 */
public class InsertValues extends TestCase {
    public void test_insert_values() throws Exception {
        String sql = "insert into t (f0, f1, f2, f3, f4) values ";
        for (int i = 0; i < 1000; ++i) {
            if (i != 0) {
                sql += ", (?, ?, ?, ?, ?)";
            } else {
                sql += "(?, ?, ?, ?, ?)";
            }
        }

        MockPreparedStatement mockPstmt = new MockPreparedStatement(new MockConnection(), sql);
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        DataSourceProxyImpl ds = new DataSourceProxyImpl(null, config);
        ConnectionProxyImpl conn = new ConnectionProxyImpl(ds, null, null, 0);
        PreparedStatementProxyImpl proxy = new PreparedStatementProxyImpl(conn, mockPstmt, sql, 0);

        Field field = PreparedStatementProxyImpl.class.getDeclaredField("parameters");
        field.setAccessible(true);
        JdbcParameter[] params = (JdbcParameter[]) field.get(proxy);
        assertEquals(5000, params.length);

        proxy.setInt(1000, 3);
        proxy.setInt(1001, 3);
        proxy.setInt(1002, 3);
        proxy.setInt(1003, 3);
        proxy.setInt(1004, 3);
        proxy.setInt(1005, 3);
        proxy.setInt(1005, 3);
        proxy.setInt(1006, 3);
        proxy.setInt(1007, 3);
        proxy.setInt(1008, 3);
        proxy.setInt(1009, 3);
    }
}
