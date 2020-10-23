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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;

import java.util.List;

public class OracleCreateTableTest90 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"XZZYYY\".\"ZT_TJ_ASJ_ALL\" \n" +
                        "   (    \"XZQH\" VARCHAR2(50), \n" +
                        "    \"SJLX\" VARCHAR2(20), \n" +
                        "    \"FAS\" NUMBER, \n" +
                        "    \"SQFAS\" NUMBER, \n" +
                        "    \"TQFAS\" NUMBER, \n" +
                        "    \"TJSJ\" DATE DEFAULT SYSDATE, \n" +
                        "    \"FATB\" NUMBER GENERATED ALWAYS AS (ROUND((\"FAS\"-\"TQFAS\")/DECODE(\"TQFAS\",0,1,\"TQFAS\"),2)*100) VIRTUAL VISIBLE , \n" +
                        "    \"FAHB\" NUMBER GENERATED ALWAYS AS (ROUND((\"FAS\"-\"SQFAS\")/DECODE(\"SQFAS\",0,1,\"SQFAS\"),2)*100) VIRTUAL VISIBLE \n" +
                        "   ) SEGMENT CREATION IMMEDIATE \n" +
                        "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS LOGGING\n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"TBS_XZZYYY\" ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals("CREATE TABLE \"XZZYYY\".\"ZT_TJ_ASJ_ALL\" (\n" +
                "\t\"XZQH\" VARCHAR2(50),\n" +
                "\t\"SJLX\" VARCHAR2(20),\n" +
                "\t\"FAS\" NUMBER,\n" +
                "\t\"SQFAS\" NUMBER,\n" +
                "\t\"TQFAS\" NUMBER,\n" +
                "\t\"TJSJ\" DATE DEFAULT SYSDATE,\n" +
                "\t\"FATB\" NUMBER GENERATED ALWAYS AS ROUND((\"FAS\" - \"TQFAS\") / DECODE(\"TQFAS\", 0, 1, \"TQFAS\"), 2) * 100 VIRTUAL VISIBLE,\n" +
                "\t\"FAHB\" NUMBER GENERATED ALWAYS AS ROUND((\"FAS\" - \"SQFAS\") / DECODE(\"SQFAS\", 0, 1, \"SQFAS\"), 2) * 100 VIRTUAL VISIBLE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "LOGGING\n" +
                "TABLESPACE \"TBS_XZZYYY\"\n" +
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
                ")", stmt.toString());

    }
}
