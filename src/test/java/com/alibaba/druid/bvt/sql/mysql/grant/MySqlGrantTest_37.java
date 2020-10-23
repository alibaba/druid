package com.alibaba.druid.bvt.sql.mysql.grant;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlGrantTest_37
 * @description
 * @Author zzy
 * @Date 2019-05-22 11:11
 */
public class MySqlGrantTest_37 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `db\\_seszadjx\\_guej\\_0005`.* TO 'woaga4ym'@'%'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `db\\_seszadjx\\_guej\\_0005`.* TO 'woaga4ym'@'%'", //
                output);

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }

}
