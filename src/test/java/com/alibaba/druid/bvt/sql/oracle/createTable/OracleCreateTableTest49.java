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

public class OracleCreateTableTest49 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"ID\" NUMBER NOT NULL ENABLE, \n" +
                "  \"GMT_CREATE\" DATE, \n" +
                "  \"GMT_MODIFIED\" DATE, \n" +
                "  \"POSTING_TYPE\" VARCHAR2(20), \n" +
                "  \"POSTING_ID\" NUMBER, \n" +
                "  \"SYS_FRAUD\" NUMBER, \n" +
                "  \"CONFIRM_FRAUD\" NUMBER, \n" +
                "  \"FRAUD_RESON\" VARCHAR2(128), \n" +
                "  \"USER_ID\" NUMBER, \n" +
                "  \"IDENTIFIER_ID\" NUMBER, \n" +
                "  \"POSTING_CREATE\" DATE, \n" +
                "  \"POSTING_MODIFIER\" DATE, \n" +
                "  \"MEMBER_ID\" VARCHAR2(32), \n" +
                "  \"MEMBER_LEVEL\" VARCHAR2(16), \n" +
                "  \"SERVICE_VALUE\" VARCHAR2(32), \n" +
                "  \"ADDRESS\" VARCHAR2(256), \n" +
                "  \"COUNTRY\" VARCHAR2(64), \n" +
                "  \"PROVINCE\" VARCHAR2(128), \n" +
                "  \"CITY\" VARCHAR2(128), \n" +
                "  \"ZIP\" VARCHAR2(32), \n" +
                "  \"FIRST_NAME\" VARCHAR2(128), \n" +
                "  \"LAST_NAME\" VARCHAR2(128), \n" +
                "  \"PHONE_COUNTRY\" VARCHAR2(8), \n" +
                "  \"PHONE_AREA\" VARCHAR2(8), \n" +
                "  \"PHONE_NUMBER\" VARCHAR2(128), \n" +
                "  \"FAX_COUNTRY\" VARCHAR2(8), \n" +
                "  \"FAX_AREA\" VARCHAR2(8), \n" +
                "  \"FAX_NUMBER\" VARCHAR2(128), \n" +
                "  \"IP_COUNTRY\" VARCHAR2(128), \n" +
                "  \"MOBILE_NO\" VARCHAR2(128), \n" +
                "  \"EMAIL\" VARCHAR2(128), \n" +
                "  \"ALT_EMAIL\" VARCHAR2(128), \n" +
                "  \"COMPANY\" VARCHAR2(128), \n" +
                "  \"HOMEPAGE_URL\" VARCHAR2(128), \n" +
                "  \"CATEGORY_ID_1\" NUMBER, \n" +
                "  \"CATEGORY_ID_2\" NUMBER, \n" +
                "  \"CATEGORY_ID_3\" NUMBER, \n" +
                "  \"CATEGORY_ID_4\" NUMBER, \n" +
                "  \"CATEGORY_ID_5\" NUMBER, \n" +
                "  \"SUBJECT\" VARCHAR2(256), \n" +
                "  \"KEYWORDS\" VARCHAR2(512), \n" +
                "  \"DETAIL\" CLOB, \n" +
                "  \"POSTING_STATUS\" VARCHAR2(16), \n" +
                "  \"OFFER_TYPE\" VARCHAR2(32)\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APP_DATA1K\" \n" +
                " LOB (\"DETAIL\") STORE AS (\n" +
                "  ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT)) \n" +
                "  PARTITION BY RANGE (\"GMT_CREATE\") \n" +
                " (PARTITION \"P2008\"  VALUES LESS THAN (TO_DATE(' 2009-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APP_DATA1K\" \n" +
                " LOB (\"DETAIL\") STORE AS (\n" +
                "  TABLESPACE \"APP_DATA1K\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)) NOCOMPRESS , \n" +
                " PARTITION \"P2009\"  VALUES LESS THAN (TO_DATE(' 2010-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APP_DATA1K\" \n" +
                " LOB (\"DETAIL\") STORE AS (\n" +
                "  TABLESPACE \"APP_DATA1K\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)) NOCOMPRESS , \n" +
                " PARTITION \"P2010\"  VALUES LESS THAN (TO_DATE(' 2011-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                "  TABLESPACE \"APPDATA1M\" \n" +
                " LOB (\"DETAIL\") STORE AS (\n" +
                "  TABLESPACE \"BOPSDATATS\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)) NOCOMPRESS )   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER NOT NULL ENABLE,\n" +
                        "\t\"GMT_CREATE\" DATE,\n" +
                        "\t\"GMT_MODIFIED\" DATE,\n" +
                        "\t\"POSTING_TYPE\" VARCHAR2(20),\n" +
                        "\t\"POSTING_ID\" NUMBER,\n" +
                        "\t\"SYS_FRAUD\" NUMBER,\n" +
                        "\t\"CONFIRM_FRAUD\" NUMBER,\n" +
                        "\t\"FRAUD_RESON\" VARCHAR2(128),\n" +
                        "\t\"USER_ID\" NUMBER,\n" +
                        "\t\"IDENTIFIER_ID\" NUMBER,\n" +
                        "\t\"POSTING_CREATE\" DATE,\n" +
                        "\t\"POSTING_MODIFIER\" DATE,\n" +
                        "\t\"MEMBER_ID\" VARCHAR2(32),\n" +
                        "\t\"MEMBER_LEVEL\" VARCHAR2(16),\n" +
                        "\t\"SERVICE_VALUE\" VARCHAR2(32),\n" +
                        "\t\"ADDRESS\" VARCHAR2(256),\n" +
                        "\t\"COUNTRY\" VARCHAR2(64),\n" +
                        "\t\"PROVINCE\" VARCHAR2(128),\n" +
                        "\t\"CITY\" VARCHAR2(128),\n" +
                        "\t\"ZIP\" VARCHAR2(32),\n" +
                        "\t\"FIRST_NAME\" VARCHAR2(128),\n" +
                        "\t\"LAST_NAME\" VARCHAR2(128),\n" +
                        "\t\"PHONE_COUNTRY\" VARCHAR2(8),\n" +
                        "\t\"PHONE_AREA\" VARCHAR2(8),\n" +
                        "\t\"PHONE_NUMBER\" VARCHAR2(128),\n" +
                        "\t\"FAX_COUNTRY\" VARCHAR2(8),\n" +
                        "\t\"FAX_AREA\" VARCHAR2(8),\n" +
                        "\t\"FAX_NUMBER\" VARCHAR2(128),\n" +
                        "\t\"IP_COUNTRY\" VARCHAR2(128),\n" +
                        "\t\"MOBILE_NO\" VARCHAR2(128),\n" +
                        "\t\"EMAIL\" VARCHAR2(128),\n" +
                        "\t\"ALT_EMAIL\" VARCHAR2(128),\n" +
                        "\t\"COMPANY\" VARCHAR2(128),\n" +
                        "\t\"HOMEPAGE_URL\" VARCHAR2(128),\n" +
                        "\t\"CATEGORY_ID_1\" NUMBER,\n" +
                        "\t\"CATEGORY_ID_2\" NUMBER,\n" +
                        "\t\"CATEGORY_ID_3\" NUMBER,\n" +
                        "\t\"CATEGORY_ID_4\" NUMBER,\n" +
                        "\t\"CATEGORY_ID_5\" NUMBER,\n" +
                        "\t\"SUBJECT\" VARCHAR2(256),\n" +
                        "\t\"KEYWORDS\" VARCHAR2(512),\n" +
                        "\t\"DETAIL\" CLOB,\n" +
                        "\t\"POSTING_STATUS\" VARCHAR2(16),\n" +
                        "\t\"OFFER_TYPE\" VARCHAR2(32)\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "TABLESPACE \"APP_DATA1K\"\n" +
                        "STORAGE (\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        ")\n" +
                        "LOB (\"DETAIL\") STORE AS (\n" +
                        "\tLOGGING\n" +
                        "\tSTORAGE (\n" +
                        "\t\tBUFFER_POOL DEFAULT\n" +
                        "\t)\n" +
                        "\tENABLE STORAGE IN ROW\n" +
                        "\tCHUNK 8192\n" +
                        "\tNOCACHE\n" +
                        ")\n" +
                        "PARTITION BY RANGE (\"GMT_CREATE\") (\n" +
                        "\tPARTITION \"P2008\" VALUES LESS THAN (TO_DATE(' 2009-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"APP_DATA1K\"\n" +
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
                        "\t\tNOCOMPRESS,\n" +
                        "\tPARTITION \"P2009\" VALUES LESS THAN (TO_DATE(' 2010-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"APP_DATA1K\"\n" +
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
                        "\t\tNOCOMPRESS,\n" +
                        "\tPARTITION \"P2010\" VALUES LESS THAN (TO_DATE(' 2011-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"APPDATA1M\"\n" +
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
                        "\t\tNOCOMPRESS\n" +
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

        Assert.assertEquals(44, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
