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
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MySqlCreateEventTest extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "create definer = current_user event if not exists someevent on schedule at current_timestamp + interval 30 minute\n" +
                "on completion preserve do begin insert into test.t1 values (33), (111);select * from test.t1; end; -- //";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE DEFINER = EVENT IF NOT EXISTS someevent ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 30 MINUTE\n" +
                "DO\n" +
                "BEGIN\n" +
                "\tINSERT INTO test.t1\n" +
                "\tVALUES (33),\n" +
                "\t\t(111);\n" +
                "\tSELECT *\n" +
                "\tFROM test.t1;\n" +
                "END;", output);
    }
}
