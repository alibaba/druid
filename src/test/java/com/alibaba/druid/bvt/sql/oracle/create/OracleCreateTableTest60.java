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

public class OracleCreateTableTest60 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "    CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"MEMBER_ID\" VARCHAR2(32) NOT NULL ENABLE, \n" +
                "  \"CATEGORY_LEVEL2_ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"CATEGORY_LEVEL2_DESC\" VARCHAR2(128), \n" +
                "  \"AVG_INQUIRY_CNT\" NUMBER, \n" +
                "  \"AVG_INQUIRY_CNT_TOP10\" NUMBER\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 0 PCTUSED 40 INITRANS 1 MAXTRANS 255 COMPRESS BASIC LOGGING\n" +
                "  STORAGE(INITIAL 50331648 NEXT 4194304 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"ZEUS_IND\"   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"MEMBER_ID\" VARCHAR2(32) NOT NULL ENABLE,\n" +
                        "\t\"CATEGORY_LEVEL2_ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"CATEGORY_LEVEL2_DESC\" VARCHAR2(128),\n" +
                        "\t\"AVG_INQUIRY_CNT\" NUMBER,\n" +
                        "\t\"AVG_INQUIRY_CNT_TOP10\" NUMBER\n" +
                        ")\n" +
                        "PCTFREE 0\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "COMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"ZEUS_IND\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 50331648\n" +
                        "\tNEXT 4194304\n" +
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

        Assert.assertEquals(5, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "MEMBER_ID"));
    }
}
