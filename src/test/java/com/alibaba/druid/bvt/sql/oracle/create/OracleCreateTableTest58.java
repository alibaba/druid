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

public class OracleCreateTableTest58 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "    CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"PROPS\" VARCHAR2(4000), \n" +
                "   CONSTRAINT \"PRODUCT_PROPERTY_SEARCH_PK\" PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  LOGGING \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPINDX1K\"  LOCAL\n" +
                " (PARTITION \"P1\" \n" +
                "  PCTFREE 10 INITRANS 2 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 24117248 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPINDX1K\" , \n" +
                " PARTITION \"P2\" \n" +
                "  PCTFREE 10 INITRANS 2 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPINDX1K\" )  ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255  LOGGING \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APP_DATA1K\" \n" +
                "  PARTITION BY RANGE (\"ID\") \n" +
                " (PARTITION \"P1\"  VALUES LESS THAN (2000000000) \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 150994944 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPDATA1M\" NOCOMPRESS , \n" +
                " PARTITION \"P2\"  VALUES LESS THAN (MAXVALUE) \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APP_DATA1K\" NOCOMPRESS )   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"PROPS\" VARCHAR2(4000),\n" +
                        "\tCONSTRAINT \"PRODUCT_PROPERTY_SEARCH_PK\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"APPINDX1K\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t)\n" +
                        "\t\tENABLE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"APP_DATA1K\"\n" +
                        "STORAGE (\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        ")\n" +
                        "PARTITION BY RANGE (\"ID\") (\n" +
                        "\tPARTITION \"P1\" VALUES LESS THAN (2000000000)\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tTABLESPACE \"APPDATA1M\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 150994944\n" +
                        "\t\t\tNEXT 1048576\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"P2\" VALUES LESS THAN (MAXVALUE)\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tTABLESPACE \"APP_DATA1K\"\n" +
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
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(2, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
