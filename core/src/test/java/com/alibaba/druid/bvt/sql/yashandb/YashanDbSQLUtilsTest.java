package com.alibaba.druid.bvt.sql.yashandb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbSQLUtilsTest {

    @Test
    public void test_format_select() throws Exception {
        String sql = "select * from t where id = 1";
        String result = SQLUtils.format(sql, DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.contains("SELECT"));
        assertTrue(result.contains("FROM t"));
    }

    @Test
    public void test_format_uses_oracle_visitor() throws Exception {
        // Oracle-style ROWNUM pagination should work with yashandb
        String sql = "SELECT * FROM t WHERE ROWNUM <= 10";
        String result = SQLUtils.format(sql, DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.contains("ROWNUM"));
    }

    @Test
    public void test_toSQL_round_trip() throws Exception {
        String sql = "SELECT id, name FROM employees WHERE department = 'Engineering' ORDER BY id";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.yashandb);
        assertEquals(1, stmtList.size());
        String result = SQLUtils.toSQLString(stmtList.get(0), DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("SELECT"));
        assertTrue(result.toUpperCase().contains("EMPLOYEES"));
    }

    @Test
    public void test_format_same_as_oracle() throws Exception {
        String sql = "SELECT e.name, d.dept_name FROM employees e, departments d WHERE e.dept_id = d.id";
        String yashanResult = SQLUtils.format(sql, DbType.yashandb);
        String oracleResult = SQLUtils.format(sql, DbType.oracle);
        assertEquals(oracleResult, yashanResult);
    }

    @Test
    public void test_parse_complex_oracle_sql() throws Exception {
        // Test that complex Oracle-compatible SQL parses correctly with yashandb
        String sql = "SELECT * FROM ("
                + "SELECT t.*, ROWNUM AS rn FROM ("
                + "SELECT id, name FROM employees ORDER BY id"
                + ") t WHERE ROWNUM <= 20"
                + ") WHERE rn > 10";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.yashandb);
        assertEquals(1, stmtList.size());
        assertNotNull(SQLUtils.toSQLString(stmtList.get(0), DbType.yashandb));
    }

    @Test
    public void test_format_insert() throws Exception {
        String sql = "INSERT INTO users (id, name, email) VALUES (1, 'test', 'test@example.com')";
        String result = SQLUtils.format(sql, DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("INSERT INTO"));
    }

    @Test
    public void test_format_update() throws Exception {
        String sql = "UPDATE employees SET salary = salary * 1.1 WHERE department = 'Engineering'";
        String result = SQLUtils.format(sql, DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("UPDATE"));
    }

    @Test
    public void test_format_delete() throws Exception {
        String sql = "DELETE FROM temp_table WHERE created_at < SYSDATE - 30";
        String result = SQLUtils.format(sql, DbType.yashandb);
        assertNotNull(result);
        assertTrue(result.toUpperCase().contains("DELETE FROM"));
    }
}
