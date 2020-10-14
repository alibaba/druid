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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import java.util.List;

public class MySqlCreateIndexTest_5 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE app_info (id bigint(20) NOT NULL, app_name varchar(255) NOT NULL ,PRIMARY KEY (id),INDEX idx USING BTREE (app_name) comment '') ";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("CREATE TABLE app_info (\n" +
                "\tid bigint(20) NOT NULL,\n" +
                "\tapp_name varchar(255) NOT NULL,\n" +
                "\tPRIMARY KEY (id),\n" +
                "\tINDEX idx USING BTREE(app_name) COMMENT ''\n" +
                ")", output);
    }
}
