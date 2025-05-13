/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.postgresql.PG_getCreateTableScriptTest
 */
public class GaussDB_getCreateTableScriptTest extends DbTestCase {
    public GaussDB_getCreateTableScriptTest() {
        super("pool_config/gaussdb.properties");
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

        // Assuming the table name list you expect is as follows
        String[] expectedTables = {"gs_errors", "gs_source", "pl_profiling_functions", "pl_profiling_details", "pl_profiling_callgraph", "pl_profiling_trackinfo", "snapshot"};
        for (String expectedTable : expectedTables) {
            assertTrue("Expected table not found: " + expectedTable, tables.contains(expectedTable));
        }

        conn.close();
    }
}
