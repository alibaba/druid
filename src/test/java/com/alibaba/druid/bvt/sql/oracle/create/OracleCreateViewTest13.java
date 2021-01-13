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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateViewTest13 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "SELECT rbi.rma_id, rbi.last_update_time\n" +
                "FROM ktv_epm.ktv_eq_rprboarditem rbi\n" +
                "JOIN ktv_epm.ktv_eq_rma_header rma ON rma.rma_id = rbi.rma_id \n" +
                "\tJOIN ktv_epm.ktv_eq_rprsenditem rsi ON rsi.e_rprboardid = rbi.e_rprboardid\n" +
                "AND rsi.e_boardid = rbi.e_boardid \n" +
                "WHERE rbi.rma_id > ?\n" +
                "\tAND rsi.e_rprsendid = ?\n" +
                "\tAND rsi.e_boardid IN (?)\n" +
                "FOR UPDATE(rbi.rma_id)\n"
               ;

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("SELECT rbi.rma_id, rbi.last_update_time\n" +
                        "FROM ktv_epm.ktv_eq_rprboarditem rbi\n" +
                        "JOIN ktv_epm.ktv_eq_rma_header rma ON rma.rma_id = rbi.rma_id \n" +
                        "\tJOIN ktv_epm.ktv_eq_rprsenditem rsi ON rsi.e_rprboardid = rbi.e_rprboardid\n" +
                        "\tAND rsi.e_boardid = rbi.e_boardid \n" +
                        "WHERE rbi.rma_id > ?\n" +
                        "\tAND rsi.e_rprsendid = ?\n" +
                        "\tAND rsi.e_boardid IN (?)\n" +
                        "FOR UPDATE(rbi.rma_id)",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());

        assertEquals(8, visitor.getColumns().size());

        assertTrue(visitor.getColumns().contains(new TableStat.Column("ktv_epm.ktv_eq_rma_header", "rma_id")));
    }
}
