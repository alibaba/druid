package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

import java.util.List;

public class PGCreateExtensionTest extends TestCase {
    private final DbType dbType = DbType.postgresql;

    // Basic CREATE EXTENSION
    public void test_create_extension_basic() throws Exception {
        String sql = "CREATE EXTENSION hstore";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        assertEquals("CREATE EXTENSION hstore", SQLUtils.toPGString(stmtList.get(0)));
    }

    // With IF NOT EXISTS
    public void test_create_extension_if_not_exists() throws Exception {
        String sql = "CREATE EXTENSION IF NOT EXISTS hstore";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertEquals("CREATE EXTENSION IF NOT EXISTS hstore", result);
    }

    // With SCHEMA (doc example)
    public void test_create_extension_schema() throws Exception {
        String sql = "CREATE EXTENSION hstore SCHEMA addons";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("SCHEMA"));
        assertTrue(result.contains("addons"));
    }

    // With VERSION
    public void test_create_extension_version() throws Exception {
        String sql = "CREATE EXTENSION pg_trgm VERSION '1.5'";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("VERSION"));
    }

    // With CASCADE
    public void test_create_extension_cascade() throws Exception {
        String sql = "CREATE EXTENSION IF NOT EXISTS postgis CASCADE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("CASCADE"));
    }

    // Full syntax: all clauses
    public void test_create_extension_full() throws Exception {
        String sql = "CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public VERSION '3.4' CASCADE";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toPGString(stmtList.get(0));
        assertTrue(result.contains("IF NOT EXISTS"));
        assertTrue(result.contains("postgis"));
        assertTrue(result.contains("SCHEMA"));
        assertTrue(result.contains("public"));
        assertTrue(result.contains("CASCADE"));
    }

    // Common extensions
    public void test_create_extension_uuid_ossp() throws Exception {
        String sql = "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    public void test_create_extension_pg_stat_statements() throws Exception {
        String sql = "CREATE EXTENSION IF NOT EXISTS pg_stat_statements SCHEMA pg_catalog";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }
}
