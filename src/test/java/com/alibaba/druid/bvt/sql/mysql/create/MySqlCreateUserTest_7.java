package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

/**
 * @version 1.0
 * @ClassName MySqlCreateUserTest_7
 * @description
 * @Author zzy
 * @Date 2019-05-22 09:43
 */
public class MySqlCreateUserTest_7 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE USER 'seomdbyu'@'%'\n" +
                "IDENTIFIED BY PASSWORD '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals("CREATE USER 'seomdbyu'@'%' IDENTIFIED BY PASSWORD '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'", //
                SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("create user 'seomdbyu'@'%' identified by password '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }

    public void test_1() throws Exception {
        String sql = "CREATE USER 'seomdbyu'@'%'\n" +
                "IDENTIFIED WITH 'mysql_native_password' AS '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals("CREATE USER 'seomdbyu'@'%' IDENTIFIED WITH 'mysql_native_password' AS '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'", //
                SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("create user 'seomdbyu'@'%' identified with 'mysql_native_password' as '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }

    public void test_2() throws Exception {
        String sql = "CREATE USER 'seomdbyu'@'%'\n" +
                "IDENTIFIED WITH 'mysql_native_password' BY '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals("CREATE USER 'seomdbyu'@'%' IDENTIFIED WITH 'mysql_native_password' BY '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'", //
                SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("create user 'seomdbyu'@'%' identified with 'mysql_native_password' by '*E31E2E4B771597DE2FDECB4E0EC00BE9E87D39D2'", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
    }
}
