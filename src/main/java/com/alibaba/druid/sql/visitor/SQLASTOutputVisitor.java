/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.visitor;

import java.io.IOException;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLObjectCreateExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.SQLSelectSubqueryQuery;

public class SQLASTOutputVisitor extends SQLASTVisitorAdapter {

    protected Appendable appender;
    private String       indent      = "\t";
    private int          indentCount = 0;

    public SQLASTOutputVisitor(Appendable appender){
        this.appender = appender;
    }

    public void decrementIndent() {
        this.indentCount -= 1;
    }

    public void incrementIndent() {
        this.indentCount += 1;
    }

    public void print(char value) {
        try {
            this.appender.append(value);
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    public void print(int value) {
        print(Integer.toString(value));
    }

    public void print(long value) {
        print(Long.toString(value));
    }

    public void print(String text) {
        try {
            this.appender.append(text);
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    protected void printAlias(String alias) {
        if ((alias != null) && (alias.length() > 0)) {
            print(" ");
            print(alias);
        }
    }

    protected void printAndAccept(List<? extends SQLObject> nodes, String seperator) {
        for (int i = 0, size = nodes.size(); i < size; ++i) {
            if (i != 0) {
                print(seperator);
            }
            nodes.get(i).accept(this);
        }
    }

    protected void printSelectList(List<SQLSelectItem> selectList) {
        incrementIndent();
        for (int i = 0, size = selectList.size(); i < size; ++i) {
            if (i != 0) {
                if (i % 5 == 0) {
                    println();
                }

                print(", ");
            }

            selectList.get(i).accept(this);
        }
        decrementIndent();
    }

    protected void printlnAndAccept(List<? extends SQLObject> nodes, String seperator) {
        for (int i = 0, size = nodes.size(); i < size; ++i) {
            if (i != 0) {
                println(seperator);
            }

            ((SQLObject) nodes.get(i)).accept(this);
        }
    }

    public void printIndent() {
        for (int i = 0; i < this.indentCount; ++i)
            print(this.indent);
    }

    public void println() {
        print("\n");
        printIndent();
    }

    public void println(String text) {
        print(text);
        println();
    }

    // ////////////////////

    public boolean visit(SQLBetweenExpr x) {
        x.getTestExpr().accept(this);

        if (x.isNot()) {
            print(" NOT BETWEEN ");
        } else {
            print(" BETWEEN ");
        }

        x.getBeginExpr().accept(this);
        print(" AND ");
        x.getEndExpr().accept(this);

        return false;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr left = (SQLBinaryOpExpr) x.getLeft();
            if (left.getOperator().priority > x.getOperator().priority) {
                print('(');
                left.accept(this);
                print(')');
            } else {
                left.accept(this);
            }
        } else {
            x.getLeft().accept(this);
        }

        print(" ");
        print(x.getOperator().name);
        print(" ");

        if (x.getRight() instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr right = (SQLBinaryOpExpr) x.getRight();
            if (right.getOperator().priority >= x.getOperator().priority) {
                print('(');
                right.accept(this);
                print(')');
            } else {
                right.accept(this);
            }
        } else {
            x.getRight().accept(this);
        }

        return false;
    }

    public boolean visit(SQLCaseExpr x) {
        print("CASE ");
        if (x.getValueExpr() != null) {
            x.getValueExpr().accept(this);
            print(" ");
        }

        printAndAccept(x.getItems(), " ");

        if (x.getElseExpr() != null) {
            print(" ELSE ");
            x.getElseExpr().accept(this);
        }

        print(" END");
        return false;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        print("WHEN ");
        x.getConditionExpr().accept(this);
        print(" THEN ");
        x.getValueExpr().accept(this);
        return false;
    }

    public boolean visit(SQLCastExpr x) {
        print("CAST(");
        x.getExpr().accept(this);
        print(" AS ");
        x.getDataType().accept(this);
        print(")");

        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if ((x.getText() == null) || (x.getText().length() == 0)) {
            print("NULL");
        } else {
            print("'");
            print(x.getText().replaceAll("'", "''"));
            print("'");
        }

        return false;
    }

    public boolean visit(SQLDataType x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            printAndAccept(x.getArguments(), ", ");
            print(")");
        }

        return false;
    }

    public boolean visit(SQLExistsExpr x) {
        if (x.isNot()) print("NOT EXISTS (");
        else {
            print("EXISTS (");
        }
        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(")");
        return false;
    }

    public boolean visit(SQLIdentifierExpr astNode) {
        print(astNode.getName());
        return false;
    }

    public boolean visit(SQLInListExpr x) {
        x.getExpr().accept(this);

        if (x.isNot()) {
            print(" NOT IN (");
        } else {
            print(" IN (");
        }

        printAndAccept(x.getTargetList(), ", ");
        print(')');
        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        print(x.getNumber().toString());
        return false;
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        if (x.getOwner() != null) {
            x.getOwner().accept(this);
            print(".");
        }
        print(x.getMethodName());
        print("(");
        printAndAccept(x.getParameters(), ", ");
        print(")");
        return false;
    }

    public boolean visit(SQLAggregateExpr x) {
        x.getMethodName().accept(this);
        print("(");
        printAndAccept(x.getArguments(), ", ");
        print(")");
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
        print("*");
        return true;
    }

    public boolean visit(SQLNCharExpr x) {
        if ((x.getText() == null) || (x.getText().length() == 0)) {
            print("NULL");
        } else {
            print("N'");
            print(x.getText().replace("'", "''"));
            print("'");
        }
        return false;
    }

    public boolean visit(SQLNotExpr x) {
        print("NOT ");
        x.getExpr().accept(this);
        return false;
    }

    public boolean visit(SQLNullExpr x) {
        print("NULL");
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        print(x.getNumber().toString());
        return false;
    }

    public boolean visit(SQLObjectCreateExpr x) {
        throw new UnsupportedOperationException();
    }

    public boolean visit(SQLPropertyExpr x) {
        x.getOwner().accept(this);
        print(".");
        print(x.getName());
        return false;
    }

    public boolean visit(SQLQueryExpr x) {
        if (x.getParent() instanceof SQLStatement) {
            incrementIndent();

            println();
            x.getSubQuery().accept(this);

            decrementIndent();
        } else {
            print("(");
            incrementIndent();
            println();
            x.getSubQuery().accept(this);
            println();
            decrementIndent();
            print(")");
        }
        return false;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        if (x.getItems().size() > 0) {
            print("GROUP BY ");
            printAndAccept(x.getItems(), ", ");
        }

        if (x.getHaving() != null) {
            println();
            print("HAVING ");
            x.getHaving().accept(this);
        }
        return false;
    }

    public boolean visit(SQLSelect x) {
        x.getQuery().accept(this);

        if (x.getOrderBy() != null) {
            print(" ");
            x.getOrderBy().accept(this);
        }

        return false;
    }

    public boolean visit(SQLSelectQueryBlock select) {
        print("SELECT ");

        if (SQLSetQuantifier.ALL == select.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == select.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == select.getDistionOption()) {
            print("UNIQUE ");
        }

        printSelectList(select.getSelectList());

        if (select.getFrom() != null) {
            println();
            print("FROM ");
            select.getFrom().accept(this);
        }

        if (select.getWhere() != null) {
            println();
            print("WHERE ");
            select.getWhere().accept(this);
        }

        if (select.getGroupBy() != null) {
            print(" ");
            select.getGroupBy().accept(this);
        }

        return false;
    }

    public boolean visit(SQLSelectItem x) {
        x.getExpr().accept(this);

        if ((x.getAlias() != null) && (x.getAlias().length() > 0)) {
            print(" AS ");
            print(x.getAlias());
        }
        return false;
    }

    public boolean visit(SQLOrderBy x) {
        if (x.getItems().size() > 0) {
            print("ORDER BY ");

            printAndAccept(x.getItems(), ", ");
        }
        return false;
    }

    public boolean visit(SQLSelectOrderByItem x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(" ");
            print(x.getType().name().toUpperCase());
        }

        if (x.getCollate() != null) {
            print(" COLLATE ");
            print(x.getCollate());
        }

        return false;
    }

    public boolean visit(SQLExprTableSource x) {
        x.getExpr().accept(this);

        if (x.getAlias() != null) {
            print(' ');
            print(x.getAlias());
        }

        return false;
    }

    public boolean visit(SQLSelectStatement stmt) {
        SQLSelect select = stmt.getSelect();

        select.accept(this);

        return false;
    }

    public boolean visit(SQLSelectSubqueryQuery x) {
        throw new UnsupportedOperationException();
    }

    public boolean visit(SQLVariantRefExpr x) {
        print(x.getName());
        return false;
    }

    public boolean visit(SQLDropTableStatement x) {
        print("DROP TABLE ");
        x.getName().accept(this);
        return false;
    }

    public boolean visit(SQLTableElement x) {
        if (x instanceof SQLColumnDefinition) {
            return visit((SQLColumnDefinition) x);
        }

        throw new RuntimeException("TODO");
    }

    public boolean visit(SQLColumnDefinition x) {
        x.getName().accept(this);
        print(' ');
        x.getDataType().accept(this);

        if (x.getDefaultExpr() != null) {
            print(" DEFAULT ");
            x.getDefaultExpr().accept(this);
        }

        for (SQLColumnConstraint item : x.getConstaints()) {
            print(' ');
            item.accept(this);
        }

        return false;
    }

    public boolean visit(SQLDeleteStatement x) {
        print("DELETE FROM ");

        x.getTableName().accept(this);

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    public boolean visit(SQLCurrentOfCursorExpr x) {
        print("CURRENT OF ");
        x.getCursorName().accept(this);
        return false;
    }

    public boolean visit(SQLInsertStatement x) {
        print("INSERT INTO ");

        x.getTableName().accept(this);

        if (x.getColumns().size() > 0) {
            incrementIndent();
            println();
            print("(");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            print(")");
            decrementIndent();
        }

        if (x.getValues() != null) {
            println();
            print("VALUES ");
            x.getValues().accept(this);
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        return false;
    }

    public boolean visit(SQLUpdateSetItem x) {
        x.getColumn().accept(this);
        print(" = ");
        x.getValue().accept(this);
        return false;
    }

    public boolean visit(SQLUpdateStatement x) {
        print("UPDATE ");

        x.getTableName().accept(this);

        print(" SET ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getItems().get(i).accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    public boolean visit(SQLCreateTableStatement x) {
        print("CREATE TABLE ");
        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
            print("GLOBAL TEMPORARY ");
        } else if (SQLCreateTableStatement.Type.LOCAL_TEMPORARY.equals(x.getType())) {
            print("LOCAL TEMPORARY ");
        }

        x.getName().accept(this);
        print(" (");

        for (int i = 0, size = x.getTableElementList().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getTableElementList().get(i).accept(this);
        }
        print(")");

        return false;
    }

    public boolean visit(SQLUniqueConstraint x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }

        print("UNIQUE (");
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(")");
        return false;
    }

    public boolean visit(NotNullConstraint x) {
        print("NOT NULL");
        return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        x.getLeft().accept(this);
        println();
        print(x.getOperator().name);
        println();
        x.getRight().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        print(x.getOperator().name);
        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLBinaryOpExpr) {
            print('(');
            expr.accept(this);
            print(')');
        } else if (expr instanceof SQLUnaryExpr) {
            print('(');
            expr.accept(this);
            print(')');
        } else {
            expr.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLHexExpr x) {
        print("0x");
        print(x.getHex());

        String charset = (String) x.getAttribute("USING");
        if (charset != null) {
            print(" USING ");
            print(charset);
        }

        return false;
    }

    @Override
    public boolean visit(SQLSetStatement x) {
        print("SET ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public boolean visit(SQLSetStatement.Item x) {
        x.getTarget().accept(this);
        print(" = ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCallStatement x) {
        print("CALL ");
        x.getProcedureName().accept(this);
        print('(');
        printAndAccept(x.getParameters(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        x.getLeft().accept(this);
        if (x.getJoinType() == JoinType.COMMA) {
            print(",");
        } else {
            print(" ");
            print(JoinType.toString(x.getJoinType()));
        }
        print(" ");
        x.getRight().accept(this);

        if (x.getCondition() != null) {
            print(" ON ");
            x.getCondition().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(ValuesClause x) {
        print("(");
        incrementIndent();
        for (int i = 0, size = x.getValues().size(); i < size; ++i) {
            if (i != 0) {
                if (i % 5 == 0) {
                    println();
                }
                print(", ");
            }

            x.getValues().get(i).accept(this);
        }
        decrementIndent();
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLSomeExpr x) {
        print("SOME (");

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLAnyExpr x) {
        print("ANY (");

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLAllExpr x) {
        print("ALL (");

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        x.getExpr().accept(this);
        if (x.isNot()) {
            print(" NOT IN (");
        } else {
            print(" IN (");
        }

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(")");

        return false;
    }

    @Override
    public boolean visit(SQLListExpr x) {
        print("(");
        printAndAccept(x.getItems(), ", ");
        print(")");

        return false;
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        print("(");
        incrementIndent();
        x.getSelect().accept(this);
        println();
        decrementIndent();
        print(")");

        if (x.getAlias() != null) {
            print(' ');
            print(x.getAlias());
        }

        return false;
    }
}
