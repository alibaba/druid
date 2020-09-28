package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

/**
 * @author Dagon0577
 * @date 2019/11/7 11:03
 */
public class MySQLCheckTableTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "check table table_test fast quick";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertEquals("CHECK table table_test QUICK FAST ", stmt.toString());

        sql = "CHECK TABLE tbl_name,tbl2 CHANGED MEDIUM";

        parser = new MySqlStatementParser(sql);
        statementList = parser.parseStatementList();
        stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals(2, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertEquals("CHECK TABLE tbl_name, tbl2 MEDIUM CHANGED ", stmt.toString());
    }
}
