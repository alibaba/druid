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

public class OracleCreateViewTest3 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "    CREATE OR REPLACE FORCE VIEW \"RMAN\".\"V_001\" (\"DB_KEY\", \"DBINC_KEY\", \"FILE#\", \"BLOCK#\", \"BLOCKS\", \"CORRUPTION_CHANGE#\", \"CORRUPTION_TYPE\") AS \n" +
                "  select distinct\n" +
                "  db_key, dbinc_key, file#, block#, blocks, corruption_change#,\n" +
                "  corruption_type from\n" +
                "  (select db_key, dbinc_key, file#, block#, blocks,\n" +
                "          corruption_change#, copy_stamp stamp, corruption_type\n" +
                "   from tb_004 union\n" +
                "   select bs.db_key, dbinc_key, file#, block#, blocks,\n" +
                "          corruption_change#, bs.stamp, corruption_type\n" +
                "   from tb_005 bc, tb_006 bs\n" +
                "   where bc.bs_key = bs.bs_key) outer\n" +
                "where not exists\n" +
                "  (select 1\n" +
                "   from tb_001\n" +
                "   where outer.db_key = db_key and\n" +
                "         outer.dbinc_key = dbinc_key and\n" +
                "         scanned = 'YES' and\n" +
                "         outer.file# = file# and\n" +
                "         outer.stamp < stamp\n" +
                "   union\n" +
                "   select 1\n" +
                "   from tb_002 bdf, tb_003 bs\n" +
                "   where bdf.bs_key = bs.bs_key and\n" +
                "         outer.db_key = bdf.db_key and\n" +
                "         outer.dbinc_key = bdf.dbinc_key and\n" +
                "         outer.file# = file# and\n" +
                "         outer.stamp < bs.stamp)    ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"RMAN\".\"V_001\" (\n" +
                        "\t\"DB_KEY\", \n" +
                        "\t\"DBINC_KEY\", \n" +
                        "\t\"FILE#\", \n" +
                        "\t\"BLOCK#\", \n" +
                        "\t\"BLOCKS\", \n" +
                        "\t\"CORRUPTION_CHANGE#\", \n" +
                        "\t\"CORRUPTION_TYPE\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT DISTINCT db_key, dbinc_key, file#, block#, blocks\n" +
                        "\t, corruption_change#, corruption_type\n" +
                        "FROM (\n" +
                        "\tSELECT db_key, dbinc_key, file#, block#, blocks\n" +
                        "\t\t, corruption_change#, copy_stamp AS stamp, corruption_type\n" +
                        "\tFROM tb_004\n" +
                        "\tUNION\n" +
                        "\tSELECT bs.db_key, dbinc_key, file#, block#, blocks\n" +
                        "\t\t, corruption_change#, bs.stamp, corruption_type\n" +
                        "\tFROM tb_005 bc, tb_006 bs\n" +
                        "\tWHERE bc.bs_key = bs.bs_key\n" +
                        ") outer\n" +
                        "WHERE NOT EXISTS (\n" +
                        "\tSELECT 1\n" +
                        "\tFROM tb_001\n" +
                        "\tWHERE outer.db_key = db_key\n" +
                        "\t\tAND outer.dbinc_key = dbinc_key\n" +
                        "\t\tAND scanned = 'YES'\n" +
                        "\t\tAND outer.file# = file#\n" +
                        "\t\tAND outer.stamp < stamp\n" +
                        "\tUNION\n" +
                        "\tSELECT 1\n" +
                        "\tFROM tb_002 bdf, tb_003 bs\n" +
                        "\tWHERE bdf.bs_key = bs.bs_key\n" +
                        "\t\tAND outer.db_key = bdf.db_key\n" +
                        "\t\tAND outer.dbinc_key = bdf.dbinc_key\n" +
                        "\t\tAND outer.file# = file#\n" +
                        "\t\tAND outer.stamp < bs.stamp\n" +
                        ")",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(6, visitor.getTables().size());

        Assert.assertEquals(28, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("tb_002", "db_key")));
    }
}
