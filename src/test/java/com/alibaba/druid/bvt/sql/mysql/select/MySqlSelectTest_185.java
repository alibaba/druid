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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlSelectTest_185 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select\n" +
                "product.brand_id,\n" +
                "product.sort,\n" +
                "product.old_product_id,\n" +
                "product.old_url_key,\n" +
                "categoryProduct.category_id\n" +
                "from catalog_product as product, catalog_category_product as categoryProduct\n" +
                "where product.brand_id = categoryProduct.brand_id and product.lang_id = categoryProduct.lang_id and\n" +
                "product.product_id = categoryProduct.product_id and product.brand_id = ? and product.lang_id = ? and\n" +
                "categoryProduct.category_id = ? and product.status = 1 and\n" +
                "product.more_color between (0 AND 1) and product.master_color = 1\n" +
                "order by product.sort asc";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        assertNotNull(queryBlock.getOrderBy());

//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(13, visitor.getColumns().size());
        assertEquals(10, visitor.getConditions().size());
        assertEquals(1, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("catalog_product"));
        assertTrue(visitor.containsTable("catalog_category_product"));
        assertTrue(visitor.containsColumn("catalog_product", "brand_id"));

        String output = SQLUtils.toMySqlString(stmt);
        assertEquals("SELECT product.brand_id, product.sort, product.old_product_id, product.old_url_key, categoryProduct.category_id\n" +
                        "FROM catalog_product product, catalog_category_product categoryProduct\n" +
                        "WHERE product.brand_id = categoryProduct.brand_id\n" +
                        "\tAND product.lang_id = categoryProduct.lang_id\n" +
                        "\tAND product.product_id = categoryProduct.product_id\n" +
                        "\tAND product.brand_id = ?\n" +
                        "\tAND product.lang_id = ?\n" +
                        "\tAND categoryProduct.category_id = ?\n" +
                        "\tAND product.status = 1\n" +
                        "\tAND product.more_color BETWEEN (0\n" +
                        "\t\tAND 1)\n" +
                        "\tAND (product.master_color = 1)\n" +
                        "ORDER BY product.sort ASC", //
                            output);
    }

}
