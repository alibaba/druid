package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest112 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE opening_lines ( " +
                "id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY" +
                ", opening_line TEXT(500)" +
                ", author VARCHAR(200)" +
                ", title VARCHAR(200)" +
                ", FULLTEXT idx (opening_line) " +
                ") ENGINE=InnoDB;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(5, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE opening_lines (\n" +
                "\tid INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                "\topening_line TEXT(500),\n" +
                "\tauthor VARCHAR(200),\n" +
                "\ttitle VARCHAR(200),\n" +
                "\tFULLTEXT INDEX idx(opening_line)\n" +
                ") ENGINE = InnoDB;", stmt.toString());

        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(v);

        assertEquals(4, v.getColumns().size());
        SQLColumnDefinition column = stmt.findColumn("id");
        assertNotNull(column);
        assertEquals(2, column.getConstraints().size());
        assertTrue(column.isPrimaryKey());
    }

}