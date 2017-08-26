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
package com.alibaba.druid.sql.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerEvent;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeUpdateClause;
import com.alibaba.druid.sql.ast.statement.SQLWhileStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributes;
import com.alibaba.druid.sql.ast.statement.SQLDeclareStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreatePackageStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import static com.alibaba.druid.util.Utils.getBoolean;

public class SQLASTOutputVisitor extends SQLASTVisitorAdapter implements ParameterizedVisitor, PrintableVisitor {
    public static Boolean defaultPrintStatementAfterSemi;

    static {
        defaultPrintStatementAfterSemi = getBoolean(System.getProperties(), "druid.sql.output.printStatementAfterSemi"); // compatible for early versions
    }

    protected final Appendable appender;
    private String indent = "\t";
    protected int indentCount = 0;
    protected boolean ucase = true;
    protected int selectListNumberOfLine = 5;

    protected boolean groupItemSingleLine = false;

    protected List<Object> parameters;
    protected List<Object> inputParameters;
    protected Set<String> tables;

    protected boolean exportTables = false;

    protected String dbType;

    protected Map<String, String> tableMapping;

    protected int replaceCount;

    protected boolean parameterizedMergeInList = false;

    protected boolean parameterized = false;
    protected boolean shardingSupport = false;

    protected transient int lines = 0;


    protected Boolean printStatementAfterSemi = defaultPrintStatementAfterSemi;

    {
        features |= VisitorFeature.OutputPrettyFormat.mask;
    }

    public SQLASTOutputVisitor(Appendable appender){
        this.appender = appender;
    }

    public SQLASTOutputVisitor(Appendable appender, String dbType){
        this.appender = appender;
        this.dbType = dbType;
    }

    public SQLASTOutputVisitor(Appendable appender, boolean parameterized){
        this.appender = appender;
        this.config(VisitorFeature.OutputParameterized, parameterized);
    }

    public int getReplaceCount() {
        return this.replaceCount;
    }

    public void incrementReplaceCunt() {
        replaceCount++;
    }

    public void addTableMapping(String srcTable, String destTable) {
        if (tableMapping == null) {
            tableMapping = new HashMap<String, String>();
        }
        tableMapping.put(srcTable, destTable);
    }

    public void setTableMapping(Map<String, String> tableMapping) {
        this.tableMapping = tableMapping;
    }

