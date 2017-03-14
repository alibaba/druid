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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerEvent;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeInsertClause;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement.MergeUpdateClause;
import com.alibaba.druid.util.JdbcConstants;

public class SQLASTOutputVisitor extends SQLASTVisitorAdapter implements ParameterizedVisitor, PrintableVisitor {

    protected final Appendable appender;
    private String indent = "\t";
    private int indentCount = 0;
    private boolean prettyFormat = true;
    protected boolean ucase = true;
    protected int selectListNumberOfLine = 5;

    protected boolean groupItemSingleLine = false;

    protected List<Object> parameters;
    protected Set<String> tables;

    protected boolean exportTables = false;

    protected String dbType;

    protected Map<String, String> tableMapping;

    protected int replaceCount;

    protected boolean parameterized = false;
    protected boolean parameterizedMergeInList = false;

    protected boolean shardingSupport = false;

    public SQLASTOutputVisitor(Appendable appender){
        this.appender = appender;
    }

    public SQLASTOutputVisitor(Appendable appender, String dbType){
        this.appender = appender;
        this.dbType = dbType;
    }

    public SQLASTOutputVisitor(Appendable appender, boolean parameterized){
        this.appender = appender;
        this.parameterized = parameterized;
    }

    public int getParametersSize() {
        if (parameters == null) {
            return 0;
        }

        return this.parameters.size();
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

    public Set<String> getTables() {
        return this.tables;
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

    public boolean isParameterized() {
        return parameterized;
    }

    public void setParameterized(boolean parameterized) {
        this.parameterized = parameterized;
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

        print0(Integer.toString(value));
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

    public void print(long value) {
        if (this.appender == null) {
            return;
        }
        print0(Long.toString(value));
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

    protected void printSelectList(List<SQLSelectItem> selectList) {
        incrementIndent();
        for (int i = 0, size = selectList.size(); i < size; ++i) {
            if (i != 0) {
                if (i % selectListNumberOfLine == 0) {
                    println();
                }

                print0(", ");
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
            print0(this.indent);
        }
    }

    public void println() {
        if (!isPrettyFormat()) {
            print(' ');
            return;
        }

        print0("\n");
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

    public boolean visit(SQLBinaryOpExpr x) {
        if (this.parameterized) {
            x = ParameterizedOutputVisitorUtils.merge(this, x);
        }

        if (parameters != null
                && parameters.size() > 0
                && x.getOperator() == SQLBinaryOperator.Equality
                && x.getRight() instanceof SQLVariantRefExpr
                ) {
            SQLVariantRefExpr right = (SQLVariantRefExpr) x.getRight();
            int index = right.getIndex();
            if (index >= 0 && index < parameters.size()) {
                Object param = parameters.get(index);
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
                print0(" ");
            }
            printOperator(x.getOperator());
            print0(" ");
        }

        visitorBinaryRight(x);

        if (isRoot && relational) {
            decrementIndent();
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
        print0(ucase ? "CASE " : "case ");
        if (x.getValueExpr() != null) {
            x.getValueExpr().accept(this);
            print0(" ");
        }

        printAndAccept(x.getItems(), " ");

        if (x.getElseExpr() != null) {
            print0(ucase ? " ELSE " : " else ");
            x.getElseExpr().accept(this);
        }

        print0(ucase ? " END" : " end");
        return false;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        print0(ucase ? "WHEN " : "when ");
        x.getConditionExpr().accept(this);
        print0(ucase ? " THEN " : " then ");
        x.getValueExpr().accept(this);
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
        if (this.parameterized
            && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            print('?');
            incrementReplaceCunt();
            if (this instanceof ExportParameterVisitor || this.parameters != null) {
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        if (x.getText() == null) {
            print0(ucase ? "NULL" : "null");
        } else {
            print('\'');
            print0(x.getText().replaceAll("'", "''"));
            print('\'');
        }

        return false;
    }

    public boolean visit(SQLDataType x) {
        print0(x.getName());
        if (x.getArguments().size() > 0) {
            print('(');
            printAndAccept(x.getArguments(), ", ");
            print(')');
        }

        return false;
    }

    public boolean visit(SQLCharacterDataType x) {
        visit((SQLDataType) x);
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
        println();
        decrementIndent();
        print(')');
        return false;
    }

    public boolean visit(SQLIdentifierExpr x) {
        final String name = x.getName();
        if (!parameterized) {
            print0(x.getName());
            return false;
        }

        return printName(x, name);
    }

    private boolean printName(SQLName x, String name) {
        boolean shardingSupport = this.shardingSupport;
        if (shardingSupport) {
            SQLObject parent = x.getParent();
            shardingSupport = parent instanceof SQLExprTableSource || parent instanceof SQLPropertyExpr;
        }

        if (shardingSupport) {
            int pos = name.lastIndexOf('_');
            if (pos != -1 && pos != name.length() - 1) {
                boolean quote = name.charAt(0) == '`' && name.charAt(name.length() - 1) == '`';
                boolean isNumber = true;

                int end = name.length();
                if (quote) {
                    end--;
                }
                for (int i = pos + 1; i < end; ++i) {
                    char ch = name.charAt(i);
                    if (ch < '0' || ch > '9') {
                        isNumber = false;
                        break;
                    }
                }
                if (isNumber) {
                    boolean isAlias = false;
                    for (SQLObject parent = x.getParent();parent != null; parent = parent.getParent()) {
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
                        int start = quote ? 1 : 0;
                        String realName = name.substring(start, pos);
                        print0(realName);
                        incrementReplaceCunt();
                        return false;
                    } else {
                        print0(name);
                        return false;
                    }
                }
            }

            int numberCount = 0;
            for (int i = name.length() - 1; i >= 0; --i) {
                char ch = name.charAt(i);
                if (ch < '0' || ch > '9') {
                    break;
                } else {
                    numberCount++;
                }
            }

            if (numberCount > 1) {
                int numPos = name.length() - numberCount;
                String realName = name.substring(0, numPos);
                print0(realName);
                incrementReplaceCunt();
                return false;
            }
        }
        print0(name);
        return false;
    }

    public boolean visit(SQLInListExpr x) {
        if (this.parameterized) {
            List<SQLExpr> targetList = x.getTargetList();

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
                if(this instanceof ExportParameterVisitor || this.parameters != null) {
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
        if (this.parameterized
            && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
                return SQLASTOutputVisitorUtils.visit(this, x);
            }

            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        return SQLASTOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        if (x.getOwner() != null) {
            x.getOwner().accept(this);
            print('.');
        }
        printFunctionName(x.getMethodName());
        print('(');
        printAndAccept(x.getParameters(), ", ");

        SQLExpr from = x.getFrom();
        if (from != null) {
            print0(ucase ? " FROM " : " from ");
            from.accept(this);
        }

        print(')');
        return false;
    }

    protected void printFunctionName(String name) {
        print0(name);
    }

    public boolean visit(SQLAggregateExpr x) {
        print0(ucase ? x.getMethodName() : x.getMethodName().toLowerCase());
        print('(');

        if (x.getOption() != null) {
            print0(x.getOption().toString());
            print(' ');
        }

        printAndAccept(x.getArguments(), ", ");

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
        return false;
    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {

    }

    public boolean visit(SQLAllColumnExpr x) {
        print('*');
        return true;
    }

    public boolean visit(SQLNCharExpr x) {
        if (this.parameterized
            && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
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
            && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            SQLObject parent = x.getParent();
            if (parent instanceof ValuesClause) {
                print('?');
                incrementReplaceCunt();

                if(this instanceof ExportParameterVisitor || this.parameters != null){
                    this.getParameters().add(null);
                }
                return false;
            }
        }

        print0(ucase ? "NULL" : "null");
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        if (this.parameterized
            && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            print('?');
            incrementReplaceCunt();

            if(this instanceof ExportParameterVisitor || this.parameters != null){
                ExportParameterVisitorUtils.exportParameter((this).getParameters(), x);
            }
            return false;
        }

        return SQLASTOutputVisitorUtils.visit(this, x);
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
            owner.accept(this);
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

        if (parent instanceof SQLStatement) {
            incrementIndent();

            println();
            x.getSubQuery().accept(this);

            decrementIndent();
        } else if (parent instanceof ValuesClause) {
            println();
            print('(');
            x.getSubQuery().accept(this);
            print(')');
            println();
        } else {
            print('(');
            incrementIndent();
            println();
            x.getSubQuery().accept(this);
            println();
            decrementIndent();
            print(')');
        }
        return false;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        int itemSize = x.getItems().size();
        if (itemSize > 0) {
            print0(ucase ? "GROUP BY " : "group by ");
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
            decrementIndent();
        }

        if (x.getHaving() != null) {
            println();
            print0(ucase ? "HAVING " : "having ");
            x.getHaving().accept(this);
        }

        if (x.isWithRollUp()) {
            print0(ucase ? " WITH ROLLUP" : " with rollup");
        }

        if (x.isWithCube()) {
            print0(ucase ? " WITH CUBE" : " with cube");
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

        if (x.getFrom() != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            x.getFrom().accept(this);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            where.setParent(x);
            where.accept(this);
        }

        if (x.getGroupBy() != null) {
            println();
            x.getGroupBy().accept(this);
        }
        
        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (!JdbcConstants.INFORMIX.equals(dbType)) {
            printFetchFirst(x);
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
                    print0(ucase ? " ROWS " : " rows ");
                }
                print0(ucase ? "FETCH FIRST " : "fetch first ");
                first.accept(this);
                print0(ucase ? " ROWS ONLY" : " rows only");
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
        x.getExpr().accept(this);

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
        x.getExpr().accept(this);
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

        if (tableMapping != null && expr instanceof SQLName) {
            String tableName = expr.toString();

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

        expr.accept(this);
    }

    public boolean visit(SQLExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        if (isPrettyFormat() && x.hasAfterComment()) {
            print(' ');
            printComment(x.getAfterCommentsDirect(), "\n");
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

        select.accept(this);

        return false;
    }

    public boolean visit(SQLVariantRefExpr x) {
        int index = x.getIndex();

        if (index < 0 || parameters == null || index >= parameters.size()) {
            print0(x.getName());
            return false;
        }

        Object param = parameters.get(index);

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

        print0("'" + param.getClass().getName() + "'");
    }

    public boolean visit(SQLDropTableStatement x) {
        if (x.isTemporary()) {
            print0(ucase ? "DROP TEMPORARY TABLE " : "drop temporary table ");
        } else {
            print0(ucase ? "DROP TABLE " : "drop table ");
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
            x.getWhere().setParent(x);
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
                x.getQuery().setParent(x);
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
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        return false;
    }

    public boolean visit(SQLCreateTableStatement x) {
        print0(ucase ? "CREATE TABLE " : "create table ");
        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
            print0(ucase ? "GLOBAL TEMPORARY " : "global temporary ");
        } else if (SQLCreateTableStatement.Type.LOCAL_TEMPORARY.equals(x.getType())) {
            print0(ucase ? "LOCAL TEMPORARY " : "local temporary ");
        }

        printTableSourceExpr(x.getName());

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

        if (x.getInherits() != null) {
            print0(ucase ? " INHERITS (" : " inherits (");
            x.getInherits().accept(this);
            print(')');
        }

        return false;
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
        x.getLeft().accept(this);
        println();
        print0(ucase ? x.getOperator().name : x.getOperator().name_lcase);
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
        if (this.parameterized && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
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
        print0(ucase ? "SET " : "set ");
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
        x.getLeft().accept(this);
        incrementIndent();

        if (x.getJoinType() == JoinType.COMMA) {
            print(',');
        } else {
            println();
            printJoinType(x.getJoinType());
        }
        print(' ');
        x.getRight().accept(this);

        if (x.getCondition() != null) {
            incrementIndent();
            print0(ucase ? " ON " : " on ");
            x.getCondition().accept(this);
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
        print('(');
        incrementIndent();
        for (int i = 0, size = x.getValues().size(); i < size; ++i) {
            if (i != 0) {
                if (i % 5 == 0) {
                    println();
                }
                print0(", ");
            }

            SQLExpr expr = x.getValues().get(i);
            expr.setParent(x);
            expr.accept(this);
        }
        decrementIndent();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLSomeExpr x) {
        print0(ucase ? "SOME (" : "some (");

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAnyExpr x) {
        print0(ucase ? "ANY (" : "any (");

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
        print(')');
        return false;
    }

    @Override
    public boolean visit(SQLAllExpr x) {
        print0(ucase ? "ALL (" : "all (");

        incrementIndent();
        x.getSubQuery().accept(this);
        decrementIndent();
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
        x.getSubQuery().accept(this);
        decrementIndent();
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
        x.getSelect().accept(this);
        println();
        decrementIndent();
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
        print0(ucase ? "SAVEPOINT " : "savepoint ");
        x.getName().accept(this);
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

        x.getName().accept(this);

        if (x.getColumns().size() > 0) {
            println();
            print('(');
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
        print0(ucase ? "WITH" : "with");
        if (x.getRecursive() == Boolean.TRUE) {
            print0(ucase ? " RECURSIVE" : " recursive");
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
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }
        println();
        print0(ucase ? "AS" : "as");
        println();
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

        print0(ucase ? " REFERENCES " : " references ");
        x.getReferencedTableName().accept(this);

        if (x.getReferencedColumns().size() > 0) {
            print0(" (");
            printAndAccept(x.getReferencedColumns(), ", ");
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        print0(ucase ? "DROP SEQUENCE " : "drop sequence ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(SQLDropSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        print0(ucase ? "DROP TRIGGER " : "drop trigger ");
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
        if (x.getType() != null) {
            print0(x.getType());
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
            print0(ucase ? "OR REPLEACE " : "or repleace ");
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
        print0(x.getValue() ? "true" : "false");

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

    protected void printComment(List<String> comments, String seperator) {
        if (comments != null) {
            for (int i = 0; i < comments.size(); ++i) {
                if (i != 0) {
                    print0(seperator);
                }
                String comment = comments.get(i);
                print0(comment);
            }
        }
    }

    protected void printlnComments(List<String> comments) {
        if (comments != null) {
            for (int i = 0; i < comments.size(); ++i) {
                String comment = comments.get(i);
                print0(comment);
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
        return false;
    }

    @Override
    public boolean visit(SQLFetchStatement x) {
        print0(ucase ? "FETCH " : "fetch ");
        x.getCursorName().accept(this);
        print0(ucase ? " INTO " : " into ");
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
            item.setParent(x);
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
            item.setParent(x);
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
            item.setParent(x);
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
            item.setParent(x);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }

        decrementIndent();
        println();
        print0(ucase ? "END LOOP" : "end loop");
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

            if (x.getParamType() == SQLParameter.ParameterType.IN) {
                print0(ucase ? "IN " : "in ");
            } else if (x.getParamType() == SQLParameter.ParameterType.OUT) {
                print0(ucase ? "OUT " : "out ");
            } else if (x.getParamType() == SQLParameter.ParameterType.INOUT) {
                print0(ucase ? "INOUT " : "inout ");
            }
            x.getName().accept(this);
            print(' ');

            x.getDataType().accept(this);

            if (x.getDefaultValue() != null) {
                print0(" := ");
                x.getDefaultValue().accept(this);
            }
        }

        return false;
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
        this.ucase = val;
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

        if (x.getTableSpace() != null) {
            print0(ucase ? " TABLESPACE " : " tablespace ");
            x.getTableSpace().accept(this);
        }

        if (x.getEngine() != null) {
            print0(ucase ? " STORAGE ENGINE " : " storage engine ");
            x.getEngine().accept(this);
        }

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
            println();
            print('(');
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

        if (x.getInterval() != null) {
            print0(ucase ? " INTERVAL " : " interval ");
            x.getInterval().accept(this);
        }

        printPartitionsCountAndSubPartitions(x);

        println();
        print('(');
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

        List<SQLPartition> partitions = x.getPartitions();
        int partitionsSize = partitions.size();
        if (partitionsSize > 0) {
            println();
            incrementIndent();
            print('(');
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

        return false;
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
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getDeleteWhere() != null) {
            incrementIndent();
            println();
            print0(ucase ? "DELETE WHERE " : "delete where ");
            x.getDeleteWhere().setParent(x);
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
            x.getWhere().setParent(x);
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

        if (x.getCache() != null) {
            if (x.getCache().booleanValue()) {
                print0(ucase ? " CACHE" : " cache");
            } else {
                print0(ucase ? " NOCACHE" : " nocache");
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
}
