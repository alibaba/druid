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

public class MySqlSelectTest_82_force_partition extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  "select\n" +
                "    app_key,device_id,brand  \n" +
                "     \n" +
                "      force partition 'MASSDEVICE40'\n" +
                "     \n" +
                "    from ktvs_device_info\n" +
                "     \n" +
                "       \n" +
                "    \n" +
                "     WHERE (  app_key = ?\n" +
                "                \n" +
                "            \n" +
                "              \n" +
                "                  and package_name = ?\n" +
                "                \n" +
                "            \n" +
                "              \n" +
                "                  and app_version in\n" +
                "                  (\n" +
                "                    ?\n" +
                "                  ) ) \n" +
                "   \n" +
                "     \n" +
                "       \n" +
                "    \n" +
                "     \n" +
                "      order by gmt_modified desc";

        System.out.println(sql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL, SQLParserFeature.OptimizedForParameterized);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT app_key, device_id, brand\n" +
                            "FORCE PARTITION 'MASSDEVICE40'\n" +
                            "FROM ktvs_device_info\n" +
                            "WHERE app_key = ?\n" +
                            "\tAND package_name = ?\n" +
                            "\tAND app_version IN (?)\n" +
                            "ORDER BY gmt_modified DESC", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select app_key, device_id, brand\n" +
                            "force partition 'MASSDEVICE40'\n" +
                            "from ktvs_device_info\n" +
                            "where app_key = ?\n" +
                            "\tand package_name = ?\n" +
                            "\tand app_version in (?)\n" +
                            "order by gmt_modified desc", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT app_key, device_id, brand\n" +
                            "FORCE PARTITION 'MASSDEVICE40'\n" +
                            "FROM ktvs_device_info\n" +
                            "WHERE app_key = ?\n" +
                            "\tAND package_name = ?\n" +
                            "\tAND app_version IN (?)\n" +
                            "ORDER BY gmt_modified DESC", //
                    output);
        }
    }
}