    public List<Object> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Object>();
        }

        return parameters;
    }

    public boolean isDesensitize() {
        return isEnabled(VisitorFeature.OutputDesensitize);
    }

    public void setDesensitize(boolean desensitize) {
        config(VisitorFeature.OutputDesensitize, desensitize);
    }

    public Set<String> getTables() {
        return this.tables;
    }

    @Deprecated
    public void setParameters(List<Object> parameters) {
        if (parameters != null && parameters.size() > 0) {
            this.inputParameters = parameters;
        } else {
            this.parameters = parameters;
        }
    }

    public void setInputParameters(List<Object> parameters) {
        this.inputParameters = parameters;
    }

    public int getIndentCount() {
        return indentCount;
    }

    public Appendable getAppender() {
        return appender;
    }

    public boolean isPrettyFormat() {
        return isEnabled(VisitorFeature.OutputPrettyFormat);
    }

    public void setPrettyFormat(boolean prettyFormat) {
        config(VisitorFeature.OutputPrettyFormat, prettyFormat);
    }

    public void decrementIndent() {
        this.indentCount--;
    }

    public void incrementIndent() {
        this.indentCount++;
    }

    public boolean isParameterized() {
        return isEnabled(VisitorFeature.OutputParameterized);
    }

    public void setParameterized(boolean parameterized) {
        config(VisitorFeature.OutputParameterized, parameterized);
    }

    public boolean isParameterizedMergeInList() {
        return parameterizedMergeInList;
    }

    public void setParameterizedMergeInList(boolean parameterizedMergeInList) {
        this.parameterizedMergeInList = parameterizedMergeInList;
    }

    public boolean isExportTables() {
        return exportTables;
    }

    public void setExportTables(boolean exportTables) {
        this.exportTables = exportTables;
    }

    public void print(char value) {
        if (this.appender == null) {
            return;
        }

        try {
            this.appender.append(value);
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    public void print(int value) {
        if (this.appender == null) {
            return;
        }

        if (appender instanceof StringBuffer) {
            ((StringBuffer) appender).append(value);
        } else if (appender instanceof StringBuilder) {
            ((StringBuilder) appender).append(value);
        } else {
            print0(Integer.toString(value));
        }
    }

    public void print(long value) {
        if (this.appender == null) {
            return;
        }

        if (appender instanceof StringBuilder) {
            ((StringBuilder) appender).append(value);
        } else if (appender instanceof StringBuffer) {
            ((StringBuffer) appender).append(value);
        } else {
            print0(Long.toString(value));
        }
    }


    public void print(Date date) {
        if (this.appender == null) {
            return;
        }

        SimpleDateFormat dateFormat;
        if (date instanceof java.sql.Timestamp) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        print0("'" + dateFormat.format(date) + "'");
    }

    public void print(String text) {
        if (this.appender == null) {
            return;
        }
        print0(text);
    }

    protected void print0(String text) {
        if (appender == null) {
            return;
        }

        try {
            this.appender.append(text);
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    protected void printAlias(String alias) {
        if ((alias != null) && (alias.length() > 0)) {
            print(' ');
            print0(alias);
        }
    }

    protected void printAndAccept(List<? extends SQLObject> nodes, String seperator) {
        for (int i = 0, size = nodes.size(); i < size; ++i) {
            if (i != 0) {
                print0(seperator);
            }
            nodes.get(i).accept(this);
        }
    }

    private static int paramCount(SQLExpr x) {
        if (x instanceof SQLMethodInvokeExpr) {
            List<SQLExpr> params = ((SQLMethodInvokeExpr) x).getParameters();
            int paramCount = 1;
            for (SQLExpr param : params) {
                paramCount += paramCount(param);
            }
            return paramCount;
        }

        if (x instanceof SQLAggregateExpr) {
            List<SQLExpr> params = ((SQLAggregateExpr) x).getArguments();
            int paramCount = 1;
            for (SQLExpr param : params) {
                paramCount += paramCount(param);
            }
            return paramCount;
        }

        if (x instanceof SQLBinaryOpExpr) {
            return paramCount(((SQLBinaryOpExpr) x).getLeft())
                    + paramCount(((SQLBinaryOpExpr) x).getRight());
        }

        return 1;
    }

    protected void printSelectList(List<SQLSelectItem> selectList) {
        this.indentCount++;
        for (int i = 0, lineItemCount = 0, size = selectList.size()
             ; i < size; ++i, ++lineItemCount) {
            SQLSelectItem selectItem = selectList.get(i);
            SQLExpr selectItemExpr = selectItem.getExpr();

            int paramCount = paramCount(selectItemExpr);
            if (selectItemExpr instanceof SQLMethodInvokeExpr
                    || selectItemExpr instanceof SQLAggregateExpr
                    || selectItemExpr instanceof SQLBinaryOpExpr) {
                lineItemCount += (paramCount - 1);
            }

            if (i != 0) {
                SQLSelectItem preSelectItem = selectList.get(i - 1);
                if (preSelectItem.getAfterCommentsDirect() != null) {
                    lineItemCount = 0;
                    println();
                } else if (selectItemExpr instanceof SQLMethodInvokeExpr
                        || selectItemExpr instanceof SQLAggregateExpr
                        || selectItemExpr instanceof SQLBinaryOpExpr) {
                    if (lineItemCount >= selectListNumberOfLine) {
                        lineItemCount = paramCount;
                        println();
                    }
                } else if (lineItemCount >= selectListNumberOfLine
                        || selectItemExpr instanceof SQLQueryExpr
                        || selectItemExpr instanceof SQLCaseExpr) {
                    lineItemCount = 0;
                    println();
                }

                print0(", ");
            }

            if (selectItem.getClass() == SQLSelectItem.class) {
                this.visit(selectItem);
            } else {
                selectItem.accept(this);
            }

            if (selectItem.hasAfterComment()) {
                print(' ');
                printlnComment(selectItem.getAfterCommentsDirect());
            }
        }
        this.indentCount--;
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
            print0(this.indent);
        }
    }

    public void println() {
        if (!isPrettyFormat()) {
            print(' ');
            return;
        }

        print0("\n");
        lines++;
        printIndent();
    }

    public void println(String text) {
        print(text);
        println();
    }

    protected void println0(String text) {
        print0(text);
        println();
    }

    // ////////////////////

    public boolean visit(SQLBetweenExpr x) {
        x.getTestExpr().accept(this);

        if (x.isNot()) {
            print0(ucase ? " NOT BETWEEN " : " not between ");
        } else {
            print0(ucase ? " BETWEEN " : " between ");
        }

        x.getBeginExpr().accept(this);
        print0(ucase ? " AND " : " and ");
        x.getEndExpr().accept(this);

        return false;
    }

    public boolean visit(SQLBinaryOpExprGroup x) {
        SQLObject parent = x.getParent();
        SQLBinaryOperator operator = x.getOperator();

        boolean isRoot = parent instanceof SQLSelectQueryBlock || parent instanceof SQLBinaryOpExprGroup;

        List<SQLExpr> items = x.getItems();
        if (isRoot) {
            this.indentCount++;
        }

        if (this.parameterized) {
            SQLExpr firstLeft = null;
            SQLBinaryOperator firstOp = null;
            List<Object> parameters = new ArrayList<Object>(items.size());

            List<SQLBinaryOpExpr> literalItems = null;

            for (int i = 0; i < items.size(); i++) {
                SQLExpr item = items.get(i);
                if (item instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryItem = (SQLBinaryOpExpr) item;
                    SQLExpr left = binaryItem.getLeft();
                    SQLExpr right = binaryItem.getRight();

                    if (right instanceof SQLLiteralExpr) {
                        if (left instanceof SQLLiteralExpr) {
                            if (literalItems == null) {
                                literalItems = new ArrayList<SQLBinaryOpExpr>();
                            }
                            literalItems.add(binaryItem);
                            continue;
                        }

                        if (this instanceof ExportParameterVisitor || this.parameters != null) {
                            ExportParameterVisitorUtils.exportParameter(parameters, right);
                        }
                    } else if (right instanceof SQLVariantRefExpr) {
                        // skip
                    } else {
                        firstLeft = null;
                        break;
                    }


                    if (firstLeft == null) {
                        firstLeft = binaryItem.getLeft();
                        firstOp = binaryItem.getOperator();
                    } else {
                        if (!SQLExprUtils.equals(firstLeft, left)) {
                            firstLeft = null;
                            break;
                        }
                    }
                } else {
                    firstLeft = null;
                    break;
                }
            }

            if (firstLeft != null) {
                if (literalItems != null) {
                    for (SQLBinaryOpExpr literalItem : literalItems) {
                        visit(literalItem);
                        println();
                        printOperator(operator);
                        print(' ');

                    }
                }
                printExpr(firstLeft);
                print(' ');
                printOperator(firstOp);
                print0(" ?");

                if (this instanceof ExportParameterVisitor || this.parameters != null) {
                    if (parameters.size() > 0) {
                        this.parameters.add(parameters);
                    }
                }

                incrementReplaceCunt();
                if (isRoot) {
                    this.indentCount--;
                }
                return false;
            }
        }

        for (int i = 0; i < items.size(); i++) {
            SQLExpr item = items.get(i);

            if (i != 0) {
                println();
                printOperator(operator);
                print(' ');
            }

            if (item.hasBeforeComment()) {
                printlnComments(item.getBeforeCommentsDirect());
            }

            if (item instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) item;
                SQLExpr binaryOpExprRight = binaryOpExpr.getRight();
                SQLBinaryOperator itemOp = binaryOpExpr.getOperator();

                boolean isLogic = itemOp.isLogical();
                if (isLogic) {
                    indentCount++;
                }

                boolean bracket;
                if (itemOp.priority > operator.priority) {
                    bracket = true;
                } else {
                    bracket = binaryOpExpr.isBracket() & !parameterized;
                }
                if (bracket) {
                    print('(');
                    visit(binaryOpExpr);
                    print(')');
                } else {
                    visit(binaryOpExpr);
                }

                if (item.hasAfterComment()) {
                    print(' ');
                    printlnComment(item.getAfterCommentsDirect());
                }

                if (isLogic) {
                    indentCount--;
                }
            } else if (item instanceof SQLBinaryOpExprGroup) {
                print('(');
                visit((SQLBinaryOpExprGroup) item);
                print(')');
            } else {
                printExpr(item);
            }
        }
        if (isRoot) {
            this.indentCount--;
        }
        return false;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        SQLBinaryOperator operator = x.getOperator();
        if (this.parameterized
                && operator == SQLBinaryOperator.BooleanOr) {
            x = SQLBinaryOpExpr.merge(this, x);

            operator = x.getOperator();
        }

        if (inputParameters != null
                && inputParameters.size() > 0
                && operator == SQLBinaryOperator.Equality
                && x.getRight() instanceof SQLVariantRefExpr
                ) {
            SQLVariantRefExpr right = (SQLVariantRefExpr) x.getRight();
            int index = right.getIndex();
            if (index >= 0 && index < inputParameters.size()) {
                Object param = inputParameters.get(index);
                if (param instanceof Collection) {
                    x.getLeft().accept(this);
                    print0(" IN (");
                    right.accept(this);
                    print(')');
                    return false;
                }
            }
        }

        SQLObject parent = x.getParent();
        boolean isRoot = parent instanceof SQLSelectQueryBlock;
        boolean relational = operator == SQLBinaryOperator.BooleanAnd
                             || operator == SQLBinaryOperator.BooleanOr;

        if (isRoot && relational) {
            this.indentCount++;
        }

        List<SQLExpr> groupList = new ArrayList<SQLExpr>();
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();

        if (operator.isRelational()
                && left instanceof SQLIntegerExpr
                && right instanceof SQLIntegerExpr) {
            print(((SQLIntegerExpr) left).getNumber().longValue());
            print(' ');
            printOperator(operator);
            print(' ');
            print(((SQLIntegerExpr) right).getNumber().longValue());
            return false;
        }

        for (;;) {
            if (left instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) left).getOperator() == operator) {
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

            visitBinaryLeft(item, operator);

            if (isPrettyFormat() && item.hasAfterComment()) {
                print(' ');
                printlnComment(item.getAfterCommentsDirect());
            }

            if (i != groupList.size() - 1 && isPrettyFormat() && item.getParent().hasAfterComment()) {
                print(' ');
                printlnComment(item.getParent().getAfterCommentsDirect());
            }

            boolean printOpSpace = true;
            if (relational) {
                println();
            } else {
                if (operator == SQLBinaryOperator.Modulus
                        && JdbcConstants.ORACLE.equals(dbType)
                        && left instanceof SQLIdentifierExpr
                        && right instanceof SQLIdentifierExpr
                        && ((SQLIdentifierExpr) right).getName().equalsIgnoreCase("NOTFOUND")) {
                    printOpSpace = false;
                }
                if (printOpSpace) {
                    print(' ');
                }
            }
            printOperator(operator);
            if (printOpSpace) {
                print(' ');
            }
        }

        visitorBinaryRight(x);

        if (isRoot && relational) {
            this.indentCount--;
        }

        return false;
    }

    protected void printOperator(SQLBinaryOperator operator) {
        print0(ucase ? operator.name : operator.name_lcase);
    }

    private void visitorBinaryRight(SQLBinaryOpExpr x) {
        if (isPrettyFormat() && x.getRight().hasBeforeComment()) {
            printlnComments(x.getRight().getBeforeCommentsDirect());
        }

        if (x.getRight() instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr right = (SQLBinaryOpExpr) x.getRight();
            SQLBinaryOperator rightOp = right.getOperator();
            SQLBinaryOperator op = x.getOperator();
            boolean rightRational = rightOp == SQLBinaryOperator.BooleanAnd
                                    || rightOp == SQLBinaryOperator.BooleanOr;

            if (rightOp.priority >= op.priority
                    || (right.isBracket()
                    && rightOp != op
                    && rightOp.isLogical()
                    && op.isLogical()
            )) {
                if (rightRational) {
                    incrementIndent();
                }

                print('(');
                printExpr(right);
                print(')');

                if (rightRational) {
                    decrementIndent();
                }
            } else {
                printExpr(right);
            }
        } else {
            printExpr(x.getRight());
        }

        if (x.getRight().hasAfterComment() && isPrettyFormat()) {
            print(' ');
            printlnComment(x.getRight().getAfterCommentsDirect());
        }
    }

    private void visitBinaryLeft(SQLExpr left, SQLBinaryOperator op) {
        if (left instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
            SQLBinaryOperator leftOp = binaryLeft.getOperator();
            boolean leftRational = leftOp == SQLBinaryOperator.BooleanAnd
                                   || leftOp == SQLBinaryOperator.BooleanOr;

            if (leftOp.priority > op.priority
                    || (binaryLeft.isBracket()
                        && leftOp != op
                        && leftOp.isLogical()
                        && op.isLogical()
            )) {
                if (leftRational) {
                    incrementIndent();
                }
                print('(');
                printExpr(left);
                print(')');

                if (leftRational) {
                    decrementIndent();
                }
            } else {
                printExpr(left);
            }
        } else {
            printExpr(left);
        }
    }

    protected void printTableSource(SQLTableSource x) {
        Class<?> clazz = x.getClass();
        if (clazz == SQLJoinTableSource.class) {
            visit((SQLJoinTableSource) x);
        } else  if (clazz == SQLExprTableSource.class) {
            visit((SQLExprTableSource) x);
        } else  if (clazz == SQLSubqueryTableSource.class) {
            visit((SQLSubqueryTableSource) x);
        } else {
            x.accept(this);
        }
    }

    protected void printQuery(SQLSelectQuery x) {
        Class<?> clazz = x.getClass();
        if (clazz == SQLSelectQueryBlock.class) {
            visit((SQLSelectQueryBlock) x);
        } else if (clazz == SQLUnionQuery.class) {
            visit((SQLUnionQuery) x);
        } else {
            x.accept(this);
        }
    }

    protected final void printExpr(SQLExpr x) {
        Class<?> clazz = x.getClass();
        if (clazz == SQLIdentifierExpr.class) {
            visit((SQLIdentifierExpr) x);
        } else if (clazz == SQLPropertyExpr.class) {
            visit((SQLPropertyExpr) x);
        } else if (clazz == SQLAllColumnExpr.class) {
            print('*');
        } else if (clazz == SQLAggregateExpr.class) {
            visit((SQLAggregateExpr) x);
        } else if (clazz == SQLBinaryOpExpr.class) {
            visit((SQLBinaryOpExpr) x);
        } else if (clazz == SQLCharExpr.class) {
            visit((SQLCharExpr) x);
        } else if (clazz == SQLNullExpr.class) {
            visit((SQLNullExpr) x);
        } else if (clazz == SQLIntegerExpr.class) {
            visit((SQLIntegerExpr) x);
        } else if (clazz == SQLNumberExpr.class) {
            visit((SQLNumberExpr) x);
        } else {
            x.accept(this);
        }
    }

    public boolean visit(SQLCaseExpr x) {
        incrementIndent();
        print0(ucase ? "CASE " : "case ");

        SQLExpr valueExpr = x.getValueExpr();
        if (valueExpr != null) {
            valueExpr.accept(this);
        }

        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            println();
            x.getItems().get(i).accept(this);
        }

        SQLExpr elExpr = x.getElseExpr();
        if (elExpr != null) {
            println();
            print0(ucase ? "ELSE " : "else ");
            if (elExpr instanceof SQLCaseExpr) {
                incrementIndent();
                println();
                elExpr.accept(this);
                decrementIndent();
            } else {
                elExpr.accept(this);
            }
        }

        decrementIndent();
        println();
        print0(ucase ? "END" : "end");

        return false;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        print0(ucase ? "WHEN " : "when ");
        SQLExpr expr = x.getConditionExpr();
        expr.accept(this);
        print0(ucase ? " THEN " : " then ");
        SQLExpr valueExpr = x.getValueExpr();
        if (valueExpr instanceof SQLCaseExpr) {
            incrementIndent();
            println();
            valueExpr.accept(this);
            decrementIndent();
        } else {
            valueExpr.accept(this);
        }

        return false;
    }

    public boolean visit(SQLCaseStatement x) {
        print0(ucase ? "CASE" : "case");
        if (x.getValueExpr() != null) {
            print(' ');
            x.getValueExpr().accept(this);
        }
        incrementIndent();
        println();
        printlnAndAccept(x.getItems(), " ");

        if (x.getElseStatements().size() > 0) {
            println();
            print0(ucase ? "ELSE " : "else ");
            printlnAndAccept(x.getElseStatements(), "");
        }

        decrementIndent();

        println();
        print0(ucase ? "END CASE" : "end case");
        if (JdbcConstants.ORACLE.equals(dbType)) {
            print(';');
        }
        return false;
    }

    public boolean visit(SQLCaseStatement.Item x) {
        print0(ucase ? "WHEN " : "when ");
        x.getConditionExpr().accept(this);
        print0(ucase ? " THEN " : " then ");

        SQLStatement stmt = x.getStatement();
        if (stmt != null) {
            stmt.accept(this);
            print(';');
        }
        return false;
    }

    public boolean visit(SQLCastExpr x) {
        print0(ucase ? "CAST(" : "cast(");
        x.getExpr().accept(this);
        print0(ucase ? " AS " : " as ");
        x.getDataType().accept(this);
        print0(")");

        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();
            if (this instanceof ExportParameterVisitor || this.parameters != null) {
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        printChars(x.getText());

        return false;
    }

    protected void printChars(String text) {
        if (text == null) {
            print0(ucase ? "NULL" : "null");
        } else {
            print('\'');
            int index = text.indexOf('\'');
            if (index >= 0) {
                text = text.replaceAll("'", "''");
            }
            print0(text);
            print('\'');
        }
    }

    public boolean visit(SQLDataType x) {
        printDataType(x);

        return false;
    }

    protected void printDataType(SQLDataType x) {
        boolean parameterized = this.parameterized;
        this.parameterized = false;

        print0(x.getName());
        if (x.getArguments().size() > 0) {
            print('(');
            printAndAccept(x.getArguments(), ", ");
            print(')');
        }

        Boolean withTimeZone = x.getWithTimeZone();
        if (withTimeZone != null) {
            if (withTimeZone) {
                if (x.isWithLocalTimeZone()) {
                    print0(ucase ? " WITH LOCAL TIME ZONE" : " with local time zone");
                } else {
                    print0(ucase ? " WITH TIME ZONE" : " with time zone");
                }
            } else {
                print0(ucase ? " WITHOUT TIME ZONE" : " without time zone");
            }
        }
        this.parameterized = parameterized;
    }

    public boolean visit(SQLCharacterDataType x) {
        visit((SQLDataType) x);

        List<SQLCommentHint> hints = ((SQLCharacterDataType) x).hints;
        if (hints != null) {
            print(' ');
            for (SQLCommentHint hint : hints) {
                hint.accept(this);
            }
        }

        return false;
    }

    public boolean visit(SQLExistsExpr x) {
        if (x.isNot()) {
            print0(ucase ? "NOT EXISTS (" : "not exists (");
        } else {
            print0(ucase ? "EXISTS (" : "exists (");
        }
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(')');
        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        final String name = x.getName();
        if (!this.parameterized) {
            print0(x.getName());
            return false;
        }

        return printName(x, name);
    }

    private boolean printName(SQLName x, String name) {
        boolean shardingSupport = this.shardingSupport
                && this.parameterized;
        return printName(x, name, shardingSupport);
    }

    public String unwrapShardingTable(String name) {
        char c0 = name.charAt(0);
        char c_last = name.charAt(name.length() - 1);
        final boolean quote = (c0 == '`' && c_last == '`') || (c0 == '"' && c_last == '"');

        int end = name.length();
        if (quote) {
            end--;
        }

        int num_cnt = 0, postfixed_cnt = 0;
        for (int i = end - 1; i > 0; --i, postfixed_cnt++) {
            char ch = name.charAt(i);
            if (ch >= '0' && ch <= '9') {
                num_cnt++;
            }

            if (ch != '_' && (ch < '0' || ch > '9')) {
                break;
            }
        }
        if (num_cnt < 1 || postfixed_cnt < 2) {
            return name;
        }

        int start = end - postfixed_cnt;
        if (start < 1) {
            return name;
        }

        String realName = name.substring(quote ? 1 : 0, start);
        return realName;
    }

    private boolean printName(SQLName x, String name, boolean shardingSupport) {

        if (shardingSupport) {
            SQLObject parent = x.getParent();
            shardingSupport = parent instanceof SQLExprTableSource || parent instanceof SQLPropertyExpr;

            if (parent instanceof SQLPropertyExpr && parent.getParent() instanceof SQLExprTableSource) {
                shardingSupport = false;
            }
        }

        if (shardingSupport) {
            final boolean quote = name.charAt(0) == '`' && name.charAt(name.length() - 1) == '`';

            String unwrappedName = unwrapShardingTable(name);
            if (unwrappedName != name) {
                boolean isAlias = false;
                for (SQLObject parent = x.getParent(); parent != null; parent = parent.getParent()) {
                    if (parent instanceof SQLSelectQueryBlock) {
                        SQLTableSource from = ((SQLSelectQueryBlock) parent).getFrom();
                        if (quote) {
                            String name2 = name.substring(1, name.length() - 1);
                            if (isTableSourceAlias(from, name, name2)) {
                                isAlias = true;
                            }
                        } else {
                            if (isTableSourceAlias(from, name)) {
                                isAlias = true;
                            }
                        }
                        break;
                    }
                }

                if (!isAlias) {
                    print0(unwrappedName);
                    incrementReplaceCunt();
                    return false;
                } else {
                    print0(name);
                    return false;
                }
            }
        }
        print0(name);
        return false;
    }

    public boolean visit(SQLInListExpr x) {
        if (this.parameterized) {
            List<SQLExpr> targetList = x.getTargetList();

            boolean allLiteral = true;
            for (SQLExpr item : targetList) {
                if (!(item instanceof SQLLiteralExpr || item instanceof SQLVariantRefExpr)) {
                    allLiteral = false;
                    break;
                }
            }

            if (allLiteral) {
                boolean changed = true;
                if (targetList.size() == 1 && targetList.get(0) instanceof SQLVariantRefExpr) {
                    changed = false;
                }

                x.getExpr().accept(this);

                if (x.isNot()) {
                    print(isUppCase() ? " NOT IN (?)" : " not in (?)");
                } else {
                    print(isUppCase() ? " IN (?)" : " in (?)");
                }

                if (changed) {
                    incrementReplaceCunt();
                    if (this instanceof ExportParameterVisitor || this.parameters != null) {
                        if (parameterizedMergeInList) {
                            List<Object> subList = new ArrayList<Object>(x.getTargetList().size());
                            for (SQLExpr target : x.getTargetList()) {
                                ExportParameterVisitorUtils.exportParameter(subList, target);
                            }
                            if (subList != null) {
                                parameters.add(subList);
                            }
                        } else {
                            for (SQLExpr target : x.getTargetList()) {
                                ExportParameterVisitorUtils.exportParameter(this.parameters, target);
                            }
                        }
                    }
                }

                return false;
            }
        }

        x.getExpr().accept(this);

        if (x.isNot()) {
            print0(ucase ? " NOT IN (" : " not in (");
        } else {
            print0(ucase ? " IN (" : " in (");
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
                    print0(", ");
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
        boolean parameterized = this.parameterized;
        printInteger(x, parameterized);
        return false;
    }

    protected void printInteger(SQLIntegerExpr x, boolean parameterized) {
        long val = x.getNumber().longValue();

        if (val == 1) {
            if (JdbcConstants.ORACLE.equals(dbType)) {
                SQLObject parent = x.getParent();
                if (parent instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) parent;
                    SQLExpr left = binaryOpExpr.getLeft();
                    SQLBinaryOperator op = binaryOpExpr.getOperator();
                    if (left instanceof SQLIdentifierExpr
                            && op == SQLBinaryOperator.Equality) {
                        String name = ((SQLIdentifierExpr) left).getName();
                        if ("rownum".equals(name)) {
                            print(1);
                            return;
                        }
                    }
                }
            }
        }
        if (parameterized) {
            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return;
        }

        print(val);
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        SQLExpr owner = x.getOwner();
        if (owner != null) {
            printMethodOwner(owner);
        }

        String function = x.getMethodName();
        List<SQLExpr> parameters = x.getParameters();

        printFunctionName(function);
        print('(');

        String trimOption = x.getTrimOption();
        if (trimOption != null) {
            print0(trimOption);

            if (parameters.size() > 0) {
                print(' ');
            }
        }


        for (int i = 0, size = parameters.size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLExpr param = x.getParameters().get(i);

            if (this.parameterized) {
                if (size == 2 && i == 1 && param instanceof SQLCharExpr) {
                    if (JdbcUtils.ORACLE.equals(dbType)) {
                        if ("TO_CHAR".equalsIgnoreCase(function)
                                || "TO_DATE".equalsIgnoreCase(function)) {
                            printChars(((SQLCharExpr) param).getText());
                            continue;
                        }
                    } else if (JdbcConstants.MYSQL.equals(dbType)) {
                        if ("DATE_FORMAT".equalsIgnoreCase(function)) {
                            printChars(((SQLCharExpr) param).getText());
                            continue;
                        }
                    }
                }

            }

            if (param instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) param;
                SQLBinaryOperator op = binaryOpExpr.getOperator();
                if (op == SQLBinaryOperator.BooleanAnd || op == SQLBinaryOperator.BooleanOr) {
                    incrementIndent();
                    param.accept(this);
                    decrementIndent();
                    continue;
                }
            }

            param.accept(this);
        }

        SQLExpr from = x.getFrom();
        if (from != null) {
            print0(ucase ? " FROM " : " from ");
            from.accept(this);

            SQLExpr _for = x.getFor();
            if (_for != null) {
                print0(ucase ? " FOR " : " for ");
                _for.accept(this);
            }
        }

        SQLExpr using = x.getUsing();
        if (using != null) {
            print0(ucase ? " USING " : " using ");
            using.accept(this);
        }

        print(')');
        return false;
    }

    protected void printMethodOwner(SQLExpr owner) {
        owner.accept(this);
        print('.');
    }

    protected void printFunctionName(String name) {
        print0(name);
    }

    public boolean visit(SQLAggregateExpr x) {
        boolean parameterized = this.parameterized;
        this.parameterized = false;

        print0(ucase ? x.getMethodName() : x.getMethodName().toLowerCase());
        print('(');

        if (x.getOption() != null) {
            print0(x.getOption().toString());
            print(' ');
        }

        List<SQLExpr> arguments = x.getArguments();
        for (int i = 0, size = arguments.size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            printExpr(arguments.get(i));
        }

        visitAggreateRest(x);

        print(')');

        if (x.getWithinGroup() != null) {
            print0(ucase ? " WITHIN GROUP (" : " within group (");
            x.getWithinGroup().accept(this);
            print(')');
        }
        
        if (x.getKeep() != null) {
            print(' ');
            x.getKeep().accept(this);
        }

        if (x.getOver() != null) {
            print(' ');
            x.getOver().accept(this);
        }

        this.parameterized = parameterized;
        return false;
    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {

    }

    public boolean visit(SQLAllColumnExpr x) {
        print('*');
        return true;
    }

    public boolean visit(SQLNCharExpr x) {
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        if ((x.getText() == null) || (x.getText().length() == 0)) {
            print0(ucase ? "NULL" : "null");
        } else {
            print0(ucase ? "N'" : "n'");
            print0(x.getText().replace("'", "''"));
            print('\'');
        }
        return false;
    }

    public boolean visit(SQLNotExpr x) {
        print0(ucase ? "NOT " : "not ");
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
        if (this.parameterized
                && x.getParent() instanceof ValuesClause) {
            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                this.getParameters().add(null);
            }
            return false;
        }

        print0(ucase ? "NULL" : "null");
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                ExportParameterVisitorUtils.exportParameter((this).getParameters(), x);
            }
            return false;
        }

        if (appender instanceof StringBuilder) {
            x.output((StringBuilder) appender);
        } else if (appender instanceof StringBuilder) {
            x.output((StringBuilder) appender);
        } else {
            print0(x.getNumber().toString());
        }
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        SQLExpr owner = x.getOwner();

        String mapTableName = null, ownerName = null;
        if (owner instanceof SQLIdentifierExpr) {
            ownerName = ((SQLIdentifierExpr) owner).getName();
            if (tableMapping != null) {
                mapTableName = tableMapping.get(ownerName);

                if (mapTableName == null
                        && ownerName.length() > 2
                        && ownerName.charAt(0) == '`'
                        && ownerName.charAt(ownerName.length() - 1) == '`') {
                    ownerName = ownerName.substring(1, ownerName.length() - 1);
                    mapTableName = tableMapping.get(ownerName);
                }
            }
        }

        if (mapTableName != null) {
            for (SQLObject parent = x.getParent();parent != null; parent = parent.getParent()) {
                if (parent instanceof SQLSelectQueryBlock) {
                    SQLTableSource from = ((SQLSelectQueryBlock) parent).getFrom();
                    if (isTableSourceAlias(from, mapTableName, ownerName)) {
                        mapTableName = null;
                    }
                    break;
                }
            }
        }

        if (mapTableName != null) {
            print0(mapTableName);
        } else {
            printExpr(owner);
        }
        print('.');

        String name = x.getName();
        printName(x, name);

        return false;
    }

    protected boolean isTableSourceAlias(SQLTableSource from, String... tableNames) {
        String alias = from.getAlias();

        if (alias != null) {
            for (String tableName : tableNames) {
                if (alias.equalsIgnoreCase(tableName)) {
                    return true;
                }
            }

            if (alias.length() > 2 && alias.charAt(0) == '`' && alias.charAt(alias.length() -1) == '`') {
                alias = alias.substring(1, alias.length() -1);
                for (String tableName : tableNames) {
                    if (alias.equalsIgnoreCase(tableName)) {
                        return true;
                    }
                }
            }
        }
        if (from instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) from;
            return isTableSourceAlias(join.getLeft(), tableNames)
                    || isTableSourceAlias(join.getRight(), tableNames);
        }
        return false;
    }

    public boolean visit(SQLQueryExpr x) {
        SQLObject parent = x.getParent();
        if (parent instanceof SQLSelect) {
            parent = parent.getParent();
        }

        if (parent instanceof ValuesClause) {
            println();
            print('(');
            x.getSubQuery().accept(this);
            print(')');
            println();
        } else if (parent instanceof SQLStatement
                && !(parent instanceof OracleForStatement)) {
            incrementIndent();

            println();
            x.getSubQuery().accept(this);

            decrementIndent();
        } else if (parent instanceof SQLOpenStatement) {
            x.getSubQuery().accept(this);
        } else {
            print('(');
            incrementIndent();
            println();
            x.getSubQuery().accept(this);
            decrementIndent();
            println();
            print(')');
        }
        return false;
    }

    public boolean visit(SQLSelectGroupByClause x) {

        boolean oracle = JdbcConstants.ORACLE.equals(dbType);
        boolean rollup = x.isWithRollUp();
        boolean cube = x.isWithCube();

        int itemSize = x.getItems().size();
        if (itemSize > 0) {
            print0(ucase ? "GROUP BY " : "group by ");
            if (oracle && rollup) {
                print0(ucase ? "ROLLUP (" : "rollup (");
            } else if (oracle && cube) {
                print0(ucase ? "CUBE (" : "cube (");
            }
            incrementIndent();
            for (int i = 0; i < itemSize; ++i) {
                if (i != 0) {
                    if (groupItemSingleLine) {
                        println(", ");
                    } else {
                        print(", ");
                    }
                }
                x.getItems().get(i).accept(this);
            }
            if (oracle && rollup) {
                print(')');
            }
            decrementIndent();
        }

        if (x.getHaving() != null) {
            println();
            print0(ucase ? "HAVING " : "having ");
            x.getHaving().accept(this);
        }

        if (x.isWithRollUp() && !oracle) {
            print0(ucase ? " WITH ROLLUP" : " with rollup");
        }

        if (x.isWithCube() && !oracle) {
            print0(ucase ? " WITH CUBE" : " with cube");
        }

        return false;
    }

    public boolean visit(SQLSelect x) {
        SQLWithSubqueryClause withSubQuery = x.getWithSubQuery();
        if (withSubQuery != null) {
            withSubQuery.accept(this);
            println();
        }

        printQuery(x.getQuery());

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        if (x.getHintsSize() > 0) {
            printAndAccept(x.getHints(), "");
        }

        return false;
    }

    public boolean visit(SQLSelectQueryBlock x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "SELECT " : "select ");

        if (JdbcConstants.INFORMIX.equals(dbType)) {
            printFetchFirst(x);
        }

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");;
            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            where.accept(this);
        }

        printHierarchical(x);

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            println();
            visit(groupBy);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        if (!JdbcConstants.INFORMIX.equals(dbType)) {
            printFetchFirst(x);
        }

        if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
        }

        return false;
    }

    protected void printFetchFirst(SQLSelectQueryBlock x) {
        SQLLimit limit = x.getLimit();
        if (limit == null) {
            return;
        }

        SQLExpr offset = limit.getOffset();
        SQLExpr first = limit.getRowCount();

        if (limit != null) {
            if (JdbcConstants.INFORMIX.equals(dbType)) {
                if (offset != null) {
                    print0(ucase ? "SKIP " : "skip ");
                    offset.accept(this);
                }

                print0(ucase ? " FIRST " : " first ");
                first.accept(this);
                print(' ');
            } else if (JdbcConstants.DB2.equals(dbType)
                    || JdbcConstants.ORACLE.equals(dbType)
                    || JdbcConstants.SQL_SERVER.equals(dbType)) {
                //order by 语句必须在FETCH FIRST ROWS ONLY之前
                SQLObject parent = x.getParent();
                if (parent instanceof SQLSelect) {
                    SQLOrderBy orderBy = ((SQLSelect) parent).getOrderBy();
                    if (orderBy != null && orderBy.getItems().size() > 0) {
                        println();
                        print0(ucase ? "ORDER BY " : "order by ");
                        printAndAccept(orderBy.getItems(), ", ");
                    }
                }

                println();

                if (offset != null) {
                    print0(ucase ? "OFFSET " : "offset ");
                    offset.accept(this);
                    print0(ucase ? " ROWS" : " rows");
                }

                if (first != null) {
                    if (offset != null) {
                        print(' ');
                    }
                    if (JdbcConstants.SQL_SERVER.equals(dbType) && offset != null) {
                        print0(ucase ? "FETCH NEXT " : "fetch next ");
                    } else {
                        print0(ucase ? "FETCH FIRST " : "fetch first ");
                    }
                    first.accept(this);
                    print0(ucase ? " ROWS ONLY" : " rows only");
                }
            } else {
                println();
                limit.accept(this);
            }
        }
    }

    public boolean visit(SQLSelectItem x) {
        if (x.isConnectByRoot()) {
            print0(ucase ? "CONNECT_BY_ROOT " : "connect_by_root ");
        }

        SQLExpr expr = x.getExpr();

        printExpr(expr);

        String alias = x.getAlias();
        if (alias != null && alias.length() > 0) {
            print0(ucase ? " AS " : " as ");
            if (alias.indexOf(' ') == -1 || alias.charAt(0) == '"' || alias.charAt(0) == '\'') {
                print0(alias);
            } else {
                print('"');
                print0(alias);
                print('"');
            }
        }
        return false;
    }

    public boolean visit(SQLOrderBy x) {
        if (x.getItems().size() > 0) {
            if (x.isSibings()) {
                print0(ucase ? "ORDER SIBLINGS BY " : "order siblings by ");
            } else {
                print0(ucase ? "ORDER BY " : "order by ");
            }

            printAndAccept(x.getItems(), ", ");
        }
        return false;
    }

    public boolean visit(SQLSelectOrderByItem x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLIntegerExpr) {
            print(((SQLIntegerExpr) expr).getNumber().longValue());
        } else {
            printExpr(expr);
        }
        if (x.getType() != null) {
            print(' ');
            SQLOrderingSpecification type = x.getType();
            print0(ucase ? type.name : type.name_lcase);
        }

        if (x.getCollate() != null) {
            print0(ucase ? " COLLATE " : " collate ");
            print0(x.getCollate());
        }

        if (x.getNullsOrderType() != null) {
            print(' ');
            print0(x.getNullsOrderType().toFormalString());
        }

        return false;
    }

    protected void addTable(String table) {
        if (tables == null) {
            tables = new LinkedHashSet<String>();
        }
        this.tables.add(table);
    }

    protected void printTableSourceExpr(SQLExpr expr) {
        if (exportTables) {
            addTable(expr.toString());
        }

        if (isEnabled(VisitorFeature.OutputDesensitize)) {
            SQLExpr owner = null;
            String ident = null;
            if (expr instanceof SQLIdentifierExpr) {
                ident = ((SQLIdentifierExpr) expr).getName();
            } else if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                propertyExpr.getOwner().accept(this);
                print('.');

                ident = propertyExpr.getName();
            }

            if (ident != null) {
                String desensitizeTable = SQLUtils.desensitizeTable(ident);
                print0(desensitizeTable);
                return;
            }
        }

        if (tableMapping != null && expr instanceof SQLName) {
            String tableName;
            if (expr instanceof SQLIdentifierExpr) {
                tableName = ((SQLIdentifierExpr) expr).normalizedName();
            } else if (expr instanceof SQLPropertyExpr) {
                tableName = ((SQLPropertyExpr) expr).normalizedName();
            } else {
                tableName = expr.toString();
            }

            String destTableName = tableMapping.get(tableName);
            if (destTableName == null) {
                if (expr instanceof SQLPropertyExpr) {
                    SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                    String propName = propertyExpr.getName();
                    destTableName = tableMapping.get(propName);
                    if (destTableName == null
                            && propName.length() > 2 && propName.charAt(0) == '`' && propName.charAt(propName.length() - 1) == '`') {
                        destTableName = tableMapping.get(propName.substring(1, propName.length() - 1));
                    }

                    if (destTableName != null) {
                        propertyExpr.getOwner().accept(this);
                        print('.');
                        print(destTableName);
                        return;
                    }
                } else if (expr instanceof SQLIdentifierExpr) {
                    boolean quote = tableName.length() > 2 && tableName.charAt(0) == '`' && tableName.charAt(tableName.length() - 1) == '`';
                    if (quote) {
                        destTableName = tableMapping.get(tableName.substring(1, tableName.length() - 1));
                    }
                }
            }
            if (destTableName != null) {
                tableName = destTableName;
                print0(tableName);
                return;
            }
        }

        if (expr instanceof SQLIdentifierExpr) {
            visit((SQLIdentifierExpr) expr);
        } else if (expr instanceof SQLPropertyExpr) {
            visit((SQLPropertyExpr) expr);
        } else {
            expr.accept(this);
        }

    }

    public boolean visit(SQLExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        if (isPrettyFormat() && x.hasAfterComment()) {
            print(' ');
            printlnComment(x.getAfterCommentsDirect());
        }

        return false;
    }

    public boolean visit(SQLSelectStatement stmt) {
        List<SQLCommentHint> headHints = stmt.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        SQLSelect select = stmt.getSelect();
        this.visit(select);

        return false;
    }

    public boolean visit(SQLVariantRefExpr x) {
        int index = x.getIndex();

        if (index < 0 || inputParameters == null || index >= inputParameters.size()) {
            print0(x.getName());
            return false;
        }

        Object param = inputParameters.get(index);

        SQLObject parent = x.getParent();

        boolean in;
        if (parent instanceof SQLInListExpr) {
            in = true;
        } else if (parent instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) parent;
            if (binaryOpExpr.getOperator() == SQLBinaryOperator.Equality) {
                in = true;
            } else {
                in = false;
            }
        } else {
            in = false;
        }

        if (in && param instanceof Collection) {
            boolean first = true;
            for (Object item : (Collection) param) {
                if (!first) {
                    print0(", ");
                }
                printParameter(item);
                first = false;
            }
        } else {
            printParameter(param);
        }
        return false;
    }

    public void printParameter(Object param) {
        if (param == null) {
            print0(ucase ? "NULL" : "null");
            return;
        }

        if (param instanceof Number //
            || param instanceof Boolean) {
            print0(param.toString());
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
            print0("'<InputStream>");
            return;
        }

        if (param instanceof Reader) {
            print0("'<Reader>");
            return;
        }

        if (param instanceof Blob) {
            print0("'<Blob>");
            return;
        }

        if (param instanceof NClob) {
            print0("'<NClob>");
            return;
        }

        if (param instanceof Clob) {
            print0("'<Clob>");
            return;
        }

        if (param instanceof byte[]) {
            byte[] bytes = (byte[]) param;
            int bytesLen = bytes.length;
            char[] chars = new char[bytesLen * 2 + 3];
            chars[0] = 'x';
            chars[1] = '\'';
            for (int i = 0; i < bytes.length; i++) {
                int a = bytes[i] & 0xFF;
                int b0 = a >> 4;
                int b1 = a & 0xf;

                chars[i * 2 + 2] = (char) (b0 + (b0 < 10 ? 48 : 55)); //hexChars[b0];
                chars[i * 2 + 3] = (char) (b1 + (b1 < 10 ? 48 : 55));
            }
            chars[chars.length - 1] = '\'';
            print0(new String(chars));
            return;
        }

        print0("'" + param.getClass().getName() + "'");
    }

    public boolean visit(SQLDropTableStatement x) {
        print0(ucase ? "DROP " : "drop ");
        List<SQLCommentHint> hints = x.getHints();
        if (hints != null) {
            printAndAccept(hints, " ");
            print(' ');
        };

        if (x.isTemporary()) {
            print0(ucase ? "TEMPORARY TABLE " : "temporary table ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        printAndAccept(x.getTableSources(), ", ");

        if (x.isCascade()) {
            printCascade();
        }

        if (x.isRestrict()) {
            print0(ucase ? " RESTRICT" : " restrict");
        }

        if (x.isPurge()) {
            print0(ucase ? " PURGE" : " purge");
        }

        return false;
    }

    protected void printCascade() {
        print0(ucase ? " CASCADE" : " cascade");
    }

    public boolean visit(SQLDropViewStatement x) {
        print0(ucase ? "DROP VIEW " : "drop view ");

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
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
        boolean parameterized = this.parameterized;
        this.parameterized = false;

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
                print0(ucase ? " ENABLE" : " enable");
            }
        }

        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        this.parameterized = parameterized;

        return false;
    }

    @Override
    public boolean visit(SQLColumnDefinition.Identity x) {
        print0(ucase ? "IDENTITY" : "identity");
        if (x.getSeed() != null) {
            print0(" (");
            print(x.getSeed());
            print0(", ");
            print(x.getIncrement());
            print(')');
        }
        return false;
    }

    protected void visitColumnDefault(SQLColumnDefinition x) {
        print0(ucase ? " DEFAULT " : " default ");
        x.getDefaultExpr().accept(this);
    }

    public boolean visit(SQLDeleteStatement x) {
        SQLTableSource from = x.getFrom();

        if (from == null) {
            print0(ucase ? "DELETE FROM " : "delete from ");
            printTableSourceExpr(x.getTableName());
        } else {
            print0(ucase ? "DELETE " : "delete ");
            printTableSourceExpr(x.getTableName());
            print0(ucase ? " FROM " : " from ");
            from.accept(this);
        }

        if (x.getWhere() != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            incrementIndent();
            x.getWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    public boolean visit(SQLCurrentOfCursorExpr x) {
        print0(ucase ? "CURRENT OF " : "current of ");
        x.getCursorName().accept(this);
        return false;
    }

    public boolean visit(SQLInsertStatement x) {
        if (x.isUpsert()) {
            print0(ucase ? "UPSERT INTO " : "upsert into ");
        } else {
            print0(ucase ? "INSERT INTO " : "insert into ");
        }

        x.getTableSource().accept(this);

        printInsertColumns(x.getColumns());

        if (!x.getValuesList().isEmpty()) {
            println();
            print0(ucase ? "VALUES " : "values ");
            printAndAccept(x.getValuesList(), ", ");
        } else {
            if (x.getQuery() != null) {
                println();
                x.getQuery().accept(this);
            }
        }

        return false;
    }

    protected void printInsertColumns(List<SQLExpr> columns) {
        final int size = columns.size();
        if (size > 0) {
            if (size > 5) {
                incrementIndent();
                println();
            } else {
                print(' ');
            }
            print('(');
            for (int i = 0; i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }

                SQLExpr column = columns.get(i);
                column.accept(this);

                String dataType = (String) column.getAttribute("dataType");
                if (dataType != null) {
                    print(' ');
                    print(dataType);
                }
            }
            print(')');
            if (size > 5) {
                decrementIndent();
            }
        }
    }

    public boolean visit(SQLUpdateSetItem x) {
        x.getColumn().accept(this);
        print0(" = ");
        x.getValue().accept(this);
        return false;
    }

    public boolean visit(SQLUpdateStatement x) {
        print0(ucase ? "UPDATE " : "update ");

        x.getTableSource().accept(this);

        println();
        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getItems().get(i).accept(this);
        }

        if (x.getWhere() != null) {
            println();
            incrementIndent();
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    protected void printTableElements(List<SQLTableElement> tableElementList) {
        int size = tableElementList.size();
        if (size > 0) {
            print0(" (");
            incrementIndent();
            println();
            for (int i = 0; i < size; ++i) {
                if (i != 0) {
                    print0(",");
                    println();
                }
                tableElementList.get(i).accept(this);
            }
            decrementIndent();
            println();
            print(')');
        }
    }

    public boolean visit(SQLCreateTableStatement x) {
        printCreateTable(x);

        return false;
    }

    protected void printCreateTable(SQLCreateTableStatement x) {
        print0(ucase ? "CREATE " : "create ");

        final SQLCreateTableStatement.Type tableType = x.getType();
        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(tableType)) {
            print0(ucase ? "GLOBAL TEMPORARY " : "global temporary ");
        } else if (SQLCreateTableStatement.Type.LOCAL_TEMPORARY.equals(tableType)) {
            print0(ucase ? "LOCAL TEMPORARY " : "local temporary ");
        }
        print0(ucase ? "TABLE " : "table ");

        printTableSourceExpr(x.getName());

        printTableElements(x.getTableElementList());

        if (x.getInherits() != null) {
            print0(ucase ? " INHERITS (" : " inherits (");
            x.getInherits().accept(this);
            print(')');
        }
    }

    public boolean visit(SQLUniqueConstraint x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }

        print0(ucase ? "UNIQUE (" : "unique (");
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');
        return false;
    }

    public boolean visit(SQLNotNullConstraint x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "NOT NULL" : "not null");

        List<SQLCommentHint> hints = x.hints;

        if (hints != null) {
            print(' ');
            for (SQLCommentHint hint : hints) {
                hint.accept(this);
            }
        }

        return false;
    }

    public boolean visit(SQLNullConstraint x) {
    	if (x.getName() != null) {
    		print0(ucase ? "CONSTRAINT " : "constraint ");
    		x.getName().accept(this);
    		print(' ');
    	}
    	print0(ucase ? "NULL" : "null");
    	return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        SQLUnionOperator operator = x.getOperator();

        boolean bracket = x.isBracket() && !(x.getParent() instanceof SQLUnionQueryTableSource);

        if (bracket) {
            print('(');
        }
        x.getLeft().accept(this);

        println();
        print0(ucase ? operator.name : operator.name_lcase);
        println();

        SQLSelectQuery right = x.getRight();
        boolean needParen = x.getOrderBy() != null && !right.isBracket();

        if (needParen) {
            print('(');
            right.accept(this);
            print(')');
        } else {
            right.accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (bracket) {
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        print0(x.getOperator().name);

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
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        print0("0x");
        print0(x.getHex());

        String charset = (String) x.getAttribute("USING");
        if (charset != null) {
            print0(ucase ? " USING " : " using ");
            print0(charset);
        }

        return false;
    }

    @Override
    public boolean visit(SQLSetStatement x) {
        boolean printSet = x.getAttribute("parser.set") == Boolean.TRUE || !JdbcConstants.ORACLE.equals(dbType);
        if (printSet) {
            print0(ucase ? "SET " : "set ");
        }
        printAndAccept(x.getItems(), ", ");

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        return false;
    }

    @Override
    public boolean visit(SQLAssignItem x) {
        x.getTarget().accept(this);
        print0(" = ");
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCallStatement x) {
        if (x.isBrace()) {
            print('{');
        }
        if (x.getOutParameter() != null) {
            x.getOutParameter().accept(this);
            print0(" = ");
        }

        print0(ucase ? "CALL " : "call ");
        x.getProcedureName().accept(this);
        print('(');

        printAndAccept(x.getParameters(), ", ");
        print(')');
        if (x.isBrace()) {
            print('}');
        }
        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        SQLTableSource left = x.getLeft();

        if (left instanceof SQLJoinTableSource
                && ((SQLJoinTableSource) left).getJoinType() == JoinType.COMMA
                && x.getJoinType() != JoinType.COMMA) {
            print('(');
            printTableSource(left);
            print(')');
        } else {
            printTableSource(left);
        }
        incrementIndent();

        if (x.getJoinType() == JoinType.COMMA) {
            print(',');
        } else {
            println();

            if (x.isNatural()) {
                print0(ucase ? "NATURAL " : "natural ");
            }

            printJoinType(x.getJoinType());
        }
        print(' ');

        SQLTableSource right = x.getRight();
        if (right instanceof SQLJoinTableSource) {
            print('(');
            printTableSource(right);
            print(')');
        } else {
            printTableSource(right);
        }

        SQLExpr condition = x.getCondition();
        if (condition != null) {
            boolean newLine = false;

            if(right instanceof SQLSubqueryTableSource) {
                newLine = true;
            } else if (condition instanceof SQLBinaryOpExpr) {
                SQLBinaryOperator op = ((SQLBinaryOpExpr) condition).getOperator();
                if (op == SQLBinaryOperator.BooleanAnd || op == SQLBinaryOperator.BooleanOr) {
                    newLine = true;
                }
            } else if (condition instanceof SQLBinaryOpExprGroup) {
                newLine = true;
            }
            if (newLine) {
                println();
            } else {
                print(' ');
            }
            incrementIndent();
            print0(ucase ? "ON " : "on ");
            printExpr(condition);
            decrementIndent();
        }

        if (x.getUsing().size() > 0) {
            print0(ucase ? " USING (" : " using (");
            printAndAccept(x.getUsing(), ", ");
            print(')');
        }

        if (x.getAlias() != null) {
            print0(ucase ? " AS " : " as ");
            print0(x.getAlias());
        }

        decrementIndent();

        return false;
    }

    protected void printJoinType(JoinType joinType) {
        print0(ucase ? joinType.name : joinType.name_lcase);
    }

    @Override
    public boolean visit(ValuesClause x) {
        if ((!this.parameterized)
                && isEnabled(VisitorFeature.OutputUseInsertValueClauseOriginalString)
                && x.getOriginalString() != null) {
            print0(x.getOriginalString());
            return false;
        }

        print('(');
        this.indentCount++;

        final List<SQLExpr> values = x.getValues();
        for (int i = 0, size = values.size(); i < size; ++i) {
            if (i != 0) {
                if (i % 5 == 0) {
                    println();
                }
                print0(", ");
            }

            SQLExpr expr = values.get(i);
            if (expr instanceof SQLIntegerExpr) {
                printInteger((SQLIntegerExpr) expr, parameterized);
            } else if (expr instanceof SQLCharExpr) {
                visit((SQLCharExpr) expr);
            } else if (expr instanceof SQLBooleanExpr) {
                visit((SQLBooleanExpr) expr);
            } else if (expr instanceof SQLNumberExpr) {
                visit((SQLNumberExpr) expr);
            } else if (expr instanceof SQLNullExpr) {
                visit((SQLNullExpr) expr);
            } else if (expr instanceof SQLVariantRefExpr) {
                visit((SQLVariantRefExpr) expr);
            } else if (expr instanceof SQLNCharExpr) {
                visit((SQLNCharExpr) expr);
            } else {
                expr.accept(this);
            }
        }

        this.indentCount--;
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLSomeExpr x) {
        print0(ucase ? "SOME (" : "some (");
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAnyExpr x) {
        print0(ucase ? "ANY (" : "any (");
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAllExpr x) {
        print0(ucase ? "ALL (" : "all (");
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        x.getExpr().accept(this);
        if (x.isNot()) {
            print0(ucase ? " NOT IN (" : " not in (");
        } else {
            print0(ucase ? " IN (" : " in (");
        }

        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(')');

        return false;
    }

    @Override
    public boolean visit(SQLListExpr x) {
        print('(');
        printAndAccept(x.getItems(), ", ");
        print(')');

        return false;
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        print('(');
        incrementIndent();
        println();
        this.visit(x.getSelect());
        decrementIndent();
        println();
        print(')');

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        return false;
    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        print0(ucase ? "TRUNCATE TABLE " : "truncate table ");
        printAndAccept(x.getTableSources(), ", ");
        
        if (x.isDropStorage()) {
            print0(ucase ? " DROP STORAGE" : " drop storage");    
        }
        
        if (x.isReuseStorage()) {
            print0(ucase ? " REUSE STORAGE" : " reuse storage");    
        }
        
        if (x.isIgnoreDeleteTriggers()) {
            print0(ucase ? " IGNORE DELETE TRIGGERS" : " ignore delete triggers");    
        }
        
        if (x.isRestrictWhenDeleteTriggers()) {
            print0(ucase ? " RESTRICT WHEN DELETE TRIGGERS" : " restrict when delete triggers");    
        }
        
        if (x.isContinueIdentity()) {
            print0(ucase ? " CONTINUE IDENTITY" : " continue identity");
        }
        
        if (x.isImmediate()) {
            print0(ucase ? " IMMEDIATE" : " immediate");    
        }
        
        return false;
    }

    @Override
    public boolean visit(SQLDefaultExpr x) {
        print0(ucase ? "DEFAULT" : "default");
        return false;
    }

    @Override
    public void endVisit(SQLCommentStatement x) {

    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        print0(ucase ? "COMMENT ON " : "comment on ");
        if (x.getType() != null) {
            print0(x.getType().name());
            print(' ');
        }
        x.getOn().accept(this);

        print0(ucase ? " IS " : " is ");
        x.getComment().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLUseStatement x) {
        print0(ucase ? "USE " : "use ");
        x.getDatabase().accept(this);
        return false;
    }

    protected boolean isOdps() {
        return JdbcConstants.ODPS.equals(dbType);
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        boolean odps = isOdps();
        if (odps) {
            print0(ucase ? "ADD COLUMNS (" : "add columns (");
        } else {
            print0(ucase ? "ADD (" : "add (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {
        print0(ucase ? "DROP COLUMN " : "drop column ");
        this.printAndAccept(x.getColumns(), ", ");

        if (x.isCascade()) {
            print0(ucase ? " CASCADE" : " cascade");
        }
        return false;
    }

    @Override
    public void endVisit(SQLAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(SQLDropIndexStatement x) {
        print0(ucase ? "DROP INDEX " : "drop index ");
        x.getIndexName().accept(this);

        SQLExprTableSource table = x.getTableName();
        if (table != null) {
            print0(ucase ? " ON " : " on ");
            table.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLSavePointStatement x) {
        print0(ucase ? "SAVEPOINT" : "savepoint");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLReleaseSavePointStatement x) {
        print0(ucase ? "RELEASE SAVEPOINT " : "release savepoint ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLRollbackStatement x) {
        print0(ucase ? "ROLLBACK" : "rollback");
        if (x.getTo() != null) {
            print0(ucase ? " TO " : " to ");
            x.getTo().accept(this);
        }
        return false;
    }

    public boolean visit(SQLCommentHint x) {
        print0("/*");
        print0(x.getText());
        print0("*/");
        return false;
    }

    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        print0(ucase ? "CREATE DATABASE " : "create database ");
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        x.getName().accept(this);

        if (x.getCharacterSet() != null) {
            print0(ucase ? " CHARACTER SET " : " character set ");
            print0(x.getCharacterSet());
        }

        if (x.getCollate() != null) {
            print0(ucase ? " COLLATE " : " collate ");
            print0(x.getCollate());
        }

        return false;
    }

    @Override
    public boolean visit(SQLCreateViewStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }

        incrementIndent();
        String algorithm = x.getAlgorithm();
        if (algorithm != null && algorithm.length() > 0) {
            print0(ucase ? "ALGORITHM = " : "algorithm = ");
            print0(algorithm);
            println();
        }

        SQLName definer = x.getDefiner();
        if (definer != null) {
            print0(ucase ? "DEFINER = " : "definer = ");
            definer.accept(this);
            println();
        }

        String sqlSecurity = x.getSqlSecurity();
        if (sqlSecurity != null && sqlSecurity.length() > 0) {
            print0(ucase ? "SQL SECURITY = " : "sql security = ");
            print0(sqlSecurity);
            println();
        }

        decrementIndent();

        print0(ucase ? "VIEW " : "view ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        x.getTableSource().accept(this);

        if (x.getColumns().size() > 0) {
            print0(" (");
            incrementIndent();
            println();
            for (int i = 0; i < x.getColumns().size(); ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                x.getColumns().get(i).accept(this);
            }
            decrementIndent();
            println();
            print(')');
        }

        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }

        println();
        print0(ucase ? "AS" : "as");
        println();

        x.getSubQuery().accept(this);

        if (x.isWithCheckOption()) {
            println();
            print0(ucase ? "WITH CHECK OPTION" : "with check option");
        }

        return false;
    }

    public boolean visit(SQLCreateViewStatement.Column x) {
        x.getExpr().accept(this);

        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropIndex x) {
        print0(ucase ? "DROP INDEX " : "drop index ");
        x.getIndexName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLOver x) {
        print0(ucase ? "OVER (" : "over (");
        if (x.getPartitionBy().size() > 0) {
            print0(ucase ? "PARTITION BY " : "partition by ");
            printAndAccept(x.getPartitionBy(), ", ");
            print(' ');
        }
        
        if (x.getOrderBy() != null) {
            x.getOrderBy().accept(this);
        }
        
        if (x.getOf() != null) {
            print0(ucase ? " OF " : " of ");
            x.getOf().accept(this);
        }

        if (x.getWindowing() != null) {
            if (SQLOver.WindowingType.ROWS.equals(x.getWindowingType())) {
                print0(ucase ? " ROWS " : " rows ");
            } else if (SQLOver.WindowingType.RANGE.equals(x.getWindowingType())) {
                print0(ucase ? " RANGE " : " range ");
            }

            printWindowingExpr(x.getWindowing());

            if (x.isWindowingPreceding()) {
                print0(ucase ? " PRECEDING" : " preceding");
            } else if (x.isWindowingFollowing()) {
                print0(ucase ? " FOLLOWING" : " following");
            }
        }

        if (x.getWindowingBetweenBegin() != null) {
            if (SQLOver.WindowingType.ROWS.equals(x.getWindowingType())) {
                print0(ucase ? " ROWS BETWEEN " : " rows between ");
            } else if (SQLOver.WindowingType.RANGE.equals(x.getWindowingType())) {
                print0(ucase ? " RANGE BETWEEN " : " range between ");
            }

            printWindowingExpr(x.getWindowingBetweenBegin());

            if (x.isWindowingBetweenBeginPreceding()) {
                print0(ucase ? " PRECEDING" : " preceding");
            } else if (x.isWindowingBetweenBeginFollowing()) {
                print0(ucase ? " FOLLOWING" : " following");
            }

            print0(ucase ? " AND " : " and ");

            printWindowingExpr(x.getWindowingBetweenEnd());

            if (x.isWindowingBetweenEndPreceding()) {
                print0(ucase ? " PRECEDING" : " preceding");
            } else if (x.isWindowingBetweenEndFollowing()) {
                print0(ucase ? " FOLLOWING" : " following");
            }
        }
        
        print(')');
        return false;
    }

    void printWindowingExpr(SQLExpr expr) {
        if (expr instanceof SQLIdentifierExpr) {
            String ident = ((SQLIdentifierExpr) expr).getName();
            print0(ucase ? ident : ident.toLowerCase());
        } else {
            expr.accept(this);
        }
    }
    
    @Override
    public boolean visit(SQLKeep x) {
        if (x.getDenseRank() == SQLKeep.DenseRank.FIRST) {
            print0(ucase ? "KEEP (DENSE_RANK FIRST " : "keep (dense_rank first ");    
        } else {
            print0(ucase ? "KEEP (DENSE_RANK LAST " : "keep (dense_rank last ");
        }
        
        x.getOrderBy().accept(this);
        print(')');
        
        return false;
    }

    @Override
    public boolean visit(SQLColumnPrimaryKey x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "PRIMARY KEY" : "primary key");
        return false;
    }

    @Override
    public boolean visit(SQLColumnUniqueKey x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "UNIQUE" : "unique");
        return false;
    }

    @Override
    public boolean visit(SQLColumnCheck x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "CHECK (" : "check (");
        x.getExpr().accept(this);
        print(')');

        if (x.getEnable() != null) {
            if (x.getEnable().booleanValue()) {
                print0(ucase ? " ENABLE" : " enable");
            } else {
                print0(ucase ? " DISABLE" : " disable");
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLWithSubqueryClause x) {
        print0(ucase ? "WITH " : "with ");
        if (x.getRecursive() == Boolean.TRUE) {
            print0(ucase ? "RECURSIVE " : "recursive ");
        }
        incrementIndent();
        printlnAndAccept(x.getEntries(), ", ");
        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }
        print(' ');
        print0(ucase ? "AS " : "as ");
        print('(');
        incrementIndent();
        println();
        x.getSubQuery().accept(this);
        decrementIndent();
        println();
        print(')');

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAlterColumn x) {
        boolean odps = isOdps();
        if (odps) {
            print0(ucase ? "CHANGE COLUMN " : "change column ");
        } else {
            print0(ucase ? "ALTER COLUMN " : "alter column ");
        }
        x.getColumn().accept(this);

        if (x.isSetNotNull()) { // postgresql
            print0(ucase ? " SET NOT NULL" : " set not null");
        }
        if (x.isDropNotNull()) { // postgresql
            print0(ucase ? " DROP NOT NULL" : " drop not null");
        }
        if (x.getSetDefault() != null) { // postgresql
            print0(ucase ? " SET DEFAULT " : " set default ");
            x.getSetDefault().accept(this);
        }
        if (x.isDropDefault()) { // postgresql
            print0(ucase ? " DROP DEFAULT" : " drop default");
        }

        return false;
    }

    @Override
    public boolean visit(SQLCheck x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "CHECK (" : "check (");
        incrementIndent();
        x.getExpr().accept(this);
        decrementIndent();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropForeignKey x) {
        print0(ucase ? "DROP FOREIGN KEY " : "drop foreign key ");
        x.getIndexName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        print0(ucase ? "DROP PRIMARY KEY" : "drop primary key");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropKey x) {
        print0(ucase ? "DROP KEY " : "drop key ");
        x.getKeyName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableEnableKeys x) {
        print0(ucase ? "ENABLE KEYS" : "enable keys");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDisableKeys x) {
        print0(ucase ? "DISABLE KEYS" : "disable keys");
        return false;
    }

    public boolean visit(SQLAlterTableDisableConstraint x) {
        print0(ucase ? "DISABLE CONSTRAINT " : "disable constraint ");
        x.getConstraintName().accept(this);
        return false;
    }

    public boolean visit(SQLAlterTableEnableConstraint x) {
        print0(ucase ? "ENABLE CONSTRAINT " : "enable constraint ");
        x.getConstraintName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropConstraint x) {
        print0(ucase ? "DROP CONSTRAINT " : "drop constraint ");
        x.getConstraintName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        print0(ucase ? "ALTER TABLE " : "alter table ");
        printTableSourceExpr(x.getName());
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

        if (x.isMergeSmallFiles()) {
            print0(ucase ? " MERGE SMALLFILES" : " merge smallfiles");
        }
        return false;
    }

    @Override
    public boolean visit(SQLExprHint x) {
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.getType() != null) {
            print0(x.getType());
            print(' ');
        }

        print0(ucase ? "INDEX " : "index ");

        x.getName().accept(this);
        print0(ucase ? " ON " : " on ");
        x.getTable().accept(this);
        print0(" (");
        printAndAccept(x.getItems(), ", ");
        print(')');

        // for mysql
        if (x.getUsing() != null) {
            print0(ucase ? " USING " : " using ");
            ;
            print0(x.getUsing());
        }

        return false;
    }

    @Override
    public boolean visit(SQLUnique x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "UNIQUE (" : "unique (");
        printAndAccept(x.getColumns(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLPrimaryKeyImpl x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "PRIMARY KEY (" : "primary key (");
        printAndAccept(x.getColumns(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRenameColumn x) {
        print0(ucase ? "RENAME COLUMN " : "rename column ");
        x.getColumn().accept(this);
        print0(ucase ? " TO " : " to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLColumnReference x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }
        print0(ucase ? "REFERENCES " : "references ");
        x.getTable().accept(this);
        print0(" (");
        printAndAccept(x.getColumns(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLForeignKeyImpl x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }

        print0(ucase ? "FOREIGN KEY (" : "foreign key (");
        printAndAccept(x.getReferencingColumns(), ", ");
        print(')');

        incrementIndent();
        println();
        print0(ucase ? "REFERENCES " : "references ");
        x.getReferencedTableName().accept(this);

        if (x.getReferencedColumns().size() > 0) {
            print0(" (");
            printAndAccept(x.getReferencedColumns(), ", ");
            print(')');
        }

        if (x.isOnDeleteCascade()) {
            println();
            print0(ucase ? "ON DELETE CASCADE" : "on delete cascade");
        } else if (x.isOnDeleteSetNull()) {
            print0(ucase ? "ON DELETE SET NULL" : "on delete set null");
        }
        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        print0(ucase ? "DROP SEQUENCE " : "drop sequence ");
        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLDropSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        print0(ucase ? "DROP TRIGGER " : "drop trigger ");
        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLDropUserStatement x) {

    }

    @Override
    public boolean visit(SQLDropUserStatement x) {
        print0(ucase ? "DROP USER " : "drop user ");
        printAndAccept(x.getUsers(), ", ");
        return false;
    }

    @Override
    public boolean visit(SQLExplainStatement x) {
        print0(ucase ? "EXPLAIN" : "explain");
        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        if (x.getType() != null) {
            print(' ');
            print0(x.getType());
        }
        println();
        x.getStatement().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLGrantStatement x) {
        print0(ucase ? "GRANT " : "grant ");
        printAndAccept(x.getPrivileges(), ", ");

        printGrantOn(x);

        if (x.getTo() != null) {
            print0(ucase ? " TO " : " to ");
            x.getTo().accept(this);
        }

        boolean with = false;
        if (x.getMaxQueriesPerHour() != null) {
            if (!with) {
                print0(ucase ? " WITH" : " with");
                with = true;
            }
            print0(ucase ? " MAX_QUERIES_PER_HOUR " : " max_queries_per_hour ");
            x.getMaxQueriesPerHour().accept(this);
        }

        if (x.getMaxUpdatesPerHour() != null) {
            if (!with) {
                print0(ucase ? " WITH" : " with");
                with = true;
            }
            print0(ucase ? " MAX_UPDATES_PER_HOUR " : " max_updates_per_hour ");
            x.getMaxUpdatesPerHour().accept(this);
        }

        if (x.getMaxConnectionsPerHour() != null) {
            if (!with) {
                print0(ucase ? " WITH" : " with");
                with = true;
            }
            print0(ucase ? " MAX_CONNECTIONS_PER_HOUR " : " max_connections_per_hour ");
            x.getMaxConnectionsPerHour().accept(this);
        }

        if (x.getMaxUserConnections() != null) {
            if (!with) {
                print0(ucase ? " WITH" : " with");
                with = true;
            }
            print0(ucase ? " MAX_USER_CONNECTIONS " : " max_user_connections ");
            x.getMaxUserConnections().accept(this);
        }

        if (x.isAdminOption()) {
            if (!with) {
                print0(ucase ? " WITH" : " with");
                with = true;
            }
            print0(ucase ? " ADMIN OPTION" : " admin option");
        }

        if (x.getIdentifiedBy() != null) {
            print0(ucase ? " IDENTIFIED BY " : " identified by ");
            x.getIdentifiedBy().accept(this);
        }

        return false;
    }

    protected void printGrantOn(SQLGrantStatement x) {
        if (x.getOn() != null) {
            print0(ucase ? " ON " : " on ");

            SQLObjectType objectType = x.getObjectType();
            if (objectType != null) {
                print0(ucase ? objectType.name : objectType.name_lcase);
                print(' ');
            }

            x.getOn().accept(this);
        }
    }

    @Override
    public boolean visit(SQLRevokeStatement x) {
        print0(ucase ? "ROVOKE " : "rovoke ");
        printAndAccept(x.getPrivileges(), ", ");

        if (x.getOn() != null) {
            print0(ucase ? " ON " : " on ");

            if (x.getObjectType() != null) {
                print0(x.getObjectType().name());
                print(' ');
            }

            x.getOn().accept(this);
        }

        if (x.getFrom() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getFrom().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        print0(ucase ? "DROP DATABASE " : "drop databasE ");

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        x.getDatabase().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLDropFunctionStatement x) {
        print0(ucase ? "DROP FUNCTION " : "drop function ");

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLDropTableSpaceStatement x) {
        print0(ucase ? "DROP TABLESPACE " : "drop tablespace ");

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLDropProcedureStatement x) {
        print0(ucase ? "DROP PROCEDURE " : "drop procedure ");

        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }

        x.getName().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddIndex x) {
        print0(ucase ? "ADD " : "add ");
        String type = x.getType();

        boolean mysql = JdbcConstants.MYSQL.equals(dbType);

        if (type != null && !mysql) {
            print0(type);
            print(' ');
        }

        if (x.isUnique()) {
            print0(ucase ? "UNIQUE " : "unique ");
        }

        if (x.isKey()) {
            print0(ucase ? "KEY " : "key ");    
        } else {
            print0(ucase ? "INDEX " : "index ");
        }
        
        if (x.getName() != null) {
            x.getName().accept(this);
            print(' ');
        }

        if (type != null && mysql) {
            print0(ucase ? "USING " : "using ");
            print0(type);
            print(' ');
        }

        print('(');
        printAndAccept(x.getItems(), ", ");
        print(')');

        if (x.getUsing() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getUsing());
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddConstraint x) {
        if (x.isWithNoCheck()) {
            print0(ucase ? "WITH NOCHECK " : "with nocheck ");
        }

        print0(ucase ? "ADD " : "add ");

        x.getConstraint().accept(this);
        return false;
    }

    public boolean visit(SQLCreateTriggerStatement x) {
        print0(ucase ? "CREATE " : "create ");

        if (x.isOrReplace()) {
            print0(ucase ? "OR REPLACE " : "or replace ");
        }

        print0(ucase ? "TRIGGER " : "trigger ");

        x.getName().accept(this);

        incrementIndent();
        println();
        if (TriggerType.INSTEAD_OF.equals(x.getTriggerType())) {
            print0(ucase ? "INSTEAD OF" : "instead of");
        } else {
            String triggerTypeName = x.getTriggerType().name();
            print0(ucase ? triggerTypeName : triggerTypeName.toLowerCase());
        }

        for (TriggerEvent event : x.getTriggerEvents()) {
            print(' ');
            print0(event.name());
        }
        println();
        print0(ucase ? "ON " : "on ");
        x.getOn().accept(this);

        if (x.isForEachRow()) {
            println();
            print0(ucase ? "FOR EACH ROW" : "for each row");
        }
        decrementIndent();
        println();
        x.getBody().accept(this);
        return false;
    }

    public boolean visit(SQLBooleanExpr x) {
        print0(x.getBooleanValue() ? "true" : "false");
        return false;
    }

    public void endVisit(SQLBooleanExpr x) {
    }

    @Override
    public boolean visit(SQLUnionQueryTableSource x) {
        print('(');
        incrementIndent();
        println();
        x.getUnion().accept(this);
        decrementIndent();
        println();
        print(')');

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        return false;
    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        print0(ucase ? "TIMESTAMP " : "timestamp ");

        if (x.isWithTimeZone()) {
            print0(ucase ? " WITH TIME ZONE " : " with time zone ");
        }

        print('\'');
        print0(x.getLiteral());
        print('\'');

        if (x.getTimeZone() != null) {
            print0(ucase ? " AT TIME ZONE '" : " at time zone '");
            print0(x.getTimeZone());
            print('\'');
        }

        return false;
    }

    @Override
    public boolean visit(SQLBinaryExpr x) {
        print0("b'");
        print0(x.getValue());
        print('\'');

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRename x) {
        print0(ucase ? "RENAME TO " : "rename to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLShowTablesStatement x) {
        print0(ucase ? "SHOW TABLES" : "show tables");
        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }
        return false;
    }

    protected void printlnComment(List<String> comments) {
        if (comments != null) {
            for (int i = 0; i < comments.size(); ++i) {
                String comment = comments.get(i);
                if (i != 0 && comment.startsWith("--")) {
                    println();
                }

                printComment(comment);
            }
        }
    }

    public void printComment(String comment) {
        if (comment.startsWith("--") && comment.length() > 2 && comment.charAt(2) != ' ') {
            print0("-- ");
            print0(comment.substring(2));
        } else {
            print0(comment);
        }
    }

    protected void printlnComments(List<String> comments) {
        if (comments != null) {
            for (int i = 0; i < comments.size(); ++i) {
                String comment = comments.get(i);
                printComment(comment);
                println();
            }
        }
    }

    @Override
    public boolean visit(SQLAlterViewRenameStatement x) {
        print0(ucase ? "ALTER VIEW " : "alter view ");
        x.getName().accept(this);
        print0(ucase ? " RENAME TO " : " rename to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddPartition x) {
        print0(ucase ? "ADD " : "add ");
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        
        if (x.getPartitionCount() != null) {
            print0(ucase ? "PARTITION PARTITIONS " : "partition partitions ");
            x.getPartitionCount().accept(this);
        }

        if (x.getPartitions().size() > 0) {
            print0(ucase ? "PARTITION (" : "partition (");
            printAndAccept(x.getPartitions(), ", ");
            print(')');
        }
        
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableReOrganizePartition x) {
        print0(ucase ? "REORGANIZE " : "reorganize ");

        printAndAccept(x.getNames(), ", ");

        print0(ucase ? " INTO (" : " into (");
        printAndAccept(x.getPartitions(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropPartition x) {
        print0(ucase ? "DROP " : "drop ");
        if (x.isIfExists()) {
            print0(ucase ? "IF EXISTS " : "if exists ");
        }
        print0(ucase ? "PARTITION " : "partition ");

        if (x.getPartitions().size() == 1 && x.getPartitions().get(0) instanceof SQLName) {
            x.getPartitions().get(0).accept(this);
        } else {
            print('(');
            printAndAccept(x.getPartitions(), ", ");
            print(')');
        }

        if (x.isPurge()) {
            print0(ucase ? " PURGE" : " purge");
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableRenamePartition x) {
        print0(ucase ? "PARTITION (" : "partition (");
        printAndAccept(x.getPartition(), ", ");
        print0(ucase ? ") RENAME TO PARTITION(" : ") rename to partition(");
        printAndAccept(x.getTo(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableSetComment x) {
        print0(ucase ? "SET COMMENT " : "set comment ");
        x.getComment().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableSetLifecycle x) {
        print0(ucase ? "SET LIFECYCLE " : "set lifecycle ");
        x.getLifecycle().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableEnableLifecycle x) {
        if (x.getPartition().size() != 0) {
            print0(ucase ? "PARTITION (" : "partition (");
            printAndAccept(x.getPartition(), ", ");
            print0(") ");
        }

        print0(ucase ? "ENABLE LIFECYCLE" : "enable lifecycle");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDisableLifecycle x) {
        if (x.getPartition().size() != 0) {
            print0(ucase ? "PARTITION (" : "partition (");
            printAndAccept(x.getPartition(), ", ");
            print0(") ");
        }

        print0(ucase ? "DISABLE LIFECYCLE" : "disable lifecycle");
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableTouch x) {
        print0(ucase ? "TOUCH" : "touch");
        if (x.getPartition().size() != 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(x.getPartition(), ", ");
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        x.getExpr().accept(this);
        print('[');
        printAndAccept(x.getValues(), ", ");
        print(']');
        return false;
    }

    @Override
    public boolean visit(SQLOpenStatement x) {
        print0(ucase ? "OPEN " : "open ");
        print0(x.getCursorName());

        SQLExpr forExpr = x.getFor();
        if (forExpr != null) {
            print0(ucase ? " FOR " : "for ");
            forExpr.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLFetchStatement x) {
        print0(ucase ? "FETCH " : "fetch ");
        x.getCursorName().accept(this);
        if (x.isBulkCollect()) {
            print0(ucase ? " BULK COLLECT INTO " : " bulk collect into ");
        } else {
            print0(ucase ? " INTO " : " into ");
        }
        printAndAccept(x.getInto(), ", ");
        return false;
    }

    @Override
    public boolean visit(SQLCloseStatement x) {
        print0(ucase ? "CLOSE " : "close ");
        print0(x.getCursorName());
        return false;
    }

    @Override
    public boolean visit(SQLGroupingSetExpr x) {
        print0(ucase ? "GROUPING SETS" : "grouping sets");
        print0(" (");
        printAndAccept(x.getParameters(), ", ");
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement x) {
        print0(ucase ? "IF " : "if ");
        x.getCondition().accept(this);
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        decrementIndent();

        for (SQLIfStatement.ElseIf elseIf : x.getElseIfList()) {
            println();
            elseIf.accept(this);
        }

        if (x.getElseItem() != null) {
            println();
            x.getElseItem().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        print0(ucase ? "ELSE" : "else");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
        }

        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        print0(ucase ? "ELSE IF" : "else if");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
        incrementIndent();
        println();

        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
        }

        decrementIndent();
        return false;
    }

    @Override
    public boolean visit(SQLLoopStatement x) {
        print0(ucase ? "LOOP" : "loop");
        incrementIndent();
        println();


        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);

            if (i != size - 1) {
                println();
            }
        }

        decrementIndent();
        println();
        print0(ucase ? "END LOOP" : "end loop");
        if (x.getLabelName() != null) {
            print(' ');
            print0(x.getLabelName());
        }
        return false;
    }

    @Override
    public boolean visit(SQLParameter x) {
        if (x.getDataType().getName().equalsIgnoreCase("CURSOR")) {
            print0(ucase ? "CURSOR " : "cursor ");
            x.getName().accept(this);
            print0(ucase ? " IS" : " is");
            incrementIndent();
            println();
            SQLSelect select = ((SQLQueryExpr) x.getDefaultValue()).getSubQuery();
            select.accept(this);
            decrementIndent();

        } else {
            SQLDataType dataType = x.getDataType();

            if (JdbcConstants.ORACLE.equals(dbType)) {
                String dataTypeName = dataType.getName();
                boolean printType = (dataTypeName.startsWith("TABLE OF") && x.getDefaultValue() == null)
                        || dataTypeName.equalsIgnoreCase("REF CURSOR")
                        || dataTypeName.startsWith("VARRAY(");
                if (printType) {
                    print0(ucase ? "TYPE " : "type ");
                }

                x.getName().accept(this);
                if (x.getParamType() == SQLParameter.ParameterType.IN) {
                    print0(ucase ? " IN " : " in ");
                } else if (x.getParamType() == SQLParameter.ParameterType.OUT) {
                    print0(ucase ? " OUT " : " out ");
                } else if (x.getParamType() == SQLParameter.ParameterType.INOUT) {
                    print0(ucase ? " IN OUT " : " in out ");
                } else {
                    print(' ');
                }

                if (x.isNoCopy()) {
                    print0(ucase ? "NOCOPY " : "nocopy ");
                }

                if (x.isConstant()) {
                    print0(ucase ? "CONSTANT " : "constant ");
                }

                if (printType) {
                    print0(ucase ? "IS " : "is ");
                }
            } else {
                if (x.getParamType() == SQLParameter.ParameterType.IN) {
                    boolean skip = JdbcConstants.MYSQL.equals(dbType)
                            && x.getParent() instanceof SQLCreateFunctionStatement;

                    if (!skip) {
                        print0(ucase ? "IN " : "in ");
                    }
                } else if (x.getParamType() == SQLParameter.ParameterType.OUT) {
                    print0(ucase ? "OUT " : "out ");
                } else if (x.getParamType() == SQLParameter.ParameterType.INOUT) {
                    print0(ucase ? "INOUT " : "inout ");
                }
                x.getName().accept(this);
                print(' ');
            }

            dataType.accept(this);

            printParamDefaultValue(x);
        }

        return false;
    }

    protected void printParamDefaultValue(SQLParameter x) {
        if (x.getDefaultValue() != null) {
            print0(" := ");
            x.getDefaultValue().accept(this);
        }
    }

    @Override
    public boolean visit(SQLDeclareItem x) {
        x.getName().accept(this);

        if (x.getType() == SQLDeclareItem.Type.TABLE) {
            print0(ucase ? " TABLE" : " table");
            int size = x.getTableElementList().size();

            if (size > 0) {
                print0(" (");
                incrementIndent();
                println();
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print(',');
                        println();
                    }
                    x.getTableElementList().get(i).accept(this);
                }
                decrementIndent();
                println();
                print(')');
            }
        } else if (x.getType() == SQLDeclareItem.Type.CURSOR) {
            print0(ucase ? " CURSOR" : " cursor");
        } else {
            if (x.getDataType() != null) {
                print(' ');
                x.getDataType().accept(this);
            }
            if (x.getValue() != null) {
                if (JdbcConstants.MYSQL.equals(getDbType())) {
                    print0(ucase ? " DEFAULT " : " default ");
                } else {
                    print0(" = ");
                }
                x.getValue().accept(this);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLPartitionValue x) {
        if (x.getOperator() == SQLPartitionValue.Operator.LessThan //
            && (!JdbcConstants.ORACLE.equals(getDbType())) && x.getItems().size() == 1 //
            && x.getItems().get(0) instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr ident = (SQLIdentifierExpr) x.getItems().get(0);
            if ("MAXVALUE".equalsIgnoreCase(ident.getName())) {
                print0(ucase ? "VALUES LESS THAN MAXVALUE" : "values less than maxvalue");
                return false;
            }
        }

        if (x.getOperator() == SQLPartitionValue.Operator.LessThan) {
            print0(ucase ? "VALUES LESS THAN (" : "values less than (");
        } else if (x.getOperator() == SQLPartitionValue.Operator.In) {
            print0(ucase ? "VALUES IN (" : "values in (");
        } else {
            print(ucase ? "VALUES (" : "values (");
        }
        printAndAccept(x.getItems(), ", ");
        print(')');
        return false;
    }

    public String getDbType() {
        return dbType;
    }

    public boolean isUppCase() {
        return ucase;
    }

    public void setUppCase(boolean val) {
        this.config(VisitorFeature.OutputUCase, true);
    }

    @Override
    public boolean visit(SQLPartition x) {
        print0(ucase ? "PARTITION " : "partition ");
        x.getName().accept(this);
        if (x.getValues() != null) {
            print(' ');
            x.getValues().accept(this);
        }

        if (x.getDataDirectory() != null) {
            incrementIndent();
            println();
            print0(ucase ? "DATA DIRECTORY " : "data directory ");
            x.getDataDirectory().accept(this);
            decrementIndent();
        }

        if (x.getIndexDirectory() != null) {
            incrementIndent();
            println();
            print0(ucase ? "INDEX DIRECTORY " : "index directory ");
            x.getIndexDirectory().accept(this);
            decrementIndent();
        }

        incrementIndent();
        printOracleSegmentAttributes(x);


        if (x.getEngine() != null) {
            println();
            print0(ucase ? "STORAGE ENGINE " : "storage engine ");
            x.getEngine().accept(this);
        }
        decrementIndent();

        if (x.getMaxRows() != null) {
            print0(ucase ? " MAX_ROWS " : " max_rows ");
            x.getMaxRows().accept(this);
        }

        if (x.getMinRows() != null) {
            print0(ucase ? " MIN_ROWS " : " min_rows ");
            x.getMinRows().accept(this);
        }

        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        if (x.getSubPartitionsCount() != null) {
            incrementIndent();
            println();
            print0(ucase ? "SUBPARTITIONS " : "subpartitions ");
            x.getSubPartitionsCount().accept(this);
            decrementIndent();
        }

        if (x.getSubPartitions().size() > 0) {
            print(" (");
            incrementIndent();
            for (int i = 0; i < x.getSubPartitions().size(); ++i) {
                if (i != 0) {
                    print(',');
                }
                println();
                x.getSubPartitions().get(i).accept(this);
            }
            decrementIndent();
            println();
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(SQLPartitionByRange x) {
        print0(ucase ? "PARTITION BY RANGE" : "partition by range");
        if (x.getExpr() != null) {
            print0(" (");
            x.getExpr().accept(this);
            print(')');
        } else {
            if (JdbcConstants.MYSQL.equals(getDbType())) {
                print0(ucase ? " COLUMNS (" : " columns (");
            } else {
                print0(" (");
            }
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }

        SQLExpr interval = x.getInterval();
        if (interval != null) {
            print0(ucase ? " INTERVAL (" : " interval (");
            interval.accept(this);
            print(')');
        }

        printPartitionsCountAndSubPartitions(x);

        print(" (");
        incrementIndent();
        for (int i = 0, size = x.getPartitions().size(); i < size; ++i) {
            if (i != 0) {
                print(',');
            }
            println();
            x.getPartitions().get(i).accept(this);
        }
        decrementIndent();
        println();
        print(')');

        return false;
    }

    @Override
    public boolean visit(SQLPartitionByList x) {
        print0(ucase ? "PARTITION BY LIST " : "partition by list ");
        if (x.getExpr() != null) {
            print('(');
            x.getExpr().accept(this);
            print0(")");
        } else {
            print0(ucase ? "COLUMNS (" : "columns (");
            printAndAccept(x.getColumns(), ", ");
            print0(")");
        }

        printPartitionsCountAndSubPartitions(x);

        printSQLPartitions(x.getPartitions());
        return false;
    }

    @Override
    public boolean visit(SQLPartitionByHash x) {
        if (x.isLinear()) {
            print0(ucase ? "PARTITION BY LINEAR HASH " : "partition by linear hash ");
        } else {
            print0(ucase ? "PARTITION BY HASH " : "partition by hash ");
        }

        if (x.isKey()) {
            print0(ucase ? "KEY" : "key");
        }

        print('(');
        x.getExpr().accept(this);
        print(')');

        printPartitionsCountAndSubPartitions(x);

        printSQLPartitions(x.getPartitions());

        return false;
    }

    private void printSQLPartitions(List<SQLPartition> partitions) {
        int partitionsSize = partitions.size();
        if (partitionsSize > 0) {
            print0(" (");
            incrementIndent();
            for (int i = 0; i < partitionsSize; ++i) {
                println();
                partitions.get(i).accept(this);
                if (i != partitionsSize - 1) {
                    print0(", ");
                }
            }
            decrementIndent();
            println();
            print(')');
        }
    }

    protected void printPartitionsCountAndSubPartitions(SQLPartitionBy x) {
        if (x.getPartitionsCount() != null) {

            if (Boolean.TRUE.equals(x.getAttribute("ads.partition"))) {
                print0(ucase ? " PARTITION NUM " : " partition num ");
            } else {
                print0(ucase ? " PARTITIONS " : " partitions ");
            }

            x.getPartitionsCount().accept(this);
        }

        if (x.getSubPartitionBy() != null) {
            println();
            x.getSubPartitionBy().accept(this);
        }

        if (x.getStoreIn().size() > 0) {
            println();
            print0(ucase ? "STORE IN (" : "store in (");
            printAndAccept(x.getStoreIn(), ", ");
            print(')');
        }
    }

    @Override
    public boolean visit(SQLSubPartitionByHash x) {
        if (x.isLinear()) {
            print0(ucase ? "SUBPARTITION BY LINEAR HASH " : "subpartition by linear hash ");
        } else {
            print0(ucase ? "SUBPARTITION BY HASH " : "subpartition by hash ");
        }

        if (x.isKey()) {
            print0(ucase ? "KEY" : "key");
        }

        print('(');
        x.getExpr().accept(this);
        print(')');

        if (x.getSubPartitionsCount() != null) {
            print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
            x.getSubPartitionsCount().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLSubPartitionByList x) {
        if (x.isLinear()) {
            print0(ucase ? "SUBPARTITION BY LINEAR HASH " : "subpartition by linear hash ");
        } else {
            print0(ucase ? "SUBPARTITION BY HASH " : "subpartition by hash ");
        }

        print('(');
        x.getColumn().accept(this);
        print(')');

        if (x.getSubPartitionsCount() != null) {
            print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
            x.getSubPartitionsCount().accept(this);
        }

        if (x.getSubPartitionTemplate().size() > 0) {
            incrementIndent();
            println();
            print0(ucase ? "SUBPARTITION TEMPLATE (" : "subpartition template (");
            incrementIndent();
            println();
            printlnAndAccept(x.getSubPartitionTemplate(), ",");
            decrementIndent();
            println();
            print(')');
            decrementIndent();
        }

        return false;
    }

    @Override
    public boolean visit(SQLSubPartition x) {
        print0(ucase ? "SUBPARTITION " : "subpartition ");
        x.getName().accept(this);

        if (x.getValues() != null) {
            print(' ');
            x.getValues().accept(this);
        }

        SQLName tableSpace = x.getTableSpace();
        if (tableSpace != null) {
            print0(ucase ? " TABLESPACE " : " tablespace ");
            tableSpace.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterDatabaseStatement x) {
        print0(ucase ? "ALTER DATABASE " : "alter database ");
        x.getName().accept(this);
        if (x.isUpgradeDataDirectoryName()) {
            print0(ucase ? " UPGRADE DATA DIRECTORY NAME" : " upgrade data directory name");
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableConvertCharSet x) {
        print0(ucase ? "CONVERT TO CHARACTER SET " : "convert to character set ");
        x.getCharset().accept(this);

        if (x.getCollate() != null) {
            print0(ucase ? "COLLATE " : "collate ");
            x.getCollate().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableCoalescePartition x) {
        print0(ucase ? "COALESCE PARTITION " : "coalesce partition ");
        x.getCount().accept(this);
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableTruncatePartition x) {
        print0(ucase ? "TRUNCATE PARTITION " : "truncate partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableDiscardPartition x) {
        print0(ucase ? "DISCARD PARTITION " : "discard partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableImportPartition x) {
        print0(ucase ? "IMPORT PARTITION " : "import partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableAnalyzePartition x) {
        print0(ucase ? "ANALYZE PARTITION " : "analyze partition ");
        
        printPartitions(x.getPartitions());
        return false;
    }
    
    protected void printPartitions(List<SQLName> partitions) {
        if (partitions.size() == 1 && "ALL".equalsIgnoreCase(partitions.get(0).getSimpleName())) {
            print0(ucase ? "ALL" : "all");    
        } else {
            printAndAccept(partitions, ", ");
        }
    }
    
    @Override
    public boolean visit(SQLAlterTableCheckPartition x) {
        print0(ucase ? "CHECK PARTITION " : "check partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableOptimizePartition x) {
        print0(ucase ? "OPTIMIZE PARTITION " : "optimize partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableRebuildPartition x) {
        print0(ucase ? "REBUILD PARTITION " : "rebuild partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLAlterTableRepairPartition x) {
        print0(ucase ? "REPAIR PARTITION " : "repair partition ");
        printPartitions(x.getPartitions());
        return false;
    }
    
    @Override
    public boolean visit(SQLSequenceExpr x) {
        x.getSequence().accept(this);
        print('.');
        print0(ucase ? x.getFunction().name : x.getFunction().name_lcase);
        return false;
    }
    
    @Override
    public boolean visit(SQLMergeStatement x) {
        print0(ucase ? "MERGE " : "merge ");
        if (x.getHints().size() > 0) {
            printAndAccept(x.getHints(), ", ");
            print(' ');
        }

        print0(ucase ? "INTO " : "into ");
        x.getInto().accept(this);

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        println();
        print0(ucase ? "USING " : "using ");
        x.getUsing().accept(this);

        print0(ucase ? " ON (" : " on (");
        x.getOn().accept(this);
        print0(") ");

        if (x.getUpdateClause() != null) {
            println();
            x.getUpdateClause().accept(this);
        }

        if (x.getInsertClause() != null) {
            println();
            x.getInsertClause().accept(this);
        }

        if (x.getErrorLoggingClause() != null) {
            println();
            x.getErrorLoggingClause().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MergeUpdateClause x) {
        print0(ucase ? "WHEN MATCHED THEN UPDATE SET " : "when matched then update set ");
        printAndAccept(x.getItems(), ", ");
        if (x.getWhere() != null) {
            incrementIndent();
            println();
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getDeleteWhere() != null) {
            incrementIndent();
            println();
            print0(ucase ? "DELETE WHERE " : "delete where ");
            x.getDeleteWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public boolean visit(MergeInsertClause x) {
        print0(ucase ? "WHEN NOT MATCHED THEN INSERT" : "when not matched then insert");
        if (x.getColumns().size() > 0) {
            print(' ');
            printAndAccept(x.getColumns(), ", ");
        }
        print0(ucase ? " VALUES (" : " values (");
        printAndAccept(x.getValues(), ", ");
        print(')');
        if (x.getWhere() != null) {
            incrementIndent();
            println();
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public boolean visit(SQLErrorLoggingClause x) {
        print0(ucase ? "LOG ERRORS " : "log errors ");
        if (x.getInto() != null) {
            print0(ucase ? "INTO " : "into ");
            x.getInto().accept(this);
            print(' ');
        }

        if (x.getSimpleExpression() != null) {
            print('(');
            x.getSimpleExpression().accept(this);
            print(')');
        }

        if (x.getLimit() != null) {
            print0(ucase ? " REJECT LIMIT " : " reject limit ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLCreateSequenceStatement x) {
        print0(ucase ? "CREATE SEQUENCE " : "create sequence ");
        x.getName().accept(this);

        if (x.getStartWith() != null) {
            print0(ucase ? " START WITH " : " start with ");
            x.getStartWith().accept(this);
        }

        if (x.getIncrementBy() != null) {
            print0(ucase ? " INCREMENT BY " : " increment by ");
            x.getIncrementBy().accept(this);
        }

        if (x.getMaxValue() != null) {
            print0(ucase ? " MAXVALUE " : " maxvalue ");
            x.getMaxValue().accept(this);
        }

        if (x.isNoMaxValue()) {
            print0(ucase ? " NOMAXVALUE" : " nomaxvalue");
        }

        if (x.getMinValue() != null) {
            print0(ucase ? " MINVALUE " : " minvalue ");
            x.getMinValue().accept(this);
        }

        if (x.isNoMinValue()) {
            print0(ucase ? " NOMINVALUE" : " nominvalue");
        }

        if (x.getCycle() != null) {
            if (x.getCycle().booleanValue()) {
                print0(ucase ? " CYCLE" : " cycle");
            } else {
                print0(ucase ? " NOCYCLE" : " nocycle");
            }
        }

        Boolean cache = x.getCache();
        if (cache != null) {
            if (cache.booleanValue()) {
                print0(ucase ? " CACHE" : " cache");

                SQLExpr cacheValue = x.getCacheValue();
                if (cacheValue != null) {
                    print(' ');
                    cacheValue.accept(this);
                }
            } else {
                print0(ucase ? " NOCACHE" : " nocache");
            }
        }

        Boolean order = x.getOrder();
        if (order != null) {
            if (order.booleanValue()) {
                print0(ucase ? " ORDER" : " order");
            } else {
                print0(ucase ? " NOORDER" : " noorder");
            }
        }

        return false;
    }

    public boolean visit(SQLDateExpr x) {
        print0(ucase ? "DATE '" : "date '");
        print0(x.getLiteral());
        print('\'');
        return false;
    }

    public boolean visit(SQLLimit x) {
        print0(ucase ? "LIMIT " : "limit ");
        if (x.getOffset() != null) {
            x.getOffset().accept(this);
            print0(", ");
        }
        x.getRowCount().accept(this);

        return false;
    }

    public boolean visit(SQLDescribeStatement x) {
        print0(ucase ? "DESC " : "desc ");
        if (x.getObjectType() != null) {
            print0(x.getObjectType().name());
            print(' ');
        }

        if(x.getObject() != null) {
            x.getObject().accept(this);
        }

        if (x.getPartition().size() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printAndAccept(x.getPartition(), ", ");
            print(')');
        }
        return false;
    }

    protected void printHierarchical(SQLSelectQueryBlock x) {
        SQLExpr startWith = x.getStartWith(), connectBy = x.getConnectBy();
        if (startWith != null || connectBy != null){
            println();
            if (x.getStartWith() != null) {
                print0(ucase ? "START WITH " : "start with ");
                x.getStartWith().accept(this);
                println();
            }

            print0(ucase ? "CONNECT BY " : "connect by ");

            if (x.isNoCycle()) {
                print0(ucase ? "NOCYCLE " : "nocycle ");
            }

            if (x.isPrior()) {
                print0(ucase ? "PRIOR " : "prior ");
            }

            x.getConnectBy().accept(this);
        }
    }

    public void printOracleSegmentAttributes(OracleSegmentAttributes x) {

        if (x.getPctfree() != null) {
            println();
            print0(ucase ? "PCTFREE " : "pctfree ");
            print(x.getPctfree());
        }

        if (x.getPctused() != null) {
            println();
            print0(ucase ? "PCTUSED " : "pctused ");
            print(x.getPctused());
        }

        if (x.getInitrans() != null) {
            println();
            print0(ucase ? "INITRANS " : "initrans ");
            print(x.getInitrans());
        }

        if (x.getMaxtrans() != null) {
            println();
            print0(ucase ? "MAXTRANS " : "maxtrans ");
            print(x.getMaxtrans());
        }

        if (x.getCompress() == Boolean.FALSE) {
            println();
            print0(ucase ? "NOCOMPRESS" : "nocompress");
        } else if (x.getCompress() == Boolean.TRUE) {
            println();
            print0(ucase ? "COMPRESS" : "compress");

            if (x.getCompressLevel() != null) {
                print(' ');
                print(x.getCompressLevel());
            }
        }

        if (x.getLogging() == Boolean.TRUE) {
            println();
            print0(ucase ? "LOGGING" : "logging");
        } else if (x.getLogging() == Boolean.FALSE) {
            println();
            print0(ucase ? "NOLOGGING" : "nologging");
        }

        if (x.getTablespace() != null) {
            println();
            print0(ucase ? "TABLESPACE " : "tablespace ");
            x.getTablespace().accept(this);
        }

        if (x.getStorage() != null) {
            println();
            x.getStorage().accept(this);
        }
    }

    @Override
    public boolean visit(SQLWhileStatement x) {
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }
        print0(ucase ? "WHILE " : "while ");
        x.getCondition().accept(this);
        print0(ucase ? " DO" : " do");
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        println();
        print0(ucase ? "END WHILE" : "end while");
        if (x.getLabelName() != null && !x.getLabelName().equals("")) print(' ');
        print0(x.getLabelName());
        return false;
    }

    @Override
    public boolean visit(SQLDeclareStatement x) {
        boolean printDeclare = !(x.getParent() instanceof OracleCreatePackageStatement);
        if (printDeclare) {
            print0(ucase ? "DECLARE " : "declare ");
        }
        this.printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public boolean visit(SQLReturnStatement x) {
        print0(ucase ? "RETURN" : "return");

        if (x.getExpr() != null) {
            print(' ');
            x.getExpr().accept(this);
        }
        return false;
    }

    public void postVisit(SQLObject x) {
        if (x instanceof SQLStatement) {
            SQLStatement stmt = (SQLStatement) x;
            boolean printSemi = printStatementAfterSemi == null
                    ? stmt.isAfterSemi()
                    : printStatementAfterSemi.booleanValue();
            if (printSemi) {
                print(';');
            }
        }
    }

    @Override
    public boolean visit(SQLArgument x) {
        SQLParameter.ParameterType type = x.getType();
        if (type != null) {
            print0(type.name());
            print(' ');
        }

        x.getExpr().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCommitStatement x) {
        print0(ucase ? "COMMIT" : "commit");

        if (x.isWrite()) {
            print0(ucase ? " WRITE" : " write");
            if (x.getWait() != null) {
                if (x.getWait().booleanValue()) {
                    print0(ucase ? " WAIT" : " wait");
                } else {
                    print0(ucase ? " NOWAIT" : " nowait");
                }
            }

            if (x.getImmediate() != null) {
                if (x.getImmediate().booleanValue()) {
                    print0(ucase ? " IMMEDIATE" : " immediate");
                } else {
                    print0(ucase ? " BATCH" : " batch");
                }
            }
        }

        if (x.isWork()) {
            print0(ucase ? " WORK" : " work");
        }

        if (x.getChain() != null) {
            if (x.getChain().booleanValue()) {
                print0(ucase ? " AND CHAIN" : " and chain");
            } else {
                print0(ucase ? " AND NO CHAIN" : " and no chain");
            }
        }

        if (x.getRelease() != null) {
            if (x.getRelease().booleanValue()) {
                print0(ucase ? " AND RELEASE" : " and release");
            } else {
                print0(ucase ? " AND NO RELEASE" : " and no release");
            }
        }

        return false;
    }

    public boolean visit(SQLFlashbackExpr x) {
        print0(x.getType().name());
        print(' ');
        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLBinaryOpExpr) {
            print('(');
            expr.accept(this);
            print(')');
        } else {
            expr.accept(this);
        }
        return false;
    }

    public boolean visit(SQLCreateMaterializedViewStatement x) {
        print0(ucase ? "CREATE MATERIALIZED VIEW " : "create materialized view ");
        x.getName().accept(this);
        this.printOracleSegmentAttributes(x);
        println();

        Boolean parallel = x.getParallel();
        if (parallel != null) {
            if (parallel) {
                print(ucase ? "PARALLEL" : "parallel");
                Integer parallelValue = x.getParallelValue();
                if (parallelValue != null) {
                    print(' ');
                    print(parallelValue);
                }
            } else {
                print(ucase ? "NOPARALLEL" : "noparallel");
            }
            println();
        }

        if (x.isBuildImmediate()) {
            println(ucase ? "BUILD IMMEDIATE" : "build immediate");
        }

        if (x.isRefresh()) {
            print(ucase ? "REFRESH" : "refresh");

            if (x.isRefreshFast()) {
                print(ucase ? " FAST" : " fast");
            } else if (x.isRefreshComlete()) {
                print(ucase ? " COMPLETE" : " complete");
            } else if (x.isRefreshForce()) {
                print(ucase ? " FORCE" : " force");
            }

            if (x.isRefreshOnCommit()) {
                print(ucase ? " ON COMMIT" : " on commit");
            } else if (x.isRefreshOnDemand()) {
                print(ucase ? " ON DEMAND" : " on demand");
            }

            println();
        }

        Boolean enableQueryRewrite = x.getEnableQueryRewrite();
        if (enableQueryRewrite != null) {
            if (enableQueryRewrite) {
                print(ucase ? "ENABLE QUERY REWRITE" : "enable query rewrite");
            } else {
                print(ucase ? "DISABLE QUERY REWRITE" : "disable query rewrite");
            }
            println();
        }

        println(ucase ? "AS" : "as");
        x.getQuery().accept(this);
        return false;
    }

    public Boolean getPrintStatementAfterSemi() {
        return printStatementAfterSemi;
    }

    public void setPrintStatementAfterSemi(Boolean printStatementAfterSemi) {
        this.printStatementAfterSemi = printStatementAfterSemi;
    }

    public void config(VisitorFeature feature, boolean state) {
        super.config(feature, state);
        if (feature == VisitorFeature.OutputUCase) {
            this.ucase = state;
        } else if (feature == VisitorFeature.OutputParameterized) {
            this.parameterized = state;
        }
    }

    public void setFeatures(int features) {
        super.setFeatures(features);
        this.ucase = isEnabled(VisitorFeature.OutputUCase);
        this.parameterized = isEnabled(VisitorFeature.OutputParameterized);
    }
}
