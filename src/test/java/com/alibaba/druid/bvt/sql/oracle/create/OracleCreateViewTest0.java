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

public class OracleCreateViewTest0 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE OR REPLACE FORCE VIEW \"SC0\".\"V_001\" (\"ID\", \"GROUP_ID\", \"IND_BY_ALL\", \"IND_BY_GROUP\", \"OWNER_MEMBER_ID\", \"OWNER_MEMBER_SEQ\", \"GMT_MODIFIED\") AS \n" +
                "  select id, GROUP_ID ,IND_BY_ALL, IND_BY_GROUP, OWNER_MEMBER_ID, OWNER_MEMBER_SEQ,gmt_modified     ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"SC0\".\"V_001\" (\n" +
                        "\t\"ID\", \n" +
                        "\t\"GROUP_ID\", \n" +
                        "\t\"IND_BY_ALL\", \n" +
                        "\t\"IND_BY_GROUP\", \n" +
                        "\t\"OWNER_MEMBER_ID\", \n" +
                        "\t\"OWNER_MEMBER_SEQ\", \n" +
                        "\t\"GMT_MODIFIED\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT id, GROUP_ID, IND_BY_ALL, IND_BY_GROUP, OWNER_MEMBER_ID\n" +
                        "\t, OWNER_MEMBER_SEQ, gmt_modified\n" +
                        "FROM DUAL",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(0, visitor.getTables().size());

//        Assert.assertEquals(0, visitor.getColumns().size());

//        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("customers_part", "customer_id")));
    }
}
