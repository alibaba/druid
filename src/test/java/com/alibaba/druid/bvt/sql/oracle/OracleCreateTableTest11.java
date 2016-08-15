/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleCreateTableTest11 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "create table zfj_en_click(" + //
                "P_M CHAR(6), KEYWORD VARCHAR2(256), " + //
                "REGION_NAME VARCHAR2(100), " + //
                "COUNTRY_ACCORD_NAME VARCHAR2(256), " + //
                "P_NAME  VARCHAR2(256), " + //
                "P_VALUE VARCHAR2(256), " + //
                "CLICK_CNT NUMBER" + //
                ") " + //
                "partition by range (p_m) " + //
                "(" + //
                "partition part_201108 values less than('201109')," + //
                "partition part_201109 values less than('201110')," + //
                "partition part_201110 values less than('201111'), " + //
                "partition part_201111 values less than('201112'), " + //
                "partition part_201112 values less than('201201'), " + //
                "partition part_201201 values less than('201202'), " + //
                "partition part_201202 values less than('201203'), " + //
                "partition part_201203 values less than('201204'), " + //
                "partition part_201204 values less than('201205'), " + //
                "partition part_201205 values less than('201206'), " + //
                "partition part_201206 values less than('201207'), " + //
                "partition part_201207 values less than('201208'), " + //
                "partition part_201208 values less than('201209'), " + //
                "partition part_201209 values less than('201210'), " + //
                "partition part_201210 values less than('201211'), " + //
                "partition part_201211 values less than('201212'), " + //
                "partition part_201212 values less than('201301'), " + //
                "partition part_201301 values less than('201302'), " + //
                "partition part_201302 values less than('201303'), " + //
                "partition part_201303 values less than('201304'), " + //
                "partition part_201304 values less than('201305'), " + //
                "partition part_201305 values less than('201306'), " + //
                "partition part_201306 values less than('201307') " + //
                ") ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("zfj_en_click")));

        Assert.assertEquals(7, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "P_M")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "KEYWORD")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "REGION_NAME")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "COUNTRY_ACCORD_NAME")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "P_NAME")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "P_VALUE")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("zfj_en_click", "CLICK_CNT")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
