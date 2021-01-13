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
package com.alibaba.druid.bvt.sql.oracle.createTable;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest59 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "    CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"CATEGORY_ROOT_ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"CATEGORY_ROOT_DESC\" VARCHAR2(128) NOT NULL ENABLE, \n" +
                "  \"CATEGORY_LEVEL2_ID\" NUMBER, \n" +
                "  \"CATEGORY_LEVEL2_DESC\" VARCHAR2(128), \n" +
                "  \"CATEGORY_LEVEL3_ID\" NUMBER, \n" +
                "  \"CATEGORY_LEVEL3_DESC\" VARCHAR2(128), \n" +
                "  \"CATEGORY_LEAF_ID\" NUMBER, \n" +
                "  \"CATEGORY_LEAF_DESC\" VARCHAR2(128), \n" +
                "  \"IS_LEAF\" CHAR(1), \n" +
                "   CONSTRAINT \"EN_CATE_POST_C_DIMT0_PK\" PRIMARY KEY (\"CATEGORY_LEVEL2_ID\", \"CATEGORY_LEAF_ID\") ENABLE\n" +
                "   ) ORGANIZATION INDEX COMPRESS 1 PCTFREE 10 INITRANS 2 MAXTRANS 255 LOGGING\n" +
                "  STORAGE(INITIAL 4194304 NEXT 4194304 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"ZEUS_IND\" \n" +
                " PCTTHRESHOLD 50   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"CATEGORY_ROOT_ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"CATEGORY_ROOT_DESC\" VARCHAR2(128) NOT NULL ENABLE,\n" +
                        "\t\"CATEGORY_LEVEL2_ID\" NUMBER,\n" +
                        "\t\"CATEGORY_LEVEL2_DESC\" VARCHAR2(128),\n" +
                        "\t\"CATEGORY_LEVEL3_ID\" NUMBER,\n" +
                        "\t\"CATEGORY_LEVEL3_DESC\" VARCHAR2(128),\n" +
                        "\t\"CATEGORY_LEAF_ID\" NUMBER,\n" +
                        "\t\"CATEGORY_LEAF_DESC\" VARCHAR2(128),\n" +
                        "\t\"IS_LEAF\" CHAR(1),\n" +
                        "\tCONSTRAINT \"EN_CATE_POST_C_DIMT0_PK\" PRIMARY KEY (\"CATEGORY_LEVEL2_ID\", \"CATEGORY_LEAF_ID\") ENABLE\n" +
                        ")\n" +
                        "ORGANIZATION INDEX\n" +
                        "\tPCTFREE 10\n" +
                        "\tINITRANS 2\n" +
                        "\tMAXTRANS 255\n" +
                        "\tCOMPRESS 1\n" +
                        "\tLOGGING\n" +
                        "\tTABLESPACE \"ZEUS_IND\"\n" +
                        "\tSTORAGE (\n" +
                        "\t\tINITIAL 4194304\n" +
                        "\t\tNEXT 4194304\n" +
                        "\t\tMINEXTENTS 1\n" +
                        "\t\tMAXEXTENTS 2147483645\n" +
                        "\t\tPCTINCREASE 0\n" +
                        "\t\tFREELISTS 1\n" +
                        "\t\tFREELIST GROUPS 1\n" +
                        "\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t)\n" +
                        "\tPCTTHRESHOLD 50",//
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

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "CATEGORY_ROOT_DESC"));
    }
}
