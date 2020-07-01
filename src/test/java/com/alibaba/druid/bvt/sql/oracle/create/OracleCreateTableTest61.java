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

public class OracleCreateTableTest61 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SC_001\".\"TB_001\" \n" +
                "   (  \"ID\" NUMBER(11,0) NOT NULL ENABLE, \n" +
                "  \"GMT_CREATED\" DATE, \n" +
                "  \"GMT_MODIFIED\" DATE, \n" +
                "  \"CREATOR\" VARCHAR2(32), \n" +
                "  \"MODIFIER\" VARCHAR2(32), \n" +
                "  \"TEMPLATE_NAME\" VARCHAR2(64), \n" +
                "  \"MAIL_CATEGORY_ID\" NUMBER(11,0) NOT NULL ENABLE, \n" +
                "  \"DEFAULT_SUBJECT\" VARCHAR2(128), \n" +
                "  \"CONTENT_TYPE\" VARCHAR2(32) NOT NULL ENABLE, \n" +
                "  \"CONTENT\" CLOB, \n" +
                "  \"SHARE_FLAG\" VARCHAR2(32), \n" +
                "  \"STATUS\" VARCHAR2(32), \n" +
                "  \"DOUBT_IDS\" VARCHAR2(4000), \n" +
                "  \"PARA_1\" VARCHAR2(128), \n" +
                "  \"PARA_2\" VARCHAR2(128), \n" +
                "  \"PARA_3\" VARCHAR2(128), \n" +
                "   CONSTRAINT \"PK_ADMIN_MAIL_TEMPLATE\" PRIMARY KEY (\"ID\")\n" +
                "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS \n" +
                "  STORAGE(INITIAL 1048576 NEXT 131072 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"NIRVANA1M_IND\"  ENABLE, \n" +
                "   CONSTRAINT \"FK_ADMIN_MA_REFERENCE_ADMIN_MA\" FOREIGN KEY (\"MAIL_CATEGORY_ID\")\n" +
                "    REFERENCES \"NIRVANA\".\"ADMIN_MAIL_CATEGORY\" (\"ID\") ENABLE NOVALIDATE\n" +
                "   ) SEGMENT CREATION IMMEDIATE \n" +
                "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING\n" +
                "  STORAGE(INITIAL 1048576 NEXT 131072 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)\n" +
                "  TABLESPACE \"NIRVANA1M\" \n" +
                " LOB (\"CONTENT\") STORE AS BASICFILE (\n" +
                "  TABLESPACE \"NIRVANA1M\" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10\n" +
                "  NOCACHE LOGGING \n" +
                "  STORAGE(INITIAL 1048576 NEXT 131072 MINEXTENTS 1 MAXEXTENTS 2147483645\n" +
                "  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) \n   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SC_001\".\"TB_001\" (\n" +
                        "\t\"ID\" NUMBER(11, 0) NOT NULL ENABLE,\n" +
                        "\t\"GMT_CREATED\" DATE,\n" +
                        "\t\"GMT_MODIFIED\" DATE,\n" +
                        "\t\"CREATOR\" VARCHAR2(32),\n" +
                        "\t\"MODIFIER\" VARCHAR2(32),\n" +
                        "\t\"TEMPLATE_NAME\" VARCHAR2(64),\n" +
                        "\t\"MAIL_CATEGORY_ID\" NUMBER(11, 0) NOT NULL ENABLE,\n" +
                        "\t\"DEFAULT_SUBJECT\" VARCHAR2(128),\n" +
                        "\t\"CONTENT_TYPE\" VARCHAR2(32) NOT NULL ENABLE,\n" +
                        "\t\"CONTENT\" CLOB,\n" +
                        "\t\"SHARE_FLAG\" VARCHAR2(32),\n" +
                        "\t\"STATUS\" VARCHAR2(32),\n" +
                        "\t\"DOUBT_IDS\" VARCHAR2(4000),\n" +
                        "\t\"PARA_1\" VARCHAR2(128),\n" +
                        "\t\"PARA_2\" VARCHAR2(128),\n" +
                        "\t\"PARA_3\" VARCHAR2(128),\n" +
                        "\tCONSTRAINT \"PK_ADMIN_MAIL_TEMPLATE\" PRIMARY KEY (\"ID\")\n" +
                        "\t\tUSING INDEX\n" +
                        "\t\tPCTFREE 10\n" +
                        "\t\tINITRANS 2\n" +
                        "\t\tMAXTRANS 255\n" +
                        "\t\tTABLESPACE \"NIRVANA1M_IND\"\n" +
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
                        "\t\tCOMPUTE STATISTICS\n" +
                        "\t\tENABLE,\n" +
                        "\tCONSTRAINT \"FK_ADMIN_MA_REFERENCE_ADMIN_MA\" FOREIGN KEY (\"MAIL_CATEGORY_ID\")\n" +
                        "\t\tREFERENCES \"NIRVANA\".\"ADMIN_MAIL_CATEGORY\" (\"ID\") ENABLE\n" +
                        ")\n" +
                        "PCTFREE 10\n" +
                        "PCTUSED 40\n" +
                        "INITRANS 1\n" +
                        "MAXTRANS 255\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "TABLESPACE \"NIRVANA1M\"\n" +
                        "STORAGE (\n" +
                        "\tINITIAL 1048576\n" +
                        "\tNEXT 131072\n" +
                        "\tMINEXTENTS 1\n" +
                        "\tMAXEXTENTS 2147483645\n" +
                        "\tPCTINCREASE 0\n" +
                        "\tFREELISTS 1\n" +
                        "\tFREELIST GROUPS 1\n" +
                        "\tBUFFER_POOL DEFAULT\n" +
                        "\tFLASH_CACHE DEFAULT\n" +
                        "\tCELL_FLASH_CACHE DEFAULT\n" +
                        ")\n" +
                        "LOB (\"CONTENT\") STORE AS BASICFILE (\n" +
                        "\tLOGGING\n" +
                        "\tTABLESPACE \"NIRVANA1M\"\n" +
                        "\tSTORAGE (\n" +
                        "\t\tINITIAL 1048576\n" +
                        "\t\tNEXT 131072\n" +
                        "\t\tMINEXTENTS 1\n" +
                        "\t\tMAXEXTENTS 2147483645\n" +
                        "\t\tPCTINCREASE 0\n" +
                        "\t\tFREELISTS 1\n" +
                        "\t\tFREELIST GROUPS 1\n" +
                        "\t\tBUFFER_POOL DEFAULT\n" +
                        "\t\tFLASH_CACHE DEFAULT\n" +
                        "\t\tCELL_FLASH_CACHE DEFAULT\n" +
                        "\t)\n" +
                        "\tENABLE STORAGE IN ROW\n" +
                        "\tCHUNK 8192\n" +
                        "\tNOCACHE\n" +
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(17, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("SC_001.TB_001", "ID"));
    }
}
