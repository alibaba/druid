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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;


public class MySqlSelectTest_243 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "Select DISTINCT buyer_id,\n" +
                "       DISTINCT buyer_full_name\n" +
                "  from trade_order_line\n" +
                " WHERE 1= 1\n" +
                "   AND enable_status= 1\n" +
                "  -- AND seller_id= 100154704\n" +
                "   AND buyer_full_name like concat('%', 'Zaini Abd', '%')\n" +
                "   AND _features.wt= 'dropshipping'\n" +
                "   AND _features.ads= 'pending'\n" +
                "-- GROUP BY buyer_id,\n" +
                "       --  buyer_full_name \n" +
                "LIMIT 0,\n" +
                "         10";


        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.elastic_search);

        assertEquals("SELECT DISTINCT buyer_id, buyer_full_name\n" +
                "FROM trade_order_line\n" +
                "WHERE 1 = 1\n" +
                "\tAND enable_status = 1 -- AND seller_id= 100154704\n" +
                "\tAND buyer_full_name LIKE concat('%', 'Zaini Abd', '%')\n" +
                "\tAND _features.wt = 'dropshipping'\n" +
                "\tAND _features.ads = 'pending'\n" +
                "LIMIT 0, 10", stmt.toString());


        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
    }



}