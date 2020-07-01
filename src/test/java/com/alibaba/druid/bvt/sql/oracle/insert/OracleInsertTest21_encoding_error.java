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
package com.alibaba.druid.bvt.sql.oracle.insert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleInsertTest21_encoding_error extends OracleTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO  x1_use_agent (ID, company_name, company_id, amount, start_time, end_time, is_deleted,attr2) VALUES (seq_fee_use_agent.nextval,'\n" +
                "w[���р  Pl�','19929',(select c.FEE_AGENT from cfg_fee_agent c), to_date((select to_char(sysdate,'yyyy-mm-dd HH24:mi:ss') from dual), 'yyyy-mm-dd HH24:mi:ss'),to_date((select to_char(sysdate + interval '1' year ,'yyyy-mm-dd HH24:mi:ss') from dual), 'yyyy-mm-dd HH24:mi:ss'),'0','ZB20170831142805148890')";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("INSERT INTO x1_use_agent\n" +
                        "\t(ID, company_name, company_id, amount, start_time\n" +
                        "\t, end_time, is_deleted, attr2)\n" +
                        "VALUES (seq_fee_use_agent.NEXTVAL, '\n" +
                        "w[���р  Pl�', '19929', \n" +
                        "\t(SELECT c.FEE_AGENT\n" +
                        "\tFROM cfg_fee_agent c)\n" +
                        "\t, to_date((\n" +
                        "\t\tSELECT to_char(SYSDATE, 'yyyy-mm-dd HH24:mi:ss')\n" +
                        "\t\tFROM dual\n" +
                        "\t), 'yyyy-mm-dd HH24:mi:ss')\n" +
                        "\t, to_date((\n" +
                        "\t\tSELECT to_char(SYSDATE + INTERVAL '1' YEAR, 'yyyy-mm-dd HH24:mi:ss')\n" +
                        "\t\tFROM dual\n" +
                        "\t), 'yyyy-mm-dd HH24:mi:ss'), '0', 'ZB20170831142805148890')",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());

        assertEquals(1, visitor.getTables().size());
        assertEquals(8, visitor.getColumns().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("x1_use_agent")));

         assertTrue(visitor.getColumns().contains(new TableStat.Column("x1_use_agent", "ID")));
    }

}
