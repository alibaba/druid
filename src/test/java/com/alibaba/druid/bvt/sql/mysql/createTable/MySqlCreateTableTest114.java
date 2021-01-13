package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.sql.Types;
import java.util.List;

public class MySqlCreateTableTest114 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE best_sign_cont_task ( \n" +
                "  sys_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '系统时间' \n" +
                ") ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT '上上签合同创建任务记录'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(1, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE best_sign_cont_task (\n" +
                "\tsys_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '系统时间'\n" +
                ") ENGINE = INNODB CHARSET = utf8 COMMENT '上上签合同创建任务记录'", stmt.toString());

        SchemaStatVisitor v = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(v);

        assertEquals(1, v.getColumns().size());
        SQLColumnDefinition column = stmt.findColumn("sys_time");
        assertNotNull(column);
        assertEquals(1, column.getConstraints().size());
        assertFalse(column.isPrimaryKey());
        assertEquals(Types.TIMESTAMP, column.jdbcType());
    }

}