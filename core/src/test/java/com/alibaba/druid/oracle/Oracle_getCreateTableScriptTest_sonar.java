package com.alibaba.druid.oracle;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

/**
 * Created by wenshao on 23/07/2017.
 */
public class Oracle_getCreateTableScriptTest_sonar extends DbTestCase {
    public Oracle_getCreateTableScriptTest_sonar() {
        super("pool_config/oracle_db_sonar.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        // 从Oracle通过DBMS_METADATA.GET_DDL获取CreateTable语句列表
        //String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.ORACLE);
        //System.out.println(createTableScript);

        List<String> tables = JdbcUtils.showTables(conn, JdbcConstants.ORACLE);
        for (String table : tables) {
            Object cnt = JdbcUtils.executeQuery(conn, "select count(*) CNT from " + table, Collections.emptyList())
                    .get(0)
                    .get("CNT");
            System.out.println(table + " : " + cnt);
        }

        conn.close();
    }
}
