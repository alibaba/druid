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
package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;

import java.util.List;

public class PGInsertTest15 extends PGTest {

    public void test_0() throws Exception {
        String sql = "insert into tb1\n" +
                "         (a, b, c, d, e, f, g, create_time, update_time )\n" +
                "         values\n" +
                "         ('a1', 'b1', 'c1', 'd1', 1, 'f1', 1, '2019-11-26 21:43:08.417', '2019-11-26 21:43:08.417')\n" +
                "        ON CONFLICT ON CONSTRAINT pk_tb1_a\n" +
                "        DO\n" +
                "        UPDATE\n" +
                "        SET\n" +
                "          f = 'f2',\n" +
                "          g = 2,\n" +
                "          update_time = '2019-11-26 21:43:08.417'\n" +
                "        WHERE a = 'a1'";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);
    }

}
