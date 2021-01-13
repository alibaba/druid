package com.alibaba.druid.mysql;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.DbType;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;

/**
 * Created by wenshao on 23/07/2017.
 */
public class MySql_Failover_test extends DbTestCase {
    public MySql_Failover_test() {
        super("pool_config/mysql_db_failover.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        String createTableScript = JdbcUtils.getCreateTableScript(conn, DbType.mysql);
        System.out.println(createTableScript);


        conn.close();
    }
}
