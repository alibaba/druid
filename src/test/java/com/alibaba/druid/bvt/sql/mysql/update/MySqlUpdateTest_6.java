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
package com.alibaba.druid.bvt.sql.mysql.update;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;

import java.util.List;

public class MySqlUpdateTest_6 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT id, user_id, item_id, sku_id, flag, sellable_quantity, fff03, status, feature, feature_lock, version, gmt_create, gmt_modified, root_id, parent_id, dist_type, dist_id, occupy_quantity, user_type\n" +
                "FROM UPDATE COMMIT_ON_SUCCESS ROLLBACK_ON_FAIL TARGET_AFFECT_ROW ? `table_3966`\n" +
                "SET `fff03` = `fff03` + ?, `flag` = `flag` & (~(1 << 10)) & ~(1 << 11), `version` = `version` + 3, `gmt_modified` = NOW()\n" +
                "WHERE `root_id` = ?\n" +
                "\tAND `status` = 1\n" +
                "\tAND `id` IN (?, ?)\n" +
                "\tAND `fff03` + ? >= 0";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(19, visitor.getColumns().size());
        Assert.assertEquals(3, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("table_3966")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("table_3966", "user_id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("table_3966", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("table_3966", "item_id")));

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("SELECT id, user_id, item_id, sku_id, flag, sellable_quantity, fff03, status, feature, feature_lock, version, gmt_create, gmt_modified, root_id, parent_id, dist_type, dist_id, occupy_quantity, user_type\n" +
                            "FROM UPDATE COMMIT_ON_SUCCESS ROLLBACK_ON_FAIL TARGET_AFFECT_ROW ? `table_3966`\n" +
                            "SET `fff03` = `fff03` + ?, `flag` = `flag` & (~(1 << 10)) & ~(1 << 11), `version` = `version` + 3, `gmt_modified` = NOW()\n" +
                            "WHERE `root_id` = ?\n" +
                            "\tAND `status` = 1\n" +
                            "\tAND `id` IN (?, ?)\n" +
                            "\tAND `fff03` + ? >= 0", //
                    output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("select id, user_id, item_id, sku_id, flag, sellable_quantity, fff03, status, feature, feature_lock, version, gmt_create, gmt_modified, root_id, parent_id, dist_type, dist_id, occupy_quantity, user_type\n" +
                            "from update commit_on_success rollback_on_fail target_affect_row ? `table_3966`\n" +
                            "set `fff03` = `fff03` + ?, `flag` = `flag` & (~(1 << 10)) & ~(1 << 11), `version` = `version` + 3, `gmt_modified` = NOW()\n" +
                            "where `root_id` = ?\n" +
                            "\tand `status` = 1\n" +
                            "\tand `id` in (?, ?)\n" +
                            "\tand `fff03` + ? >= 0", //
                    output);
        }
    }
}
