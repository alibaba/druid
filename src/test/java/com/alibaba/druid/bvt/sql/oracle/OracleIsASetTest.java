package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleIsASetTest extends TestCase {

    public void test_is_a_set() throws Exception {
        String sql = "SELECT customer_id, cust_address_ntab FROM customers_demo WHERE cust_address_ntab IS A SET;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.output(stmt);

        Assert.assertEquals("SELECT customer_id, cust_address_ntab\n" + "FROM customers_demo\n"
                            + "WHERE cust_address_ntab IS A SET;\n", text);

        System.out.println(text);
    }
}
