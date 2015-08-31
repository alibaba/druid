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
package com.alibaba.druid.sql.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
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
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddPartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropForeignKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRename;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenamePartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSetComment;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSetLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableTouch;
import com.alibaba.druid.sql.ast.statement.SQLAlterViewRenameStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLColumnReference;
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerEvent;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerType;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropProcedureStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropSequenceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropUserStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExplainStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLRevokeStatement;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;

public class SQLASTOutputVisitor extends SQLASTVisitorAdapter implements PrintableVisitor {

    protected final Appendable appender;
    private String             indent                 = "\t";
    private int                indentCount            = 0;
    private boolean            prettyFormat           = true;
    protected int              selectListNumberOfLine = 5;

    private List<Object> parameters;

    public SQLASTOutputVisitor(Appendable appender){
        this.appender = appender;
    }

    public int getParametersSize() {
        if (parameters == null) {
            return 0;
        }

        return this.parameters.size();
    }

    public List<Object> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Object>();
        }

        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public int getIndentCount() {
        return indentCount;
    }

    public Appendable getAppender() {
        return appender;
    }

    public boolean isPrettyFormat() {
        return prettyFormat;
    }

    public void setPrettyFormat(boolean prettyFormat) {
        this.prettyFormat = prettyFormat;
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

    public void print(Date date) {
        SimpleDateFormat dateFormat;
        if (date instanceof java.sql.Timestamp) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        print("'" + dateFormat.format(date) + "'");
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
                if (i % selectListNumberOfLine == 0) {
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
        for (int i = 0; i < this.indentCount; ++i) {
            print(this.indent);
        }
    }

    public void println() {
        if (!isPrettyFormat()) {
            print(' ');
            return;
        }

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
        SQLObject parent = x.getParent();
        boolean isRoot = parent instanceof SQLSelectQueryBlock;
        boolean relational = x.getOperator() == SQLBinaryOperator.BooleanAnd
                             || x.getOperator() == SQLBinaryOperator.BooleanOr;

        if (isRoot && relational) {
            incrementIndent();
        }

        List<SQLExpr> groupList = new ArrayList<SQLExpr>();
        SQLExpr left = x.getLeft();
        for (;;) {
            if (left instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) left).getOperator() == x.getOperator()) {
                SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
                groupList.add(binaryLeft.getRight());
                left = binaryLeft.getLeft();
            } else {
                groupList.add(left);
                break;
            }
        }

        for (int i = groupList.size() - 1; i >= 0; --i) {
            SQLExpr item = groupList.get(i);
            
            if (relational) {
                if (isPrettyFormat() && item.hasBeforeComment()) {
                    printlnComments(item.getBeforeCommentsDirect());
                }
            }
            
            if (isPrettyFormat() && item.hasBeforeComment()) {
                printlnComments(item.getBeforeCommentsDirect());
            }
            
            visitBinaryLeft(item, x.getOperator());

            if (isPrettyFormat() && item.hasAfterComment()) {
                print(' ');
                printComment(item.getAfterCommentsDirect(), "\n");
            }
            
            if (i != groupList.size() - 1 && isPrettyFormat() && item.getParent().hasAfterComment()) {
                print(' ');
                printComment(item.getParent().getAfterCommentsDirect(), "\n");
            }
            
            if (relational) {
                println();
            } else {
                print(" ");
            }
            print(x.getOperator().name);
            print(" ");
        }

        visitorBinaryRight(x);

        if (isRoot && relational) {
            decrementIndent();
        }

        return false;
    }

    private void visitorBinaryRight(SQLBinaryOpExpr x) {
        if (isPrettyFormat() && x.getRight().hasBeforeComment()) {
            printlnComments(x.getRight().getBeforeCommentsDirect());
        }
        
        if (x.getRight() instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr right = (SQLBinaryOpExpr) x.getRight();
            boolean rightRational = right.getOperator() == SQLBinaryOperator.BooleanAnd
                                    || right.getOperator() == SQLBinaryOperator.BooleanOr;

            if (right.getOperator().priority >= x.getOperator().priority) {
                if (rightRational) {
                    incrementIndent();
                }

                print('(');
                right.accept(this);
                print(')');

                if (rightRational) {
                    decrementIndent();
                }
            } else {
                right.accept(this);
            }
        } else {
            x.getRight().accept(this);
        }
        
        if (x.getRight().hasAfterComment() && isPrettyFormat()) {
            print(' ');
            printlnComments(x.getRight().getAfterCommentsDirect());
        }
    }

    private void visitBinaryLeft(SQLExpr left, SQLBinaryOperator op) {
        if (left instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
            boolean leftRational = binaryLeft.getOperator() == SQLBinaryOperator.BooleanAnd
                                   || binaryLeft.getOperator() == SQLBinaryOperator.BooleanOr;

            if (binaryLeft.getOperator().priority > op.priority) {
                if (leftRational) {
                    incrementIndent();
                }
                print('(');
                left.accept(this);
                print(')');

                if (leftRational) {
                    decrementIndent();
                }
            } else {
                left.accept(this);
            }
        } else {
            left.accept(this);
        }
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
        if (x.getText() == null) {
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

    public boolean visit(SQLCharacterDataType x) {
        visit((SQLDataType) x);
        return false;
    }

    public boolean visit(SQLExistsExpr x) {
        if (x.isNot()) {
            print("NOT EXISTS (");
        } else {
            print("EXISTS (");
        }
        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(")");
        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        print(x.getName());
        return false;
    }

    public boolean visit(SQLInListExpr x) {
        x.getExpr().accept(this);

        if (x.isNot()) {
            print(" NOT IN (");
        } else {
            print(" IN (");
        }

        final List<SQLExpr> list = x.getTargetList();
        
        boolean printLn = false;
        if (list.size() > 5) {
            printLn = true;
            for (int i = 0, size = list.size(); i < size; ++i) {
                if (!(list.get(i) instanceof SQLCharExpr)) {
                    printLn = false;
                    break;
                }
            }
        }
        
        if (printLn) {
            incrementIndent();
            println();
            for (int i = 0, size = list.size(); i < size; ++i) {
                if (i != 0) {
                    print(", ");
                    println();
                }
                list.get(i).accept(this);
            }
            decrementIndent();
            println();
        } else {
            printAndAccept(x.getTargetList(), ", ");
        }

        print(')');
        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        return SQLASTOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        if (x.getOwner() != null) {
            x.getOwner().accept(this);
            print(".");
        }
        printFunctionName(x.getMethodName());
        print("(");
        printAndAccept(x.getParameters(), ", ");
        print(")");
        return false;
    }
    
    protected void printFunctionName(String name) {
        print(name);
    }

    public boolean visit(SQLAggregateExpr x) {
        print(x.getMethodName());
        print("(");

        if (x.getOption() != null) {
            print(x.getOption().toString());
            print(' ');
        }

        printAndAccept(x.getArguments(), ", ");

        visitAggreateRest(x);

        print(")");

        if (x.getWithinGroup() != null) {
            print(" WITHIN GROUP (");
            x.getWithinGroup().accept(this);
            print(")");
        }

        if (x.getOver() != null) {
            print(" ");
            x.getOver().accept(this);
        }
        return false;
    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {

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
        SQLExpr expr = x.getExpr();

        boolean needQuote = false;

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) expr;
            needQuote = binaryOpExpr.getOperator().isLogical();
        }

        if (needQuote) {
            print('(');
        }
        expr.accept(this);

        if (needQuote) {
            print(')');
        }
        return false;
    }

    public boolean visit(SQLNullExpr x) {
        print("NULL");
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        return SQLASTOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLPropertyExpr x) {
        x.getOwner().accept(this);
        print(".");
        print(x.getName());
        return false;
    }

    public boolean visit(SQLQueryExpr x) {
        SQLObject parent = x.getParent();
        if (parent instanceof SQLSelect) {
            parent = parent.getParent();
        }

        if (parent instanceof SQLStatement) {
            incrementIndent();

            println();
            x.getSubQuery().accept(this);

            decrementIndent();
        } else if (parent instanceof ValuesClause) {
            println();
            print("(");
            x.getSubQuery().accept(this);
            print(")");
            println();
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
        x.getQuery().setParent(x);

        if (x.getWithSubQuery() != null) {
            x.getWithSubQuery().accept(this);
            println();
        }

        x.getQuery().accept(this);

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getHintsSize() > 0) {
            printAndAccept(x.getHints(), "");
        }

        return false;
    }

    public boolean visit(SQLSelectQueryBlock x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printComment(x.getBeforeCommentsDirect(), "\n");
        }
        
        print("SELECT ");

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print("UNIQUE ");
        }

        printSelectList(x.getSelectList());

        if (x.getFrom() != null) {
            println();
            print("FROM ");
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }

        return false;
    }

    public boolean visit(SQLSelectItem x) {
        if (x.isConnectByRoot()) {
            print("CONNECT_BY_ROOT ");
        }
        x.getExpr().accept(this);

        String alias = x.getAlias();
        if (alias != null && alias.length() > 0) {
            print(" AS ");
            if (alias.indexOf(' ') == -1 || alias.charAt(0) == '"' || alias.charAt(0) == '\'') {
                print(alias);
            } else {
                print('"');
                print(alias);
                print('"');
            }
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
        
        if (isPrettyFormat() && x.hasAfterComment()) {
            print(' ');
            printComment(x.getAfterCommentsDirect(), "\n");
        }

        return false;
    }

    public boolean visit(SQLSelectStatement stmt) {
        SQLSelect select = stmt.getSelect();

        select.accept(this);

        return false;
    }

    public boolean visit(SQLVariantRefExpr x) {
        int index = x.getIndex();

        if (parameters == null || index >= parameters.size()) {
            print(x.getName());
            return false;
        }

        Object param = parameters.get(index);
        printParameter(param);
        return false;
    }

    public void printParameter(Object param) {
        if (param == null) {
            print("NULL");
            return;
        }

        if (param instanceof Number //
            || param instanceof Boolean) {
            print(param.toString());
            return;
        }

        if (param instanceof String) {
            SQLCharExpr charExpr = new SQLCharExpr((String) param);
            visit(charExpr);
            return;
        }

        if (param instanceof Date) {
            print((Date) param);
            return;
        }

        if (param instanceof InputStream) {
            print("'<InputStream>");
            return;
        }

        if (param instanceof Reader) {
            print("'<Reader>");
            return;
        }

        if (param instanceof Blob) {
            print("'<Blob>");
            return;
        }

        if (param instanceof NClob) {
            print("'<NClob>");
            return;
        }

        if (param instanceof Clob) {
            print("'<Clob>");
            return;
        }

        print("'" + param.getClass().getName() + "'");
    }

    public boolean visit(SQLDropTableStatement x) {
        if (x.isTemporary()) {
            print("DROP TEMPORARY TABLE ");
        } else {
            print("DROP TABLE ");
        }

        if (x.isIfExists()) {
            print("IF EXISTS ");
        }

        printAndAccept(x.getTableSources(), ", ");

        if (x.isCascade()) {
            printCascade();
        }

        if (x.isRestrict()) {
            print(" RESTRICT");
        }

        if (x.isPurge()) {
            print(" PURGE");
        }

        return false;
    }

    protected void printCascade() {
        print(" CASCADE");
    }

    public boolean visit(SQLDropViewStatement x) {
        print("DROP VIEW ");

        if (x.isIfExists()) {
            print("IF EXISTS ");
        }

        printAndAccept(x.getTableSources(), ", ");

        if (x.isCascade()) {
            printCascade();
        }
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

        if (x.getDataType() != null) {
            print(' ');
            x.getDataType().accept(this);
        }

        if (x.getDefaultExpr() != null) {
            visitColumnDefault(x);
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            boolean newLine = item instanceof SQLForeignKeyConstraint //
                              || item instanceof SQLPrimaryKey //
                              || item instanceof SQLColumnCheck //
                              || item instanceof SQLColumnCheck //
                              || item.getName() != null;
            if (newLine) {
                incrementIndent();
                println();
            } else {
                print(' ');
            }

            item.accept(this);

            if (newLine) {
                decrementIndent();
            }
        }

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print(" ENABLE");
            }
        }

        if (x.getComment() != null) {
            print(" COMMENT ");
            x.getComment().accept(this);
        }
        
        return false;
    }

    protected void visitColumnDefault(SQLColumnDefinition x) {
        print(" DEFAULT ");
        x.getDefaultExpr().accept(this);
    }

    public boolean visit(SQLDeleteStatement x) {
        print("DELETE FROM ");

        x.getTableName().accept(this);

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            incrementIndent();
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
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

        x.getTableSource().accept(this);

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
            print("VALUES");
            println();
            x.getValues().accept(this);
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().setParent(x);
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

        x.getTableSource().accept(this);

        println();
        print("SET ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getItems().get(i).accept(this);
        }

        if (x.getWhere() != null) {
            println();
            incrementIndent();
            print("WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
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

        int size = x.getTableElementList().size();

        if (size > 0) {
            print(" (");
            incrementIndent();
            println();
            for (int i = 0; i < size; ++i) {
                if (i != 0) {
                    print(",");
                    println();
                }
                x.getTableElementList().get(i).accept(this);
            }
            decrementIndent();
            println();
            print(")");
        }

        if (x.getInherits() != null) {
            print(" INHERITS (");
            x.getInherits().accept(this);
            print(")");
        }

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
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }
        print("NOT NULL");
        return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        x.getLeft().accept(this);
        println();
        print(x.getOperator().name);
        println();

        boolean needParen = false;

        if (x.getOrderBy() != null) {
            needParen = true;
        }

        if (needParen) {
            print('(');
            x.getRight().accept(this);
            print(')');
        } else {
            x.getRight().accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        print(x.getOperator().name);

        SQLExpr expr = x.getExpr();

        switch (x.getOperator()) {
            case BINARY:
            case Prior:
            case ConnectByRoot:
                print(' ');
                expr.accept(this);
                return false;
            default:
                break;
        }

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

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(" ");
            printAndAccept(x.getHints(), " ");
        }

        return false;
    }

    @Override
    public boolean visit(SQLAssignItem x) {
        x.getTarget().accept(this);
        print(" = ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCallStatement x) {
        if (x.isBrace()) {
            print("{");
        }
        if (x.getOutParameter() != null) {
            x.getOutParameter().accept(this);
            print(" = ");
        }

        print("CALL ");
        x.getProcedureName().accept(this);
        print('(');

        printAndAccept(x.getParameters(), ", ");
        print(')');
        if (x.isBrace()) {
            print("}");
        }
        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        x.getLeft().accept(this);
        incrementIndent();

        if (x.getJoinType() == JoinType.COMMA) {
            print(",");
        } else {
            println();
            printJoinType(x.getJoinType());
        }
        print(" ");
        x.getRight().accept(this);

        if (x.getCondition() != null) {
            incrementIndent();
            print(" ON ");
            x.getCondition().accept(this);
            decrementIndent();
        }

        if (x.getUsing().size() > 0) {
            print(" USING (");
            printAndAccept(x.getUsing(), ", ");
            print(")");
        }

        if (x.getAlias() != null) {
            print(" AS ");
            print(x.getAlias());
        }

        decrementIndent();

        return false;
    }

    protected void printJoinType(JoinType joinType) {
        print(JoinType.toString(joinType));
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

            SQLExpr expr = x.getValues().get(i);
            expr.setParent(x);
            expr.accept(this);
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

    @Override
    public boolean visit(SQLTruncateStatement x) {
        print("TRUNCATE TABLE ");
        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public boolean visit(SQLDefaultExpr x) {
        print("DEFAULT");
        return false;
    }

    @Override
    public void endVisit(SQLCommentStatement x) {

    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        print("COMMENT ON ");
        if (x.getType() != null) {
            print(x.getType().name());
            print(" ");
        }
        x.getOn().accept(this);

        print(" IS ");
        x.getComment().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLUseStatement x) {
        print("USE ");
        x.getDatabase().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        print("ADD (");
        printAndAccept(x.getColumns(), ", ");
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {
        print("DROP COLUMN ");
        this.printAndAccept(x.getColumns(), ", ");

        if (x.isCascade()) {
            print(" CASCADE");
        }
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(SQLDropIndexStatement x) {
        print("DROP INDEX ");
        x.getIndexName().accept(this);

        SQLExprTableSource table = x.getTableName();
        if (table != null) {
            print(" ON ");
            table.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLSavePointStatement x) {
        print("SAVEPOINT ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLReleaseSavePointStatement x) {
        print("RELEASE SAVEPOINT ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLRollbackStatement x) {
        print("ROLLBACK");
        if (x.getTo() != null) {
            print(" TO ");
            x.getTo().accept(this);
        }
        return false;
    }

    public boolean visit(SQLCommentHint x) {
        print("/*");
        print(x.getText());
        print("*/");
        return false;
    }

    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        print("CREATE DATABASE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCreateViewStatement x) {
        print("CREATE ");
        if (x.isOrReplace()) {
            print("OR REPLACE ");
        }
        print("VIEW ");

        if (x.isIfNotExists()) {
            print("IF NOT EXISTS ");
        }

        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            println();
            print("(");
            incrementIndent();
            println();
            for (int i = 0; i < x.getColumns().size(); ++i) {
                if (i != 0) {
                    print(", ");
                    println();
                }
                x.getColumns().get(i).accept(this);
            }
            decrementIndent();
            println();
            print(")");
        }

        if (x.getComment() != null) {
            println();
            print("COMMENT ");
            x.getComment().accept(this);
        }

        println();
        print("AS");
        println();

        x.getSubQuery().accept(this);
        return false;
    }

    public boolean visit(SQLCreateViewStatement.Column x) {
        x.getExpr().accept(this);

        if (x.getComment() != null) {
            print(" COMMENT ");
            x.getComment().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropIndex x) {
        print("DROP INDEX ");
        x.getIndexName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLOver x) {
        print("OVER (");
        if (x.getPartitionBy().size() > 0) {
            print("PARTITION BY ");
            printAndAccept(x.getPartitionBy(), ", ");
            print(' ');
        }
        if (x.getOrderBy() != null) {
            x.getOrderBy().accept(this);
        }
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLColumnPrimaryKey x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }
        print("PRIMARY KEY");
        return false;
    }

    @Override
    public boolean visit(SQLColumnUniqueKey x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }
        print("UNIQUE");
        return false;
    }

    @Override
    public boolean visit(SQLColumnCheck x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }
        print("CHECK (");
        x.getExpr().accept(this);
        print(')');

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print(" ENABLE");
            } else {
                print(" DISABLE");
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLWithSubqueryClause x) {
        print("WITH");
        if (x.getRecursive() == Boolean.TRUE) {
            print(" RECURSIVE");
        }
        incrementIndent();
        println();
        printlnAndAccept(x.getEntries(), ", ");
        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            print(" (");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }
        println();
        print("AS");
        println();
        print("(");
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(")");

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAlterColumn x) {
        print("ALTER COLUMN ");
        x.getColumn().accept(this);

        if (x.isSetNotNull()) { // postgresql
            print(" SET NOT NULL");
        }
        if (x.isDropNotNull()) { // postgresql
            print(" DROP NOT NULL");
        }
        if (x.getSetDefault() != null) { // postgresql
            print(" SET DEFAULT ");
            x.getSetDefault().accept(this);
        }
        if (x.isDropDefault()) { // postgresql
            print(" DROP DEFAULT");
        }

        return false;
    }

    @Override
    public boolean visit(SQLCheck x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }
        print("CHECK (");
        incrementIndent();
        x.getExpr().accept(this);
        decrementIndent();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropForeignKey x) {
        print("DROP FOREIGN KEY ");
        x.getIndexName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        print("DROP PRIMARY KEY");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableEnableKeys x) {
        print("ENABLE KEYS");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDisableKeys x) {
        print("DISABLE KEYS");
        return false;
    }

    public boolean visit(SQLAlterTableDisableConstraint x) {
        print("DISABLE CONSTRAINT ");
        x.getConstraintName().accept(this);
        return false;
    }

    public boolean visit(SQLAlterTableEnableConstraint x) {
        print("ENABLE CONSTRAINT ");
        x.getConstraintName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropConstraint x) {
        print("DROP CONSTRAINT ");
        x.getConstraintName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        print("ALTER TABLE ");
        x.getName().accept(this);
        incrementIndent();
        for (int i = 0; i < x.getItems().size(); ++i) {
            SQLAlterTableItem item = x.getItems().get(i);
            if (i != 0) {
                print(',');
            }
            println();
            item.accept(this);
        }
        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(SQLExprHint x) {
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        print("CREATE ");
        if (x.getType() != null) {
            print(x.getType());
            print(" ");
        }

        print("INDEX ");

        x.getName().accept(this);
        print(" ON ");
        x.getTable().accept(this);
        print(" (");
        printAndAccept(x.getItems(), ", ");
        print(")");

        return false;
    }

    @Override
    public boolean visit(SQLUnique x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(" ");
        }
        print("UNIQUE (");
        printAndAccept(x.getColumns(), ", ");
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLPrimaryKeyImpl x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(" ");
        }
        print("PRIMARY KEY (");
        printAndAccept(x.getColumns(), ", ");
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRenameColumn x) {
        print("RENAME COLUMN ");
        x.getColumn().accept(this);
        print(" TO ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLColumnReference x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(" ");
        }
        print("REFERENCES ");
        x.getTable().accept(this);
        print(" (");
        printAndAccept(x.getColumns(), ", ");
        print(")");
        return false;
    }

    @Override
    public boolean visit(SQLForeignKeyImpl x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }

        print("FOREIGN KEY (");
        printAndAccept(x.getReferencingColumns(), ", ");
        print(")");

        print(" REFERENCES ");
        x.getReferencedTableName().accept(this);

        if (x.getReferencedColumns().size() > 0) {
            print(" (");
            printAndAccept(x.getReferencedColumns(), ", ");
            print(")");
        }
        return false;
    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        print("DROP SEQUENCE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLDropSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        print("DROP TRIGGER ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLDropUserStatement x) {

    }

    @Override
    public boolean visit(SQLDropUserStatement x) {
        print("DROP USER ");
        printAndAccept(x.getUsers(), ", ");
        return false;
    }

    @Override
    public boolean visit(SQLExplainStatement x) {
        print("EXPLAIN");
        if (x.getHints() != null && x.getHints().size() > 0) {
            print(" ");
            printAndAccept(x.getHints(), " ");
        }
        println();
        x.getStatement().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLGrantStatement x) {
        print("GRANT ");
        printAndAccept(x.getPrivileges(), ", ");

        printGrantOn(x);

        if (x.getTo() != null) {
            print(" TO ");
            x.getTo().accept(this);
        }

        boolean with = false;
        if (x.getMaxQueriesPerHour() != null) {
            if (!with) {
                print(" WITH");
                with = true;
            }
            print(" MAX_QUERIES_PER_HOUR ");
            x.getMaxQueriesPerHour().accept(this);
        }

        if (x.getMaxUpdatesPerHour() != null) {
            if (!with) {
                print(" WITH");
                with = true;
            }
            print(" MAX_UPDATES_PER_HOUR ");
            x.getMaxUpdatesPerHour().accept(this);
        }

        if (x.getMaxConnectionsPerHour() != null) {
            if (!with) {
                print(" WITH");
                with = true;
            }
            print(" MAX_CONNECTIONS_PER_HOUR ");
            x.getMaxConnectionsPerHour().accept(this);
        }

        if (x.getMaxUserConnections() != null) {
            if (!with) {
                print(" WITH");
                with = true;
            }
            print(" MAX_USER_CONNECTIONS ");
            x.getMaxUserConnections().accept(this);
        }

        if (x.isAdminOption()) {
            if (!with) {
                print(" WITH");
                with = true;
            }
            print(" ADMIN OPTION");
        }

        if (x.getIdentifiedBy() != null) {
            print(" IDENTIFIED BY ");
            x.getIdentifiedBy().accept(this);
        }

        return false;
    }

    protected void printGrantOn(SQLGrantStatement x) {
        if (x.getOn() != null) {
            print(" ON ");

            if (x.getObjectType() != null) {
                print(x.getObjectType().name());
                print(' ');
            }

            x.getOn().accept(this);
        }
    }

    @Override
    public boolean visit(SQLRevokeStatement x) {
        print("ROVOKE ");
        printAndAccept(x.getPrivileges(), ", ");

        if (x.getOn() != null) {
            print(" ON ");

            if (x.getObjectType() != null) {
                print(x.getObjectType().name());
                print(' ');
            }

            x.getOn().accept(this);
        }

        if (x.getFrom() != null) {
            print(" FROM ");
            x.getFrom().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        print("DROP DATABASE ");

        if (x.isIfExists()) {
            print("IF EXISTS ");
        }

        x.getDatabase().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLDropFunctionStatement x) {
        print("DROP FUNCTION ");

        if (x.isIfExists()) {
            print("IF EXISTS ");
        }

        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLDropTableSpaceStatement x) {
        print("DROP TABLESPACE ");

        if (x.isIfExists()) {
            print("IF EXISTS ");
        }

        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLDropProcedureStatement x) {
        print("DROP PROCEDURE ");

        if (x.isIfExists()) {
            print("IF EXISTS ");
        }

        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddIndex x) {
        print("ADD ");
        if (x.getType() != null) {
            print(x.getType());
            print(" ");
        }

        if (x.isUnique()) {
            print("UNIQUE ");
        }

        print(x.getKeyOrIndex());
        print(" ");

        if (x.getName() != null) {
            x.getName().accept(this);
            print(' ');
        }
        print("(");
        printAndAccept(x.getItems(), ", ");
        print(")");

        if (x.getUsing() != null) {
            print(" USING ");
            print(x.getUsing());
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddConstraint x) {
        if (x.isWithNoCheck()) {
            print("WITH NOCHECK ");
        }

        print("ADD ");

        x.getConstraint().accept(this);
        return false;
    }

    public boolean visit(SQLCreateTriggerStatement x) {
        print("CREATE ");

        if (x.isOrReplace()) {
            print("OR REPLEACE ");
        }

        print("TRIGGER ");

        x.getName().accept(this);

        incrementIndent();
        println();
        if (TriggerType.INSTEAD_OF.equals(x.getTriggerType())) {
            print("INSTEAD OF");
        } else {
            print(x.getTriggerType().name());
        }

        for (TriggerEvent event : x.getTriggerEvents()) {
            print(' ');
            print(event.name());
        }
        println();
        print("ON ");
        x.getOn().accept(this);

        if (x.isForEachRow()) {
            println();
            print("FOR EACH ROW");
        }
        decrementIndent();
        println();
        x.getBody().accept(this);
        return false;
    }

    public boolean visit(SQLBooleanExpr x) {
        print(x.getValue() ? "true" : "false");

        return false;
    }

    public void endVisit(SQLBooleanExpr x) {
    }

    @Override
    public boolean visit(SQLUnionQueryTableSource x) {
        print("(");
        incrementIndent();
        println();
        x.getUnion().accept(this);
        decrementIndent();
        println();
        print(")");

        if (x.getAlias() != null) {
            print(' ');
            print(x.getAlias());
        }

        return false;
    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        print("TIMESTAMP ");

        if (x.isWithTimeZone()) {
            print(" WITH TIME ZONE ");
        }

        print('\'');
        print(x.getLiteral());
        print('\'');

        if (x.getTimeZone() != null) {
            print(" AT TIME ZONE '");
            print(x.getTimeZone());
            print('\'');
        }

        return false;
    }

    @Override
    public boolean visit(SQLBinaryExpr x) {
        print("b'");
        print(x.getValue());
        print('\'');

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRename x) {
        print("RENAME TO ");
        x.getTo().accept(this);
        return false;
    }
    
    @Override
    public boolean visit(SQLShowTablesStatement x) {
        print("SHOW TABLES");
        if (x.getDatabase() != null) {
            print(" FROM ");
            x.getDatabase().accept(this);
        }
        
        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }
        return false;
    }
    
    protected void printComment(List<String> comments, String seperator) {
        if (comments != null) {
            for (int i = 0; i < comments.size(); ++i) {
                if (i != 0) {
                    print(seperator);
                }
                String comment = comments.get(i);
                print(comment);
            }
        }
    }
    
    protected void printlnComments(List<String> comments) {
        if (comments != null) {
            for (int i = 0; i < comments.size(); ++i) {
                String comment = comments.get(i);
                println(comment);
            }
        }
    }
    
    @Override
    public boolean visit(SQLAlterViewRenameStatement x) {
        print("ALTER VIEW ");
        x.getName().accept(this);
        print(" RENAME TO ");
        x.getTo().accept(this);
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableAddPartition x) {
        print("ADD ");
        if (x.isIfNotExists()) {
            print("IF NOT EXISTS ");
        }
        print("PARTITION (");
        printAndAccept(x.getPartition(), ", ");
        print(")");
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableDropPartition x) {
        print("DROP ");
        if (x.isIfNotExists()) {
            print("IF NOT EXISTS ");
        }
        print("PARTITION (");
        printAndAccept(x.getPartition(), ", ");
        print(")");
        
        if (x.isPurge()) {
            print(" PURGE");
        }
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableRenamePartition x) {
        print("PARTITION (");
        printAndAccept(x.getPartition(), ", ");
        print(") RENAME TO PARTITION(");
        printAndAccept(x.getTo(), ", ");
        print(")");
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableSetComment x) {
        print("SET COMMENT ");
        x.getComment().accept(this);
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableSetLifecycle x) {
        print("SET LIFECYCLE ");
        x.getLifecycle().accept(this);
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableEnableLifecycle x) {
        if (x.getPartition().size() != 0) {
            print("PARTITION (");
            printAndAccept(x.getPartition(), ", ");
            print(") ");
        }
        
        print("ENABLE LIFECYCLE");
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableDisableLifecycle x) {
        if (x.getPartition().size() != 0) {
            print("PARTITION (");
            printAndAccept(x.getPartition(), ", ");
            print(") ");
        }
        
        print("DISABLE LIFECYCLE");
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableTouch x) {
        print("TOUCH");
        if (x.getPartition().size() != 0) {
            print(" PARTITION (");
            printAndAccept(x.getPartition(), ", ");
            print(")");
        }
        return false;
    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        x.getExpr().accept(this);
        print("[");
        printAndAccept(x.getValues(), ", ");
        print("]");
        return false;
    }
}
