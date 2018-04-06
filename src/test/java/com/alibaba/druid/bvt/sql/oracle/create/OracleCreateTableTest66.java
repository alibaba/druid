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

public class OracleCreateTableTest66 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (\t\"ID\" VARCHAR2(32) DEFAULT Sys_guid() NOT NULL ENABLE, \n" +
                "\t\"ISDEL\" NUMBER(1,0) DEFAULT 0, \n" +
                "\t\"DATAVERSION\" CHAR(14) DEFAULT to_char(sysdate,'yyyymmddhh24miss'), \n" +
                "\t\"LRR_SFZH\" VARCHAR2(50), \n" +
                "\t\"LRSJ\" DATE DEFAULT sysdate, \n" +
                "\t\"JQZT\" VARCHAR2(10), \n" +
                "\t\"JQDD\" VARCHAR2(200), \n" +
                "\t\"BJFS\" VARCHAR2(10), \n" +
                "\t\"BJSJ\" DATE, \n" +
                "\t\"BJR\" VARCHAR2(150), \n" +
                "\t\"BJDH\" VARCHAR2(50), \n" +
                "\t\"BJNR\" VARCHAR2(4000), \n" +
                "\t\"PDDDSJ\" DATE, \n" +
                "\t\"PDJSSJ\" DATE, \n" +
                "\t\"CJMJ\" VARCHAR2(50), \n" +
                "\t\"CJDW\" VARCHAR2(50), \n" +
                "\t\"CJSJ\" DATE, \n" +
                "\t\"DDXCSJ\" DATE, \n" +
                "\t\"CJCS\" VARCHAR2(2000), \n" +
                "\t\"CJJG\" VARCHAR2(2000), \n" +
                "\t\"GZYQ\" VARCHAR2(2000), \n" +
                "\t\"JQBH\" VARCHAR2(30), \n" +
                "\t\"XGR_SFZH\" VARCHAR2(50), \n" +
                "\t\"XGSJ\" DATE DEFAULT sysdate, \n" +
                "\t\"AJLY\" VARCHAR2(10) DEFAULT '03', \n" +
                "\t\"JQMC\" VARCHAR2(200), \n" +
                "\t\"CJJGSM\" VARCHAR2(2000), \n" +
                "\t\"CJMJ_XM\" VARCHAR2(200), \n" +
                "\t\"CJDW_MC\" VARCHAR2(200), \n" +
                "\t\"JQLB\" VARCHAR2(20), \n" +
                "\t\"XXLX\" VARCHAR2(20), \n" +
                "\t\"JJDBH\" VARCHAR2(50), \n" +
                "\t\"XZQH\" VARCHAR2(50), \n" +
                "\t\"CBQY_BH\" VARCHAR2(50), \n" +
                "\t\"SJLY\" VARCHAR2(200) DEFAULT '????', \n" +
                "\t\"GXDWDM\" VARCHAR2(20), \n" +
                "\t\"BJLXDM\" VARCHAR2(20), \n" +
                "\t\"BJXLDM\" VARCHAR2(20), \n" +
                "\t\"CJDW_OLD\" VARCHAR2(50), \n" +
                "\t\"JQLY\" VARCHAR2(20), \n" +
                "\t\"IS_AJ_ANALYZE\" VARCHAR2(1) DEFAULT '9', \n" +
                "\t\"SJJE\" VARCHAR2(20), \n" +
                "\t\"YSDWFL\" VARCHAR2(5), \n" +
                "\t\"YSDWMC\" VARCHAR2(50), \n" +
                "\t\"YSDWCBR\" VARCHAR2(20), \n" +
                "\t\"CBRLXDH\" VARCHAR2(20), \n" +
                "\t\"TBDW\" VARCHAR2(50), \n" +
                "\t\"TBR\" VARCHAR2(20), \n" +
                "\t\"TBRQ\" DATE, \n" +
                "\t\"JZ_JQLB\" VARCHAR2(20), \n" +
                "\t\"SFZR\" VARCHAR2(10), \n" +
                "\t\"SJDWDM\" VARCHAR2(40), \n" +
                "\t CONSTRAINT \"PK_CASE_GG_JQXX1\" PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 150994944 NEXT 8388608 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_IDX\"  ENABLE\n" +
                "   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                "  STORAGE(\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" \n" +
                "  PARTITION BY RANGE (\"LRSJ\") \n" +
                " (PARTITION \"T_RANGE_V1\"  VALUES LESS THAN (TO_DATE(' 2015-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 80 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 553648128 NEXT 16384 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" , \n" +
                " PARTITION \"T_RANGE_V2\"  VALUES LESS THAN (TO_DATE(' 2015-07-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 80 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 553648128 NEXT 16384 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" , \n" +
                " PARTITION \"T_RANGE_V3\"  VALUES LESS THAN (TO_DATE(' 2016-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 80 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 553648128 NEXT 16384 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" , \n" +
                " PARTITION \"T_RANGE_V4\"  VALUES LESS THAN (TO_DATE(' 2016-07-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 80 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 553648128 NEXT 16384 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" , \n" +
                " PARTITION \"T_RANGE_V5\"  VALUES LESS THAN (TO_DATE(' 2017-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 80 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 553648128 NEXT 16384 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" , \n" +
                " PARTITION \"T_RANGE_V6\"  VALUES LESS THAN (TO_DATE(' 2017-07-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN')) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 80 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 553648128 NEXT 16384 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" , \n" +
                " PARTITION \"MAXPART\"  VALUES LESS THAN (MAXVALUE) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 \n" +
                " COMPRESS FOR OLTP LOGGING \n" +
                "  STORAGE(INITIAL 8388608 NEXT 8388608 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1\n" +
                "  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"RDP_DATA\" )    ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" VARCHAR2(32) DEFAULT Sys_guid() NOT NULL ENABLE,\n" +
                        "\t\"ISDEL\" NUMBER(1, 0) DEFAULT 0,\n" +
                        "\t\"DATAVERSION\" CHAR(14) DEFAULT to_char(SYSDATE, 'yyyymmddhh24miss'),\n" +
                        "\t\"LRR_SFZH\" VARCHAR2(50),\n" +
                        "\t\"LRSJ\" DATE DEFAULT SYSDATE,\n" +
                        "\t\"JQZT\" VARCHAR2(10),\n" +
                        "\t\"JQDD\" VARCHAR2(200),\n" +
                        "\t\"BJFS\" VARCHAR2(10),\n" +
                        "\t\"BJSJ\" DATE,\n" +
                        "\t\"BJR\" VARCHAR2(150),\n" +
                        "\t\"BJDH\" VARCHAR2(50),\n" +
                        "\t\"BJNR\" VARCHAR2(4000),\n" +
                        "\t\"PDDDSJ\" DATE,\n" +
                        "\t\"PDJSSJ\" DATE,\n" +
                        "\t\"CJMJ\" VARCHAR2(50),\n" +
                        "\t\"CJDW\" VARCHAR2(50),\n" +
                        "\t\"CJSJ\" DATE,\n" +
                        "\t\"DDXCSJ\" DATE,\n" +
                        "\t\"CJCS\" VARCHAR2(2000),\n" +
                        "\t\"CJJG\" VARCHAR2(2000),\n" +
                        "\t\"GZYQ\" VARCHAR2(2000),\n" +
                        "\t\"JQBH\" VARCHAR2(30),\n" +
                        "\t\"XGR_SFZH\" VARCHAR2(50),\n" +
                        "\t\"XGSJ\" DATE DEFAULT SYSDATE,\n" +
                        "\t\"AJLY\" VARCHAR2(10) DEFAULT '03',\n" +
                        "\t\"JQMC\" VARCHAR2(200),\n" +
                        "\t\"CJJGSM\" VARCHAR2(2000),\n" +
                        "\t\"CJMJ_XM\" VARCHAR2(200),\n" +
                        "\t\"CJDW_MC\" VARCHAR2(200),\n" +
                        "\t\"JQLB\" VARCHAR2(20),\n" +
                        "\t\"XXLX\" VARCHAR2(20),\n" +
                        "\t\"JJDBH\" VARCHAR2(50),\n" +
                        "\t\"XZQH\" VARCHAR2(50),\n" +
                        "\t\"CBQY_BH\" VARCHAR2(50),\n" +
                        "\t\"SJLY\" VARCHAR2(200) DEFAULT '????',\n" +
                        "\t\"GXDWDM\" VARCHAR2(20),\n" +
                        "\t\"BJLXDM\" VARCHAR2(20),\n" +
                        "\t\"BJXLDM\" VARCHAR2(20),\n" +
                        "\t\"CJDW_OLD\" VARCHAR2(50),\n" +
                        "\t\"JQLY\" VARCHAR2(20),\n" +
                        "\t\"IS_AJ_ANALYZE\" VARCHAR2(1) DEFAULT '9',\n" +
                        "\t\"SJJE\" VARCHAR2(20),\n" +
                        "\t\"YSDWFL\" VARCHAR2(5),\n" +
                        "\t\"YSDWMC\" VARCHAR2(50),\n" +
                        "\t\"YSDWCBR\" VARCHAR2(20),\n" +
                        "\t\"CBRLXDH\" VARCHAR2(20),\n" +
                        "\t\"TBDW\" VARCHAR2(50),\n" +
                        "\t\"TBR\" VARCHAR2(20),\n" +
                        "\t\"TBRQ\" DATE,\n" +
                        "\t\"JZ_JQLB\" VARCHAR2(20),\n" +
                        "\t\"SFZR\" VARCHAR2(10),\n" +
                        "\t\"SJDWDM\" VARCHAR2(40),\n" +
                        "\tCONSTRAINT \"PK_CASE_GG_JQXX1\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"RDP_IDX\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 150994944\n" +
                        "\t\t\tNEXT 8388608\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t)\n" +
                        "\t\tCOMPUTE STATISTICS\n" +
                        "\t\tENABLE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "TABLESPACE \"RDP_DATA\"\n" +
                        "STORAGE (\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        "\tFLASH_CACHE DEFAULT\n" +
                        "\tCELL_FLASH_CACHE DEFAULT\n" +
                        ")\n" +
                        "PARTITION BY RANGE (\"LRSJ\") (\n" +
                        "\tPARTITION \"T_RANGE_V1\" VALUES LESS THAN (TO_DATE(' 2015-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 80\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 553648128\n" +
                        "\t\t\tNEXT 16384\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"T_RANGE_V2\" VALUES LESS THAN (TO_DATE(' 2015-07-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 80\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 553648128\n" +
                        "\t\t\tNEXT 16384\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"T_RANGE_V3\" VALUES LESS THAN (TO_DATE(' 2016-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 80\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 553648128\n" +
                        "\t\t\tNEXT 16384\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"T_RANGE_V4\" VALUES LESS THAN (TO_DATE(' 2016-07-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 80\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 553648128\n" +
                        "\t\t\tNEXT 16384\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"T_RANGE_V5\" VALUES LESS THAN (TO_DATE(' 2017-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 80\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 553648128\n" +
                        "\t\t\tNEXT 16384\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"T_RANGE_V6\" VALUES LESS THAN (TO_DATE(' 2017-07-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))\n" +
                        "\t\tPCTFREE 80\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 553648128\n" +
                        "\t\t\tNEXT 16384\n" +
                        "\t\t\tMINEXTENTS 1\n" +
                        "\t\t\tMAXEXTENTS 2147483645\n" +
                        "\t\t\tPCTINCREASE 0\n" +
                        "\t\t\tFREELISTS 1\n" +
                        "\t\t\tFREELIST GROUPS 1\n" +
                        "\t\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t\t),\n" +
                        "\tPARTITION \"MAXPART\" VALUES LESS THAN (MAXVALUE)\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tPCTUSED 40\n" +
                        "\t\tINITRANS 1\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        "\t\tTABLESPACE \"RDP_DATA\"\n" +
                        "\t\tSTORAGE (\n" +
                        "\t\t\tINITIAL 8388608\n" +
                        "\t\t\tNEXT 8388608\n" +
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

        Assert.assertEquals(52, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
