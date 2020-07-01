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

public class OracleCreateTableTest67 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        " CREATE TABLE new_duplications_index (\"ID\" PRIMARY KEY, project_snapshot_id, snapshot_id, hash, index_in_file, start_line, end_line) NOLOGGING AS SELECT CAST(duplications_index_seq.nextval AS NUMBER(38)) \"ID\", project_snapshot_id, snapshot_id, hash, index_in_file, start_line, end_line FROM duplications_index  ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TABLE new_duplications_index (\n" +
                        "\t\"ID\" PRIMARY KEY,\n" +
                        "\tproject_snapshot_id,\n" +
                        "\tsnapshot_id,\n" +
                        "\thash,\n" +
                        "\tindex_in_file,\n" +
                        "\tstart_line,\n" +
                        "\tend_line\n" +
                        ")\n" +
                        "NOLOGGING\n" +
                        "AS\n" +
                        "SELECT CAST(duplications_index_seq.NEXTVAL AS NUMBER(38)) AS \"ID\", project_snapshot_id, snapshot_id, hash, index_in_file\n" +
                        "\t, start_line, end_line\n" +
                        "FROM duplications_index",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(13, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("duplications_index", "project_snapshot_id")));
    }
}
