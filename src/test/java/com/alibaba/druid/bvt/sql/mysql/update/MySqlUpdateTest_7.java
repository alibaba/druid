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
package com.alibaba.druid.bvt.sql.mysql.update;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlUpdateTest_7 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "UPDATE ROLLBACK_ON_FAIL TARGET_AFFECT_ROW 1 "
                + "`table_3966` AS `table_3966_11` SET `version` = `version` + 3, `gmt_modified` = NOW(), `optype` = ?, `feature` = ? "
                + "WHERE `sub_biz_order_id` = ? AND `biz_order_type` = ? AND `id` = ? AND `ti_id` = ? AND `optype` = ? AND `root_id` = ?";

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
        Assert.assertEquals(9, visitor.getColumns().size());
        Assert.assertEquals(6, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("table_3966")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("table_3966", "sub_biz_order_id")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("table_3966", "feature")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("table_3966", "id")));

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("UPDATE ROLLBACK_ON_FAIL TARGET_AFFECT_ROW 1 `table_3966` `table_3966_11`"
                    + "\nSET `version` = `version` + 3, `gmt_modified` = NOW(), `optype` = ?, `feature` = ?"
                    + "\nWHERE `sub_biz_order_id` = ?"
                    + "\n\tAND `biz_order_type` = ?"
                    + "\n\tAND `id` = ?"
                    + "\n\tAND `ti_id` = ?"
                    + "\n\tAND `optype` = ?"
                    + "\n\tAND `root_id` = ?", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("update rollback_on_fail target_affect_row 1 `table_3966` `table_3966_11`"
                    + "\nset `version` = `version` + 3, `gmt_modified` = NOW(), `optype` = ?, `feature` = ?"
                    + "\nwhere `sub_biz_order_id` = ?"
                    + "\n\tand `biz_order_type` = ?"
                    + "\n\tand `id` = ?"
                    + "\n\tand `ti_id` = ?"
                    + "\n\tand `optype` = ?"
                    + "\n\tand `root_id` = ?", //
                                output);
        }
    }
}
