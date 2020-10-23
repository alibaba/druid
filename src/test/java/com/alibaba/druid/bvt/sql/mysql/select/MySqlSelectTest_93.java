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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

import java.util.List;

public class MySqlSelectTest_93 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select a as `a`, b `b`, c 'c', d \"d\" from t1";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT a AS a, b AS b, c AS c, d AS d\n" + "FROM t1", stmt.toString());
    }

    public void test_1() throws Exception {
        String sql = "select a as `b` from t1 `t`";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT a AS b\n" + "FROM t1 t", stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select a as `b` from t1 `t`";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT a AS b\n" + "FROM t1 t", stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select a as `a.a`, b 'b.b' from t1 `t`";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT a AS `a.a`, b AS `b.b`\n" +
                "FROM t1 t", stmt.toString());
    }

    public void test_5() throws Exception {
        String sql = "INSERT INTO brand_crm_ship.imp_order_lock \n" + "            (tid, \n" + "             pid, \n"
                     + "             __aid, \n" + "             pv) SELECT 121, \n" + "       48868196, \n"
                     + "       t2.__aid, \n" + "       t3.pv \n" + "FROM   (SELECT DISTINCT __aid \n"
                     + "        FROM   (SELECT DISTINCT \n"
                     + "       brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.__aid \n"
                     + "       AS __aid \n"
                     + "       FROM   brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump \n"
                     + "       WHERE \n" + "( \n" + "( \n"
                     + "brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.label_list_interest IN ( '1142' )\n"
                     + "AND brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.user_age IN \n" + "( \n"
                     + "'4', '3', '2', '1' ) \n" + "AND \n"
                     + "brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.label_list_basic IN ( '1605', '1603', '1604', '1563' ) )\n"
                     + "AND NOT (brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.label_list_industry IN ( '1140' )) )) t1) t2\n"
                     + "JOIN (SELECT __aid, \n" + "             pv \n"
                     + "      FROM   brand_crm_ship.palgo_o2o_imp_px_log_sample_adzone_aid_merge \n"
                     + "      WHERE  adzone_id = 48868196) t3 \n" + "  ON t3.__aid = t2.__aid ";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());
        String s = SQLUtils.toMySqlString(statementList.get(0));
        System.out.println(s);
    }

    public void test_4() throws Exception {
        String sql = "select  b 'c' from t1 `t`";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT b AS c\n" + "FROM t1 t", stmt.toString());
    }

    public void test_9() throws Exception {
        String sql = " select * from (select pk from test_tb) as `a` order by `a`.pk;";

        MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.IgnoreNameQuotes);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        assertEquals("SELECT *\n" + "FROM (\n" + "\tSELECT pk\n" + "\tFROM test_tb\n" + ") a\n" + "ORDER BY a.pk;", stmt.toString());
    }
}
