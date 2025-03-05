package com.alibaba.druid.gaussdb;

import com.alibaba.druid.DbTestCase;
import com.alibaba.druid.pool.vendor.PGValidConnectionChecker;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Created by wenshao on 23/07/2017.
 */
public class GaussDB_getCreateTableScriptTest extends DbTestCase {
    public GaussDB_getCreateTableScriptTest() {
        super("pool_config/pg_db.properties");
    }

    public void test_gaussdb() throws Exception {
        Connection conn = getConnection();

        PGValidConnectionChecker checker = new PGValidConnectionChecker();
        Connection raw = conn.unwrap(Connection.class);
        checker.isValidConnection(raw, "select 1", 100);

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM pg_catalog.pg_tables " +
                "where schemaname not in ('pg_catalog', 'information_schema', 'sys')");
        JdbcUtils.printResultSet(rs);

        List<String> tables = JdbcUtils.showTables(conn, JdbcConstants.GAUSSDB);
        for (String table : tables) {
            System.out.println(table);
        }

        conn.close();
    }
}
