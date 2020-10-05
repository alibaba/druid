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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallUtils;

import java.util.List;


public class MySqlSelectTest_145_bigand extends MysqlTest {
    public void test_small_10() throws Exception {
        StringBuilder buf = new StringBuilder();
        {
            buf.append("SELECT GOODS_SID FROM PCM_STOCK \n" +
                    "WHERE  SHOP_MERCHANT_CODE = ? \n" +
                    "\tAND BIZID = ?");

            for (int i = 0; i < 10; ++i) {
                buf.append("\n and GOODS_SID <> ?");
            }
        }
        String sql = buf.toString();

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT GOODS_SID\n" +
                "FROM PCM_STOCK\n" +
                "WHERE SHOP_MERCHANT_CODE = ?\n" +
                "\tAND BIZID = ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?\n" +
                "\tAND GOODS_SID <> ?", stmt.toString());
    }


    public void test_big_100000() throws Exception {
        StringBuilder buf = new StringBuilder();
        {
            buf.append("SELECT GOODS_SID FROM PCM_STOCK \n" +
                    "WHERE  SHOP_MERCHANT_CODE = ? \n" +
                    "\tAND BIZID = ?");

            for (int i = 0; i < 10; ++i) {
                buf.append("\n and GOODS_SID <> ?");
            }
        }
        String sql = buf.toString();

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        stmt.toString();

        WallUtils.isValidateMySql(sql);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(statVisitor);
    }
}