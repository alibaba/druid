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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;


public class MySqlSelectTest_221 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT\n" +
                "    (\n" +
                "        (\n" +
                "            (\n" +
                "                (\n" +
                "                    CAST(`calcs`.`date0` AS TIMESTAMP) + CAST(\n" +
                "                        { fn TRUNCATE (- `calcs`.`num4`, 0) } AS INTEGER\n" +
                "                    ) * INTERVAL '1' DAY\n" +
                "                ) + CAST(\n" +
                "                    { fn TRUNCATE (\n" +
                "                        (\n" +
                "                            `calcs`.`num4` - CAST(\n" +
                "                                { fn TRUNCATE (`calcs`.`num4`, 0) } AS INTEGER\n" +
                "                            )\n" +
                "                        ) * - 24,\n" +
                "                        0\n" +
                "                    ) } AS INTEGER\n" +
                "                ) * INTERVAL '1' HOUR\n" +
                "            ) + CAST(\n" +
                "                { fn TRUNCATE (\n" +
                "                    (\n" +
                "                        `calcs`.`num4` * 24 - CAST(\n" +
                "                            { fn TRUNCATE (`calcs`.`num4` * 24, 0) } AS INTEGER\n" +
                "                        )\n" +
                "                    ) * - 60,\n" +
                "                    0\n" +
                "                ) } AS INTEGER\n" +
                "            ) * INTERVAL '1' MINUTE\n" +
                "        ) + CAST(\n" +
                "            { fn TRUNCATE (\n" +
                "                (\n" +
                "                    `calcs`.`num4` * 24 * 60 - CAST(\n" +
                "                        { fn TRUNCATE (`calcs`.`num4` * 24 * 60, 0) } AS INTEGER\n" +
                "                    )\n" +
                "                ) * - 60,\n" +
                "                0\n" +
                "            ) } AS INTEGER\n" +
                "        ) * INTERVAL '1' SECOND\n" +
                "    ) AS `TEMP(Test)(2923065813)(0)`\n" +
                "FROM\n" +
                "    `calcs`\n" +
                "GROUP BY\n" +
                "    1";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT CAST(`calcs`.`date0` AS TIMESTAMP) + CAST(TRUNCATE(-`calcs`.`num4`, 0) AS INTEGER) * INTERVAL '1' DAY + CAST(TRUNCATE((`calcs`.`num4` - CAST(TRUNCATE(`calcs`.`num4`, 0) AS INTEGER)) * -24, 0) AS INTEGER) * INTERVAL '1' HOUR + CAST(TRUNCATE((`calcs`.`num4` * 24 - CAST(TRUNCATE(`calcs`.`num4` * 24, 0) AS INTEGER)) * -60, 0) AS INTEGER) * INTERVAL '1' MINUTE + CAST(TRUNCATE((`calcs`.`num4` * 24 * 60 - CAST(TRUNCATE(`calcs`.`num4` * 24 * 60, 0) AS INTEGER)) * -60, 0) AS INTEGER) * INTERVAL '1' SECOND AS `TEMP(Test)(2923065813)(0)`\n" +
                "FROM `calcs`\n" +
                "GROUP BY 1", stmt.toString());

        assertEquals("select cast(`calcs`.`date0` as TIMESTAMP) + cast(TRUNCATE(-`calcs`.`num4`, 0) as INTEGER) * interval '1' day + cast(TRUNCATE((`calcs`.`num4` - cast(TRUNCATE(`calcs`.`num4`, 0) as INTEGER)) * -24, 0) as INTEGER) * interval '1' hour + cast(TRUNCATE((`calcs`.`num4` * 24 - cast(TRUNCATE(`calcs`.`num4` * 24, 0) as INTEGER)) * -60, 0) as INTEGER) * interval '1' minute + cast(TRUNCATE((`calcs`.`num4` * 24 * 60 - cast(TRUNCATE(`calcs`.`num4` * 24 * 60, 0) as INTEGER)) * -60, 0) as INTEGER) * interval '1' second as `TEMP(Test)(2923065813)(0)`\n" +
                "from `calcs`\n" +
                "group by 1", stmt.clone().toLowerCaseString());
    }
}