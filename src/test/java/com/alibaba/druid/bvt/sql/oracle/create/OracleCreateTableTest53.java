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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest53 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "     CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"RUNID\" NUMBER, \n" +
                "  \"UNIT_NUMBER\" NUMBER, \n" +
                "  \"LINE#\" NUMBER NOT NULL ENABLE, \n" +
                "  \"TOTAL_OCCUR\" NUMBER, \n" +
                "  \"TOTAL_TIME\" NUMBER, \n" +
                "  \"MIN_TIME\" NUMBER, \n" +
                "  \"MAX_TIME\" NUMBER, \n" +
                "  \"SPARE1\" NUMBER, \n" +
                "  \"SPARE2\" NUMBER, \n" +
                "  \"SPARE3\" NUMBER, \n" +
                "  \"SPARE4\" NUMBER, \n" +
                "   PRIMARY KEY (\"RUNID\", \"UNIT_NUMBER\", \"LINE#\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"SYSTEM\"  ENABLE, \n" +
                "   FOREIGN KEY (\"RUNID\", \"UNIT_NUMBER\")\n" +
                "    REFERENCES \"B2BDBA\".\"PLSQL_PROFILER_UNITS\" (\"RUNID\", \"UNIT_NUMBER\") ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"SYSTEM\"      ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"RUNID\" NUMBER,\n" +
                        "\t\"UNIT_NUMBER\" NUMBER,\n" +
                        "\t\"LINE#\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"TOTAL_OCCUR\" NUMBER,\n" +
                        "\t\"TOTAL_TIME\" NUMBER,\n" +
                        "\t\"MIN_TIME\" NUMBER,\n" +
                        "\t\"MAX_TIME\" NUMBER,\n" +
                        "\t\"SPARE1\" NUMBER,\n" +
                        "\t\"SPARE2\" NUMBER,\n" +
                        "\t\"SPARE3\" NUMBER,\n" +
                        "\t\"SPARE4\" NUMBER,\n" +
                        "\tPRIMARY KEY (\"RUNID\", \"UNIT_NUMBER\", \"LINE#\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"SYSTEM\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 65536\n" +
                        "\t\t\tNEXT 1048576\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t)\n" +
                        "\t\tCOMPUTE STATISTICS\n" +
                        "\t\tENABLE,\n" +
                        "\tFOREIGN KEY (\"RUNID\", \"UNIT_NUMBER\")\n" +
                        "\t\tREFERENCES \"B2BDBA\".\"PLSQL_PROFILER_UNITS\" (\"RUNID\", \"UNIT_NUMBER\") ENABLE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"SYSTEM\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 65536\n" +
                        "\tNEXT 1048576\n" +
                        "\tMINEXTENTS 1\n" +
                        "\tMAXEXTENTS 2147483645\n" +
                        "\tPCTINCREASE 0\n" +
                        "\tFREELISTS 1\n" +
                        "\tFREELIST GROUPS 1\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(13, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "RUNID"));
    }
}
