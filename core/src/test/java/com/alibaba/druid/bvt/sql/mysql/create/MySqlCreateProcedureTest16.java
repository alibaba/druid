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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlCreateProcedureTest16 extends MysqlTest {

    public void test_0() {
        String sql = "CREATE PROCEDURE drop_tab_index (IN db varchar(50),IN tb_name varchar(512),IN idx_type varchar(50),IN idx_name varchar(512))\n" +
                "BEGIN\n" +
                "    CASE \n" +
                "        WHEN idx_type='PRIMARY' OR idx_type='primary' THEN\n" +
                "            IF EXISTS(SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=db AND table_name=tb_name AND INDEX_NAME='PRIMARY') THEN\n" +
                "                SET @alter_sql=concat(\"ALTER TABLE \",db,\".\",tb_name,\" DROP PRIMARY KEY\");\n" +
                "                prepare stmt from @alter_sql;\n" +
                "                execute stmt;\n" +
                "                SELECT @alter_sql;\n" +
                "            END IF;\n" +
                "        WHEN idx_type='UNIQUE' OR idx_type='unique' THEN\n" +
                "            IF EXISTS(SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=db AND table_name=tb_name AND INDEX_NAME=idx_name) THEN\n" +
                "                SET @alter_sql=concat(\"ALTER TABLE \",db,\".\",tb_name,\" DROP KEY \",idx_name);\n" +
                "                prepare stmt from @alter_sql;\n" +
                "                execute stmt;\n" +
                "                SELECT @alter_sql;\n" +
                "            END IF;\n" +
                "        WHEN idx_type='SECONDARY' OR idx_type='secondary' THEN\n" +
                "            IF EXISTS(SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=db AND table_name=tb_name AND INDEX_NAME=idx_name) THEN\n" +
                "                SET @alter_sql=concat(\"ALTER TABLE \",db,\".\",tb_name,\" DROP KEY \",idx_name);\n" +
                "                prepare stmt from @alter_sql;\n" +
                "                execute stmt;\n" +
                "                SELECT @alter_sql;\n" +
                "            END IF;\n" +
                "    END CASE;\n" +
                "END;;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatement();
        System.out.println(SQLUtils.toMySqlString(stmt));
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        assertEquals(3, visitor.getConditions().size());
    }
}
