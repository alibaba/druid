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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;


public class MySqlSelectTest_216 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT   t2.id,   t2.sellerId,   t2.buyer_id,   t2.balance,   t2.parent_accountId,   t2.used,   t2.freeze,   t2.useless,   t2.status,   t2.remark,   t2.gmt_create,   t2.create_by,   t2.period_amount,   t2.buyer_nick,   t2.buyer_mobile,   t2.buyer_level,   t2.total_amount,   t2.account_type_id,   t2.pre_effect,   t2.gmt_modified,   t2.modified_by,   t2.last_statistic_time,   t2.period_amount,   t2.buyer_level_time,   t2.encrypt_mobile,   t2.encrypt_nick,   t2.fixed_phone,   t2.latest_expire_cal_time,   t2.pre_expire   FROM (    SELECT id FROM t_jifen_account    WHERE sellerId = 15142801667171005                 AND parent_accountId > 0                                AND balance >= '100'               AND   balance <= '100000'                  AND total_amount >= '50'               AND   total_amount <= '10000000'            LIMIT 500, 500    ORDER BY id   )t1, t_jifen_account t2   WHERE t1.id = t2.id   AND t2.sellerId = 15142801667171005";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT t2.id, t2.sellerId, t2.buyer_id, t2.balance, t2.parent_accountId\n" +
                "\t, t2.used, t2.freeze, t2.useless, t2.status, t2.remark\n" +
                "\t, t2.gmt_create, t2.create_by, t2.period_amount, t2.buyer_nick, t2.buyer_mobile\n" +
                "\t, t2.buyer_level, t2.total_amount, t2.account_type_id, t2.pre_effect, t2.gmt_modified\n" +
                "\t, t2.modified_by, t2.last_statistic_time, t2.period_amount, t2.buyer_level_time, t2.encrypt_mobile\n" +
                "\t, t2.encrypt_nick, t2.fixed_phone, t2.latest_expire_cal_time, t2.pre_expire\n" +
                "FROM (\n" +
                "\tSELECT id\n" +
                "\tFROM t_jifen_account\n" +
                "\tWHERE sellerId = 15142801667171005\n" +
                "\t\tAND parent_accountId > 0\n" +
                "\t\tAND balance >= '100'\n" +
                "\t\tAND balance <= '100000'\n" +
                "\t\tAND total_amount >= '50'\n" +
                "\t\tAND total_amount <= '10000000'\n" +
                "\tORDER BY id\n" +
                "\tLIMIT 500, 500\n" +
                ") t1, t_jifen_account t2\n" +
                "WHERE t1.id = t2.id\n" +
                "\tAND t2.sellerId = 15142801667171005", stmt.toString());

        assertEquals("select t2.id, t2.sellerId, t2.buyer_id, t2.balance, t2.parent_accountId\n" +
                "\t, t2.used, t2.freeze, t2.useless, t2.status, t2.remark\n" +
                "\t, t2.gmt_create, t2.create_by, t2.period_amount, t2.buyer_nick, t2.buyer_mobile\n" +
                "\t, t2.buyer_level, t2.total_amount, t2.account_type_id, t2.pre_effect, t2.gmt_modified\n" +
                "\t, t2.modified_by, t2.last_statistic_time, t2.period_amount, t2.buyer_level_time, t2.encrypt_mobile\n" +
                "\t, t2.encrypt_nick, t2.fixed_phone, t2.latest_expire_cal_time, t2.pre_expire\n" +
                "from (\n" +
                "\tselect id\n" +
                "\tfrom t_jifen_account\n" +
                "\twhere sellerId = 15142801667171005\n" +
                "\t\tand parent_accountId > 0\n" +
                "\t\tand balance >= '100'\n" +
                "\t\tand balance <= '100000'\n" +
                "\t\tand total_amount >= '50'\n" +
                "\t\tand total_amount <= '10000000'\n" +
                "\torder by id\n" +
                "\tlimit 500, 500\n" +
                ") t1, t_jifen_account t2\n" +
                "where t1.id = t2.id\n" +
                "\tand t2.sellerId = 15142801667171005", stmt.clone().toLowerCaseString());

    }
}