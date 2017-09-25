package com.alibaba.druid.postgresql;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.benckmark.proxy.BenchmarkExecutor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * Created by wenshao on 23/07/2017.
 */
public class PG_getCreateTableScriptTest extends DbTestCase {
    public PG_getCreateTableScriptTest() {
        super("pool_config/pg_db.properties");
    }

    public void test_oracle() throws Exception {
        Connection conn = getConnection();

        // 从Oracle通过DBMS_METADATA.GET_DDL获取CreateTable语句列表
        //String createTableScript = JdbcUtils.getCreateTableScript(conn, JdbcConstants.ORACLE);
        //System.out.println(createTableScript);

        Statement stmt = conn.createStatement();;
        ResultSet rs = stmt.executeQuery("SELECT * FROM pg_catalog.pg_tables " +
                "where schemaname not in ('pg_catalog', 'information_schema', 'sys')");
        JdbcUtils.printResultSet(rs);

        List<String> tables = JdbcUtils.showTables(conn, JdbcConstants.POSTGRESQL);
        for (String table : tables) {
//            Object cnt = JdbcUtils.executeQuery(conn, "select count(*) CNT from " + table, Collections.emptyList())
//                    .get(0)
//                    .get("CNT");
            System.out.println(table);
        }

        conn.close();
    }
}
