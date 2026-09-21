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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySqlCreateViewTest_issue4593 {
    @Test
    public void test_local_check_option() {
        assertCheckOption("WITH LOCAL CHECK OPTION");
    }

    @Test
    public void test_cascaded_check_option() {
        assertCheckOption("WITH CASCADED CHECK OPTION");
    }

    @Test
    public void test_check_option() {
        assertCheckOption("WITH CHECK OPTION");
    }

    @Test
    public void test_without_check_option() {
        assertCheckOption("");
    }

    private void assertCheckOption(String checkOption) {
        String sql = "CREATE VIEW v AS SELECT * FROM t";
        String expected = "CREATE VIEW v\nAS\nSELECT *\nFROM t";
        if (!checkOption.isEmpty()) {
            sql += " " + checkOption;
            expected += "\n" + checkOption;
        }

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        assertEquals(1, statementList.size());

        SQLStatement stmt = statementList.get(0);
        assertEquals(expected, SQLUtils.toMySqlString(stmt));
        assertEquals(expected.toLowerCase(Locale.ROOT),
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
