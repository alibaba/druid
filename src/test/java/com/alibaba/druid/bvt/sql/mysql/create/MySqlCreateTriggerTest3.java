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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTriggerTest3 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "-- Create trigger 1\n" +
                "-- delimiter //\n" +
                "create trigger trg_my1 before delete on test.t1 for each row begin insert into log_table values (\"delete row from test.t1\"); insert into t4 values (old.col1, old.col1 + 5, old.col1 + 7); end; -- //-- delimiter ;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
//        print(statementList);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        assertEquals("CREATE TRIGGER trg_my1\n" +
                "\tBEFORE DELETE\n" +
                "\tON test.t1\n" +
                "\tFOR EACH ROW\n" +
                "BEGIN\n" +
                "\tINSERT INTO log_table\n" +
                "\tVALUES ('delete row from test.t1');\n" +
                "\tINSERT INTO t4\n" +
                "\tVALUES (old.col1, old.col1 + 5, old.col1 + 7);\n" +
                "END;", stmt.toString());
    }


}
