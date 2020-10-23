/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_199_hint extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "  SELECT NAME \n" + "  FROM CUSTOMER \n" + "  INNER JOIN ORDERS ON\n"
                     + "  CUSTOMER.CUSTKEY = ORDERS.CUSTKEY /*+ dynamicFilter = true */\n";


        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);


        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT NAME\n" + "FROM CUSTOMER\n"
                                + "\tINNER JOIN ORDERS ON CUSTOMER.CUSTKEY = ORDERS.CUSTKEY/*+ dynamicFilter = true*/", //
                                output);
        }
    }

    public void test_1() throws Exception {
        String sql = "SELECT NAME\n" + "  FROM CUSTOMER, ORDERS\n"
                     + "  WHERE CUSTOMER.CUSTKEY = ORDERS.CUSTKEY /*+ dynamicFilter = true*/  \n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT NAME\n" + "FROM CUSTOMER, ORDERS\n"
                                + "WHERE CUSTOMER.CUSTKEY = ORDERS.CUSTKEY/*+ dynamicFilter = true*/", //
                                output);
        }
    }

    public void test_2() throws Exception {
        String sql = "SELECT NAME \n"
                     + "      FROM CUSTOMER \n"
                     + "      WHERE CUSTOMER.CUSTKEY IN ( \n"
                     + "      SELECT CUSTKEY FROM ORDERS\n"
                     + "      ) /*+ DYNAMICFILTER = TRUE*/";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT NAME\n" + "FROM CUSTOMER\n" + "WHERE CUSTOMER.CUSTKEY IN (\n"
                                + "\tSELECT CUSTKEY\n" + "\tFROM ORDERS\n" + ")/*+ DYNAMICFILTER = TRUE*/", //
                                output);
        }
    }

    public void test_3() throws Exception {
        String sql = "\n  EXPLAIN SELECT name \n" + "  FROM customer  \n" + "  INNER JOIN orders  \n"
                     + "  ON customer.custkey=orders.custkey/*+ dynamicFilter=true */ \n"
                     + "  AND customer.nationkey = orders.orderkey/*+ dynamicFilter=true */\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("EXPLAIN SELECT name\n" + "FROM customer\n" + "\tINNER JOIN orders\n"
                                + "\tON customer.custkey = orders.custkey/*+ dynamicFilter=true*/\n"
                                + "\t\tAND customer.nationkey = orders.orderkey/*+ dynamicFilter=true*/", //
                                output);
        }
    }

    public void test_4() throws Exception {
        String sql = "EXPLAIN SELECT name \n" + "  FROM customer  \n" + "  INNER JOIN orders  \n"
                     + "  ON customer.custkey=orders.custkey/*+ dynamicFilter=fdseoi */ \n"
                     + "  AND customer.nationkey = orders.orderkey/*+ dynamicFilter=we */\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("EXPLAIN SELECT name\n" +
                            "FROM customer\n" +
                            "\tINNER JOIN orders\n" +
                            "\tON customer.custkey = orders.custkey/*+ dynamicFilter=fdseoi*/\n" +
                            "\t\tAND customer.nationkey = orders.orderkey/*+ dynamicFilter=we*/", //
                                output);
        }
    }

    public void test_5() throws Exception {
        String sql = "EXPLAIN SELECT name \n" + "  FROM customer  \n" + "  INNER JOIN orders  \n"
                     + "  ON customer.custkey=orders.custkey/*+ wefwe=true */ \n"
                     + "  AND customer.nationkey = orders.orderkey/* wef=false */\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("EXPLAIN SELECT name\n" +
                            "FROM customer\n" +
                            "\tINNER JOIN orders\n" +
                            "\tON customer.custkey = orders.custkey/*+ wefwe=true*/\n" +
                            "\t\tAND customer.nationkey = orders.orderkey", //
                                output);
        }
    }

    public void test_6() throws Exception {
        String sql = "explain (format text)\n"
                     + "SELECT c_name,\n"
                     + "       c_custkey,\n"
                     + "       o_orderkey,\n"
                     + "       o_orderdate,\n"
                     + "       o_totalprice,\n"
                     + "       Sum(l_quantity)\n"
                     + "FROM   customer,\n"
                     + "       orders,\n"
                     + "       lineitem\n"
                     + "WHERE  o_orderkey IN (SELECT l_orderkey\n"
                     + "                      FROM   lineitem\n"
                     + "                      GROUP  BY l_orderkey\n"
                     + "                      HAVING Sum(l_quantity) > 314) /*+dynamicFilter=true*/\n"
                     + "       AND c_custkey = o_custkey\n"
                     + "       AND o_orderkey = l_orderkey\n"
                     + "GROUP  BY c_name,\n"
                     + "          c_custkey,\n"
                     + "          o_orderkey,\n"
                     + "          o_orderdate,\n"
                     + "          o_totalprice\n"
                     + "ORDER  BY o_totalprice DESC,\n"
                     + "          o_orderdate\n"
                     + "LIMIT  100;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("EXPLAIN (FORMAT text) SELECT c_name, c_custkey, o_orderkey, o_orderdate, o_totalprice\n"
                                + "\t, Sum(l_quantity)\n" + "FROM customer, orders, lineitem\n"
                                + "WHERE o_orderkey IN (\n" + "\t\tSELECT l_orderkey\n" + "\t\tFROM lineitem\n"
                                + "\t\tGROUP BY l_orderkey\n" + "\t\tHAVING Sum(l_quantity) > 314\n"
                                + "\t)/*+dynamicFilter=true*/\n" + "\tAND c_custkey = o_custkey\n"
                                + "\tAND o_orderkey = l_orderkey\n"
                                + "GROUP BY c_name, c_custkey, o_orderkey, o_orderdate, o_totalprice\n"
                                + "ORDER BY o_totalprice DESC, o_orderdate\n" + "LIMIT 100;", //
                                output);
        }
    }

    public void test_7() throws Exception {
        String sql = "SELECT ss_store_sk, sum(ss_ext_sales_price) ext_price \n"
                     + "FROM item, store_sales \n"
                     + "WHERE store_sales.ss_sold_date_sk IN \n"
                     + "  (SELECT d_date_sk FROM date_dim WHERE (d_moy = 11) AND (d_year = 2000)) /*+ dynamicFilter = true*/\n"
                     + "  AND store_sales.ss_item_sk = item.i_item_sk /*+ dynamicFilter = true*/\n"
                     + "  AND item.i_manager_id = 1 \n"
                     + "GROUP BY ss_store_sk \n"
                     + "ORDER BY ext_price DESC \n"
                     + "LIMIT 100\n";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals(
                    "SELECT ss_store_sk, sum(ss_ext_sales_price) AS ext_price\n"
                    + "FROM item, store_sales\n"
                    + "WHERE store_sales.ss_sold_date_sk IN (\n"
                    + "\t\tSELECT d_date_sk\n"
                    + "\t\tFROM date_dim\n"
                    + "\t\tWHERE d_moy = 11\n"
                    + "\t\t\tAND d_year = 2000\n"
                    + "\t)/*+ dynamicFilter = true*/\n"
                    + "\tAND store_sales.ss_item_sk = item.i_item_sk/*+ dynamicFilter = true*/\n"
                    + "\tAND item.i_manager_id = 1\n" + "GROUP BY ss_store_sk\n"
                    + "ORDER BY ext_price DESC\n"
                    + "LIMIT 100", //
                    output);
        }
    }

    public void test_8() throws Exception {
        String sql = "SELECT\n"
                     + "  w_state\n"
                     + ", i_item_id\n"
                     + ", sum((CASE WHEN (CAST(d_date AS DATE) < CAST('2000-03-11' AS DATE)) THEN (cs_sales_price - COALESCE(cr_refunded_cash, 0)) ELSE 0 END)) sales_before\n"
                     + ", sum((CASE WHEN (CAST(d_date AS DATE) >= CAST('2000-03-11' AS DATE)) THEN (cs_sales_price - COALESCE(cr_refunded_cash, 0)) ELSE 0 END)) sales_after\n"
                     + "FROM\n"
                     + "  ( catalog_sales\n"
                     + "LEFT JOIN  catalog_returns ON (cs_order_number = cr_order_number)\n"
                     + "   AND (cs_item_sk = cr_item_sk))\n"
                     + ",  warehouse\n"
                     + ",  item\n"
                     + ",  date_dim\n"
                     + "WHERE (i_current_price BETWEEN DECIMAL '0.99' AND DECIMAL '1.49')\n"
                     + "   AND (i_item_sk = cs_item_sk) /*+ dynamicFilter = true*/\n"
                     + "   AND (cs_warehouse_sk = w_warehouse_sk)\n"
                     + "   AND (cs_sold_date_sk = d_date_sk) /*+ dynamicFilter = true*/\n"
                     + "   AND (CAST(d_date AS DATE) BETWEEN (CAST('2000-03-11' AS DATE) - INTERVAL  '30' DAY) AND (CAST('2000-03-11' AS DATE) + INTERVAL  '30' DAY))\n"
                     + "GROUP BY w_state, i_item_id\n"
                     + "ORDER BY w_state ASC, i_item_id ASC\n"
                     + "LIMIT 100";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT w_state, i_item_id\n" +
                                "\t, sum(CASE \n"
                                + "\t\tWHEN CAST(d_date AS DATE) < CAST('2000-03-11' AS DATE) THEN cs_sales_price - COALESCE(cr_refunded_cash, 0)\n"
                                + "\t\tELSE 0\n"
                                + "\tEND) AS sales_before\n"
                                + "\t, sum(CASE \n"
                                + "\t\tWHEN CAST(d_date AS DATE) >= CAST('2000-03-11' AS DATE) THEN cs_sales_price - COALESCE(cr_refunded_cash, 0)\n"
                                + "\t\tELSE 0\n"
                                + "\tEND) AS sales_after\n"
                                + "FROM catalog_sales\n"
                                + "\tLEFT JOIN catalog_returns\n"
                                + "\tON cs_order_number = cr_order_number\n"
                                + "\t\tAND cs_item_sk = cr_item_sk, warehouse, item, date_dim\n"
                                + "WHERE i_current_price BETWEEN DECIMAL '0.99' AND DECIMAL '1.49'\n"
                                + "\tAND i_item_sk = cs_item_sk/*+ dynamicFilter = true*/\n"
                                + "\tAND cs_warehouse_sk = w_warehouse_sk\n"
                                + "\tAND cs_sold_date_sk = d_date_sk/*+ dynamicFilter = true*/\n"
                                + "\tAND CAST(d_date AS DATE) BETWEEN (CAST('2000-03-11' AS DATE) - INTERVAL '30' DAY) AND (CAST('2000-03-11' AS DATE) + INTERVAL '30' DAY)\n"
                                + "GROUP BY w_state, i_item_id\n"
                                + "ORDER BY w_state ASC, i_item_id ASC\n"
                                + "LIMIT 100", //
                                output);
        }
    }

    public void test_9() throws Exception {
        String sql = "SELECT ss_store_sk, sum(ss_ext_sales_price) ext_price \n"
                     + "FROM item, store_sales \n"
                     + "WHERE \n"
                     + "\tstore_sales.ss_item_sk = item.i_item_sk /*+ dynamicFilter = true*/\n"
                     + "\tAND store_sales.ss_sold_date_sk IN \n"
                     + "  (SELECT d_date_sk FROM date_dim WHERE (d_moy = 11) AND (d_year = 2000)) /*+ dynamicFilter = true*/\n"
                     + "  \n"
                     + "  AND item.i_manager_id = 1 \n"
                     + "GROUP BY ss_store_sk \n"
                     + "ORDER BY ext_price DESC \n"
                     + "LIMIT 100";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT ss_store_sk, sum(ss_ext_sales_price) AS ext_price\n"
                                + "FROM item, store_sales\n"
                                + "WHERE store_sales.ss_item_sk = item.i_item_sk/*+ dynamicFilter = true*/\n"
                                + "\tAND store_sales.ss_sold_date_sk IN (\n"
                                + "\t\tSELECT d_date_sk\n"
                                + "\t\tFROM date_dim\n"
                                + "\t\tWHERE d_moy = 11\n"
                                + "\t\t\tAND d_year = 2000\n"
                                + "\t)/*+ dynamicFilter = true*/\n"
                                + "\tAND item.i_manager_id = 1\n"
                                + "GROUP BY ss_store_sk\n"
                                + "ORDER BY ext_price DESC\n"
                                + "LIMIT 100", //
                                output);
        }
    }
    public void test_10() throws Exception {
        String sql = "SELECT count(*)\n" +
                "FROM orders\n" +
                "  JOIN lineitem ON o_orderkey = l_orderkey/*+joinMethod=hash,distributionType=partitioned*/\n" +
                "LIMIT 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*)\n" +
                            "FROM orders\n" +
                            "\tJOIN lineitem ON o_orderkey = l_orderkey/*+joinMethod=hash,distributionType=partitioned*/\n" +
                            "LIMIT 1", //
                                output);
        }
    }
    public void test_11() throws Exception {
        String sql = "SELECT count(*)\n" +
                "FROM orders\n" +
                "  JOIN lineitem ON o_orderkey = l_orderkey/*+joinMethod=hash */\n" +
                "LIMIT 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*)\n" +
                            "FROM orders\n" +
                            "\tJOIN lineitem ON o_orderkey = l_orderkey/*+joinMethod=hash*/\n" +
                            "LIMIT 1", //
                                output);
        }
    }

    public void test_12() throws Exception {
        String sql = "SELECT count(*)\n" +
                "FROM orders\n" +
                "  JOIN lineitem ON o_orderkey = l_orderkey/*+distributionType=partitioned*/\n" +
                "LIMIT 1";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*)\n" +
                            "FROM orders\n" +
                            "\tJOIN lineitem ON o_orderkey = l_orderkey/*+distributionType=partitioned*/\n" +
                            "LIMIT 1", //
                    output);
        }
    }


    public void test_13() throws Exception {
        String sql = " explain (FORMAT detail) SELECT customer.NAME, MAX(customer.CUSTKEY) MAXKEY" +
                "      FROM CUSTOMER," +
                "      ORDERS  " +
                "      WHERE  CUSTOMER.CUSTKEY NOT IN ( SELECT CUSTKEY FROM ORDERS WHERE CUSTKEY > 100) /*+ dynamicFilter=true*/" +
                "       AND   CUSTOMER.CUSTKEY IN (SELECT CUSTKEY FROM ORDERS WHERE CUSTKEY <10) /*+ dynamicFilter=true*/" +
                "       GROUP BY NAME" +
                "       ORDER BY MAXKEY" +
                "       LIMIT 1";

        String newsql = SQLUtils.formatMySql(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("EXPLAIN (FORMAT detail) SELECT customer.NAME, MAX(customer.CUSTKEY) AS MAXKEY\n" +
                            "FROM CUSTOMER, ORDERS\n" +
                            "WHERE CUSTOMER.CUSTKEY NOT IN (\n" +
                            "\t\tSELECT CUSTKEY\n" +
                            "\t\tFROM ORDERS\n" +
                            "\t\tWHERE CUSTKEY > 100\n" +
                            "\t)\n" +
                            "\tAND CUSTOMER.CUSTKEY IN (\n" +
                            "\t\tSELECT CUSTKEY\n" +
                            "\t\tFROM ORDERS\n" +
                            "\t\tWHERE CUSTKEY < 10\n" +
                            "\t)/*+ dynamicFilter=true*/\n" +
                            "GROUP BY NAME\n" +
                            "ORDER BY MAXKEY\n" +
                            "LIMIT 1", //
                    output);
        }
    }

    public void test_14() throws Exception {
        String sql = "SELECT  count(*)" +
                "FROM     orders o " +
                "JOIN  lineitem l " +
                "ON o.orderkey = l.orderkey /*+distributionType=partition*/" +
                "LIMIT    100";;

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM orders o\n" +
                            "\tJOIN lineitem l ON o.orderkey = l.orderkey/*+distributionType=partition*/\n" +
                            "LIMIT 100", //
                    output);
        }
    }

    public void test_15() throws Exception {
        String sql = "SELECT  count(*)" +
                "FROM     orders o " +
                "JOIN  lineitem l " +
                "ON o.orderkey = l.orderkey /*+distributionType=repartition*/" +
                "LIMIT    100";;

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM orders o\n" +
                            "\tJOIN lineitem l ON o.orderkey = l.orderkey/*+distributionType=repartition*/\n" +
                            "LIMIT 100", //
                    output);
        }
    }

    public void test_16() throws Exception {
        String sql = "SELECT  count(*)" +
                "FROM     orders o " +
                "JOIN  lineitem l " +
                "ON o.orderkey = l.orderkey /*+distributionType=broadcast*/" +
                "LIMIT    100";;

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM orders o\n" +
                            "\tJOIN lineitem l ON o.orderkey = l.orderkey/*+distributionType=broadcast*/\n" +
                            "LIMIT 100", //
                    output);
        }
    }

    public void test_17() throws Exception {
        String sql = "SELECT  count(*)" +
                "FROM     orders o " +
                "JOIN  lineitem l " +
                "ON o.orderkey = l.orderkey /*+distribution_type=broadcast*/" +
                "LIMIT    100";;

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM orders o\n" +
                            "\tJOIN lineitem l ON o.orderkey = l.orderkey/*+distribution_type=broadcast*/\n" +
                            "LIMIT 100", //
                    output);
        }
    }

    public void test_18() throws Exception {
        String sql = "SELECT  count(*)" +
                "FROM     orders o " +
                "JOIN  lineitem l " +
                "ON o.orderkey = l.orderkey /*+distribution_type=repartition*/" +
                "LIMIT    100";

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM orders o\n" +
                            "\tJOIN lineitem l ON o.orderkey = l.orderkey/*+distribution_type=repartition*/\n" +
                            "LIMIT 100", //
                    output);
        }
    }

    public void test_19() throws Exception {
        String sql = "select count(*) from t1 inner join t2 on t1.col1=t2.col1 and t1.col2=t2.col2 /*+join_criteria=2*/;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM t1\n" +
                            "\tINNER JOIN t2\n" +
                            "\tON t1.col1 = t2.col1\n" +
                            "\t\tAND t1.col2 = t2.col2/*+join_criteria=2*/;", //
                    output);
        }
    }

    public void test_20() throws Exception {
        String sql = "select count(*) from t1, t2 where t1.col1=t2.col1 and t1.col2=t2.col2 /*+join_criteria=2*/;";

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                            sql,
                            DbType.mysql,
                            SQLParserFeature.PipesAsConcat,
                            SQLParserFeature.EnableSQLBinaryOpExprGroup,
                            SQLParserFeature.SelectItemGenerateAlias,
                            SQLParserFeature.EnableMultiUnion);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT count(*) AS `count(*)`\n" +
                            "FROM t1, t2\n" +
                            "WHERE t1.col1 = t2.col1\n" +
                            "\tAND t1.col2 = t2.col2/*+join_criteria=2*/;", //
                    output);
        }
    }
}
