/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
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
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class MySqlSelectTest_304_adb_hints
        extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1 /*+output_rows=100, aggregation_path_type=singleAgg*/\n" +
                "HAVING COUNT(1) > 10 /*+output_rows=50*/\n" +
                "ORDER BY t1.c1 ";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1/*+output_rows=100, aggregation_path_type=singleAgg*/\n" +
                "HAVING COUNT(1) > 10/*+output_rows=50*/\n" +
                "ORDER BY t1.c1", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "\n" +
                "GROUP BY t1.c1 /*+output_rows=100, aggregation_path_type=single*/\n" +
                "\n" +
                "HAVING COUNT(1) > 10 /*+output_rows=50*/\n" +
                "\n" +
                "ORDER BY t1.c1 /*+output_rows=100*/" +
                "\n";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1/*+output_rows=100, aggregation_path_type=single*/\n" +
                "HAVING COUNT(1) > 10/*+output_rows=50*/\n" +
                "ORDER BY t1.c1/*+output_rows=100*/", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "SELECT count(distinct c1.custkey) " +
                        "   from customer c1, customer c2 " +
                        "   where c1.custkey=c2.custkey /*+output_rows=2*/" +
                        "   and c1.custkey=1 /*+filter_factor=0.5*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT count(DISTINCT c1.custkey)\n" +
                            "FROM customer c1, customer c2\n" +
                            "WHERE c1.custkey = c2.custkey/*+output_rows=2*/\n" +
                            "\tAND c1.custkey = 1/*+filter_factor=0.5*/", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "SELECT COUNT(*) \n" +
                        "FROM  t1  , t2 \n" +
                        "WHERE t1.c1 = t2.c1 /*+output_Rows=100*/\n" +
                        "  AND t1.c1 < 10 ";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1/*+output_Rows=100*/\n" +
                "\tAND t1.c1 < 10", stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "SELECT COUNT(*) \n" +
                "FROM  t1, t2 \n" +
                "WHERE t1.c1 = t2.c1 \n" +
                "  AND t1.c1 < 10 /*+output_Rows=100*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "\tAND t1.c1 < 10/*+output_Rows=100*/", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "SELECT COUNT(*) \n" +
                "FROM t1, t2 \n" +
                "WHERE t1.c1 = t2.c1 /*+filter_factor=0.5*/\n" +
                "AND t1.c1 < 10 ";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1/*+filter_factor=0.5*/\n" +
                "\tAND t1.c1 < 10", stmt.toString());
    }

    public void test_6() throws Exception {
        String sql = "SELECT COUNT(*) \n" +
                "FROM t1, t2 \n" +
                "WHERE t1.c1 = t2.c1 \n" +
                "AND t1.c1 < 10 /*+filter_factor=0.1*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "\tAND t1.c1 < 10/*+filter_factor=0.1*/", stmt.toString());
    }

    public void test_7() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1 /*+aggregation_path_type=single*/\n" +
                "ORDER BY t1.c1";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1/*+aggregation_path_type=single*/\n" +
                "ORDER BY t1.c1", stmt.toString());
    }

    public void test_8() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1 /*+outputRows=100*/\n" +
                "ORDER BY t1.c1 ";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1/*+outputRows=100*/\n" +
                "ORDER BY t1.c1", stmt.toString());
    }

    public void test_9() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1 /*+output_Rows=100*/\n" +
                "ORDER BY t1.c1 ";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1/*+output_Rows=100*/\n" +
                "ORDER BY t1.c1", stmt.toString());
    }

    public void test_10() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1 \n" +
                "ORDER BY t1.c1 /*+outputRows=100*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1\n" +
                "ORDER BY t1.c1/*+outputRows=100*/", stmt.toString());
    }

    public void test_11() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1 \n" +
                "HAVING COUNT(1) > 10 /*+output_Rows=10*/\n" +
                "ORDER BY t1.c1 \n" +
                "LIMIT 10";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "GROUP BY t1.c1\n" +
                "HAVING COUNT(1) > 10/*+output_Rows=10*/\n" +
                "ORDER BY t1.c1\n" +
                "LIMIT 10", stmt.toString());
    }

    public void test_12() throws Exception {
        String sql = " SELECT   s.acctbal, \n" +
                "         s.name, \n" +
                "         n.name, \n" +
                "         p.partkey, \n" +
                "         p.mfgr, \n" +
                "         s.address, \n" +
                "         s.phone, \n" +
                "         s.comment \n" +
                "FROM     part p, \n" +
                "         supplier s, \n" +
                "         partsupp ps, \n" +
                "         nation n, \n" +
                "         region r\n" +
                "WHERE    p.partkey = ps.partkey \n" +
                "AND      s.suppkey = ps.suppkey \n" +
                "AND      p.size = 35 /*+output_rows=2*/" +
                "AND      p.type LIKE '%NICKEL' \n" +
                "AND      s.nationkey = n.nationkey \n" +
                "AND      n.regionkey = r.regionkey \n" +
                "AND      r.name = 'MIDDLE EAST' " +
                "AND      ps.supplycost IN \n" +
                "         ( \n" +
                "                SELECT min(ps.supplycost) \n" +
                "                FROM   partsupp ps, \n" +
                "                       supplier s, \n" +
                "                       nation n, \n" +
                "                       region r\n" +
                "                WHERE  s.suppkey = ps.suppkey \n" +
                "                AND    s.nationkey = n.nationkey \n" +
                "                AND    n.regionkey = r.regionkey \n" +
                "                AND    r.name = 'MIDDLE EAST' ) /*+output_rows=100*/" +
                "ORDER BY s.acctbal DESC, \n" +
                "         n.name, \n" +
                "         s.name, \n" +
                "         p.partkey \n" +
                "LIMIT    100";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT s.acctbal, s.name, n.name, p.partkey, p.mfgr\n" +
                "\t, s.address, s.phone, s.comment\n" +
                "FROM part p, supplier s, partsupp ps, nation n, region r\n" +
                "WHERE p.partkey = ps.partkey\n" +
                "\tAND s.suppkey = ps.suppkey\n" +
                "\tAND p.size = 35/*+output_rows=2*/\n" +
                "\tAND p.type LIKE '%NICKEL'\n" +
                "\tAND s.nationkey = n.nationkey\n" +
                "\tAND n.regionkey = r.regionkey\n" +
                "\tAND r.name = 'MIDDLE EAST'\n" +
                "\tAND ps.supplycost IN (\n" +
                "\t\tSELECT min(ps.supplycost)\n" +
                "\t\tFROM partsupp ps, supplier s, nation n, region r\n" +
                "\t\tWHERE s.suppkey = ps.suppkey\n" +
                "\t\t\tAND s.nationkey = n.nationkey\n" +
                "\t\t\tAND n.regionkey = r.regionkey\n" +
                "\t\t\tAND r.name = 'MIDDLE EAST'\n" +
                "\t)/*+output_rows=100*/\n" +
                "ORDER BY s.acctbal DESC, n.name, s.name, p.partkey\n" +
                "LIMIT 100", stmt.toString());
    }


    public void test_13() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1 and a like '%abc' /*+output_Rows=10*/\n" +
                "GROUP BY t1.c1 \n" +
                "ORDER BY t1.c1 \n" +
                "LIMIT 10";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE t1.c1 = t2.c1\n" +
                "\tAND a LIKE '%abc'/*+output_Rows=10*/\n" +
                "GROUP BY t1.c1\n" +
                "ORDER BY t1.c1\n" +
                "LIMIT 10", stmt.toString());
    }

    public void test_exists() throws Exception {
        String sql = "SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE exists (select * from t)/*+output_Rows=10*/ \n" +
                "GROUP BY t1.c1 \n" +
                "ORDER BY t1.c1 \n" +
                "LIMIT 10";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT COUNT(*)\n" +
                "FROM t1, t2\n" +
                "WHERE EXISTS (\n" +
                "\tSELECT *\n" +
                "\tFROM t\n" +
                ") /*+output_Rows=10*/\n" +
                "GROUP BY t1.c1\n" +
                "ORDER BY t1.c1\n" +
                "LIMIT 10", stmt.toString());
    }

    public void test_exists2() throws Exception {
        String sql = "SELECT count(1) " +
        "   from customer c1 " +
        "   where exists (" +
        "       select c2.custkey from customer c2 " +
        "       where c2.custkey=1 " +
        "   ) /*+output_rows=10*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT count(1)\n" +
                "FROM customer c1\n" +
                "WHERE EXISTS (\n" +
                "\tSELECT c2.custkey\n" +
                "\tFROM customer c2\n" +
                "\tWHERE c2.custkey = 1\n" +
                ") /*+output_rows=10*/", stmt.toString());
    }

    public void test_not() throws Exception {
        String sql = "SELECT count(1) " +
        "   from customer c1 " +
        "   where not exists (" +
        "       select c2.custkey from customer c2 " +
        "       where c2.custkey=1 " +
        "   ) /*+output_rows=10*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT count(1)\n" +
                "FROM customer c1\n" +
                "WHERE NOT EXISTS (\n" +
                "\tSELECT c2.custkey\n" +
                "\tFROM customer c2\n" +
                "\tWHERE c2.custkey = 1\n" +
                ") /*+output_rows=10*/", stmt.toString());
    }

    public void test_not2() throws Exception {
        String sql = "SELECT count(1) " +
        "   from customer c1 " +
        "   where not(c1.a > 1 && a < 10) /*+output_rows=10*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT count(1)\n" +
                "FROM customer c1\n" +
                "WHERE NOT (c1.a > 1\n" +
                "AND a < 10)/*+output_rows=10*/", stmt.toString());
    }

    public void test_groupby() throws Exception {
        String sql = "SELECT /*+output_rows=count_order*/\n" +
                "  l.returnflag,\n" +
                "  l.linestatus,\n" +
                "  count(*) AS count_order\n" +
                "FROM\n" +
                "  lineitem AS l\n" +
                "WHERE\n" +
                "  l.shipdate <= DATE '1998-12-01' - INTERVAL '120' DAY /*+output_rows=1*/" +
                "GROUP BY\n" +
                "  l.returnflag,\n" +
                "  l.linestatus /*+output_rows=1000*/ " +
                "ORDER BY\n" +
                "  l.returnflag,\n" +
                "  l.linestatus";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT /*+output_rows=count_order*/ l.returnflag, l.linestatus, count(*) AS count_order\n" +
                "FROM lineitem l\n" +
                "WHERE l.shipdate <= DATE '1998-12-01' - INTERVAL '120' DAY/*+output_rows=1*/\n" +
                "GROUP BY l.returnflag, l.linestatus/*+output_rows=1000*/\n" +
                "ORDER BY l.returnflag, l.linestatus", stmt.toString());
    }

    public void test_groupby2() throws Exception {
        String sql =
                "       SELECT\n" +
                "        * "+
                "       FROM\n" +
                "         supplier AS s,\n" +
                "         lineitem AS l,\n" +
                "         orders AS o,\n" +
                "         customer AS c,\n" +
                "         nation AS n1,\n" +
                "         nation AS n2\n" +
                "       WHERE\n" +
                "         s.suppkey = l.suppkey\n" +
                "         AND (\n" +
                "           (n1.name = 'CANADA' AND n2.name = 'BRAZIL')\n" +
                "           OR (n1.name = 'BRAZIL' AND n2.name = 'CANADA')\n" +
                "         )/*+output_rows=1000*/" +
                "         AND l.shipdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31' /*+output_rows=1*/";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT *\n" +
                "FROM supplier s, lineitem l, orders o, customer c, nation n1, nation n2\n" +
                "WHERE s.suppkey = l.suppkey\n" +
                "\tAND ((n1.name = 'CANADA'\n" +
                "\t\t\tAND n2.name = 'BRAZIL')\n" +
                "\t\tOR (n1.name = 'BRAZIL'\n" +
                "\t\t\tAND n2.name = 'CANADA')/*+output_rows=1000*/)\n" +
                "\tAND l.shipdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31'/*+output_rows=1*/", stmt.toString());
    }

    public void test_tpch_q7() throws Exception {
        String sql = "SELECT\n" +
                "  supp_nation,\n" +
                "  cust_nation,\n" +
                "  l_year,\n" +
                "  sum(volume) AS revenue\n" +
                "FROM (\n" +
                "       SELECT\n" +
                "         n1.name                          AS supp_nation,\n" +
                "         n2.name                          AS cust_nation,\n" +
                "         extract(YEAR FROM l.shipdate)      AS l_year,\n" +
                "         l.extendedprice * (1 - l.discount) AS volume\n" +
                "       FROM\n" +
                "         supplier AS s,\n" +
                "         lineitem AS l,\n" +
                "         orders AS o,\n" +
                "         customer AS c,\n" +
                "         nation AS n1,\n" +
                "         nation AS n2\n" +
                "       WHERE\n" +
                "         s.suppkey = l.suppkey\n" +
                "         AND o.orderkey = l.orderkey\n" +
                "         AND c.custkey = o.custkey\n" +
                "         AND s.nationkey = n1.nationkey\n" +
                "         AND c.nationkey = n2.nationkey\n" +
                "         AND (\n" +
                "           (n1.name = 'CANADA' AND n2.name = 'BRAZIL')\n" +
                "           OR (n1.name = 'BRAZIL' AND n2.name = 'CANADA')\n" +
                "         )/*+output_rows=1000*/" +
                "         AND l.shipdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31' /*+output_rows=1*/" +
                "     ) AS shipping\n" +
                "GROUP BY\n" +
                "  supp_nation,\n" +
                "  cust_nation,\n" +
                "  l_year /*+output_rows=10*/" +
                "ORDER BY\n" +
                "  supp_nation,\n" +
                "  cust_nation,\n" +
                "  l_year";

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                sql,
                DbType.mysql,
                SQLParserFeature.PipesAsConcat,
                SQLParserFeature.EnableSQLBinaryOpExprGroup,
                SQLParserFeature.SelectItemGenerateAlias,
                SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT supp_nation, cust_nation, l_year, sum(volume) AS revenue\n" +
                "FROM (\n" +
                "\tSELECT n1.name AS supp_nation, n2.name AS cust_nation, EXTRACT(YEAR FROM l.shipdate) AS l_year\n" +
                "\t\t, l.extendedprice * (1 - l.discount) AS volume\n" +
                "\tFROM supplier s, lineitem l, orders o, customer c, nation n1, nation n2\n" +
                "\tWHERE s.suppkey = l.suppkey\n" +
                "\t\tAND o.orderkey = l.orderkey\n" +
                "\t\tAND c.custkey = o.custkey\n" +
                "\t\tAND s.nationkey = n1.nationkey\n" +
                "\t\tAND c.nationkey = n2.nationkey\n" +
                "\t\tAND ((n1.name = 'CANADA'\n" +
                "\t\t\t\tAND n2.name = 'BRAZIL')\n" +
                "\t\t\tOR (n1.name = 'BRAZIL'\n" +
                "\t\t\t\tAND n2.name = 'CANADA')/*+output_rows=1000*/)\n" +
                "\t\tAND l.shipdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31'/*+output_rows=1*/\n" +
                ") shipping\n" +
                "GROUP BY supp_nation, cust_nation, l_year/*+output_rows=10*/\n" +
                "ORDER BY supp_nation, cust_nation, l_year", stmt.toString());
    }

    public void test_tpch_q8() throws Exception {
        String sql = "SELECT\n" +
                "  o_year,\n" +
                "  sum(CASE\n" +
                "      WHEN nation = 'BRAZIL'\n" +
                "        THEN volume\n" +
                "      ELSE 0\n" +
                "      END) / sum(volume) AS mkt_share\n" +
                "FROM (\n" +
                "       SELECT\n" +
                "         extract(YEAR FROM o.orderdate)     AS o_year,\n" +
                "         l.extendedprice * (1 - l.discount) AS volume,\n" +
                "         n2.name                          AS nation\n" +
                "       FROM\n" +
                "         part AS p,\n" +
                "         supplier AS s,\n" +
                "         lineitem AS l,\n" +
                "         orders AS o,\n" +
                "         customer AS c,\n" +
                "         nation AS n1,\n" +
                "         nation AS n2,\n" +
                "         region AS r\n" +
                "       WHERE\n" +
                "         p.partkey = l.partkey\n" +
                "         AND s.suppkey = l.suppkey\n" +
                "         AND l.orderkey = o.orderkey\n" +
                "         AND o.custkey = c.custkey\n" +
                "         AND c.nationkey = n1.nationkey\n" +
                "         AND n1.regionkey = r.regionkey\n" +
                "         AND r.name = 'AMERICA' /*+output_rows=20*/ \n" +
                "         AND s.nationkey = n2.nationkey\n" +
                "         AND o.orderdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31'  /*+output_rows=40*/ \n" +
                "         AND p.type = 'LARGE ANODIZED COPPER'  \n" +
                "     ) AS all_nations\n" +
                "GROUP BY\n" +
                "  o_year /*+output_rows=10*/ " +
                "ORDER BY\n" +
                "  o_year\n";

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                sql,
                DbType.mysql,
                SQLParserFeature.PipesAsConcat,
                SQLParserFeature.EnableSQLBinaryOpExprGroup,
                SQLParserFeature.SelectItemGenerateAlias,
                SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT o_year\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN nation = 'BRAZIL' THEN volume\n" +
                "\t\tELSE 0\n" +
                "\tEND) / sum(volume) AS mkt_share\n" +
                "FROM (\n" +
                "\tSELECT EXTRACT(YEAR FROM o.orderdate) AS o_year, l.extendedprice * (1 - l.discount) AS volume, n2.name AS nation\n" +
                "\tFROM part p, supplier s, lineitem l, orders o, customer c, nation n1, nation n2, region r\n" +
                "\tWHERE p.partkey = l.partkey\n" +
                "\t\tAND s.suppkey = l.suppkey\n" +
                "\t\tAND l.orderkey = o.orderkey\n" +
                "\t\tAND o.custkey = c.custkey\n" +
                "\t\tAND c.nationkey = n1.nationkey\n" +
                "\t\tAND n1.regionkey = r.regionkey\n" +
                "\t\tAND r.name = 'AMERICA'/*+output_rows=20*/\n" +
                "\t\tAND s.nationkey = n2.nationkey\n" +
                "\t\tAND o.orderdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31'/*+output_rows=40*/\n" +
                "\t\tAND p.type = 'LARGE ANODIZED COPPER'\n" +
                ") all_nations\n" +
                "GROUP BY o_year/*+output_rows=10*/\n" +
                "ORDER BY o_year", stmt.toString());
    }

    public void test_between_and() throws Exception {
        String sql =
                "       SELECT\n" +
                "         n1.name                          AS supp_nation,\n" +
                "         n2.name                          AS cust_nation,\n" +
                "         extract(YEAR FROM l.shipdate)      AS l_year,\n" +
                "         l.extendedprice * (1 - l.discount) AS volume\n" +
                "       FROM\n" +
                "         supplier AS s,\n" +
                "         lineitem AS l,\n" +
                "         orders AS o,\n" +
                "         customer AS c,\n" +
                "         nation AS n1,\n" +
                "         nation AS n2\n" +
                "       WHERE\n" +
                "         l.shipdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31' /*+output_rows=1*/ \n" +
                "         AND c.custkey = o.custkey";

        SQLStatement stmt = SQLUtils.parseSingleStatement(
                sql,
                DbType.mysql,
                SQLParserFeature.PipesAsConcat,
                SQLParserFeature.EnableSQLBinaryOpExprGroup,
                SQLParserFeature.SelectItemGenerateAlias,
                SQLParserFeature.EnableMultiUnion);

        assertEquals("SELECT n1.name AS supp_nation, n2.name AS cust_nation, EXTRACT(YEAR FROM l.shipdate) AS l_year\n" +
                "\t, l.extendedprice * (1 - l.discount) AS volume\n" +
                "FROM supplier s, lineitem l, orders o, customer c, nation n1, nation n2\n" +
                "WHERE l.shipdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31'/*+output_rows=1*/\n" +
                "\tAND c.custkey = o.custkey", stmt.toString());
    }
}