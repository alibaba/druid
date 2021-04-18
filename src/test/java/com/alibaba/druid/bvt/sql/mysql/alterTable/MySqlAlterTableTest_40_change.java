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
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlAlterTableTest_40_change extends TestCase {

    public void test_alter_constraint() throws Exception {
        String sql = "alter table sdfwef change column a c int";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals("ALTER TABLE sdfwef\n" +
                "\tCHANGE COLUMN a c int", SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("alter table sdfwef\n" +
                "\tchange column a c int", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(1, visitor.getColumns().size());

        assertTrue(
                isRenameColumn((SQLAlterTableStatement) stmt)
        );
    }

    public boolean isRenameColumn(SQLAlterTableStatement stmt) {
        for (SQLAlterTableItem item : stmt.getItems()) {
            if (item instanceof MySqlAlterTableChangeColumn) {
                MySqlAlterTableChangeColumn changeColumn = (MySqlAlterTableChangeColumn) item;
                SQLIdentifierExpr columnName = (SQLIdentifierExpr) changeColumn.getColumnName();
                String newColumnName = changeColumn.getNewColumnDefinition().getColumnName();
                if (!columnName.nameEquals(newColumnName)) {
                    return true;
                }
            }

            if (item instanceof SQLAlterTableRenameColumn) {
                return true;
            }
        }
        return false;
    }

}
