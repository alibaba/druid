package com.alibaba.druid.oracle;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.OracleUtils;

import java.sql.Connection;
import java.util.List;

/**
 * Created by wenshao on 23/07/2017.
 */
public class Oracle_getCreateTableScriptTest extends DbTestCase {
    public Oracle_getCreateTableScriptTest() {
        super("pool_config/oracle_db.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        // 从Oracle通过DBMS_METADATA.GET_DDL获取CreateTable语句列表
        String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.ORACLE);
        System.out.println(createTableScript);

        conn.close();
    }
}
