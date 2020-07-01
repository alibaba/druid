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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_update extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select current_no from "
                     + "update wlb_waybill_branch_rule  set current_no = current_no + ? ,gmt_modified = now()   "
                     + "where id = ? and status = ? and end_no > current_no " 
                     + "order by current_no desc "
                     + "limit 10";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        print(statementList);

        Assert.assertEquals(1, statementList.size());
        
        String expected = "SELECT current_no"
                + "\nFROM UPDATE wlb_waybill_branch_rule"
                + "\nSET current_no = current_no + ?, gmt_modified = now()"
                + "\nWHERE id = ?"
                + "\n\tAND status = ?"
                + "\n\tAND end_no > current_no"
                + "\nORDER BY current_no DESC"
                + "\nLIMIT 10";

        Assert.assertEquals(expected, stmt.toString());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(4, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.containsColumn("wlb_waybill_branch_rule", "current_no"));
        Assert.assertTrue(visitor.containsColumn("wlb_waybill_branch_rule", "gmt_modified"));
        Assert.assertTrue(visitor.containsColumn("wlb_waybill_branch_rule", "id"));
        Assert.assertTrue(visitor.containsColumn("wlb_waybill_branch_rule", "status"));
        Assert.assertTrue(visitor.containsColumn("wlb_waybill_branch_rule", "end_no"));
    }
}
