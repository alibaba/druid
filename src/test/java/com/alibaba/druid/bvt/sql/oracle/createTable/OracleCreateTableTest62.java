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

public class OracleCreateTableTest62 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"TOTAL_PRICE_ACTUAL\" NUMBER, \n" +
                "  \"GMT_VOUCHER_RECEIVE\" DATE, \n" +
                "  \"CS4_OWNER_ID\" VARCHAR2(32), \n" +
                "  \"DISABLE_DATE\" DATE, \n" +
                "  \"MEMBER_ID\" VARCHAR2(32) NOT NULL ENABLE, \n" +
                "  \"ITEM_ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"CONTACT_COUNT\" NUMBER(11,0), \n" +
                "  \"ORIGIN_DISABLE_DATE\" DATE, \n" +
                "  \"GMT_PAYMENT_REMIT\" DATE, \n" +
                "  \"PRODUCT_ID\" NUMBER, \n" +
                "  \"PERIOD\" NUMBER, \n" +
                "  \"PERIOD_UNIT\" VARCHAR2(32), \n" +
                "  \"MEMBER_TYPE\" VARCHAR2(20), \n" +
                "  \"GMT_AV_SEND\" DATE, \n" +
                "  \"GMT_ENABLE\" DATE, \n" +
                "  \"COMPANY\" VARCHAR2(128), \n" +
                "  \"PAYMENT_STATUS\" VARCHAR2(32), \n" +
                "  \"TMP_ALIID\" VARCHAR2(64)\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255  LOGGING \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"NIRVANA1M\" \n" +
                "  PARTITION BY RANGE (\"GMT_VOUCHER_RECEIVE\") \n" +
                " (PARTITION \"PONLY\"  VALUES LESS THAN (MAXVALUE) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING \n" +
                "  STORAGE(INITIAL 1048576 NEXT 131072 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"NIRVANA1M\" )    ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"TOTAL_PRICE_ACTUAL\" NUMBER,\n" +
                        "\t\"GMT_VOUCHER_RECEIVE\" DATE,\n" +
                        "\t\"CS4_OWNER_ID\" VARCHAR2(32),\n" +
                        "\t\"DISABLE_DATE\" DATE,\n" +
                        "\t\"MEMBER_ID\" VARCHAR2(32) NOT NULL ENABLE,\n" +
                        "\t\"ITEM_ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"CONTACT_COUNT\" NUMBER(11, 0),\n" +
                        "\t\"ORIGIN_DISABLE_DATE\" DATE,\n" +
                        "\t\"GMT_PAYMENT_REMIT\" DATE,\n" +
                        "\t\"PRODUCT_ID\" NUMBER,\n" +
                        "\t\"PERIOD\" NUMBER,\n" +
                        "\t\"PERIOD_UNIT\" VARCHAR2(32),\n" +
                        "\t\"MEMBER_TYPE\" VARCHAR2(20),\n" +
                        "\t\"GMT_AV_SEND\" DATE,\n" +
                        "\t\"GMT_ENABLE\" DATE,\n" +
                        "\t\"COMPANY\" VARCHAR2(128),\n" +
                        "\t\"PAYMENT_STATUS\" VARCHAR2(32),\n" +
                        "\t\"TMP_ALIID\" VARCHAR2(64)\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"NIRVANA1M\"\n" +
                        "STORAGE (\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        "\tFLASH_CACHE DEFAULT\n" +
                        "\tCELL_FLASH_CACHE DEFAULT\n" +
                        ")\n" +
                        "PARTITION BY RANGE (\"GMT_VOUCHER_RECEIVE\") (\n" +
                        "\tPARTITION \"PONLY\" VALUES LESS THAN (MAXVALUE)\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"NIRVANA1M\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 1048576\n" +
                        "\t\t\tNEXT 131072\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
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

        Assert.assertEquals(18, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "MEMBER_ID"));
    }
}
