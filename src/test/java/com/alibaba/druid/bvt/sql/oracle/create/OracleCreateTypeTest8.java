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
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateTypeTest8 extends OracleTest {

    public void test_types() throws Exception {
        String sql = "CREATE OR REPLACE TYPE dmbanimp AUTHID CURRENT_USER AS OBJECT (\n" +
                "  key RAW(4),\n" +
                "  shared_ctx RAW(8),\n" +
                "  STATIC FUNCTION ODCITablePrepare (\n" +
                "                    sctx                  IN OUT dmbanimp,\n" +
                "                    tf_info                   SYS.ODCITabFuncInfo,\n" +
                "                    qkn_ptr               IN  RAW,\n" +
                "                    heap_ptr              IN  RAW,\n" +
                "                    query_sequence        IN  CLOB,\n" +
                "                    seqdb_cursor              SYS_REFCURSOR,\n" +
                "                    subsequence_from      IN  PLS_INTEGER  DEFAULT 1,\n" +
                "                    subsequence_to        IN  PLS_INTEGER  DEFAULT -1,\n" +
                "                    filter_low_complexity IN  PLS_INTEGER  DEFAULT 0,\n" +
                "                    mask_lower_case       IN  PLS_INTEGER  DEFAULT 0,\n" +
                "                    expect_value          IN  NUMBER       DEFAULT 10,\n" +
                "                    open_gap_cost         IN  PLS_INTEGER  DEFAULT 5,\n" +
                "                    extend_gap_cost       IN  PLS_INTEGER  DEFAULT 2,\n" +
                "                    mismatch_cost         IN  PLS_INTEGER  DEFAULT -3,\n" +
                "                    match_reward          IN  PLS_INTEGER  DEFAULT 1,\n" +
                "                    word_size             IN  PLS_INTEGER  DEFAULT 11,\n" +
                "                    xdropoff              IN  PLS_INTEGER  DEFAULT 30,\n" +
                "                    final_x_dropoff       IN  PLS_INTEGER  DEFAULT 50\n" +
                "                  ) RETURN PLS_INTEGER,\n" +
                "  STATIC FUNCTION ODCITableStart (\n" +
                "                    sctx                  IN OUT dmbanimp,\n" +
                "                    rws_ptr               IN  RAW,\n" +
                "                    query_sequence        IN  CLOB,\n" +
                "                    seqdb_cursor              SYS_REFCURSOR,\n" +
                "                    subsequence_from      IN  PLS_INTEGER  DEFAULT 1,\n" +
                "                    subsequence_to        IN  PLS_INTEGER  DEFAULT -1,\n" +
                "                    filter_low_complexity IN  PLS_INTEGER  DEFAULT 0,\n" +
                "                    mask_lower_case       IN  PLS_INTEGER  DEFAULT 0,\n" +
                "                    expect_value          IN  NUMBER       DEFAULT 10,\n" +
                "                    open_gap_cost         IN  PLS_INTEGER  DEFAULT 5,\n" +
                "                    extend_gap_cost       IN  PLS_INTEGER  DEFAULT 2,\n" +
                "                    mismatch_cost         IN  PLS_INTEGER  DEFAULT -3,\n" +
                "                    match_reward          IN  PLS_INTEGER  DEFAULT 1,\n" +
                "                    word_size             IN  PLS_INTEGER  DEFAULT 11,\n" +
                "                    xdropoff              IN  PLS_INTEGER  DEFAULT 30,\n" +
                "                    final_x_dropoff       IN  PLS_INTEGER  DEFAULT 50\n" +
                "                  ) RETURN NUMBER,\n" +
                "  MEMBER FUNCTION ODCITableFetch (\n" +
                "                    self             IN OUT dmbanimp,\n" +
                "                    nrows            IN     NUMBER,\n" +
                "                    outset           OUT    dmbaos\n" +
                "                  ) RETURN NUMBER,\n" +
                "  MEMBER FUNCTION ODCITableClose (\n" +
                "                    self             IN  dmbanimp\n" +
                "                                 ) RETURN NUMBER,\n" +
                "  STATIC FUNCTION dmbanPrepareStub (\n" +
                "                    sctx                  IN OUT dmbanimp,\n" +
                "                    tf_info                  SYS.ODCITabFuncInfo,\n" +
                "                    qkn_ptr               IN RAW,\n" +
                "                    heap_ptr              IN RAW,\n" +
                "                    query_sequence        IN CLOB,\n" +
                "                    seqdb_cursor             SYS_REFCURSOR,\n" +
                "                    subsequence_from      IN PLS_INTEGER,\n" +
                "                    subsequence_to        IN PLS_INTEGER,\n" +
                "                    filter_low_complexity IN PLS_INTEGER,\n" +
                "                    mask_lower_case       IN PLS_INTEGER,\n" +
                "                    expect_value          IN NUMBER,\n" +
                "                    open_gap_cost         IN PLS_INTEGER,\n" +
                "                    extend_gap_cost       IN PLS_INTEGER,\n" +
                "                    mismatch_cost         IN PLS_INTEGER,\n" +
                "                    match_reward          IN PLS_INTEGER,\n" +
                "                    word_size             IN PLS_INTEGER,\n" +
                "                    xdropoff              IN PLS_INTEGER,\n" +
                "                    final_x_dropoff       IN PLS_INTEGER\n" +
                "                  ) RETURN PLS_INTEGER,\n" +
                "  STATIC FUNCTION dmbanStartStub (\n" +
                "                    sctx                  IN OUT dmbanimp,\n" +
                "                    rws_ptr               IN  RAW,\n" +
                "                    query_sequence        IN CLOB,\n" +
                "                    seqdb_cursor             SYS_REFCURSOR,\n" +
                "                    subsequence_from      IN PLS_INTEGER,\n" +
                "                    subsequence_to        IN PLS_INTEGER,\n" +
                "                    filter_low_complexity IN PLS_INTEGER,\n" +
                "                    mask_lower_case       IN PLS_INTEGER,\n" +
                "                    expect_value          IN NUMBER,\n" +
                "                    open_gap_cost         IN PLS_INTEGER,\n" +
                "                    extend_gap_cost       IN PLS_INTEGER,\n" +
                "                    mismatch_cost         IN PLS_INTEGER,\n" +
                "                    match_reward          IN PLS_INTEGER,\n" +
                "                    word_size             IN PLS_INTEGER,\n" +
                "                    xdropoff              IN PLS_INTEGER,\n" +
                "                    final_x_dropoff       IN PLS_INTEGER\n" +
                "                  ) RETURN NUMBER,\n" +
                "  MEMBER FUNCTION dmbanFetchStub (\n" +
                "                    self             IN OUT dmbanimp,\n" +
                "                    nrows            IN     NUMBER,\n" +
                "                    outset           OUT    dmbaos\n" +
                "                  ) RETURN NUMBER,\n" +
                "  MEMBER FUNCTION dmbanCloseStub (\n" +
                "                    self             IN dmbanimp\n" +
                "                  ) RETURN NUMBER\n" +
                ");";


        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE TYPE dmbanimp AUTHID CURRENT_USER AS OBJECT (\n" +
                        "\tkey RAW(4), \n" +
                        "\tshared_ctx RAW(8), \n" +
                        "\tSTATIC FUNCTION ODCITablePrepare (sctx IN OUT dmbanimp, tf_info SYS.ODCITabFuncInfo, qkn_ptr IN RAW, heap_ptr IN RAW, query_sequence IN CLOB, seqdb_cursor SYS_REFCURSOR, subsequence_from IN PLS_INTEGER := 1, subsequence_to IN PLS_INTEGER := -1, filter_low_complexity IN PLS_INTEGER := 0, mask_lower_case IN PLS_INTEGER := 0, expect_value IN NUMBER := 10, open_gap_cost IN PLS_INTEGER := 5, extend_gap_cost IN PLS_INTEGER := 2, mismatch_cost IN PLS_INTEGER := -3, match_reward IN PLS_INTEGER := 1, word_size IN PLS_INTEGER := 11, xdropoff IN PLS_INTEGER := 30, final_x_dropoff IN PLS_INTEGER := 50) RETURN PLS_INTEGER, \n" +
                        "\tSTATIC FUNCTION ODCITableStart (sctx IN OUT dmbanimp, rws_ptr IN RAW, query_sequence IN CLOB, seqdb_cursor SYS_REFCURSOR, subsequence_from IN PLS_INTEGER := 1, subsequence_to IN PLS_INTEGER := -1, filter_low_complexity IN PLS_INTEGER := 0, mask_lower_case IN PLS_INTEGER := 0, expect_value IN NUMBER := 10, open_gap_cost IN PLS_INTEGER := 5, extend_gap_cost IN PLS_INTEGER := 2, mismatch_cost IN PLS_INTEGER := -3, match_reward IN PLS_INTEGER := 1, word_size IN PLS_INTEGER := 11, xdropoff IN PLS_INTEGER := 30, final_x_dropoff IN PLS_INTEGER := 50) RETURN NUMBER, \n" +
                        "\tMEMBER FUNCTION ODCITableFetch (self IN OUT dmbanimp, nrows IN NUMBER, outset OUT dmbaos) RETURN NUMBER, \n" +
                        "\tMEMBER FUNCTION ODCITableClose (self IN dmbanimp) RETURN NUMBER, \n" +
                        "\tSTATIC FUNCTION dmbanPrepareStub (sctx IN OUT dmbanimp, tf_info SYS.ODCITabFuncInfo, qkn_ptr IN RAW, heap_ptr IN RAW, query_sequence IN CLOB, seqdb_cursor SYS_REFCURSOR, subsequence_from IN PLS_INTEGER, subsequence_to IN PLS_INTEGER, filter_low_complexity IN PLS_INTEGER, mask_lower_case IN PLS_INTEGER, expect_value IN NUMBER, open_gap_cost IN PLS_INTEGER, extend_gap_cost IN PLS_INTEGER, mismatch_cost IN PLS_INTEGER, match_reward IN PLS_INTEGER, word_size IN PLS_INTEGER, xdropoff IN PLS_INTEGER, final_x_dropoff IN PLS_INTEGER) RETURN PLS_INTEGER, \n" +
                        "\tSTATIC FUNCTION dmbanStartStub (sctx IN OUT dmbanimp, rws_ptr IN RAW, query_sequence IN CLOB, seqdb_cursor SYS_REFCURSOR, subsequence_from IN PLS_INTEGER, subsequence_to IN PLS_INTEGER, filter_low_complexity IN PLS_INTEGER, mask_lower_case IN PLS_INTEGER, expect_value IN NUMBER, open_gap_cost IN PLS_INTEGER, extend_gap_cost IN PLS_INTEGER, mismatch_cost IN PLS_INTEGER, match_reward IN PLS_INTEGER, word_size IN PLS_INTEGER, xdropoff IN PLS_INTEGER, final_x_dropoff IN PLS_INTEGER) RETURN NUMBER, \n" +
                        "\tMEMBER FUNCTION dmbanFetchStub (self IN OUT dmbanimp, nrows IN NUMBER, outset OUT dmbaos) RETURN NUMBER, \n" +
                        "\tMEMBER FUNCTION dmbanCloseStub (self IN dmbanimp) RETURN NUMBER\n" +
                        ");",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(0, visitor.getTables().size());

        assertEquals(0, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
