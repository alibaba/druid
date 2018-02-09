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

public class OracleCreateTableTest56 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "      \n" +
                "  CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"SNAP_ID\" NUMBER(6,0) NOT NULL ENABLE, \n" +
                "  \"DBID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"INSTANCE_NUMBER\" NUMBER NOT NULL ENABLE, \n" +
                "  \"EVENT\" VARCHAR2(64) NOT NULL ENABLE, \n" +
                "  \"TOTAL_WAITS\" NUMBER, \n" +
                "  \"TOTAL_TIMEOUTS\" NUMBER, \n" +
                "  \"TIME_WAITED_MICRO\" NUMBER, \n" +
                "   CONSTRAINT \"STATS$BG_EVENT_SUMMARY_PK\" PRIMARY KEY (\"SNAP_ID\", \"DBID\", \"INSTANCE_NUMBER\", \"EVENT\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"PERFSTAT\"  ENABLE, \n" +
                "   CONSTRAINT \"STATS$BG_EVENT_SUMMARY_FK\" FOREIGN KEY (\"SNAP_ID\", \"DBID\", \"INSTANCE_NUMBER\")\n" +
                "    REFERENCES \"PERFSTAT\".\"STATS$SNAPSHOT\" (\"SNAP_ID\", \"DBID\", \"INSTANCE_NUMBER\") ON DELETE CASCADE ENABLE\n" +
                "   ) PCTFREE 5 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"PERFSTAT\"     ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"SNAP_ID\" NUMBER(6, 0) NOT NULL ENABLE,\n" +
                        "\t\"DBID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"INSTANCE_NUMBER\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"EVENT\" VARCHAR2(64) NOT NULL ENABLE,\n" +
                        "\t\"TOTAL_WAITS\" NUMBER,\n" +
                        "\t\"TOTAL_TIMEOUTS\" NUMBER,\n" +
                        "\t\"TIME_WAITED_MICRO\" NUMBER,\n" +
                        "\tCONSTRAINT \"STATS$BG_EVENT_SUMMARY_PK\" PRIMARY KEY (\"SNAP_ID\", \"DBID\", \"INSTANCE_NUMBER\", \"EVENT\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"PERFSTAT\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 1048576\n" +
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
                        "\tCONSTRAINT \"STATS$BG_EVENT_SUMMARY_FK\" FOREIGN KEY (\"SNAP_ID\", \"DBID\", \"INSTANCE_NUMBER\")\n" +
                        "\t\tREFERENCES \"PERFSTAT\".\"STATS$SNAPSHOT\" (\"SNAP_ID\", \"DBID\", \"INSTANCE_NUMBER\")\n" +
                        "\t\tON DELETE CASCADE ENABLE\n" +
                        ")\n" +
                        "PCTFREE 5\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"PERFSTAT\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 1048576\n" +
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

        Assert.assertEquals(10, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "SNAP_ID"));
    }
}
