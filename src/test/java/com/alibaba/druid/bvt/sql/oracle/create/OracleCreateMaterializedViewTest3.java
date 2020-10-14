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

public class OracleCreateMaterializedViewTest3 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "DROP MATERIALIZED VIEW ATOM_MVIEW.NONAUTO_CLAIM_FOLDER_T;\n" +
                "--create materualized_view\n" +
                "CREATE MATERIALIZED VIEW ATOM_MVIEW.NONAUTO_CLAIM_FOLDER_T\n" +
                "PARTITION BY RANGE (BRANCH_COMPANY_CODE)\n" +
                "(  \n" +
                "  PARTITION P1010100 VALUES LESS THAN ('1020100')\n" +
                "    LOGGING\n" +
                "    NOCOMPRESS ,  \n" +
                "  PARTITION P1020100 VALUES LESS THAN ('1030100')\n" +
                "    LOGGING\n" +
                "    NOCOMPRESS ,  \n" +
                "  PARTITION P7040100 VALUES LESS THAN ('7050100')\n" +
                "    LOGGING\n" +
                "    NOCOMPRESS ,  \n" +
                "  PARTITION P7050100 VALUES LESS THAN ('7060100')\n" +
                "    LOGGING\n" +
                "    NOCOMPRESS ,  \n" +
                "  PARTITION P7060100 VALUES LESS THAN (MAXVALUE)\n" +
                "    LOGGING\n" +
                "    NOCOMPRESS\n" +
                ")\n" +
                "NOCACHE\n" +
                "LOGGING\n" +
                "NOCOMPRESS\n" +
                "NOPARALLEL\n" +
                "BUILD DEFERRED\n" +
                "REFRESH FAST ON DEMAND\n" +
                "WITH ROWID\n" +
                "AS SELECT * FROM atom.NONAUTO_CLAIM_FOLDER_T@IDS_44;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(1);
        print(statementList);

        assertEquals(2, statementList.size());

        assertEquals("CREATE MATERIALIZED VIEW ATOM_MVIEW.NONAUTO_CLAIM_FOLDER_T\n" +
                        "PARTITION BY RANGE (BRANCH_COMPANY_CODE) (\n" +
                        "\tPARTITION P1010100 VALUES LESS THAN ('1020100')\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING,\n" +
                        "\tPARTITION P1020100 VALUES LESS THAN ('1030100')\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING,\n" +
                        "\tPARTITION P7040100 VALUES LESS THAN ('7050100')\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING,\n" +
                        "\tPARTITION P7050100 VALUES LESS THAN ('7060100')\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING,\n" +
                        "\tPARTITION P7060100 VALUES LESS THAN (MAXVALUE)\n" +
                        "\t\tNOCOMPRESS\n" +
                        "\t\tLOGGING\n" +
                        ")\n" +
                        "NOCOMPRESS\n" +
                        "LOGGING\n" +
                        "NOCACHE\n" +
                        "NOPARALLEL\n" +
                        "REFRESH FAST ON DEMAND\n" +
                        "AS\n" +
                        "SELECT *\n" +
                        "FROM atom.NONAUTO_CLAIM_FOLDER_T@IDS_44;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(1, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("times", "calendar_month_desc")));
    }
}
