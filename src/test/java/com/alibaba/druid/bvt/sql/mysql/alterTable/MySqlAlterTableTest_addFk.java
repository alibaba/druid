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
package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlAlterTableTest_addFk extends TestCase {

    public void test_alter_first() throws Exception {
        String sql = "alter table Test2 add index FK4CF5DC0F5DD7C31 (test1_name), " + //
                     "add constraint FK4CF5DC0F5DD7C31 foreign key (test1_name) references Test1 (name)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals("ALTER TABLE Test2" + //
                                    "\n\tADD INDEX FK4CF5DC0F5DD7C31 (test1_name)," + //
                                    "\n\tADD CONSTRAINT FK4CF5DC0F5DD7C31 FOREIGN KEY (test1_name) REFERENCES Test1 (name)",
                                    SQLUtils.toMySqlString(stmt));
        
        Assert.assertEquals("alter table Test2" + //
                "\n\tadd index FK4CF5DC0F5DD7C31 (test1_name)," + //
                "\n\tadd constraint FK4CF5DC0F5DD7C31 foreign key (test1_name) references Test1 (name)",
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());

        TableStat tableStat = visitor.getTableStat("Test2");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }
}
