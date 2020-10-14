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
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;


public class MySqlSelectTest_234 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT ema_user_id\n"
                + "   FROM(\n"
                + "         SELECT ema_user_id, 0 as o  \n"
                + "         FROM g170_user\n"
                + "         WHERE `account`= '1_30002_35104'  LIMIT 1  \n"
                + "         union \n"
                + "         SELECT ema_user_id, 1 as o FROM g170_user  WHERE `g_distinct_id`= ''  LIMIT 1       )  ORDER BY o";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("SELECT ema_user_id\n" +
                "FROM (\n" +
                "\tSELECT ema_user_id, 0 AS o\n" +
                "\tFROM g170_user\n" +
                "\tWHERE `account` = '1_30002_35104'\n" +
                "\tLIMIT 1\n" +
                "\tUNION\n" +
                "\tSELECT ema_user_id, 1 AS o\n" +
                "\tFROM g170_user\n" +
                "\tWHERE `g_distinct_id` = ''\n" +
                "\tLIMIT 1\n" +
                ")\n" +
                "ORDER BY o", stmt.toString());
    }



}