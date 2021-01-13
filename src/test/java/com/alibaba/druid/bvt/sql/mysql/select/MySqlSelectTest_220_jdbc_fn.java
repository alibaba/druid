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


public class MySqlSelectTest_220_jdbc_fn extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT TRIM(BOTH ' ' FROM {fn CONCAT({fn CONCAT(' ', `calcs`.`str2`)}, ' ')}) AS `TEMP(Test)(1903992131)(0)`\n" +
                "\n" +
                "FROM `calcs`\n" +
                "\n" +
                "GROUP BY 1";

        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);

        assertEquals("SELECT TRIM(BOTH ' ' FROM CONCAT(CONCAT(' ', `calcs`.`str2`), ' ')) AS `TEMP(Test)(1903992131)(0)`\n" +
                "FROM `calcs`\n" +
                "GROUP BY 1", stmt.toString());

        assertEquals("select TRIM(BOTH ' ' from CONCAT(CONCAT(' ', `calcs`.`str2`), ' ')) as `TEMP(Test)(1903992131)(0)`\n" +
                "from `calcs`\n" +
                "group by 1", stmt.clone().toLowerCaseString());
    }
}