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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTableTest28 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE TABLE divisions  " //
                + "   (div_no    NUMBER  CONSTRAINT check_divno" //
                + "              CHECK (div_no BETWEEN 10 AND 99) " //
                + "              DISABLE, " //
                + "    div_name  VARCHAR2(9)  CONSTRAINT check_divname" //
                + "              CHECK (div_name = UPPER(div_name)) " //
                + "              DISABLE, " //
                + "    office    VARCHAR2(10)  CONSTRAINT check_office" //
                + "              CHECK (office IN ('DALLAS','BOSTON'," //
                + "              'PARIS','TOKYO')) " //
                + "              DISABLE); ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE divisions (" //
                + "\n\tdiv_no NUMBER" //
                + "\n\t\tCONSTRAINT check_divno CHECK (div_no BETWEEN 10 AND 99) DISABLE," //
                + "\n\tdiv_name VARCHAR2(9)" //
                + "\n\t\tCONSTRAINT check_divname CHECK (div_name = UPPER(div_name)) DISABLE," //
                + "\n\toffice VARCHAR2(10)" //
                + "\n\t\tCONSTRAINT check_office CHECK (office IN ('DALLAS', 'BOSTON', 'PARIS', 'TOKYO')) DISABLE" //
                + "\n);",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(3, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("divisions", "div_no")));
    }
}
