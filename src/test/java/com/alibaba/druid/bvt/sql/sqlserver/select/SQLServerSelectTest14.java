/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.sqlserver.select;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.test.TestUtils;

public class SQLServerSelectTest14 extends TestCase {

    public void test_simple() throws Exception {
        String sql = "SELECT " + //
                     "    a.* " + //
                     "FROM " + //
                     "    ( " + //
                     "            SELECT " + //
                     "                    row_number () over (ORDER BY a.time_add DESC) ROW, " + //
                     "                    a.detail_no AS detailNo, " + //
                     "                    a.ba_id AS baId, " + //
                     "                    a.ba_name AS baName, " + //
                     "                    a.tran_no AS tranNo, " + //
                     "                    a.tran_name AS tranName, " + //
                     "                    a.tran_type AS tranType, " + //
                     "                    a.balance_type AS balanceType, " + //
                     "                    a.detail_income AS detailIncome, " + //
                     "                    a.detail_payout AS detailPayout, " + //
                     "                    a.before_balance AS beforeBalance, " + //
                     "                    a.after_balance AS afterBalance, " + //
                     "                    a.time_add AS timeAdd, " + //
                     "                    a.user_add AS userAdd, " + //
                     "                    a.remark AS remark, " + //
                     "                    ( " + //
                     "                            SELECT " + //
                     "                                    top 1 t.param_name " + //
                     "                            FROM " + //
                     "                                    config.sys_params t " + //
                     "                            WHERE " + //
                     "                                    t.param_type = 2 " + //
                     "                            AND t.param_value = a.tran_type " + //
                     "                    ) AS tranTypeName " + //
                     "            FROM " + //
                     "                    bussiness.account_detail a " + //
                     "            WHERE " + //
                     "                    1 = 1 " + //
                     "            AND a.time_add >= 2 " + //
                     "            AND a.time_add <= 3 " + //
                     "    ) a " + //
                     "WHERE " + //
                     "    a.ROW BETWEEN (10+2) AND 20 "; //

        String expect = "SELECT a.*\n" +
                "FROM (\n" +
                "\tSELECT row_number() OVER (ORDER BY a.time_add DESC) AS ROW, a.detail_no AS detailNo, a.ba_id AS baId, a.ba_name AS baName, a.tran_no AS tranNo\n" +
                "\t\t, a.tran_name AS tranName, a.tran_type AS tranType, a.balance_type AS balanceType, a.detail_income AS detailIncome, a.detail_payout AS detailPayout\n" +
                "\t\t, a.before_balance AS beforeBalance, a.after_balance AS afterBalance, a.time_add AS timeAdd, a.user_add AS userAdd, a.remark AS remark\n" +
                "\t\t, (\n" +
                "\t\t\tSELECT TOP 1 t.param_name\n" +
                "\t\t\tFROM config.sys_params t\n" +
                "\t\t\tWHERE t.param_type = 2\n" +
                "\t\t\t\tAND t.param_value = a.tran_type\n" +
                "\t\t) AS tranTypeName\n" +
                "\tFROM bussiness.account_detail a\n" +
                "\tWHERE 1 = 1\n" +
                "\t\tAND a.time_add >= 2\n" +
                "\t\tAND a.time_add <= 3\n" +
                ") a\n" +
                "WHERE a.ROW BETWEEN (10 + 2) AND 20";

        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);

        String text = TestUtils.outputSqlServer(stmt);

        assertEquals(expect, text);

//        System.out.println(text);
    }
}
