package com.alibaba.druid.mysql;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.druid.util.OracleUtils;

import java.sql.Connection;
import java.util.List;

/**
 * Created by wenshao on 23/07/2017.
 */
public class MySql_getCreateTableScriptTest extends DbTestCase {
    public MySql_getCreateTableScriptTest() {
        super("pool_config/mysql_db.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.MYSQL);
        System.out.println(createTableScript);


        conn.close();
    }
}
