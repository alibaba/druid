package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleIsEmptyTest extends TestCase {

    public void test_isEmpty() throws Exception {
        String sql = "SELECT product_id, TO_CHAR(ad_finaltext) FROM print_media WHERE ad_textdocs_ntab IS NOT EMPTY;";

        String expect = "SELECT product_id, TO_CHAR(ad_finaltext)\n" + "FROM print_media\n"
                        + "WHERE ad_textdocs_ntab IS NOT EMPTY;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.output(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
