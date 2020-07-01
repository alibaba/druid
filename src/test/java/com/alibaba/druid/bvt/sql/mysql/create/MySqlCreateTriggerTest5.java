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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlCreateTriggerTest5 extends MysqlTest {


    public void test_0() throws Exception {
        String sql = " CREATE DEFINER=`test_app`@`%` TRIGGER `trg_xxx_update` AFTER UPDATE ON `txxx` FOR EACH ROW begin"
                + "\ninsert into record_history_log (object, identity, action) values('txxx', new.object_id, 'UPDATE');"
                + "\ninsert into record_history_log_sim (object, identity, action) values('txxx', new.object_id, 'UPDATE');end";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
//        print(statementList);

        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);

        assertEquals("CREATE TRIGGER `trg_xxx_update`\n" +
                "\tAFTER UPDATE\n" +
                "\tON `txxx`\n" +
                "\tFOR EACH ROW\n" +
                "BEGIN\n" +
                "\tINSERT INTO record_history_log (object, identity, action)\n" +
                "\tVALUES ('txxx', new.object_id, 'UPDATE');\n" +
                "\tINSERT INTO record_history_log_sim (object, identity, action)\n" +
                "\tVALUES ('txxx', new.object_id, 'UPDATE');\n" +
                "END", stmt.toString());
    }


}
