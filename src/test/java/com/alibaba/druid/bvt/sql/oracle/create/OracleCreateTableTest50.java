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

public class OracleCreateTableTest50 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"GMT_CREATE\" DATE, \n" +
                "  \"GMT_MODIFIED\" DATE, \n" +
                "  \"COMPANY_ID\" NUMBER, \n" +
                "  \"IMAGE_REPOSITORY_IDS\" VARCHAR2(128), \n" +
                "  \"HAVE_IMAGE\" CHAR(1), \n" +
                "  \"IMAGE_COUNT\" NUMBER, \n" +
                "  \"IMAGE_WATERMARK\" CHAR(1), \n" +
                "  \"IMAGE_PROCESS\" VARCHAR2(16), \n" +
                "  \"MEMBER_ID\" VARCHAR2(32), \n" +
                "  \"MEMBER_SEQ\" NUMBER, \n" +
                "  \"SUBJECT\" VARCHAR2(256), \n" +
                "  \"KEYWORDS\" VARCHAR2(512), \n" +
                "  \"REPOSITORY_TYPE\" VARCHAR2(16), \n" +
                "  \"TYPE\" VARCHAR2(16), \n" +
                "  \"CATEGORY_ID\" NUMBER, \n" +
                "  \"GROUP_ID\" NUMBER DEFAULT 0, \n" +
                "  \"STATUS\" VARCHAR2(16), \n" +
                "  \"IS_DISPLAY\" CHAR(1), \n" +
                "  \"HS_CODE\" VARCHAR2(16), \n" +
                "  \"IND_BY_ALL\" NUMBER, \n" +
                "  \"IND_BY_GROUP\" NUMBER, \n" +
                "  \"OWNER_MEMBER_ID\" VARCHAR2(32), \n" +
                "  \"OWNER_MEMBER_SEQ\" NUMBER, \n" +
                "  \"CERTIFICATE_IDS\" VARCHAR2(256), \n" +
                "  \"MONEY_TYPE\" NUMBER, \n" +
                "  \"PRICE_RANGE\" VARCHAR2(256), \n" +
                "  \"PORT\" VARCHAR2(256), \n" +
                "  \"PAYMENT_METHOD\" VARCHAR2(128), \n" +
                "  \"PAYMENT_METHOD_OTHER\" VARCHAR2(256), \n" +
                "  \"MIN_ORDER_QUANTITY\" VARCHAR2(64), \n" +
                "  \"MIN_ORDER_UNIT\" NUMBER, \n" +
                "  \"MIN_ORDER_OTHER\" VARCHAR2(256), \n" +
                "  \"SUPPLY_QUANTITY\" VARCHAR2(64), \n" +
                "  \"SUPPLY_UNIT\" NUMBER, \n" +
                "  \"SUPPLY_PERIOD\" VARCHAR2(16), \n" +
                "  \"SUPPLY_OTHER\" VARCHAR2(256), \n" +
                "  \"PACKAGING_DESC\" VARCHAR2(512), \n" +
                "  \"CONSIGNMENT_TERM\" VARCHAR2(64), \n" +
                "  \"IS_VALIDATE\" CHAR(1), \n" +
                "  \"CREATE_TYPE\" VARCHAR2(16), \n" +
                "  \"RED_MODEL\" VARCHAR2(128), \n" +
                "  \"DRAFT_STATUS\" VARCHAR2(16) DEFAULT 'no_status', \n" +
                "  \"EXPORT_TYPE\" VARCHAR2(16) DEFAULT 'normal', \n" +
                "  \"IS_ESCROW\" CHAR(1) DEFAULT 'N', \n" +
                "  \"SALE_TYPE\" VARCHAR2(64) DEFAULT 'sourcing', \n" +
                "  \"WS_DISPLAY\" VARCHAR2(16), \n" +
                "  \"WS_OFFLINE_DATE\" DATE, \n" +
                "  \"WS_VALID_NUM\" NUMBER, \n" +
                "  \"WS_REPOST_NUM\" NUMBER, \n" +
                "  \"WS_AUTO_REPOST\" CHAR(1), \n" +
                "  \"PRICE_UNIT\" NUMBER, \n" +
                "  \"IMAGE_VERSION\" NUMBER, \n" +
                "  \"SCORE\" NUMBER, \n" +
                "  \"GROUP_ID2\" NUMBER, \n" +
                "  \"GROUP_ID3\" NUMBER, \n" +
                "  \"IND_BY_GROUP2\" NUMBER, \n" +
                "  \"IND_BY_GROUP3\" NUMBER, \n" +
                "   CONSTRAINT \"P_PRODUCT_PK1\" PRIMARY KEY (\"ID\", \"COMPANY_ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPINDX1M\"  LOCAL\n" +
                " (PARTITION \"PRODUCT_HASH_P1\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P2\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P3\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P4\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P5\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P6\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P7\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P8\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P9\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P10\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P11\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P12\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P13\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P14\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P15\" \n" +
                "   TABLESPACE \"APPINDX1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P16\" \n" +
                "   TABLESPACE \"APPINDX1M\")  ENABLE NOVALIDATE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APP_DATA1K\" \n" +
                "  PARTITION BY HASH (\"COMPANY_ID\") \n" +
                " (PARTITION \"PRODUCT_HASH_P1\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P2\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P3\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P4\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P5\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P6\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P7\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P8\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P9\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P10\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P11\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P12\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P13\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P14\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P15\" \n" +
                "   TABLESPACE \"APPDATA1M\", \n" +
                " PARTITION \"PRODUCT_HASH_P16\" \n" +
                "   TABLESPACE \"APPDATA1M\")    ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"GMT_CREATE\" DATE,\n" +
                        "\t\"GMT_MODIFIED\" DATE,\n" +
                        "\t\"COMPANY_ID\" NUMBER,\n" +
                        "\t\"IMAGE_REPOSITORY_IDS\" VARCHAR2(128),\n" +
                        "\t\"HAVE_IMAGE\" CHAR(1),\n" +
                        "\t\"IMAGE_COUNT\" NUMBER,\n" +
                        "\t\"IMAGE_WATERMARK\" CHAR(1),\n" +
                        "\t\"IMAGE_PROCESS\" VARCHAR2(16),\n" +
                        "\t\"MEMBER_ID\" VARCHAR2(32),\n" +
                        "\t\"MEMBER_SEQ\" NUMBER,\n" +
                        "\t\"SUBJECT\" VARCHAR2(256),\n" +
                        "\t\"KEYWORDS\" VARCHAR2(512),\n" +
                        "\t\"REPOSITORY_TYPE\" VARCHAR2(16),\n" +
                        "\t\"TYPE\" VARCHAR2(16),\n" +
                        "\t\"CATEGORY_ID\" NUMBER,\n" +
                        "\t\"GROUP_ID\" NUMBER DEFAULT 0,\n" +
                        "\t\"STATUS\" VARCHAR2(16),\n" +
                        "\t\"IS_DISPLAY\" CHAR(1),\n" +
                        "\t\"HS_CODE\" VARCHAR2(16),\n" +
                        "\t\"IND_BY_ALL\" NUMBER,\n" +
                        "\t\"IND_BY_GROUP\" NUMBER,\n" +
                        "\t\"OWNER_MEMBER_ID\" VARCHAR2(32),\n" +
                        "\t\"OWNER_MEMBER_SEQ\" NUMBER,\n" +
                        "\t\"CERTIFICATE_IDS\" VARCHAR2(256),\n" +
                        "\t\"MONEY_TYPE\" NUMBER,\n" +
                        "\t\"PRICE_RANGE\" VARCHAR2(256),\n" +
                        "\t\"PORT\" VARCHAR2(256),\n" +
                        "\t\"PAYMENT_METHOD\" VARCHAR2(128),\n" +
                        "\t\"PAYMENT_METHOD_OTHER\" VARCHAR2(256),\n" +
                        "\t\"MIN_ORDER_QUANTITY\" VARCHAR2(64),\n" +
                        "\t\"MIN_ORDER_UNIT\" NUMBER,\n" +
                        "\t\"MIN_ORDER_OTHER\" VARCHAR2(256),\n" +
                        "\t\"SUPPLY_QUANTITY\" VARCHAR2(64),\n" +
                        "\t\"SUPPLY_UNIT\" NUMBER,\n" +
                        "\t\"SUPPLY_PERIOD\" VARCHAR2(16),\n" +
                        "\t\"SUPPLY_OTHER\" VARCHAR2(256),\n" +
                        "\t\"PACKAGING_DESC\" VARCHAR2(512),\n" +
                        "\t\"CONSIGNMENT_TERM\" VARCHAR2(64),\n" +
                        "\t\"IS_VALIDATE\" CHAR(1),\n" +
                        "\t\"CREATE_TYPE\" VARCHAR2(16),\n" +
                        "\t\"RED_MODEL\" VARCHAR2(128),\n" +
                        "\t\"DRAFT_STATUS\" VARCHAR2(16) DEFAULT 'no_status',\n" +
                        "\t\"EXPORT_TYPE\" VARCHAR2(16) DEFAULT 'normal',\n" +
                        "\t\"IS_ESCROW\" CHAR(1) DEFAULT 'N',\n" +
                        "\t\"SALE_TYPE\" VARCHAR2(64) DEFAULT 'sourcing',\n" +
                        "\t\"WS_DISPLAY\" VARCHAR2(16),\n" +
                        "\t\"WS_OFFLINE_DATE\" DATE,\n" +
                        "\t\"WS_VALID_NUM\" NUMBER,\n" +
                        "\t\"WS_REPOST_NUM\" NUMBER,\n" +
                        "\t\"WS_AUTO_REPOST\" CHAR(1),\n" +
                        "\t\"PRICE_UNIT\" NUMBER,\n" +
                        "\t\"IMAGE_VERSION\" NUMBER,\n" +
                        "\t\"SCORE\" NUMBER,\n" +
                        "\t\"GROUP_ID2\" NUMBER,\n" +
                        "\t\"GROUP_ID3\" NUMBER,\n" +
                        "\t\"IND_BY_GROUP2\" NUMBER,\n" +
                        "\t\"IND_BY_GROUP3\" NUMBER,\n" +
                        "\tCONSTRAINT \"P_PRODUCT_PK1\" PRIMARY KEY (\"ID\", \"COMPANY_ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE NOVALIDATE\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t)\n" +
                        "\t\tENABLE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "TABLESPACE \"APP_DATA1K\"\n" +
                        "STORAGE (\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        ")\n" +
                        "PARTITION BY HASH (\"COMPANY_ID\") (\n" +
                        "\tPARTITION \"PRODUCT_HASH_P1\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P2\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P3\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P4\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P5\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P6\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P7\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P8\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P9\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P10\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P11\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P12\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P13\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P14\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P15\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\", \n" +
                        "\tPARTITION \"PRODUCT_HASH_P16\"\n" +
                        "\t\tTABLESPACE \"APPDATA1M\"\n" +
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

        Assert.assertEquals(58, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
