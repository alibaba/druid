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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;


public class MySqlSelectTest_106_hints extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "\n" +
                "select sum(CASE WHEN a.purchase_times=1 THEN 1 else 0 END ) oneCustomersNum, sum(CASE WHEN a.purchase_times=1 THEN a.payment else 0 END ) onceMoney, sum(CASE WHEN a.purchase_times=1 THEN a.interval_day else 0 END ) oneIntervalDay, sum(CASE WHEN a.purchase_times=2 THEN 1 else 0 END ) twoCustomersNum, sum(CASE WHEN a.purchase_times=2 THEN a.payment else 0 END ) twoMoney, sum(CASE WHEN a.purchase_times=2 THEN a.interval_day else 0 END ) twoIntervalDay, sum(CASE WHEN a.purchase_times=3 THEN 1 else 0 END ) threeCustomersNum, sum(CASE WHEN a.purchase_times=3 THEN a.payment else 0 END ) threeMoney, sum(CASE WHEN a.purchase_times=3 THEN a.interval_day else 0 END ) threeIntervalDay, sum(CASE WHEN a.purchase_times=4 THEN 1 else 0 END ) fourCustomersNum, sum(CASE WHEN a.purchase_times=4 THEN a.payment else 0 END ) fourMoney, sum(CASE WHEN a.purchase_times=4 THEN a.interval_day else 0 END ) fourIntervalDay, sum(CASE WHEN a.purchase_times=5 THEN 1 else 0 END ) fiveCustomersNum, sum(CASE WHEN a.purchase_times=5 THEN a.payment else 0 END ) fiveMoney, sum(CASE WHEN a.purchase_times=5 THEN a.interval_day else 0 END ) fiveIntervalDay from t_buyer_day a force index (sellerId_during) WHERE a.sellerId = 3234284498 and a.pay_trades>0 and ( a.during = str_to_date('2018-01-10', '%Y-%m-%d') );";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 1 THEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS oneCustomersNum\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 1 THEN a.payment\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS onceMoney\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 1 THEN a.interval_day\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS oneIntervalDay\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 2 THEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS twoCustomersNum\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 2 THEN a.payment\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS twoMoney\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 2 THEN a.interval_day\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS twoIntervalDay\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 3 THEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS threeCustomersNum\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 3 THEN a.payment\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS threeMoney\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 3 THEN a.interval_day\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS threeIntervalDay\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 4 THEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS fourCustomersNum\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 4 THEN a.payment\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS fourMoney\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 4 THEN a.interval_day\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS fourIntervalDay\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 5 THEN 1\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS fiveCustomersNum\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 5 THEN a.payment\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS fiveMoney\n" +
                "\t, sum(CASE \n" +
                "\t\tWHEN a.purchase_times = 5 THEN a.interval_day\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS fiveIntervalDay\n" +
                "FROM t_buyer_day a FORCE INDEX (sellerId_during)\n" +
                "WHERE a.sellerId = 3234284498\n" +
                "\tAND a.pay_trades > 0\n" +
                "\tAND a.during = str_to_date('2018-01-10', '%Y-%m-%d');", stmt.toString());
    }

}