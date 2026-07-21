package com.alibaba.druid.bvt.sql.yashandb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbSQLParserTest {

    @Test
    public void test_parse_select() throws Exception {
        String sql = "SELECT id, name FROM employees WHERE salary > 5000";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        assertNotNull(parser);

        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_insert() throws Exception {
        String sql = "INSERT INTO users (id, name) VALUES (1, 'test')";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_update() throws Exception {
        String sql = "UPDATE employees SET salary = salary * 1.1 WHERE dept_id = 10";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_delete() throws Exception {
        String sql = "DELETE FROM temp_table WHERE created_at < SYSDATE - 30";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_oracle_connect_by() throws Exception {
        // Oracle-specific syntax: CONNECT BY should parse correctly with yashandb
        String sql = "SELECT LEVEL, ROWNUM FROM DUAL CONNECT BY ROWNUM <= 10";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_create_table() throws Exception {
        String sql = "CREATE TABLE test_table ("
                + "id NUMBER PRIMARY KEY, "
                + "name VARCHAR2(100) NOT NULL, "
                + "created_at DATE DEFAULT SYSDATE)";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_multiple_statements() throws Exception {
        String sql = "SELECT * FROM t1; SELECT * FROM t2";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        List<SQLStatement> stmts = parser.parseStatementList();
        assertEquals(2, stmts.size());
    }

    @Test
    public void test_parse_merge_statement() throws Exception {
        String sql = "MERGE INTO target_table t "
                + "USING source_table s ON (t.id = s.id) "
                + "WHEN MATCHED THEN UPDATE SET t.name = s.name "
                + "WHEN NOT MATCHED THEN INSERT (id, name) VALUES (s.id, s.name)";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        assertNotNull(stmt);
    }

    @Test
    public void test_parse_analyze_same_as_oracle() throws Exception {
        // Verify that yashandb produces same parse result as oracle
        String sql = "SELECT e.*, d.dept_name FROM employees e "
                + "LEFT JOIN departments d ON e.dept_id = d.id "
                + "WHERE e.salary > (SELECT AVG(salary) FROM employees)";

        SQLStatementParser yashanParser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatementParser oracleParser = SQLParserUtils.createSQLStatementParser(sql, DbType.oracle);

        SQLStatement yashanStmt = yashanParser.parseStatement();
        SQLStatement oracleStmt = oracleParser.parseStatement();

        assertEquals(oracleStmt.getClass(), yashanStmt.getClass());
    }
}
