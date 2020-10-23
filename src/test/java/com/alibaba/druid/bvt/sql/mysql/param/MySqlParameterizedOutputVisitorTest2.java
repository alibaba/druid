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
package com.alibaba.druid.bvt.sql.mysql.param;

public class MySqlParameterizedOutputVisitorTest2 extends MySQLParameterizedTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM T WHERE ID = ?";
        for (int i = 0; i < 100; ++i) {
            sql += " OR ID = ?";
        }

        validate(sql, "SELECT *\nFROM T\nWHERE ID = ?");
        validateOracle(sql, "SELECT *\nFROM T\nWHERE ID = ?");

        paramaterizeAST(sql, "SELECT *\n" +
                "FROM T\n" +
                "WHERE ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?\n" +
                "\tOR ID = ?");
    }
}
