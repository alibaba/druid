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
package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleUpdateTest5_encoding_error extends OracleTest {

    public void test_0() throws Exception {
        String sql = "update x1_use_agent t2   set t2.start_time =to_date((select to_char(sysdate,'yyyy-mm-dd HH24:mi:ss') from dual), 'yyyy-mm-dd HH24:mi:ss'),  t2.end_time=to_date((select to_char(sysdate + interval '1' year ,'yyyy-mm-dd HH24:mi:ss') from dual), 'yyyy-mm-dd HH24:mi:ss') where t2.attr2 ='ZB201708311440560'";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("UPDATE x1_use_agent t2\n" +
                        "SET t2.start_time = to_date((\n" +
                        "\tSELECT to_char(SYSDATE, 'yyyy-mm-dd HH24:mi:ss')\n" +
                        "\tFROM dual\n" +
                        "), 'yyyy-mm-dd HH24:mi:ss'), t2.end_time = to_date((\n" +
                        "\tSELECT to_char(SYSDATE + INTERVAL '1' YEAR, 'yyyy-mm-dd HH24:mi:ss')\n" +
                        "\tFROM dual\n" +
                        "), 'yyyy-mm-dd HH24:mi:ss')\n" +
                        "WHERE t2.attr2 = 'ZB201708311440560'",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("x1_use_agent")));

         assertTrue(visitor.getColumns().contains(new TableStat.Column("x1_use_agent", "start_time")));
    }

}
