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

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.wall.WallUtils;
import org.junit.Assert;

import java.util.List;

public class MySqlUpdateTest_11_using extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "update t1, t2, t3 inner join t4 using (col_name1, col_name2)\n" +
                "set t1.value_col = t3.new_value_col, t4.`some-col*` = `t2`.`***` * 2\n" +
                "where  t1.pk = t2.fk_t1_pk and t2.id = t4.fk_id_entity;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(4, visitor.getTables().size());
        Assert.assertEquals(8, visitor.getColumns().size());
        // Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.containsTable("t4"));

        Assert.assertTrue(visitor.getColumns().contains(new Column("t1", "value_col")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t1", "pk")));

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("UPDATE (t1, t2, t3)\n" +
                            "\tINNER JOIN t4 USING (col_name1, col_name2)\n" +
                            "SET t1.value_col = t3.new_value_col, t4.`some-col*` = `t2`.`***` * 2\n" +
                            "WHERE t1.pk = t2.fk_t1_pk\n" +
                            "\tAND t2.id = t4.fk_id_entity;", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("update (t1, t2, t3)\n" +
                            "\tinner join t4 using (col_name1, col_name2)\n" +
                            "set t1.value_col = t3.new_value_col, t4.`some-col*` = `t2`.`***` * 2\n" +
                            "where t1.pk = t2.fk_t1_pk\n" +
                            "\tand t2.id = t4.fk_id_entity;", //
                                output);
        }

        assertTrue(WallUtils.isValidateMySql(sql));
    }
}
