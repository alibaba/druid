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
import org.junit.Assert;

import java.util.List;

public class OracleCreateTableTest89 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"ACCOUNT\".\"GROUP_BILLGEN_REQUEST\" \n" +
                        "   (  \"BG_REQ_ID\" NUMBER(12,0) NOT NULL ENABLE, \n" +
                        "  \"HOME_CITY\" NUMBER(3,0) NOT NULL ENABLE, \n" +
                        "  \"DETAILBILL_ID\" NUMBER(12,0), \n" +
                        "  \"USER_ID\" NUMBER(15,0) NOT NULL ENABLE, \n" +
                        "  \"CREATE_TIME\" DATE NOT NULL ENABLE, \n" +
                        "  \"REQUEST_SOURCE\" NUMBER(6,0) NOT NULL ENABLE, \n" +
                        "  \"EXEC_STATUS\" NUMBER(2,0) NOT NULL ENABLE, \n" +
                        "  \"EXEC_TIME\" DATE, \n" +
                        "  \"OPERATOR\" NUMBER(8,0)\n" +
                        "   )\n" +
                        "    PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255  NOLOGGING \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        "  \n" +
                        "  PARTITION BY RANGE (\"HOME_CITY\") \n" +
                        "  SUBPARTITION BY LIST (\"EXEC_STATUS\") \n" +
                        " (PARTITION \"FZ\"  VALUES LESS THAN (592) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"FZ_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"FZ_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"FZ_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"FZ_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"FZ_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"XM\"  VALUES LESS THAN (593) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"XM_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"XM_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"XM_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"XM_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"XM_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"ND\"  VALUES LESS THAN (594) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"ND_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ND_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ND_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ND_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ND_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"PT\"  VALUES LESS THAN (595) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"PT_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"PT_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"PT_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"PT_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"PT_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"QZ\"  VALUES LESS THAN (596) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"QZ_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"QZ_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"QZ_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"QZ_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"QZ_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"ZZ\"  VALUES LESS THAN (597) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"ZZ_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ZZ_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ZZ_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ZZ_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"ZZ_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"LY\"  VALUES LESS THAN (598) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"LY_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"LY_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"LY_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"LY_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"LY_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"SM\"  VALUES LESS THAN (599) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"SM_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"SM_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"SM_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"SM_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"SM_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) , \n" +
                        " PARTITION \"NP\"  VALUES LESS THAN (MAXVALUE) \n" +
                        "PCTFREE 5 PCTUSED 40 INITRANS 10 MAXTRANS 255 \n" +
                        "  STORAGE(\n" +
                        "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " ( SUBPARTITION \"NP_STA01\"  VALUES (0) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"NP_STA02\"  VALUES (1) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"NP_STA03\"  VALUES (2) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"NP_STA04\"  VALUES (3, 7) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS , \n" +
                        "  SUBPARTITION \"NP_STA99\"  VALUES (default) \n" +
                        "  TABLESPACE \"ACCOUNT_DATA_TS\" \n" +
                        " NOCOMPRESS ) )  ENABLE ROW MOVEMENT ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

    }
}
