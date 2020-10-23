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

public class OracleCreateTableTest88 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE TABLE \"FOGLIGHT\".\"Q_ALERT_LOG_ZJDBXE\"\n" +
                        "(   \"TEXT\" VARCHAR2(500)\n" +
                        ")\n" +
                        "ORGANIZATION EXTERNAL\n" +
                        "( TYPE ORACLE_LOADER\n" +
                        "DEFAULT DIRECTORY \"Q_ALERT_LOG_ZJDBXE_LOC\"\n" +
                        "ACCESS PARAMETERS\n" +
                        "( records delimited by newline nologfile nobadfile fields terminated by \"$\" ltrim REJECT ROWS WITH ALL NULL FIELDS )\n" +
                        "LOCATION\n" +
                        "( 'alert_zjdbxe.log'\n" +
                        ")\n" +
                        ")\n" +
                        "REJECT LIMIT UNLIMITED";

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
