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


public class MySqlSelectTest_280 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select date(created_at)创建日期,\n" +
                "count(distinct task_id)整体维修创建量,\n" +
                "count(distinct case when order_type='客户预约'then task_id end)预约报修创建量,\n" +
                "count(distinct case when order_type='客户预约'then task_id end)/count(distinct task_id)预约维修创建比例,\n" +
                "count(distinct case when hour(created_at)>=9 and hour(created_at)<21 and order_type='客户预约'then task_id end)预约维修工作时间创建量,\n" +
                "count(case when hour(created_at)>=9 and hour(created_at)<21 and timestampdiff(MINUTE,created_at,assign_at)<=30 then task_id end)预约维修工作时间及时受理量,\n" +
                "count(case when hour(created_at)>=9 and hour(created_at)<21 and timestampdiff(MINUTE,created_at,assign_at)<=30 then task_id end)/count(distinct case when hour(created_at)>=9 and hour(created_at)<21 and order_type='客户预约'then task_id end)30分钟受理率（工作时间）\n" +
                "from basic_repair_tasks\n" +
                "where date(created_at)>='2019-03-01'\n" +
                "and date(created_at)<=date_sub(curdate(),interval 1 day)\n" +
                "group by date(created_at)";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("SELECT date(created_at) AS 创建日期, count(DISTINCT task_id) AS 整体维修创建量\n" +
                "\t, count(DISTINCT CASE \n" +
                "\t\tWHEN order_type = '客户预约' THEN task_id\n" +
                "\tEND) AS 预约报修创建量\n" +
                "\t, count(DISTINCT CASE \n" +
                "\t\tWHEN order_type = '客户预约' THEN task_id\n" +
                "\tEND) / count(DISTINCT task_id) AS 预约维修创建比例\n" +
                "\t, count(DISTINCT CASE \n" +
                "\t\tWHEN hour(created_at) >= 9\n" +
                "\t\t\tAND hour(created_at) < 21\n" +
                "\t\t\tAND order_type = '客户预约'\n" +
                "\t\tTHEN task_id\n" +
                "\tEND) AS 预约维修工作时间创建量\n" +
                "\t, count(CASE \n" +
                "\t\tWHEN hour(created_at) >= 9\n" +
                "\t\t\tAND hour(created_at) < 21\n" +
                "\t\t\tAND timestampdiff(MINUTE, created_at, assign_at) <= 30\n" +
                "\t\tTHEN task_id\n" +
                "\tEND) AS 预约维修工作时间及时受理量\n" +
                "\t, count(CASE \n" +
                "\t\tWHEN hour(created_at) >= 9\n" +
                "\t\t\tAND hour(created_at) < 21\n" +
                "\t\t\tAND timestampdiff(MINUTE, created_at, assign_at) <= 30\n" +
                "\t\tTHEN task_id\n" +
                "\tEND) / count(DISTINCT CASE \n" +
                "\t\tWHEN hour(created_at) >= 9\n" +
                "\t\t\tAND hour(created_at) < 21\n" +
                "\t\t\tAND order_type = '客户预约'\n" +
                "\t\tTHEN task_id\n" +
                "\tEND) AS 30分钟受理率（工作时间）\n" +
                "FROM basic_repair_tasks\n" +
                "WHERE date(created_at) >= '2019-03-01'\n" +
                "\tAND date(created_at) <= date_sub(curdate(), INTERVAL 1 DAY)\n" +
                "GROUP BY date(created_at)", stmt.toString());
    }



}