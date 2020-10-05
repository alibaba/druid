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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_66 extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  "SELECT * FROM (SELECT number_ios,type,subtype,displayname,source FROM hot_number_20170921 WHERE day_modify > 20170921 AND priority >= 4499000 ORDER BY priority DESC LIMIT 500000) ORDER BY number_ios ASC";

        System.out.println(sql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL, SQLParserFeature.OptimizedForParameterized);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT *\n" +
                            "FROM (\n" +
                            "\tSELECT number_ios, type, subtype, displayname, source\n" +
                            "\tFROM hot_number_20170921\n" +
                            "\tWHERE day_modify > 20170921\n" +
                            "\t\tAND priority >= 4499000\n" +
                            "\tORDER BY priority DESC\n" +
                            "\tLIMIT 500000\n" +
                            ")\n" +
                            "ORDER BY number_ios ASC", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select *\n" +
                            "from (\n" +
                            "\tselect number_ios, type, subtype, displayname, source\n" +
                            "\tfrom hot_number_20170921\n" +
                            "\twhere day_modify > 20170921\n" +
                            "\t\tand priority >= 4499000\n" +
                            "\torder by priority desc\n" +
                            "\tlimit 500000\n" +
                            ")\n" +
                            "order by number_ios asc", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT *\n" +
                            "FROM (\n" +
                            "\tSELECT number_ios, type, subtype, displayname, source\n" +
                            "\tFROM hot_number\n" +
                            "\tWHERE day_modify > ?\n" +
                            "\t\tAND priority >= ?\n" +
                            "\tORDER BY priority DESC\n" +
                            "\tLIMIT ?\n" +
                            ")\n" +
                            "ORDER BY number_ios ASC", //
                    output);
        }
    }
}
