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
package com.alibaba.druid.bvt.sql.mysql.alter;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlAlterTableTest29 extends TestCase {

    public void test_alter_add_key() throws Exception {
        String sql = "alter TABLE project_measures\n" +
                "        DROP COLUMN diff_value_1,\n" +
                "        DROP COLUMN diff_value_2,\n" +
                "        DROP COLUMN diff_value_3,\n" +
                "        ADD COLUMN variation_value_1 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_2 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_3 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_4 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "        ADD COLUMN variation_value_5 DECIMAL(30, 20) NULL DEFAULT NULL";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("ALTER TABLE project_measures\n" +
                "\tDROP COLUMN diff_value_1,\n" +
                "\tDROP COLUMN diff_value_2,\n" +
                "\tDROP COLUMN diff_value_3,\n" +
                "\tADD COLUMN variation_value_1 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "\tADD COLUMN variation_value_2 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "\tADD COLUMN variation_value_3 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "\tADD COLUMN variation_value_4 DECIMAL(30, 20) NULL DEFAULT NULL,\n" +
                "\tADD COLUMN variation_value_5 DECIMAL(30, 20) NULL DEFAULT NULL", output);
    }
}
