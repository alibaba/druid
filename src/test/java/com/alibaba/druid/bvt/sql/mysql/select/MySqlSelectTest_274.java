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


public class MySqlSelectTest_274 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+ run_async=true,mq-notify-by=ons,mq-delayed-by=10 */ /*+ orc-bloomfilters-enabled=true */ insert into t1( x,y) select * from t2";

        SQLStatement stmt = SQLUtils
                .parseSingleStatement(sql, DbType.mysql);

        assertEquals("/*+ run_async=true,mq-notify-by=ons,mq-delayed-by=10 */\n" +
                "/*+ orc-bloomfilters-enabled=true */\n" +
                "INSERT INTO t1 (x, y)\n" +
                "SELECT *\n" +
                "FROM t2", stmt.toString());
    }



}