package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleListAggTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "SELECT prod_id, LISTAGG(cust_first_name||' '||cust_last_name, '; ') \n" //
                     + "  WITHIN GROUP (ORDER BY amount_sold DESC) cust_list\n" //
                     + "FROM sales, customers\n" //
                     + "WHERE sales.cust_id = customers.cust_id AND cust_gender = 'M' \n" //
                     + "  AND cust_credit_limit = 15000 AND prod_id BETWEEN 15 AND 18 \n" //
                     + "  AND channel_id = 2 AND time_id > '01-JAN-01'\n" //
                     + "GROUP BY prod_id;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("SELECT prod_id\n" +
                        "\t, LISTAGG(cust_first_name || ' ' || cust_last_name, '; ') WITHIN GROUP (ORDER BY amount_sold DESC) AS cust_list\n" +
                        "FROM sales, customers\n" +
                        "WHERE sales.cust_id = customers.cust_id\n" +
                        "\tAND cust_gender = 'M'\n" +
                        "\tAND cust_credit_limit = 15000\n" +
                        "\tAND prod_id BETWEEN 15 AND 18\n" +
                        "\tAND channel_id = 2\n" +
                        "\tAND time_id > '01-JAN-01'\n" +
                        "GROUP BY prod_id;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(10, visitor.getColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("sales")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("customers")));

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sales", "cust_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("customers", "cust_id")));
    }

}
