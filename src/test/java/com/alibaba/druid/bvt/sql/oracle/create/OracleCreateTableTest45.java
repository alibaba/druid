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

public class OracleCreateTableTest45 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"ID\" NUMBER, \n" +
                "  \"COL_CHAR1\" CHAR(128), \n" +
                "  \"COL_VARCHAR1\" VARCHAR2(128), \n" +
                "  \"COL_DATE\" DATE, \n" +
                "  \"COL_TIMESTAMP\" TIMESTAMP (6), \n" +
                "  \"COL_CHAR2\" CHAR(128), \n" +
                "  \"COL_VARCHAR2\" VARCHAR2(128), \n" +
                "  \"COL_CHAR3\" CHAR(128), \n" +
                "  \"COL_NUMBER\" NUMBER\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 COMPRESS FOR OLTP LOGGING\n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"USERS\" ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER,\n" +
                        "\t\"COL_CHAR1\" CHAR(128),\n" +
                        "\t\"COL_VARCHAR1\" VARCHAR2(128),\n" +
                        "\t\"COL_DATE\" DATE,\n" +
                        "\t\"COL_TIMESTAMP\" TIMESTAMP(6),\n" +
                        "\t\"COL_CHAR2\" CHAR(128),\n" +
                        "\t\"COL_VARCHAR2\" VARCHAR2(128),\n" +
                        "\t\"COL_CHAR3\" CHAR(128),\n" +
                        "\t\"COL_NUMBER\" NUMBER\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "COMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"USERS\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 65536\n" +
                        "\tNEXT 1048576\n" +
                        "\tMINEXTENTS 1\n" +
                        "\tMAXEXTENTS 2147483645\n" +
                        "\tPCTINCREASE 0\n" +
                        "\tFREELISTS 1\n" +
                        "\tFREELIST GROUPS 1\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        "\tFLASH_CACHE DEFAULT\n" +
                        "\tCELL_FLASH_CACHE DEFAULT\n" +
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

        Assert.assertEquals(9, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
