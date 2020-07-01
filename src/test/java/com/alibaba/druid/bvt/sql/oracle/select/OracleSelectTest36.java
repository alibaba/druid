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
package com.alibaba.druid.bvt.sql.oracle.select;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest36 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "select ID,name from druid_test where (name>=? or name is null) and card_id<?"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);
        
        String result = SQLUtils.toOracleString(stmt);
        String result_lcase = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
        Assert.assertEquals("SELECT ID, name"
                + "\nFROM druid_test"
                + "\nWHERE (name >= ?"
                + "\n\t\tOR name IS NULL)"
                + "\n\tAND card_id < ?", result);
        
        Assert.assertEquals("select ID, name"
                + "\nfrom druid_test"
                + "\nwhere (name >= ?"
                + "\n\t\tor name is null)"
                + "\n\tand card_id < ?", result_lcase);
        
        {
            SQLSelect select = ((SQLSelectStatement) stmt).getSelect();
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
            SQLBinaryOpExpr where = (SQLBinaryOpExpr) queryBlock.getWhere();
            Assert.assertEquals(SQLBinaryOperator.BooleanAnd, where.getOperator());
            
            SQLBinaryOpExpr left = (SQLBinaryOpExpr) where.getLeft();
            Assert.assertEquals(SQLBinaryOperator.BooleanOr, left.getOperator());
            
            SQLBinaryOpExpr nameGTEQ = (SQLBinaryOpExpr) left.getLeft();
            Assert.assertEquals(SQLBinaryOperator.GreaterThanOrEqual, nameGTEQ.getOperator());
            
            SQLBinaryOpExpr nameIS = (SQLBinaryOpExpr) left.getRight();
            Assert.assertEquals(SQLBinaryOperator.Is, nameIS.getOperator());
        }

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("druid_test")));

        Assert.assertEquals(3, visitor.getColumns().size());

         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("druid_test", "ID")));
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("druid_test", "name")));
         Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("druid_test", "card_id")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
