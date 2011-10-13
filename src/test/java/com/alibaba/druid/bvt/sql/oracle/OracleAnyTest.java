package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class OracleAnyTest extends TestCase {

    public void test_any() throws Exception {
        String sql = "SELECT country, prod, year, s FROM sales_view " + "MODEL PARTITION BY (country) "
                     + "DIMENSION BY (prod, year) MEASURES (sale s) " + "IGNORE NAV "
                     + "UNIQUE DIMENSION RULES UPSERT SEQUENTIAL ORDER (s[ANY, 2000] = 0) "
                     + "ORDER BY country, prod, year;";

        String expect = "SELECT country, prod, YEAR, s\n" + "FROM sales_view\n" + "MODEL\n"
                        + "\tPARTITION BY (country)\n" + "\tDIMENSION BY (prod, YEAR)\n" + "\tMEASURES (sale s)\n"
                        + "\tIGNORE NAV\n" + "\tUNIQUE DIMENSION\n" + "\tRULES UPSERT SEQUENTIAL ORDER (s[ANY, 2000] = 0)\n"
                        + "ORDER BY country, prod, YEAR;\n";

        OracleStatementParser parser = new OracleStatementParser(sql);
        SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatementList().get(0);

        String text = TestUtils.output(stmt);

        Assert.assertEquals(expect, text);

        System.out.println(text);
    }
}
