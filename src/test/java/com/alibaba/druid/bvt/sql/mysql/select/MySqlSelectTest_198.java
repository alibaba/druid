package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class MySqlSelectTest_198 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select `orders_text_date`.`o_clerk` as o_clerk, `orders_text_date`.`o_comment` as o_comment, `orders_text_date`.`o_custkey` as o_custkey, `orders_text_date`.`o_orderdate` as o_orderdate, `orders_text_date`.`o_orderkey` as o_orderkey , `orders_text_date`.`o_orderpriority` as o_orderpriority, `orders_text_date`.`o_orderstatus` as o_orderstatus, `orders_text_date`.`o_shippriority` as o_shippriority, `orders_text_date`.`o_totalprice` as o_totalprice from hive.oa1013022312866336_bj_tpch_10x_oss.orders_text_date limit 1000";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT orders_text_date.o_clerk AS o_clerk, orders_text_date.o_comment AS o_comment, orders_text_date.o_custkey AS o_custkey, orders_text_date.o_orderdate AS o_orderdate, orders_text_date.o_orderkey AS o_orderkey\n" +
                "\t, orders_text_date.o_orderpriority AS o_orderpriority, orders_text_date.o_orderstatus AS o_orderstatus, orders_text_date.o_shippriority AS o_shippriority, orders_text_date.o_totalprice AS o_totalprice\n" +
                "FROM hive.oa1013022312866336_bj_tpch_10x_oss.orders_text_date\n" +
                "LIMIT 1000", stmt.toString());

        assertEquals("select orders_text_date.o_clerk as o_clerk, orders_text_date.o_comment as o_comment, orders_text_date.o_custkey as o_custkey, orders_text_date.o_orderdate as o_orderdate, orders_text_date.o_orderkey as o_orderkey\n" +
                "\t, orders_text_date.o_orderpriority as o_orderpriority, orders_text_date.o_orderstatus as o_orderstatus, orders_text_date.o_shippriority as o_shippriority, orders_text_date.o_totalprice as o_totalprice\n" +
                "from hive.oa1013022312866336_bj_tpch_10x_oss.orders_text_date\n" +
                "limit 1000", stmt.toLowerCaseString());
    }
}