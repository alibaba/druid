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

public class MySqlCreateTriggerTest4 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create definer = current_user() trigger trg_my2 after insert on test.t2 for each row insert into log_table values (concat(\"inserted into table test.t2 values: (1c, _) = (\", cast(NEW.col1 as char(100)), \", \", convertToSqlNode(new.`_`, char(100)), \")\"));";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
//        print(statementList);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        assertEquals("CREATE TRIGGER trg_my2\n" +
                "\tAFTER INSERT\n" +
                "\tON test.t2\n" +
                "\tFOR EACH ROW\n" +
                "INSERT INTO log_table\n" +
                "VALUES (concat('inserted into table test.t2 values: (1c, _) = (', CAST(NEW.col1 AS char(100)), ', ', convertToSqlNode(new.`_`, char(100)), ')'));", stmt.toString());
    }


}
