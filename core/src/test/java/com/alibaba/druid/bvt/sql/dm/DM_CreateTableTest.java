package com.alibaba.druid.bvt.sql.dm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DM_CreateTableTest {
    private final DbType dbType = DbType.dm;

    @Test
    public void test_create_table_basic() {
        String sql = "CREATE TABLE t1 (id INT PRIMARY KEY, name VARCHAR(100) NOT NULL, status INT DEFAULT 0)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_table_with_constraints() {
        String sql = "CREATE TABLE t1 (" +
                "id INT NOT NULL, " +
                "name VARCHAR(100), " +
                "dept_id INT, " +
                "CONSTRAINT pk_t1 PRIMARY KEY (id), " +
                "CONSTRAINT fk_dept FOREIGN KEY (dept_id) REFERENCES dept(id)" +
                ")";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_table_identity() {
        // DM official: IDENTITY [(seed, increment)]
        String sql = "CREATE TABLE t1 (id INT IDENTITY(1, 1) PRIMARY KEY, name VARCHAR(100))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_identity_no_args() {
        // DM: IDENTITY without arguments (defaults to seed=1, increment=1)
        String sql = "CREATE TABLE t1 (id INT IDENTITY PRIMARY KEY, name VARCHAR(100))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_auto_increment() {
        String sql = "CREATE TABLE t1 (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_if_not_exists() {
        String sql = "CREATE TABLE IF NOT EXISTS t1 (id INT, name VARCHAR(100))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_table_as_select() {
        String sql = "CREATE TABLE t2 AS SELECT id, name FROM t1 WHERE status = 1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_with_comment() {
        String sql = "CREATE TABLE t1 (id INT COMMENT 'primary key', name VARCHAR(100) COMMENT 'user name')";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_with_tablespace() {
        String sql = "CREATE TABLE t1 (id INT, name VARCHAR(100)) TABLESPACE ts1";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_with_check() {
        String sql = "CREATE TABLE t1 (id INT, age INT, CONSTRAINT chk_age CHECK (age >= 0 AND age <= 150))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_table_dm_types() {
        // DM-specific data types
        String sql = "CREATE TABLE t1 (" +
                "id BIGINT, " +
                "tiny_val TINYINT, " +
                "small_val SMALLINT, " +
                "float_val DOUBLE PRECISION, " +
                "txt TEXT, " +
                "bin_data BLOB, " +
                "char_data CLOB, " +
                "ts TIMESTAMP" +
                ")";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_create_table_with_unique() {
        String sql = "CREATE TABLE t1 (id INT, email VARCHAR(200), CONSTRAINT uk_email UNIQUE (email))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
        SQLParseAssertUtil.assertParseSql(sql, dbType);
    }

    @Test
    public void test_create_table_default_on_null() {
        String sql = "CREATE TABLE t1 (id INT, name VARCHAR(100) DEFAULT 'unknown' NOT NULL)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());
    }
}
