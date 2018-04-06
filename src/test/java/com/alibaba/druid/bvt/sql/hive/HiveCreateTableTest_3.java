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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_3 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE pub_ae_sess_type_dimt0_text\n" +
                        "(        \n" +
                        "        SESSION_TYPE_ID         BIGINT          COMMENT '会话类型',\n" +
                        "        DW_INS_DATE             STRING          COMMENT '数据仓库插入时间' \n" +
                        ")\n" +
                        "COMMENT 'session来源类型维表'\n" +
                        "PARTITIONED BY(date STRING COMMENT '日期', pos STRING COMMENT '位置')\n" +
                        "ROW FORMAT DELIMITED FIELDS TERMINATED BY ','\n" +
                        "STORED AS SEQUENCEFILE"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE TABLE pub_ae_sess_type_dimt0_text (\n" +
                    "\tSESSION_TYPE_ID BIGINT COMMENT '会话类型',\n" +
                    "\tDW_INS_DATE STRING COMMENT '数据仓库插入时间'\n" +
                    ")\n" +
                    "COMMENT 'session来源类型维表'\n" +
                    "PARTITIONED BY (\n" +
                    "\tdate STRING COMMENT '日期',\n" +
                    "\tpos STRING COMMENT '位置'\n" +
                    ")\n" +
                    "ROW FORMAT DELIMITED \n" +
                    "FIELDS TERMINATED BY ','\n" +
                    "STORE AS SEQUENCEFILE", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(2, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.containsTable("pub_ae_sess_type_dimt0_text"));

    }
}
