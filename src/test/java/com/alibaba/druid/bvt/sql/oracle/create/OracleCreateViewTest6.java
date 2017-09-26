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
import org.junit.Assert;

import java.util.List;

public class OracleCreateViewTest6 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE OR REPLACE FORCE VIEW \"SC_001\".\"TV_001\" (\"ID\", \"GMT_CREATE\", \"CREATOR\", \"GMT_MODIFIED\", \"MODIFIER\", \"IS_DELETED\", \"CONTRACT_PARTY\", \"COMPANY_NAME\", \"PRODUCT_TYPE\", \"PERIOD\", \"JOIN\", \"OVER\", \"CONTRACT_TERM\", \"CONTRACT_CASH\", \"EXEC_CASH\", \"CANCELED_CASH\", \"RECEIVERD_CASH\", \"O_S_CASH\", \"REMARK\", \"FENTAN_CHECK\", \"TOTAL_CASH\", \"NOT_CONFIRMED\", \"PRE_RECEIVED\", \"NOT_RECEIVED\", \"CONTRACT_DATE\", \"OWNER_1\", \"AREA_ID_1\", \"FIRST_OWNER\", \"FIRST_AREA_ID\", \"CONTRACT_ID\", \"IS_RENEW_UPGRADE\", \"CATEGORY_ID_1\", \"CATEGORY_ID_2\", \"FIRST_RECEIPT_DATE\", \"RECEIPT_REMARK\", \"CONTRACT_SERIAL\", \"IS_MERGED\") AS \n" +
                "  SELECT\n" +
                "ID,\n" +
                "GMT_CREATE,\n" +
                "utl_raw.cast_to_raw(CREATOR) AS CREATOR,\n" +
                "GMT_MODIFIED,\n" +
                "utl_raw.cast_to_raw(MODIFIER) AS MODIFIER,\n" +
                "IS_DELETED,\n" +
                "utl_raw.cast_to_raw(CONTRACT_PARTY) AS CONTRACT_PARTY,\n" +
                "utl_raw.cast_to_raw(COMPANY_NAME) AS COMPANY_NAME,\n" +
                "utl_raw.cast_to_raw(PRODUCT_TYPE) AS PRODUCT_TYPE,\n" +
                "utl_raw.cast_to_raw(PERIOD) AS PERIOD,\n" +
                "JOIN,\n" +
                "OVER,\n" +
                "CONTRACT_TERM,\n" +
                "CONTRACT_CASH,\n" +
                "EXEC_CASH,\n" +
                "CANCELED_CASH,\n" +
                "RECEIVERD_CASH,\n" +
                "O_S_CASH,\n" +
                "utl_raw.cast_to_raw(REMARK) AS REMARK,\n" +
                "FENTAN_CHECK,\n" +
                "TOTAL_CASH,\n" +
                "NOT_CONFIRMED,\n" +
                "PRE_RECEIVED,\n" +
                "NOT_RECEIVED,\n" +
                "CONTRACT_DATE,\n" +
                "utl_raw.cast_to_raw(OWNER_1) AS OWNER_1,\n" +
                "AREA_ID_1,\n" +
                "utl_raw.cast_to_raw(FIRST_OWNER) AS FIRST_OWNER,\n" +
                "FIRST_AREA_ID,\n" +
                "utl_raw.cast_to_raw(CONTRACT_ID) AS CONTRACT_ID,\n" +
                "utl_raw.cast_to_raw(IS_RENEW_UPGRADE) AS IS_RENEW_UPGRADE,\n" +
                "CATEGORY_ID_1,\n" +
                "CATEGORY_ID_2,\n" +
                "FIRST_RECEIPT_DATE,\n" +
                "utl_raw.cast_to_raw(RECEIPT_REMARK) AS RECEIPT_REMARK,\n" +
                "utl_raw.cast_to_raw(CONTRACT_SERIAL) AS CONTRACT_SERIAL,\n" +
                "IS_MERGED\n" +
                "FROM TB_002  ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"SC_001\".\"TV_001\" (\n" +
                        "\t\"ID\", \n" +
                        "\t\"GMT_CREATE\", \n" +
                        "\t\"CREATOR\", \n" +
                        "\t\"GMT_MODIFIED\", \n" +
                        "\t\"MODIFIER\", \n" +
                        "\t\"IS_DELETED\", \n" +
                        "\t\"CONTRACT_PARTY\", \n" +
                        "\t\"COMPANY_NAME\", \n" +
                        "\t\"PRODUCT_TYPE\", \n" +
                        "\t\"PERIOD\", \n" +
                        "\t\"JOIN\", \n" +
                        "\t\"OVER\", \n" +
                        "\t\"CONTRACT_TERM\", \n" +
                        "\t\"CONTRACT_CASH\", \n" +
                        "\t\"EXEC_CASH\", \n" +
                        "\t\"CANCELED_CASH\", \n" +
                        "\t\"RECEIVERD_CASH\", \n" +
                        "\t\"O_S_CASH\", \n" +
                        "\t\"REMARK\", \n" +
                        "\t\"FENTAN_CHECK\", \n" +
                        "\t\"TOTAL_CASH\", \n" +
                        "\t\"NOT_CONFIRMED\", \n" +
                        "\t\"PRE_RECEIVED\", \n" +
                        "\t\"NOT_RECEIVED\", \n" +
                        "\t\"CONTRACT_DATE\", \n" +
                        "\t\"OWNER_1\", \n" +
                        "\t\"AREA_ID_1\", \n" +
                        "\t\"FIRST_OWNER\", \n" +
                        "\t\"FIRST_AREA_ID\", \n" +
                        "\t\"CONTRACT_ID\", \n" +
                        "\t\"IS_RENEW_UPGRADE\", \n" +
                        "\t\"CATEGORY_ID_1\", \n" +
                        "\t\"CATEGORY_ID_2\", \n" +
                        "\t\"FIRST_RECEIPT_DATE\", \n" +
                        "\t\"RECEIPT_REMARK\", \n" +
                        "\t\"CONTRACT_SERIAL\", \n" +
                        "\t\"IS_MERGED\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT ID, GMT_CREATE, utl_raw.cast_to_raw(CREATOR) AS CREATOR, GMT_MODIFIED\n" +
                        "\t, utl_raw.cast_to_raw(MODIFIER) AS MODIFIER, IS_DELETED\n" +
                        "\t, utl_raw.cast_to_raw(CONTRACT_PARTY) AS CONTRACT_PARTY, utl_raw.cast_to_raw(COMPANY_NAME) AS COMPANY_NAME\n" +
                        "\t, utl_raw.cast_to_raw(PRODUCT_TYPE) AS PRODUCT_TYPE, utl_raw.cast_to_raw(PERIOD) AS PERIOD\n" +
                        "\t, JOIN, OVER, CONTRACT_TERM, CONTRACT_CASH, EXEC_CASH\n" +
                        "\t, CANCELED_CASH, RECEIVERD_CASH, O_S_CASH, utl_raw.cast_to_raw(REMARK) AS REMARK\n" +
                        "\t, FENTAN_CHECK, TOTAL_CASH, NOT_CONFIRMED, PRE_RECEIVED, NOT_RECEIVED\n" +
                        "\t, CONTRACT_DATE, utl_raw.cast_to_raw(OWNER_1) AS OWNER_1, AREA_ID_1\n" +
                        "\t, utl_raw.cast_to_raw(FIRST_OWNER) AS FIRST_OWNER, FIRST_AREA_ID\n" +
                        "\t, utl_raw.cast_to_raw(CONTRACT_ID) AS CONTRACT_ID, utl_raw.cast_to_raw(IS_RENEW_UPGRADE) AS IS_RENEW_UPGRADE\n" +
                        "\t, CATEGORY_ID_1, CATEGORY_ID_2, FIRST_RECEIPT_DATE, utl_raw.cast_to_raw(RECEIPT_REMARK) AS RECEIPT_REMARK\n" +
                        "\t, utl_raw.cast_to_raw(CONTRACT_SERIAL) AS CONTRACT_SERIAL, IS_MERGED\n" +
                        "FROM TB_002",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(37, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("TB_002", "ID")));
    }
}
