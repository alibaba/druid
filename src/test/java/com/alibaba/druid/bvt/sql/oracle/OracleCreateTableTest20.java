/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest20 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE TABLE \"SONAR\".\"ACTIVE_RULE_NOTES\" " //
                + "   (    \"ID\" NUMBER(38,0) NOT NULL ENABLE, " //
                + "    \"CREATED_AT\" TIMESTAMP (6), " //
                + "    \"UPDATED_AT\" TIMESTAMP (6), " //
                + "    \"ACTIVE_RULE_ID\" NUMBER(38,0), " //
                + "    \"USER_LOGIN\" VARCHAR2(40 BYTE), " //
                + "    \"DATA\" BLOB, " //
                + "     PRIMARY KEY (\"ID\")" //
                + "  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS " //
                + "  TABLESPACE \"USERS\"  ENABLE" //
                + "   ) SEGMENT CREATION DEFERRED " //
                + "  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 " //
                + " NOCOMPRESS LOGGING" //
                + "  TABLESPACE \"USERS\" " //
                + " LOB (\"DATA\") STORE AS SECUREFILE (" //
                + "  TABLESPACE \"USERS\" ENABLE STORAGE IN ROW CHUNK 8192" //
                + "  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES ) ;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE \"SONAR\".\"ACTIVE_RULE_NOTES\" (" //
                                    + "\n\t\"ID\" NUMBER(38, 0) NOT NULL ENABLE,"//
                                    + "\n\t\"CREATED_AT\" TIMESTAMP(6),"//
                                    + "\n\t\"UPDATED_AT\" TIMESTAMP(6),"//
                                    + "\n\t\"ACTIVE_RULE_ID\" NUMBER(38, 0),"//
                                    + "\n\t\"USER_LOGIN\" VARCHAR2(40 BYTE),"//
                                    + "\n\t\"DATA\" BLOB,"//
                                    + "\n\tPRIMARY KEY (\"ID\")"//
                                    + "\n\tUSING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS TABLESPACE \"USERS\" ENABLE"//
                                    + "\n)"//
                                    + "\nPCTFREE 10"//
                                    + "\nINITRANS 1"//
                                    + "\nMAXTRANS 255"//
                                    + "\nNOCOMPRESS"//
                                    + "\nLOGGING"//
                                    + "\nTABLESPACE \"USERS\""//
                                    + "\nLOB (\"DATA\") STORE AS SECUREFILE (TABLESPACE \"USERS\" ENABLE STORAGE IN ROW CHUNK 8192 NOCACHE LOGGING NOCOMPRESS KEEP_DUPLICATES)",//
                            SQLUtils.toSQLString(statement, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(6, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("SONAR.ACTIVE_RULE_NOTES", "ID")));
    }
}
