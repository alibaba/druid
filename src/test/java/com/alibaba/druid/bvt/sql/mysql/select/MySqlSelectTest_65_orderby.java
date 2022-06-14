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

public class MySqlSelectTest_65_orderby extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT number,event_cnt,country_code,area_code,carrier,country,province,city,type,subtype,displayname,slogan,logo,source,state,priority,is_change FROM ktv_number_20170921 WHERE day_modify > 20170921 AND priority >= 4499000 ORDER BY priority DESC LIMIT 500000";

        System.out.println(sql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL, SQLParserFeature.OptimizedForParameterized);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT number, event_cnt, country_code, area_code, carrier\n" +
                            "\t, country, province, city, type, subtype\n" +
                            "\t, displayname, slogan, logo, source, state\n" +
                            "\t, priority, is_change\n" +
                            "FROM ktv_number_20170921\n" +
                            "WHERE day_modify > 20170921\n" +
                            "\tAND priority >= 4499000\n" +
                            "ORDER BY priority DESC\n" +
                            "LIMIT 500000", //
                    output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select number, event_cnt, country_code, area_code, carrier\n" +
                            "\t, country, province, city, type, subtype\n" +
                            "\t, displayname, slogan, logo, source, state\n" +
                            "\t, priority, is_change\n" +
                            "from ktv_number_20170921\n" +
                            "where day_modify > 20170921\n" +
                            "\tand priority >= 4499000\n" +
                            "order by priority desc\n" +
                            "limit 500000", //
                    output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT number, event_cnt, country_code, area_code, carrier\n" +
                            "\t, country, province, city, type, subtype\n" +
                            "\t, displayname, slogan, logo, source, state\n" +
                            "\t, priority, is_change\n" +
                            "FROM ktv_number\n" +
                            "WHERE day_modify > ?\n" +
                            "\tAND priority >= ?\n" +
                            "ORDER BY priority DESC\n" +
                            "LIMIT ?", //
                    output);
        }
    }
}
