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

public class OracleCreateTableTest57 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "       CREATE TABLE \"SC_001\".\"TB_001\" \n" +
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
                "  \"GROUP_ID\" NUMBER, \n" +
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
                "  \"IS_ESCROW\" CHAR(1), \n" +
                "  \"SALE_TYPE\" VARCHAR2(64), \n" +
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
                "   CONSTRAINT \"PRODUCT_PK\" PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 83886080 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPINDX1M\"  ENABLE\n" +
                "   ) CLUSTER \"SEARCHTEST\".\"CLUSTER_PRODUCT_COMPANY_ID\" (\"COMPANY_ID\")\n" +
                "\n" +
                "       ";

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
                        "\t\"GROUP_ID\" NUMBER,\n" +
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
                        "\t\"IS_ESCROW\" CHAR(1),\n" +
                        "\t\"SALE_TYPE\" VARCHAR2(64),\n" +
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
                        "\tCONSTRAINT \"PRODUCT_PK\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"APPINDX1M\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 83886080\n" +
                        "\t\t\tNEXT 1048576\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t)\n" +
                        "\t\tCOMPUTE STATISTICS\n" +
                        "\t\tENABLE\n" +
                        ")\n" +
                        "CLUSTER \"SEARCHTEST\".\"CLUSTER_PRODUCT_COMPANY_ID\" (\"COMPANY_ID\")",//
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
