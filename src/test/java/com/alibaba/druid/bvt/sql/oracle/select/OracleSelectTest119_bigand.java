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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallUtils;

import java.util.List;


public class OracleSelectTest119_bigand extends MysqlTest {
    public void test_small_10() throws Exception {
        StringBuilder buf = new StringBuilder();
        {
            buf.append("SELECT GOODS_SID FROM PCM_STOCK \n" +
                    "WHERE  SHOP_MERCHANT_CODE = :1 \n" +
                    "\tAND BIZID = :2");

            for (int i = 0; i < 10; ++i) {
                buf.append("\n and GOODS_SID <> :" + (i + 3));
            }
        }
        String sql = buf.toString();

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT GOODS_SID\n" +
                "FROM PCM_STOCK\n" +
                "WHERE SHOP_MERCHANT_CODE = :1\n" +
                "\tAND BIZID = :2\n" +
                "\tAND GOODS_SID <> :3\n" +
                "\tAND GOODS_SID <> :4\n" +
                "\tAND GOODS_SID <> :5\n" +
                "\tAND GOODS_SID <> :6\n" +
                "\tAND GOODS_SID <> :7\n" +
                "\tAND GOODS_SID <> :8\n" +
                "\tAND GOODS_SID <> :9\n" +
                "\tAND GOODS_SID <> :10\n" +
                "\tAND GOODS_SID <> :11\n" +
                "\tAND GOODS_SID <> :12", stmt.toString());
    }


    public void test_big_100000() throws Exception {
        StringBuilder buf = new StringBuilder();
        {
            buf.append("SELECT GOODS_SID FROM PCM_STOCK \n" +
                    "WHERE  SHOP_MERCHANT_CODE = :1 \n" +
                    "\tAND BIZID = :2");

            for (int i = 0; i < 1000 * 10; ++i) {
                buf.append("\n and GOODS_SID <> :" + (i + 3));
            }
        }
        String sql = buf.toString();

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        stmt.toString();

        WallUtils.isValidateOracle(sql);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ORACLE);
        stmt.accept(statVisitor);
    }
}