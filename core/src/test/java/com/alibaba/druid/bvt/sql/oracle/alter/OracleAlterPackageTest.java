package com.alibaba.druid.bvt.sql.oracle.alter;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterPackageStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * <a href="https://docs.oracle.com/en/database/oracle/oracle-database/19/lnpls/ALTER-PACKAGE-statement.html">ALTER PACKAGE Statement</a>
 */
public class OracleAlterPackageTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "ALTER PACKAGE TEST.PACK_TEST1 COMPILE";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        SQLStatement statement = statementList.get(0);
        Assert.assertTrue(statement instanceof OracleAlterPackageStatement);
        Assert.assertEquals("TEST.PACK_TEST1", ((OracleAlterPackageStatement) statement).getName().toString());
        Assert.assertTrue(((OracleAlterPackageStatement) statement).isCompile());
        Assert.assertFalse(((OracleAlterPackageStatement) statement).isPack());
        Assert.assertFalse(((OracleAlterPackageStatement) statement).isBody());

        sql = "ALTER PACKAGE TEST.PACK_TEST1 PACKAGE";
        parser = new OracleStatementParser(sql);
        statementList = parser.parseStatementList();
        print(statementList);
        statement = statementList.get(0);
        Assert.assertTrue(statement instanceof OracleAlterPackageStatement);
        Assert.assertTrue(((OracleAlterPackageStatement) statement).isPack());
        Assert.assertFalse(((OracleAlterPackageStatement) statement).isCompile());
        Assert.assertFalse(((OracleAlterPackageStatement) statement).isBody());

        sql = "ALTER PACKAGE TEST.PACK_TEST1 COMPILE BODY";
        parser = new OracleStatementParser(sql);
        statementList = parser.parseStatementList();
        print(statementList);
        statement = statementList.get(0);
        Assert.assertTrue(statement instanceof OracleAlterPackageStatement);
        Assert.assertTrue(((OracleAlterPackageStatement) statement).isBody());
        Assert.assertTrue(((OracleAlterPackageStatement) statement).isCompile());
        Assert.assertFalse(((OracleAlterPackageStatement) statement).isPack());
    }
}
