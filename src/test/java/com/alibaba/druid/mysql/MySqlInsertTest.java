package com.alibaba.druid.mysql;

import com.alibaba.druid.DbTestCase;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.Statement;

public class MySqlInsertTest  extends DbTestCase {
    public MySqlInsertTest() {
        super("pool_config/mysql_oracle_info.properties");
    }
    public void test_for_mysql() throws Exception {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("use oracle_info");

        stmt.close();
        conn.close();
    }
}
