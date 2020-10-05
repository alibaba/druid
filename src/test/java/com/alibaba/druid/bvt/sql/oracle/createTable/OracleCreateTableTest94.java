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

public class OracleCreateTableTest94 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"CITSONLINE\".\"ORDER_INFO_HIS_2015\" \n" +
                        "   (\"ORDER_ID\" VARCHAR2(20) NOT NULL ENABLE, \n" +
                        "  \"HIS_ID\" VARCHAR2(15) NOT NULL ENABLE, \n" +
                        "  \"LAN_ID\" CHAR(2) NOT NULL ENABLE, \n" +
                        "  \"CAR_ID\" CHAR(10), \n" +
                        "  \"PRODUCT_ID\" VARCHAR2(20) NOT NULL ENABLE, \n" +
                        "  \"PRODUCT_FLG\" CHAR(1), \n" +
                        "  \"PRODUCT_TYPE\" VARCHAR2(2) NOT NULL ENABLE, \n" +
                        "  \"TOURIST_ID\" CHAR(8), \n" +
                        "  \"LOGIN_ID\" VARCHAR2(30), \n" +
                        "  \"STATUS\" VARCHAR2(1), \n" +
                        "  \"START_DATE\" TIMESTAMP (6), \n" +
                        "  \"ADULT_NUM\" NUMBER(5,0), \n" +
                        "  \"CHILD_NUM\" NUMBER(5,0), \n" +
                        "  \"BABY_NUM\" NUMBER(3,0), \n" +
                        "  \"ADDR\" VARCHAR2(255), \n" +
                        "  \"PCODE\" VARCHAR2(100), \n" +
                        "  \"TEL\" VARCHAR2(20), \n" +
                        "  \"MEMO\" VARCHAR2(600), \n" +
                        "  \"ADD_USER\" VARCHAR2(30) NOT NULL ENABLE, \n" +
                        "  \"ADD_DATE\" TIMESTAMP (6) NOT NULL ENABLE, \n" +
                        "  \"UPD_USER\" VARCHAR2(30), \n" +
                        "  \"UPD_DATE\" TIMESTAMP (6), \n" +
                        "  \"END_DATE\" TIMESTAMP (6), \n" +
                        "  \"PERSON\" VARCHAR2(50), \n" +
                        "  \"EMAIL\" VARCHAR2(40), \n" +
                        "  \"OTHER_INFO\" VARCHAR2(100), \n" +
                        "  \"PRICE\" NUMBER(19,4), \n" +
                        "  \"ORDER_MEMO\" VARCHAR2(100), \n" +
                        "  \"ADD_FEE\" NUMBER(10,4), \n" +
                        "  \"ADD_FEE_REASON\" VARCHAR2(300), \n" +
                        "  \"OPERATOR_ID\" CHAR(6), \n" +
                        "  \"IS_NEW\" VARCHAR2(1), \n" +
                        "  \"ROOM_TYPE\" CHAR(1), \n" +
                        "  \"HIS_ADD_USER\" VARCHAR2(30), \n" +
                        "  \"HIS_ADD_DATE\" TIMESTAMP (6), \n" +
                        "  \"HIS_ORDER_ID\" VARCHAR2(20), \n" +
                        "  \"OP_TYPE\" VARCHAR2(30), \n" +
                        "  \"REPLY\" VARCHAR2(300), \n" +
                        "  \"OP_DEPT\" VARCHAR2(30), \n" +
                        "  \"PAY_STATUS\" VARCHAR2(2), \n" +
                        "  \"CUSTOM_ID\" VARCHAR2(9), \n" +
                        "  \"CUSTOM_NAME\" VARCHAR2(300), \n" +
                        "  \"FIRST_NUM\" NUMBER(3,0), \n" +
                        "  \"END_NUM\" NUMBER(3,0), \n" +
                        "  \"SINGLE_PLUS_NUM\" NUMBER(3,0), \n" +
                        "  \"IS_TRITON\" CHAR(1), \n" +
                        "  \"ALERT_NUM\" VARCHAR2(2), \n" +
                        "  \"INVOICE_SUM\" NUMBER(19,4), \n" +
                        "  \"OUT_NOTICE\" VARCHAR2(2), \n" +
                        "  \"OUT_NOTICE_TIME\" TIMESTAMP (6), \n" +
                        "  \"OUT_NOTICE_USER\" VARCHAR2(30), \n" +
                        "  \"FAX\" VARCHAR2(20), \n" +
                        "  \"AUDIT_ORDER\" CHAR(1), \n" +
                        "  \"OUT_NOTICE_MEMO\" VARCHAR2(500), \n" +
                        "  \"B2C_SAVE_FLG\" CHAR(1), \n" +
                        "  \"B2C_PAY_MODE\" CHAR(1), \n" +
                        "  \"OD_LOCK\" CHAR(1), \n" +
                        "  \"OD_CANCEL\" CHAR(1), \n" +
                        "  \"APPLY_TYPE\" VARCHAR2(10), \n" +
                        "  \"UPDATE_FLAG\" VARCHAR2(10), \n" +
                        "  \"CANCEL_CAUSE\" VARCHAR2(500), \n" +
                        "  \"B2C_MEMO\" VARCHAR2(500), \n" +
                        "  \"ECOUPON_SUM\" NUMBER(19,4), \n" +
                        "  \"CONTACT_ID\" VARCHAR2(255), \n" +
                        "  \"URGENCY_ID\" VARCHAR2(255), \n" +
                        "  \"URGENCY_CONTACT\" VARCHAR2(50), \n" +
                        "  \"URGENCY_TEL\" VARCHAR2(20), \n" +
                        "  \"URGENCY_REL\" VARCHAR2(200), \n" +
                        "  \"BELONG_CRMID\" VARCHAR2(255), \n" +
                        "  \"TEAM_HIS_ID\" VARCHAR2(15), \n" +
                        "  \"THIRD_ORDER_ID\" VARCHAR2(50), \n" +
                        "  \"THIRD_ORDER_PRICE\" NUMBER(19,4), \n" +
                        "  \"THIRD_ORDER_PAYMENT\" NUMBER(19,4)\n" +
                        "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS NOLOGGING\n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)\n" +
                        "  TABLESPACE \"CITS_BTOB1\" \n" +
                        "  PARALLEL 8";

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

        assertEquals("CREATE TABLE \"CITSONLINE\".\"ORDER_INFO_HIS_2015\" (\n" +
                "\t\"ORDER_ID\" VARCHAR2(20) NOT NULL ENABLE,\n" +
                "\t\"HIS_ID\" VARCHAR2(15) NOT NULL ENABLE,\n" +
                "\t\"LAN_ID\" CHAR(2) NOT NULL ENABLE,\n" +
                "\t\"CAR_ID\" CHAR(10),\n" +
                "\t\"PRODUCT_ID\" VARCHAR2(20) NOT NULL ENABLE,\n" +
                "\t\"PRODUCT_FLG\" CHAR(1),\n" +
                "\t\"PRODUCT_TYPE\" VARCHAR2(2) NOT NULL ENABLE,\n" +
                "\t\"TOURIST_ID\" CHAR(8),\n" +
                "\t\"LOGIN_ID\" VARCHAR2(30),\n" +
                "\t\"STATUS\" VARCHAR2(1),\n" +
                "\t\"START_DATE\" TIMESTAMP(6),\n" +
                "\t\"ADULT_NUM\" NUMBER(5, 0),\n" +
                "\t\"CHILD_NUM\" NUMBER(5, 0),\n" +
                "\t\"BABY_NUM\" NUMBER(3, 0),\n" +
                "\t\"ADDR\" VARCHAR2(255),\n" +
                "\t\"PCODE\" VARCHAR2(100),\n" +
                "\t\"TEL\" VARCHAR2(20),\n" +
                "\t\"MEMO\" VARCHAR2(600),\n" +
                "\t\"ADD_USER\" VARCHAR2(30) NOT NULL ENABLE,\n" +
                "\t\"ADD_DATE\" TIMESTAMP(6) NOT NULL ENABLE,\n" +
                "\t\"UPD_USER\" VARCHAR2(30),\n" +
                "\t\"UPD_DATE\" TIMESTAMP(6),\n" +
                "\t\"END_DATE\" TIMESTAMP(6),\n" +
                "\t\"PERSON\" VARCHAR2(50),\n" +
                "\t\"EMAIL\" VARCHAR2(40),\n" +
                "\t\"OTHER_INFO\" VARCHAR2(100),\n" +
                "\t\"PRICE\" NUMBER(19, 4),\n" +
                "\t\"ORDER_MEMO\" VARCHAR2(100),\n" +
                "\t\"ADD_FEE\" NUMBER(10, 4),\n" +
                "\t\"ADD_FEE_REASON\" VARCHAR2(300),\n" +
                "\t\"OPERATOR_ID\" CHAR(6),\n" +
                "\t\"IS_NEW\" VARCHAR2(1),\n" +
                "\t\"ROOM_TYPE\" CHAR(1),\n" +
                "\t\"HIS_ADD_USER\" VARCHAR2(30),\n" +
                "\t\"HIS_ADD_DATE\" TIMESTAMP(6),\n" +
                "\t\"HIS_ORDER_ID\" VARCHAR2(20),\n" +
                "\t\"OP_TYPE\" VARCHAR2(30),\n" +
                "\t\"REPLY\" VARCHAR2(300),\n" +
                "\t\"OP_DEPT\" VARCHAR2(30),\n" +
                "\t\"PAY_STATUS\" VARCHAR2(2),\n" +
                "\t\"CUSTOM_ID\" VARCHAR2(9),\n" +
                "\t\"CUSTOM_NAME\" VARCHAR2(300),\n" +
                "\t\"FIRST_NUM\" NUMBER(3, 0),\n" +
                "\t\"END_NUM\" NUMBER(3, 0),\n" +
                "\t\"SINGLE_PLUS_NUM\" NUMBER(3, 0),\n" +
                "\t\"IS_TRITON\" CHAR(1),\n" +
                "\t\"ALERT_NUM\" VARCHAR2(2),\n" +
                "\t\"INVOICE_SUM\" NUMBER(19, 4),\n" +
                "\t\"OUT_NOTICE\" VARCHAR2(2),\n" +
                "\t\"OUT_NOTICE_TIME\" TIMESTAMP(6),\n" +
                "\t\"OUT_NOTICE_USER\" VARCHAR2(30),\n" +
                "\t\"FAX\" VARCHAR2(20),\n" +
                "\t\"AUDIT_ORDER\" CHAR(1),\n" +
                "\t\"OUT_NOTICE_MEMO\" VARCHAR2(500),\n" +
                "\t\"B2C_SAVE_FLG\" CHAR(1),\n" +
                "\t\"B2C_PAY_MODE\" CHAR(1),\n" +
                "\t\"OD_LOCK\" CHAR(1),\n" +
                "\t\"OD_CANCEL\" CHAR(1),\n" +
                "\t\"APPLY_TYPE\" VARCHAR2(10),\n" +
                "\t\"UPDATE_FLAG\" VARCHAR2(10),\n" +
                "\t\"CANCEL_CAUSE\" VARCHAR2(500),\n" +
                "\t\"B2C_MEMO\" VARCHAR2(500),\n" +
                "\t\"ECOUPON_SUM\" NUMBER(19, 4),\n" +
                "\t\"CONTACT_ID\" VARCHAR2(255),\n" +
                "\t\"URGENCY_ID\" VARCHAR2(255),\n" +
                "\t\"URGENCY_CONTACT\" VARCHAR2(50),\n" +
                "\t\"URGENCY_TEL\" VARCHAR2(20),\n" +
                "\t\"URGENCY_REL\" VARCHAR2(200),\n" +
                "\t\"BELONG_CRMID\" VARCHAR2(255),\n" +
                "\t\"TEAM_HIS_ID\" VARCHAR2(15),\n" +
                "\t\"THIRD_ORDER_ID\" VARCHAR2(50),\n" +
                "\t\"THIRD_ORDER_PRICE\" NUMBER(19, 4),\n" +
                "\t\"THIRD_ORDER_PAYMENT\" NUMBER(19, 4)\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "NOLOGGING\n" +
                "TABLESPACE \"CITS_BTOB1\"\n" +
                "STORAGE (\n" +
                "\tINITIAL 65536\n" +
                "\tNEXT 1048576\n" +
                "\tMINEXTENTS 1\n" +
                "\tMAXEXTENTS 2147483645\n" +
                "\tPCTINCREASE 0\n" +
                "\tFREELISTS 1\n" +
                "\tFREELIST GROUPS 1\n" +
                "\tBUFFER_POOL DEFAULT\n" +
                ")\n" +
                "PARALLEL 8", stmt.toString());

    }
}
