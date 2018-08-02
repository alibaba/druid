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

public class MySqlSelectTest_63_alias extends MysqlTest {

    public void test_0() throws Exception {
        String sql =  " SELECT totalNumber, concat(\"\",? , round(memberNumber, 0) , \"\") AS totalDisplay FROM (\n" +
                "SELECT count(1) AS totalNumber, SUM(memberNumber) AS memberNumber FROM(\n" +
                "SELECT mmd.office_id AS departID,st.no AS staffNO,st.name AS staffName,COUNT(mmd.id) memberNumber FROM ms_member_def mmd\n" +
                "LEFT JOIN sys_user st ON mmd.salesman_id=st.id AND st.del_flag='0'\n" +
                "WHERE mmd.create_date BETWEEN (?) AND DATE_ADD((?),INTERVAL '23:59:59' HOUR_SECOND) AND mmd.del_flag='0'\n" +
                "AND ('' IN (?) OR st.no IN (?))\n" +
                "AND ('' IN (?) OR mmd.office_id IN (?))\n" +
                "GROUP BY mmd.office_id,st.no\n" +
                ") gg\n" +
                ")temp";

        System.out.println(sql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL, SQLParserFeature.OptimizedForParameterized);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT totalNumber\n" +
                            "\t, concat('', ?, round(memberNumber, 0), '') AS totalDisplay\n" +
                            "FROM (\n" +
                            "\tSELECT COUNT(1) AS totalNumber, SUM(memberNumber) AS memberNumber\n" +
                            "\tFROM (\n" +
                            "\t\tSELECT mmd.office_id AS departID, st.no AS staffNO, st.name AS staffName, COUNT(mmd.id) AS memberNumber\n" +
                            "\t\tFROM ms_member_def mmd\n" +
                            "\t\t\tLEFT JOIN sys_user st\n" +
                            "\t\t\tON mmd.salesman_id = st.id\n" +
                            "\t\t\t\tAND st.del_flag = '0'\n" +
                            "\t\tWHERE mmd.create_date BETWEEN ? AND DATE_ADD(?, INTERVAL '23:59:59' HOUR_SECOND)\n" +
                            "\t\t\tAND mmd.del_flag = '0'\n" +
                            "\t\t\tAND ('' IN (?)\n" +
                            "\t\t\t\tOR st.no IN (?))\n" +
                            "\t\t\tAND ('' IN (?)\n" +
                            "\t\t\t\tOR mmd.office_id IN (?))\n" +
                            "\t\tGROUP BY mmd.office_id, st.no\n" +
                            "\t) gg\n" +
                            ") temp", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select totalNumber\n" +
                            "\t, concat('', ?, round(memberNumber, 0), '') as totalDisplay\n" +
                            "from (\n" +
                            "\tselect count(1) as totalNumber, sum(memberNumber) as memberNumber\n" +
                            "\tfrom (\n" +
                            "\t\tselect mmd.office_id as departID, st.no as staffNO, st.name as staffName, count(mmd.id) as memberNumber\n" +
                            "\t\tfrom ms_member_def mmd\n" +
                            "\t\t\tleft join sys_user st\n" +
                            "\t\t\ton mmd.salesman_id = st.id\n" +
                            "\t\t\t\tand st.del_flag = '0'\n" +
                            "\t\twhere mmd.create_date between ? and DATE_ADD(?, interval '23:59:59' hour_second)\n" +
                            "\t\t\tand mmd.del_flag = '0'\n" +
                            "\t\t\tand ('' in (?)\n" +
                            "\t\t\t\tor st.no in (?))\n" +
                            "\t\t\tand ('' in (?)\n" +
                            "\t\t\t\tor mmd.office_id in (?))\n" +
                            "\t\tgroup by mmd.office_id, st.no\n" +
                            "\t) gg\n" +
                            ") temp", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT totalNumber\n" +
                            "\t, concat(?, ?, round(memberNumber, ?), ?) AS totalDisplay\n" +
                            "FROM (\n" +
                            "\tSELECT COUNT(1) AS totalNumber, SUM(memberNumber) AS memberNumber\n" +
                            "\tFROM (\n" +
                            "\t\tSELECT mmd.office_id AS departID, st.no AS staffNO, st.name AS staffName, COUNT(mmd.id) AS memberNumber\n" +
                            "\t\tFROM ms_member_def mmd\n" +
                            "\t\t\tLEFT JOIN sys_user st\n" +
                            "\t\t\tON mmd.salesman_id = st.id\n" +
                            "\t\t\t\tAND st.del_flag = ?\n" +
                            "\t\tWHERE mmd.create_date BETWEEN ? AND DATE_ADD(?, INTERVAL ? HOUR_SECOND)\n" +
                            "\t\t\tAND mmd.del_flag = ?\n" +
                            "\t\t\tAND (? IN (?)\n" +
                            "\t\t\t\tOR st.no IN (?))\n" +
                            "\t\t\tAND (? IN (?)\n" +
                            "\t\t\t\tOR mmd.office_id IN (?))\n" +
                            "\t\tGROUP BY mmd.office_id, st.no\n" +
                            "\t) gg\n" +
                            ") temp", //
                    output);
        }
    }
}
