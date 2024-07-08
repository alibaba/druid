package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDropPackageStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import org.junit.Assert;

import java.util.List;

/**
 * <a href="https://docs.oracle.com/en/database/oracle/oracle-database/19/lnpls/DROP-PACKAGE-statement.html">DROP PACKAGE Statement</a>
 */
public class OracleDropPackageTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "DROP PACKAGE TEST.PACK_TEST1";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);
        SQLStatement statement = statementList.get(0);
        Assert.assertTrue(statement instanceof OracleDropPackageStatement);
        Assert.assertEquals("TEST.PACK_TEST1", ((OracleDropPackageStatement) statement).getName().toString());
        Assert.assertFalse(((OracleDropPackageStatement) statement).isBody());
    }
}
