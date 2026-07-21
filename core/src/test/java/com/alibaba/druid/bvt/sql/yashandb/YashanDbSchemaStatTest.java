package com.alibaba.druid.bvt.sql.yashandb;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbSchemaStatTest {

    @Test
    public void test_schema_stat_select() throws Exception {
        String sql = "SELECT e.name, d.dept_name FROM employees e JOIN departments d ON e.dept_id = d.id WHERE e.salary > 5000";

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(DbType.yashandb);
        assertNotNull(statVisitor);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        stmt.accept(statVisitor);

        assertTrue(statVisitor.getTables().size() > 0);
        assertTrue(statVisitor.getColumns().size() > 0);
    }

    @Test
    public void test_schema_stat_insert() throws Exception {
        String sql = "INSERT INTO users (id, name, email) VALUES (1, 'test', 'test@example.com')";

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(DbType.yashandb);
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        stmt.accept(statVisitor);

        assertEquals(1, statVisitor.getTables().size());
    }

    @Test
    public void test_schema_stat_update() throws Exception {
        String sql = "UPDATE employees SET salary = salary * 1.1 WHERE department = 'Engineering'";

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(DbType.yashandb);
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.yashandb);
        SQLStatement stmt = parser.parseStatement();
        stmt.accept(statVisitor);

        assertTrue(statVisitor.getTables().size() > 0);
    }
}
