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

public class OracleCreateTableTest93 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"GXYY\".\"EMR_MED_REC\" \n" +
                        "   (    \"PK_REC\" CHAR(32), \n" +
                        "    \"PK_PATREC\" CHAR(32), \n" +
                        "    \"NAME\" VARCHAR2(64), \n" +
                        "    \"SEQ_NO\" NUMBER(18,0), \n" +
                        "    \"REC_DATE\" TIMESTAMP (3), \n" +
                        "    \"DESCRIBE\" VARCHAR2(64), \n" +
                        "    \"PK_PI\" CHAR(32), \n" +
                        "    \"TIMES\" NUMBER(5,0), \n" +
                        "    \"PK_PV\" CHAR(32), \n" +
                        "    \"PK_DEPT\" CHAR(32), \n" +
                        "    \"PK_WARD\" CHAR(32), \n" +
                        "    \"TYPE_CODE\" VARCHAR2(20), \n" +
                        "    \"PK_TMP\" CHAR(32), \n" +
                        "    \"PK_DOC\" CHAR(32), \n" +
                        "    \"FLAG_AUDIT\" CHAR(1), \n" +
                        "    \"EU_AUDIT_LEVEL\" NUMBER(2,0), \n" +
                        "    \"EU_DOC_STATUS\" CHAR(1), \n" +
                        "    \"EU_AUDIT_STATUS\" CHAR(1), \n" +
                        "    \"PK_EMP_REFER\" CHAR(32), \n" +
                        "    \"REFER_SIGN_DATE\" TIMESTAMP (3), \n" +
                        "    \"PK_EMP_CONSULT_ACT\" CHAR(32), \n" +
                        "    \"PK_EMP_CONSULT\" CHAR(32), \n" +
                        "    \"CONSULT_AUDIT_DATE\" TIMESTAMP (3), \n" +
                        "    \"CONSULT_SIGN_DATE\" TIMESTAMP (3), \n" +
                        "    \"PK_EMP_DIRECTOR_ACT\" CHAR(32), \n" +
                        "    \"PK_EMP_DIRECTOR\" CHAR(32), \n" +
                        "    \"DIRECTOR_AUDIT_DATE\" TIMESTAMP (3), \n" +
                        "    \"DIRECTOR_SIGN_DATE\" TIMESTAMP (3), \n" +
                        "    \"DOC_DATA\" LONG RAW, \n" +
                        "    \"DOC_XML\" \"SYS\".\"XMLTYPE\" , \n" +
                        "    \"DEL_FLAG\" CHAR(1), \n" +
                        "    \"REMARK\" VARCHAR2(64), \n" +
                        "    \"CREATOR\" VARCHAR2(32), \n" +
                        "    \"CREATE_TIME\" TIMESTAMP (3), \n" +
                        "    \"TS\" TIMESTAMP (3), \n" +
                        "    \"FLAG_AUDIT_FINISH\" CHAR(1), \n" +
                        "    \"AUDIT_LEVEL_SET\" VARCHAR2(32), \n" +
                        "    \"PK_EMP_INTERN\" VARCHAR2(32), \n" +
                        "    \"INTERN_SIGN_DATE\" DATE, \n" +
                        "    \"PK_EMP_REFER_ACT\" VARCHAR2(32), \n" +
                        "    \"REFER_AUDIT_DATE\" DATE\n" +
                        "   ) SEGMENT CREATION IMMEDIATE \n" +
                        "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                        " NOCOMPRESS LOGGING\n" +
                        "  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"GXDATA\" \n" +
                        " XMLTYPE COLUMN \"DOC_XML\" STORE AS SECUREFILE BINARY XML (\n" +
                        "  TABLESPACE \"GXDATA\" ENABLE STORAGE IN ROW CHUNK 8192\n" +
                        "  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES \n" +
                        "  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                        "  PCTINCREASE 0\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ALLOW NONSCHEMA DISALLOW ANYSCHEMA;";

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

        assertEquals("CREATE TABLE \"GXYY\".\"EMR_MED_REC\" (\n" +
                "\t\"PK_REC\" CHAR(32),\n" +
                "\t\"PK_PATREC\" CHAR(32),\n" +
                "\t\"NAME\" VARCHAR2(64),\n" +
                "\t\"SEQ_NO\" NUMBER(18, 0),\n" +
                "\t\"REC_DATE\" TIMESTAMP(3),\n" +
                "\t\"DESCRIBE\" VARCHAR2(64),\n" +
                "\t\"PK_PI\" CHAR(32),\n" +
                "\t\"TIMES\" NUMBER(5, 0),\n" +
                "\t\"PK_PV\" CHAR(32),\n" +
                "\t\"PK_DEPT\" CHAR(32),\n" +
                "\t\"PK_WARD\" CHAR(32),\n" +
                "\t\"TYPE_CODE\" VARCHAR2(20),\n" +
                "\t\"PK_TMP\" CHAR(32),\n" +
                "\t\"PK_DOC\" CHAR(32),\n" +
                "\t\"FLAG_AUDIT\" CHAR(1),\n" +
                "\t\"EU_AUDIT_LEVEL\" NUMBER(2, 0),\n" +
                "\t\"EU_DOC_STATUS\" CHAR(1),\n" +
                "\t\"EU_AUDIT_STATUS\" CHAR(1),\n" +
                "\t\"PK_EMP_REFER\" CHAR(32),\n" +
                "\t\"REFER_SIGN_DATE\" TIMESTAMP(3),\n" +
                "\t\"PK_EMP_CONSULT_ACT\" CHAR(32),\n" +
                "\t\"PK_EMP_CONSULT\" CHAR(32),\n" +
                "\t\"CONSULT_AUDIT_DATE\" TIMESTAMP(3),\n" +
                "\t\"CONSULT_SIGN_DATE\" TIMESTAMP(3),\n" +
                "\t\"PK_EMP_DIRECTOR_ACT\" CHAR(32),\n" +
                "\t\"PK_EMP_DIRECTOR\" CHAR(32),\n" +
                "\t\"DIRECTOR_AUDIT_DATE\" TIMESTAMP(3),\n" +
                "\t\"DIRECTOR_SIGN_DATE\" TIMESTAMP(3),\n" +
                "\t\"DOC_DATA\" LONG RAW,\n" +
                "\t\"DOC_XML\" \"SYS\".\"XMLTYPE\",\n" +
                "\t\"DEL_FLAG\" CHAR(1),\n" +
                "\t\"REMARK\" VARCHAR2(64),\n" +
                "\t\"CREATOR\" VARCHAR2(32),\n" +
                "\t\"CREATE_TIME\" TIMESTAMP(3),\n" +
                "\t\"TS\" TIMESTAMP(3),\n" +
                "\t\"FLAG_AUDIT_FINISH\" CHAR(1),\n" +
                "\t\"AUDIT_LEVEL_SET\" VARCHAR2(32),\n" +
                "\t\"PK_EMP_INTERN\" VARCHAR2(32),\n" +
                "\t\"INTERN_SIGN_DATE\" DATE,\n" +
                "\t\"PK_EMP_REFER_ACT\" VARCHAR2(32),\n" +
                "\t\"REFER_AUDIT_DATE\" DATE\n" +
                ")\n" +
                "PCTFREE 10\n" +
                "PCTUSED 40\n" +
                "INITRANS 1\n" +
                "MAXTRANS 255\n" +
                "NOCOMPRESS\n" +
                "LOGGING\n" +
                "TABLESPACE \"GXDATA\"\n" +
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
                ")\n" +
                "XMLTYPE \"DOC_XML\" ALLOW NONSCHEMA DISALLOW ANYSCHEMA;", stmt.toString());

    }
}
