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
package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.ConditionValue;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.ConditionValue.ConditionType;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlIntervalExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MySqlOutputVisitor extends SQLASTOutputVisitor implements MySqlASTVisitor {

    {
        this.dbType = JdbcConstants.MYSQL;
        this.shardingSupport = true;
    }

    public MySqlOutputVisitor(Appendable appender) {
        super(appender);
    }

    public MySqlOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);

        try {
            configFromProperty(System.getProperties());
        } catch (AccessControlException e) {
            // skip
        }
    }

    public void configFromProperty(Properties properties) {
        if (parameterized) {
            String property = properties.getProperty("druid.parameterized.shardingSupport");
            if ("true".equals(property)) {
                this.setShardingSupport(true);
            } else if ("false".equals(property)) {
                this.setShardingSupport(false);
            }
        }
    }

    public boolean isShardingSupport() {
        return parameterized && shardingSupport;
    }

    public void setShardingSupport(boolean shardingSupport) {
        this.shardingSupport = shardingSupport;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof MySqlSelectQueryBlock) {
            return visit((MySqlSelectQueryBlock) select);
        }

        return super.visit(select);
    }

    public boolean visit(MySqlSelectQueryBlock x) {
        final boolean bracket = x.isBracket();
        if (bracket) {
            print('(');
        }

        if ((!isParameterized()) && isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "SELECT " : "select ");

        for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
            SQLCommentHint hint = x.getHints().get(i);
            hint.accept(this);
            print(' ');
        }

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.DISTINCTROW == x.getDistionOption()) {
            print0(ucase ? "DISTINCTROW " : "distinctrow ");
        }

        if (x.isHignPriority()) {
            print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
        }

        if (x.isStraightJoin()) {
            print0(ucase ? "STRAIGHT_JOIN " : "straight_join ");
        }

        if (x.isSmallResult()) {
            print0(ucase ? "SQL_SMALL_RESULT " : "sql_small_result ");
        }

        if (x.isBigResult()) {
            print0(ucase ? "SQL_BIG_RESULT " : "sql_big_result ");
        }

        if (x.isBufferResult()) {
            print0(ucase ? "SQL_BUFFER_RESULT " : "sql_buffer_result ");
        }

        if (x.getCache() != null) {
            if (x.getCache().booleanValue()) {
                print0(ucase ? "SQL_CACHE " : "sql_cache ");
            } else {
                print0(ucase ? "SQL_NO_CACHE " : "sql_no_cache ");
            }
        }

        if (x.isCalcFoundRows()) {
            print0(ucase ? "SQL_CALC_FOUND_ROWS " : "sql_calc_found_rows ");
        }

        printSelectList(x.getSelectList());

        SQLExprTableSource into = x.getInto();
        if (into != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            into.accept(this);
        }

        SQLTableSource form = x.getFrom();
        if (form != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            form.accept(this);
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
            groupBy.accept(this);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            limit.accept(this);
        }

        SQLName procedureName = x.getProcedureName();
        if (procedureName != null) {
            print0(ucase ? " PROCEDURE " : " procedure ");
            procedureName.accept(this);
            if (!x.getProcedureArgumentList().isEmpty()) {
                print('(');
                printAndAccept(x.getProcedureArgumentList(), ", ");
                print(')');
            }
        }

        if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
            if (x.isNoWait()) {
                print0(ucase ? " NOWAIT" : " nowait");
            } else if (x.getWaitTime() != null) {
                print0(ucase ? " WAIT " : " wait ");
                x.getWaitTime().accept(this);
            }
        }

        if (x.isLockInShareMode()) {
            println();
            print0(ucase ? "LOCK IN SHARE MODE" : "lock in share mode");
        }

        if (bracket) {
            print(')');
        }

        return false;
    }

    public boolean visit(SQLColumnDefinition x) {
        x.getName().accept(this);

        SQLDataType dataType = x.getDataType();
        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        final SQLExpr charsetExpr = x.getCharsetExpr();
        if (charsetExpr != null) {
            print0(ucase ? " CHARSET " : " charset ");
            charsetExpr.accept(this);
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            print(' ');
            item.accept(this);
        }

        final SQLExpr defaultExpr = x.getDefaultExpr();
        if (defaultExpr != null) {
            print0(ucase ? " DEFAULT " : " default ");
            defaultExpr.accept(this);
        }

        final SQLExpr storage = x.getStorage();
        if (storage != null) {
            print0(ucase ? " STORAGE " : " storage ");
            storage.accept(this);
        }

        SQLExpr onUpdate = x.getOnUpdate();
        if (onUpdate != null) {
            print0(ucase ? " ON UPDATE " : " on update ");

            onUpdate.accept(this);
        }

        if (x.isAutoIncrement()) {
            print0(ucase ? " AUTO_INCREMENT" : " auto_increment");
        }

        if (x.getComment() != null) {
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        if (x.getAsExpr() != null) {
            print0(ucase ? " AS (" : " as (");
            x.getAsExpr().accept(this);
            print(')');
        }

        if (x.isSorted()) {
            print0(ucase ? " SORTED" : " sorted");
        }

        return false;
    }

    public boolean visit(SQLDataType x) {
        printDataType(x);

        if (Boolean.TRUE == x.getAttribute("UNSIGNED")) {
            print0(ucase ? " UNSIGNED" : " unsigned");
        }

        if (Boolean.TRUE == x.getAttribute("ZEROFILL")) {
            print0(ucase ? " ZEROFILL" : " zerofill");
        }

        if (x instanceof SQLCharacterDataType) {
            SQLCharacterDataType charType = (SQLCharacterDataType) x;
            if (charType.getCharSetName() != null) {
                print0(ucase ? " CHARACTER SET " : " character set ");
                print0(charType.getCharSetName());

                if (charType.getCollate() != null) {
                    print0(ucase ? " COLLATE " : " collate ");
                    print0(charType.getCollate());
                }
            }

            List<SQLCommentHint> hints = ((SQLCharacterDataType) x).hints;
            if (hints != null) {
                print(' ');
                for (SQLCommentHint hint : hints) {
                    hint.accept(this);
                }
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLCharacterDataType x) {
        printDataType(x);

        if (x.isHasBinary()) {
            print0(ucase ? " BINARY " : " binary ");
        }

        if (x.getCharSetName() != null) {
            print0(ucase ? " CHARACTER SET " : " character set ");
            print0(x.getCharSetName());
            if (x.getCollate() != null) {
                print0(ucase ? " COLLATE " : " collate ");
                print0(x.getCollate());
            }
        } else if (x.getCollate() != null) {
            print0(ucase ? " COLLATE " : " collate ");
            print0(x.getCollate());
        }

        List<SQLCommentHint> hints = ((SQLCharacterDataType) x).hints;
        if (hints != null) {
            print(' ');
            for (SQLCommentHint hint : hints) {
                hint.accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(MySqlTableIndex x) {

    }

    @Override
    public boolean visit(MySqlTableIndex x) {
        String indexType = x.getIndexType();

        if ("FULLTEXT".equalsIgnoreCase(indexType)) {
            print0(ucase ? "FULLTEXT " : "fulltext ");
        }

        print0(ucase ? "INDEX" : "index");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }

        if (indexType != null && !"FULLTEXT".equalsIgnoreCase(indexType)) {
            print0(ucase ? " USING " : " using ");
            print0(indexType);
        }

        print('(');
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');
        return false;
    }

    public boolean visit(MySqlCreateTableStatement x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "CREATE " : "create ");

        for (SQLCommentHint hint : x.getHints()) {
            hint.accept(this);
            print(' ');
        }

        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
            print0(ucase ? "TEMPORARY TABLE " : "temporary table ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        if (x.isIfNotExiists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(x.getName());

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        printTableElements(x.getTableElementList());

        for (Map.Entry<String, SQLObject> option : x.getTableOptions().entrySet()) {
            String key = option.getKey();

            print(' ');
            print0(ucase ? key : key.toLowerCase());

            if ("TABLESPACE".equals(key)) {
                print(' ');
                option.getValue().accept(this);
                continue;
            } else if ("UNION".equals(key)) {
                print0(" = (");
                option.getValue().accept(this);
                print(')');
                continue;
            }

            print0(" = ");

            option.getValue().accept(this);
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            comment.accept(this);
        }

        if (x.getPartitioning() != null) {
            println();
            x.getPartitioning().accept(this);
        }

        if (x.getTableGroup() != null) {
            println();
            print0(ucase ? "TABLEGROUP " : "tablegroup ");
            x.getTableGroup().accept(this);
        }

        if (x.getSelect() != null) {
            incrementIndent();
            println();
            x.getSelect().accept(this);
            decrementIndent();
        }

        for (SQLCommentHint hint : x.getOptionHints()) {
            print(' ');
            hint.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlKey x) {

    }

    @Override
    public void endVisit(MySqlPrimaryKey x) {

    }

    @Override
    public void endVisit(MysqlForeignKey x) {

    }

    @Override
    public boolean visit(MySqlKey x) {
        if (x.isHasConstaint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print0(ucase ? "KEY" : "key");

        SQLName name = x.getName();
        if (name != null) {
            print(' ');
            name.accept(this);
        }

        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getIndexType());
        }

        print0(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        return false;
    }

    @Override
    public boolean visit(MySqlPrimaryKey x) {
        if (x.getName() != null) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            x.getName().accept(this);
            print(' ');
        }

        print0(ucase ? "PRIMARY KEY" : "primary key");

        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            print0(x.getIndexType());
        }

        print0(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (this.appender == null) {
            return false;
        }

        try {
            if (this.parameterized
                    && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
                this.appender.append('?');
                incrementReplaceCunt();
                if (this instanceof ExportParameterVisitor || this.parameters != null) {
                    ExportParameterVisitorUtils.exportParameter(this.parameters, x);
                }
                return false;
            }

            this.appender.append('\'');

            String text = x.getText();

            boolean hasSpecial = false;
            for (int i = 0; i < text.length(); ++i) {
                char ch = text.charAt(i);
                if (ch == '\'' || ch == '\\' || ch == '\0') {
                    hasSpecial = true;
                    break;
                }
            }

            if (hasSpecial) {
                for (int i = 0; i < text.length(); ++i) {
                    char ch = text.charAt(i);
                    if (ch == '\'') {
                        appender.append('\'');
                        appender.append('\'');
                    } else if (ch == '\\') {
                        appender.append('\\');
                        appender.append('\\');
                    } else if (ch == '\0') {
                        appender.append('\\');
                        appender.append('0');
                    } else {
                        appender.append(ch);
                    }
                }
            } else {
                appender.append(text);
            }

            appender.append('\'');
        return false;
        } catch (IOException e) {
            throw new RuntimeException("println error", e);
        }
    }

    public boolean visit(SQLVariantRefExpr x) {
        {
            int index = x.getIndex();

            if (inputParameters != null && index < inputParameters.size()) {

                return super.visit(x);
            }
        }

        String varName = x.getName();
        if (x.isGlobal()) {
            print0("@@global.");
        } else {
//            if ((!varName.startsWith("@")) // /
//                    && (!varName.equals("?")) //
//                    && (!varName.startsWith("#")) //
//                    && (!varName.startsWith("$")) //
//                    && (!varName.startsWith(":"))) {
//
//                boolean subPartitionOption = false;
//                if (x.getParent() != null) {
//                    subPartitionOption = x.getParent().getParent() instanceof SQLSubPartitionBy;
//                }
//
//                if (!subPartitionOption) {
//                    print0("@@");
//                }
//            }
        }

        for (int i = 0; i < x.getName().length(); ++i) {
            char ch = x.getName().charAt(i);
            if (ch == '\'') {
                if (x.getName().startsWith("@@") && i == 2) {
                    print(ch);
                } else if (x.getName().startsWith("@") && i == 1) {
                    print(ch);
                } else if (i != 0 && i != x.getName().length() - 1) {
                    print0("\\'");
                } else {
                    print(ch);
                }
            } else {
                print(ch);
            }
        }

        String collate = (String) x.getAttribute("COLLATE");
        if (collate != null) {
            print0(ucase ? " COLLATE " : " collate ");
            print0(collate);
        }

        return false;
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        SQLExpr owner = x.getOwner();
        if ("SUBSTRING".equalsIgnoreCase(x.getMethodName())) {
            if (owner != null) {
                printMethodOwner(owner);
            }
            print0(x.getMethodName());
            print('(');
            printAndAccept(x.getParameters(), ", ");
            SQLExpr from = (SQLExpr) x.getAttribute("FROM");
            if (from != null) {
                print0(ucase ? " FROM " : " from ");
                from.accept(this);
            }

            SQLExpr forExpr = (SQLExpr) x.getAttribute("FOR");
            if (forExpr != null) {
                print0(ucase ? " FOR " : " for ");
                forExpr.accept(this);
            }
            print(')');

            return false;
        }

        if ("TRIM".equalsIgnoreCase(x.getMethodName())) {
            if (owner != null) {
                printMethodOwner(owner);
            }
            print0(x.getMethodName());
            print('(');

            String trimType = (String) x.getAttribute("TRIM_TYPE");
            if (trimType != null) {
                print0(trimType);
                print(' ');
            }

            printAndAccept(x.getParameters(), ", ");

            SQLExpr from = (SQLExpr) x.getAttribute("FROM");
            if (from != null) {
                print0(ucase ? " FROM " : " from ");
                from.accept(this);
            }

            print(')');

            return false;
        }

        if (("CONVERT".equalsIgnoreCase(x.getMethodName())) || "CHAR".equalsIgnoreCase(x.getMethodName())) {
            if (owner != null) {
                printMethodOwner(owner);
            }
            print0(x.getMethodName());
            print('(');
            printAndAccept(x.getParameters(), ", ");

            String charset = (String) x.getAttribute("USING");
            if (charset != null) {
                print0(ucase ? " USING " : " using ");
                print0(charset);
            }
            print(')');
            return false;
        }

        return super.visit(x);
    }

    @Override
    public void endVisit(MySqlIntervalExpr x) {

    }

    @Override
    public boolean visit(MySqlIntervalExpr x) {
        print0(ucase ? "INTERVAL " : "interval ");
        SQLExpr value = x.getValue();
        value.accept(this);
        print(' ');
        print0(ucase ? x.getUnit().name() : x.getUnit().name_lcase);
        return false;
    }

    @Override
    public boolean visit(MySqlExtractExpr x) {
        print0(ucase ? "EXTRACT(" : "extract(");
        print0(x.getUnit().name());
        print0(ucase ? " FROM " : " from ");
        x.getValue().accept(this);
        print(')');
        return false;
    }

    @Override
    public void endVisit(MySqlExtractExpr x) {

    }

    @Override
    public void endVisit(MySqlMatchAgainstExpr x) {

    }

    @Override
    public boolean visit(MySqlMatchAgainstExpr x) {
        print0(ucase ? "MATCH (" : "match (");
        printAndAccept(x.getColumns(), ", ");
        print(')');

        print0(ucase ? " AGAINST (" : " against (");
        x.getAgainst().accept(this);
        if (x.getSearchModifier() != null) {
            print(' ');
            print0(ucase ? x.getSearchModifier().name : x.getSearchModifier().name_lcase);
        }
        print(')');

        return false;
    }

    @Override
    public void endVisit(MySqlPrepareStatement x) {
    }

    @Override
    public boolean visit(MySqlPrepareStatement x) {
        print0(ucase ? "PREPARE " : "prepare ");
        x.getName().accept(this);
        print0(ucase ? " FROM " : " from ");
        x.getFrom().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlExecuteStatement x) {

    }

    @Override
    public boolean visit(MySqlExecuteStatement x) {
        print0(ucase ? "EXECUTE " : "execute ");
        x.getStatementName().accept(this);
        if (x.getParameters().size() > 0) {
            print0(ucase ? " USING " : " using ");
            ;
            printAndAccept(x.getParameters(), ", ");
        }
        return false;
    }

    @Override
    public void endVisit(MysqlDeallocatePrepareStatement x) {

    }

    public boolean visit(MysqlDeallocatePrepareStatement x) {
        print0(ucase ? "DEALLOCATE PREPARE " : "deallocate prepare ");
        x.getStatementName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlDeleteStatement x) {

    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        print0(ucase ? "DELETE " : "delete ");

        for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
            SQLCommentHint hint = x.getHints().get(i);
            hint.accept(this);
            print(' ');
        }

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isQuick()) {
            print0(ucase ? "QUICK " : "quick ");
        }

        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }

        if (x.getFrom() == null) {
            print0(ucase ? "FROM " : "from ");
            x.getTableSource().accept(this);
        } else {
            x.getTableSource().accept(this);
            println();
            print0(ucase ? "FROM " : "from ");
            x.getFrom().accept(this);
        }

        if (x.getUsing() != null) {
            println();
            print0(ucase ? "USING " : "using ");
            x.getUsing().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            incrementIndent();
            print0(ucase ? "WHERE " : "where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            decrementIndent();
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlInsertStatement x) {

    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        print0(ucase ? "INSERT " : "insert ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isDelayed()) {
            print0(ucase ? "DELAYED " : "delayed ");
        }

        if (x.isHighPriority()) {
            print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
        }

        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }

        if (x.isRollbackOnFail()) {
            print0(ucase ? "ROLLBACK_ON_FAIL " : "rollback_on_fail ");
        }

        print0(ucase ? "INTO " : "into ");

        SQLExprTableSource tableSource = x.getTableSource();
        if (tableSource.getClass() == SQLExprTableSource.class) {
            visit(tableSource);
        } else {
            tableSource.accept(this);
        }

        List<SQLExpr> columns = x.getColumns();
        if (columns.size() > 0) {
            incrementIndent();
            print0(" (");
            for (int i = 0, size = columns.size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }

                SQLExpr columnn = columns.get(i);
                if (columnn instanceof SQLIdentifierExpr) {
                    visit((SQLIdentifierExpr) columnn);
                }
                else {
                    columnn.accept(this);
                }
            }
            print(')');
            decrementIndent();
        }

        List<SQLInsertStatement.ValuesClause>  valuesList = x.getValuesList();
        if (!valuesList.isEmpty()) {
            println();
            printValuesList(valuesList);
        }

        if (x.getQuery() != null) {
            println();
            x.getQuery().accept(this);
        }

        if (x.getDuplicateKeyUpdate().size() != 0) {
            println();
            print0(ucase ? "ON DUPLICATE KEY UPDATE " : "on duplicate key update ");
            for (int i = 0, size = x.getDuplicateKeyUpdate().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }
                x.getDuplicateKeyUpdate().get(i).accept(this);
            }
        }

        return false;
    }

    protected void printValuesList(List<SQLInsertStatement.ValuesClause> valuesList) {

        if (parameterized) {
            print0(ucase ? "VALUES " : "values ");
            incrementIndent();
            valuesList.get(0).accept(this);
            decrementIndent();
            if (valuesList.size() > 1) {
                this.incrementReplaceCunt();
            }
            return;
        }

        print0(ucase ? "VALUES " : "values ");
        if (valuesList.size() > 1) {
            this.indentCount++;
        }
        for (int i = 0, size = valuesList.size(); i < size; ++i) {
            if (i != 0) {
                print(',');
                println();
            }

            SQLInsertStatement.ValuesClause item = valuesList.get(i);
            visit(item);
        }
        if (valuesList.size() > 1) {
            this.indentCount--;
        }
    }

    @Override
    public void endVisit(MySqlLoadDataInFileStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadDataInFileStatement x) {
        print0(ucase ? "LOAD DATA " : "load data ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isConcurrent()) {
            print0(ucase ? "CONCURRENT " : "concurrent ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "INFILE " : "infile ");

        x.getFileName().accept(this);

        if (x.isReplicate()) {
            print0(ucase ? " REPLACE " : " replace ");
        }

        if (x.isIgnore()) {
            print0(ucase ? " IGNORE " : " ignore ");
        }

        print0(ucase ? " INTO TABLE " : " into table ");
        x.getTableName().accept(this);

        if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
            print0(ucase ? " COLUMNS" : " columns");
            if (x.getColumnsTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getColumnsTerminatedBy().accept(this);
            }

            if (x.getColumnsEnclosedBy() != null) {
                if (x.isColumnsEnclosedOptionally()) {
                    print0(ucase ? " OPTIONALLY" : " optionally");
                }
                print0(ucase ? " ENCLOSED BY " : " enclosed by ");
                x.getColumnsEnclosedBy().accept(this);
            }

            if (x.getColumnsEscaped() != null) {
                print0(ucase ? " ESCAPED BY " : " escaped by ");
                x.getColumnsEscaped().accept(this);
            }
        }

        if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
            print0(ucase ? " LINES" : " lines");
            if (x.getLinesStartingBy() != null) {
                print0(ucase ? " STARTING BY " : " starting by ");
                x.getLinesStartingBy().accept(this);
            }

            if (x.getLinesTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getLinesTerminatedBy().accept(this);
            }
        }

        if (x.getIgnoreLinesNumber() != null) {
            print0(ucase ? " IGNORE " : " ignore ");
            x.getIgnoreLinesNumber().accept(this);
            print0(ucase ? " LINES" : " lines");
        }

        if (x.getColumns().size() != 0) {
            print0(" (");
            printAndAccept(x.getColumns(), ", ");
            print(')');
        }

        if (x.getSetList().size() != 0) {
            print0(ucase ? " SET " : " set ");
            printAndAccept(x.getSetList(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlReplaceStatement x) {

    }

    @Override
    public boolean visit(MySqlReplaceStatement x) {
        print0(ucase ? "REPLACE " : "replace ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isDelayed()) {
            print0(ucase ? "DELAYED " : "delayed ");
        }

        print0(ucase ? "INTO " : "into ");

        x.getTableName().accept(this);

        if (x.getColumns().size() > 0) {
            print0(" (");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    print0(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            print(')');
        }

        if (x.getValuesList().size() != 0) {
            println();
            print0(ucase ? "VALUES " : "values ");
            int size = x.getValuesList().size();
            if (size == 0) {
                print0("()");
            } else {
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print0(", ");
                    }
                    x.getValuesList().get(i).accept(this);
                }
            }
        }

        if (x.getQuery() != null) {
            x.getQuery().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLStartTransactionStatement x) {
        print0(ucase ? "START TRANSACTION" : "start transaction");
        if (x.isConsistentSnapshot()) {
            print0(ucase ? " WITH CONSISTENT SNAPSHOT" : " with consistent snapshot");
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        if (x.isBegin()) {
            print0(ucase ? " BEGIN" : " begin");
        }

        if (x.isWork()) {
            print0(ucase ? " WORK" : " work");
        }

        return false;
    }

    public boolean visit(SQLRollbackStatement x) {
        print0(ucase ? "ROLLBACK" : "rollback");

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

        if (x.getTo() != null) {
            print0(ucase ? " TO " : " to ");
            x.getTo().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowColumnsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowColumnsStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL COLUMNS" : "show full columns");
        } else {
            print0(ucase ? "SHOW COLUMNS" : "show columns");
        }

        if (x.getTable() != null) {
            print0(ucase ? " FROM " : " from ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLShowTablesStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL TABLES" : "show full tables");
        } else {
            print0(ucase ? "SHOW TABLES" : "show tables");
        }

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowDatabasesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowDatabasesStatement x) {
        print0(ucase ? "SHOW DATABASES" : "show databases");

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowWarningsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowWarningsStatement x) {
        if (x.isCount()) {
            print0(ucase ? "SHOW COUNT(*) WARNINGS" : "show count(*) warnings");
        } else {
            print0(ucase ? "SHOW WARNINGS" : "show warnings");
            if (x.getLimit() != null) {
                print(' ');
                x.getLimit().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowStatusStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        }

        if (x.isSession()) {
            print0(ucase ? "SESSION " : "session ");
        }

        print0(ucase ? "STATUS" : "status");

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlLoadXmlStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadXmlStatement x) {
        print0(ucase ? "LOAD XML " : "load xml ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isConcurrent()) {
            print0(ucase ? "CONCURRENT " : "concurrent ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "INFILE " : "infile ");

        x.getFileName().accept(this);

        if (x.isReplicate()) {
            print0(ucase ? " REPLACE " : " replace ");
        }

        if (x.isIgnore()) {
            print0(ucase ? " IGNORE " : " ignore ");
        }

        print0(ucase ? " INTO TABLE " : " into table ");
        x.getTableName().accept(this);

        if (x.getCharset() != null) {
            print0(ucase ? " CHARSET " : " charset ");
            print0(x.getCharset());
        }

        if (x.getRowsIdentifiedBy() != null) {
            print0(ucase ? " ROWS IDENTIFIED BY " : " rows identified by ");
            x.getRowsIdentifiedBy().accept(this);
        }

        if (x.getSetList().size() != 0) {
            print0(ucase ? " SET " : " set ");
            printAndAccept(x.getSetList(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(CobarShowStatus x) {

    }

    @Override
    public boolean visit(CobarShowStatus x) {
        print0(ucase ? "SHOW COBAR_STATUS" : "show cobar_status");
        return false;
    }

    @Override
    public void endVisit(MySqlKillStatement x) {

    }

    @Override
    public boolean visit(MySqlKillStatement x) {
        if (MySqlKillStatement.Type.CONNECTION.equals(x.getType())) {
            print0(ucase ? "KILL CONNECTION " : "kill connection ");
        } else if (MySqlKillStatement.Type.QUERY.equals(x.getType())) {
            print0(ucase ? "KILL QUERY " : "kill query ");
        } else {
            print0(ucase ? "KILL " : "kill ");
        }

        printAndAccept(x.getThreadIds(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlBinlogStatement x) {

    }

    @Override
    public boolean visit(MySqlBinlogStatement x) {
        print0(ucase ? "BINLOG " : "binlog ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlResetStatement x) {

    }

    @Override
    public boolean visit(MySqlResetStatement x) {
        print0(ucase ? "RESET " : "reset ");
        for (int i = 0; i < x.getOptions().size(); ++i) {
            if (i != 0) {
                print0(", ");
            }
            print0(x.getOptions().get(i));
        }
        return false;
    }

    @Override
    public void endVisit(MySqlCreateUserStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateUserStatement x) {
        print0(ucase ? "CREATE USER " : "create user ");
        printAndAccept(x.getUsers(), ", ");
        return false;
    }

    @Override
    public void endVisit(UserSpecification x) {

    }

    @Override
    public boolean visit(UserSpecification x) {
        x.getUser().accept(this);

        if (x.getPassword() != null) {
            print0(ucase ? " IDENTIFIED BY " : " identified by ");
            if (x.isPasswordHash()) {
                print0(ucase ? "PASSWORD " : "password ");
            }
            x.getPassword().accept(this);
        }

        if (x.getAuthPlugin() != null) {
            print0(ucase ? " IDENTIFIED WITH " : " identified with ");
            x.getAuthPlugin().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlPartitionByKey x) {

    }

    @Override
    public boolean visit(MySqlPartitionByKey x) {
        if (x.isLinear()) {
            print0(ucase ? "PARTITION BY LINEAR KEY (" : "partition by linear key (");
        } else {
            print0(ucase ? "PARTITION BY KEY (" : "partition by key (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(')');

        printPartitionsCountAndSubPartitions(x);
        return false;
    }


    //

    @Override
    public void endVisit(MySqlSelectQueryBlock x) {

    }

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        print0(ucase ? "OUTFILE " : "outfile ");
        x.getFile().accept(this);

        if (x.getCharset() != null) {
            print0(ucase ? " CHARACTER SET " : " character set ");
            print0(x.getCharset());
        }

        if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
            print0(ucase ? " COLUMNS" : " columns");
            if (x.getColumnsTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getColumnsTerminatedBy().accept(this);
            }

            if (x.getColumnsEnclosedBy() != null) {
                if (x.isColumnsEnclosedOptionally()) {
                    print0(ucase ? " OPTIONALLY" : " optionally");
                }
                print0(ucase ? " ENCLOSED BY " : " enclosed by ");
                x.getColumnsEnclosedBy().accept(this);
            }

            if (x.getColumnsEscaped() != null) {
                print0(ucase ? " ESCAPED BY " : " escaped by ");
                x.getColumnsEscaped().accept(this);
            }
        }

        if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
            print0(ucase ? " LINES" : " lines");
            if (x.getLinesStartingBy() != null) {
                print0(ucase ? " STARTING BY " : " starting by ");
                x.getLinesStartingBy().accept(this);
            }

            if (x.getLinesTerminatedBy() != null) {
                print0(ucase ? " TERMINATED BY " : " terminated by ");
                x.getLinesTerminatedBy().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(MySqlOutFileExpr x) {

    }

    @Override
    public boolean visit(MySqlExplainStatement x) {
        String name = x.isDescribe() ? "desc" : "explain";
        print0(ucase ? name.toUpperCase() : name);
        print(' ');

        // tbl_name [col_name | wild]
        if (x.getTableName() != null) {
            x.getTableName().accept(this);
            if (x.getColumnName() != null) {
                print(' ');
                x.getColumnName().accept(this);
            } else if (x.getWild() != null) {
                print(' ');
                x.getWild().accept(this);
            }
        } else {
            // [explain_type]
            String type = x.getType();
            if (type != null) {
                print0(type);
                print(' ');
                if ("format".equalsIgnoreCase(type)) {
                    print0("= ");
                    print0(x.getFormat());
                    print(' ');
                }
            }

            // {explainable_stmt | FOR CONNECTION connection_id}
            if (x.getConnectionId() != null) {
                print0(ucase ? "FOR CONNECTION " : "for connection ");
                x.getConnectionId().accept(this);
            } else {
                x.getStatement().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(MySqlExplainStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        if (x.getReturning() != null && x.getReturning().size() > 0) {
            print0(ucase ? "SELECT " : "select ");
            printAndAccept(x.getReturning(), ", ");
            println();
            print0(ucase ? "FROM " : "from ");
        }

        print0(ucase ? "UPDATE " : "update ");

        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }

        if (x.isCommitOnSuccess()) {
            print0(ucase ? "COMMIT_ON_SUCCESS " : "commit_on_success ");
        }

        if (x.isRollBackOnFail()) {
            print0(ucase ? "ROLLBACK_ON_FAIL " : "rollback_on_fail ");
        }

        if (x.isQueryOnPk()) {
            print0(ucase ? "QUEUE_ON_PK " : "queue_on_pk ");
        }

        if (x.getTargetAffectRow() != null) {
            print0(ucase ? "TARGET_AFFECT_ROW " : "target_affect_row ");
            x.getTargetAffectRow().accept(this);
            print(' ');
        }

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

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUpdateStatement x) {

    }

    @Override
    public boolean visit(MySqlSetTransactionStatement x) {
        if (x.getGlobal() == null) {
            print0(ucase ? "SET TRANSACTION " : "set transaction ");
        } else if (x.getGlobal().booleanValue()) {
            print0(ucase ? "SET GLOBAL TRANSACTION " : "set global transaction ");
        } else {
            print0(ucase ? "SET SESSION TRANSACTION " : "set session transaction ");
        }

        if (x.getIsolationLevel() != null) {
            print0(ucase ? "ISOLATION LEVEL " : "isolation level ");
            print0(x.getIsolationLevel());
        }

        if (x.getAccessModel() != null) {
            print0(ucase ? "READ " : "read ");
            print0(x.getAccessModel());
        }

        return false;
    }

    @Override
    public void endVisit(MySqlSetTransactionStatement x) {

    }
//
//    @Override
//    public boolean visit(MySqlSetNamesStatement x) {
//        print0(ucase ? "SET NAMES " : "set names ");
//        if (x.isDefault()) {
//            print0(ucase ? "DEFAULT" : "default");
//        } else {
//            print0(x.getCharSet());
//            if (x.getCollate() != null) {
//                print0(ucase ? " COLLATE " : " collate ");
//                print0(x.getCollate());
//            }
//        }
//        return false;
//    }

//    public boolean visit(MySqlSetCharSetStatement x) {
//        print0(ucase ? "SET CHARACTER SET " : "set character set ");
//        if (x.isDefault()) {
//            print0(ucase ? "DEFAULT" : "default");
//        } else {
//            print0(x.getCharSet());
//            if (x.getCollate() != null) {
//                print0(ucase ? " COLLATE " : " collate ");
//                print0(x.getCollate());
//            }
//        }
//        return false;
//    }

    @Override
    public void endVisit(MySqlShowAuthorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowAuthorsStatement x) {
        print0(ucase ? "SHOW AUTHORS" : "show authors");
        return false;
    }

    @Override
    public void endVisit(MySqlShowBinaryLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinaryLogsStatement x) {
        print0(ucase ? "SHOW BINARY LOGS" : "show binary logs");
        return false;
    }

    @Override
    public boolean visit(MySqlShowMasterLogsStatement x) {
        print0(ucase ? "SHOW MASTER LOGS" : "show master logs");
        return false;
    }

    @Override
    public void endVisit(MySqlShowMasterLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCollationStatement x) {
        print0(ucase ? "SHOW COLLATION" : "show collation");
        if (x.getPattern() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getPattern().accept(this);
        }
        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowCollationStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinLogEventsStatement x) {
        print0(ucase ? "SHOW BINLOG EVENTS" : "show binlog events");
        if (x.getIn() != null) {
            print0(ucase ? " IN " : " in ");
            x.getIn().accept(this);
        }
        if (x.getFrom() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getFrom().accept(this);
        }
        if (x.getLimit() != null) {
            print(' ');
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowBinLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCharacterSetStatement x) {
        print0(ucase ? "SHOW CHARACTER SET" : "show character set");
        if (x.getPattern() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getPattern().accept(this);
        }
        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowCharacterSetStatement x) {

    }

    @Override
    public boolean visit(MySqlShowContributorsStatement x) {
        print0(ucase ? "SHOW CONTRIBUTORS" : "show contributors");
        return false;
    }

    @Override
    public void endVisit(MySqlShowContributorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateDatabaseStatement x) {
        print0(ucase ? "SHOW CREATE DATABASE " : "show create database ");
        x.getDatabase().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateEventStatement x) {
        print0(ucase ? "SHOW CREATE EVENT " : "show create event ");
        x.getEventName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateEventStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateFunctionStatement x) {
        print0(ucase ? "SHOW CREATE FUNCTION " : "show create function ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateProcedureStatement x) {
        print0(ucase ? "SHOW CREATE PROCEDURE " : "show create procedure ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTableStatement x) {
        print0(ucase ? "SHOW CREATE TABLE " : "show create table ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateTableStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTriggerStatement x) {
        print0(ucase ? "SHOW CREATE TRIGGER " : "show create trigger ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateViewStatement x) {
        print0(ucase ? "SHOW CREATE VIEW " : "show create view ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateViewStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEngineStatement x) {
        print0(ucase ? "SHOW ENGINE " : "show engine ");
        x.getName().accept(this);
        print(' ');
        print0(x.getOption().name());
        return false;
    }

    @Override
    public void endVisit(MySqlShowEngineStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEventsStatement x) {
        print0(ucase ? "SHOW EVENTS" : "show events");
        if (x.getSchema() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getSchema().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionCodeStatement x) {
        print0(ucase ? "SHOW FUNCTION CODE " : "show function code ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowFunctionCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionStatusStatement x) {
        print0(ucase ? "SHOW FUNCTION STATUS" : "show function status");
        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowFunctionStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEnginesStatement x) {
        if (x.isStorage()) {
            print0(ucase ? "SHOW STORAGE ENGINES" : "show storage engines");
        } else {
            print0(ucase ? "SHOW ENGINES" : "show engines");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowEnginesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowErrorsStatement x) {
        if (x.isCount()) {
            print0(ucase ? "SHOW COUNT(*) ERRORS" : "show count(*) errors");
        } else {
            print0(ucase ? "SHOW ERRORS" : "show errors");
            if (x.getLimit() != null) {
                print(' ');
                x.getLimit().accept(this);
            }
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowErrorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowGrantsStatement x) {
        print0(ucase ? "SHOW GRANTS" : "show grants");
        if (x.getUser() != null) {
            print0(ucase ? " FOR " : " for ");
            x.getUser().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowGrantsStatement x) {

    }

    @Override
    public boolean visit(MySqlUserName x) {
        print0(x.getUserName());
        if (x.getHost() != null) {
            print('@');
            print0(x.getHost());
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUserName x) {

    }

    @Override
    public boolean visit(MySqlShowIndexesStatement x) {
        print0(ucase ? "SHOW INDEX" : "show index");

        if (x.getTable() != null) {
            print0(ucase ? " FROM " : " from ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowIndexesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowKeysStatement x) {
        print0(ucase ? "SHOW KEYS" : "show keys");

        if (x.getTable() != null) {
            print0(ucase ? " FROM " : " from ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowKeysStatement x) {

    }

    @Override
    public boolean visit(MySqlShowMasterStatusStatement x) {
        print0(ucase ? "SHOW MASTER STATUS" : "show master status");
        return false;
    }

    @Override
    public void endVisit(MySqlShowMasterStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowOpenTablesStatement x) {
        print0(ucase ? "SHOW OPEN TABLES" : "show open tables");

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowOpenTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPluginsStatement x) {
        print0(ucase ? "SHOW PLUGINS" : "show plugins");
        return false;
    }

    @Override
    public void endVisit(MySqlShowPluginsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPrivilegesStatement x) {
        print0(ucase ? "SHOW PRIVILEGES" : "show privileges");
        return false;
    }

    @Override
    public void endVisit(MySqlShowPrivilegesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureCodeStatement x) {
        print0(ucase ? "SHOW PROCEDURE CODE " : "show procedure code ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcedureCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureStatusStatement x) {
        print0(ucase ? "SHOW PROCEDURE STATUS" : "show procedure status");
        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcedureStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcessListStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL PROCESSLIST" : "show full processlist");
        } else {
            print0(ucase ? "SHOW PROCESSLIST" : "show processlist");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcessListStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfileStatement x) {
        print0(ucase ? "SHOW PROFILE" : "show profile");
        for (int i = 0; i < x.getTypes().size(); ++i) {
            if (i == 0) {
                print(' ');
            } else {
                print0(", ");
            }
            print0(x.getTypes().get(i).name);
        }

        if (x.getForQuery() != null) {
            print0(ucase ? " FOR QUERY " : " for query ");
            x.getForQuery().accept(this);
        }

        if (x.getLimit() != null) {
            print(' ');
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowProfileStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfilesStatement x) {
        print0(ucase ? "SHOW PROFILES" : "show profiles");
        return false;
    }

    @Override
    public void endVisit(MySqlShowProfilesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowRelayLogEventsStatement x) {
        print0("SHOW RELAYLOG EVENTS");

        if (x.getLogName() != null) {
            print0(ucase ? " IN " : " in ");
            x.getLogName().accept(this);
        }

        if (x.getFrom() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getFrom().accept(this);
        }

        if (x.getLimit() != null) {
            print(' ');
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowRelayLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveHostsStatement x) {
        print0(ucase ? "SHOW SLAVE HOSTS" : "show slave hosts");
        return false;
    }

    @Override
    public void endVisit(MySqlShowSlaveHostsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveStatusStatement x) {
        print0(ucase ? "SHOW SLAVE STATUS" : "show slave status");
        return false;
    }

    @Override
    public void endVisit(MySqlShowSlaveStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTableStatusStatement x) {
        print0(ucase ? "SHOW TABLE STATUS" : "show table status");
        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowTableStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTriggersStatement x) {
        print0(ucase ? "SHOW TRIGGERS" : "show triggers");

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowTriggersStatement x) {

    }

    @Override
    public boolean visit(MySqlShowVariantsStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        }

        if (x.isSession()) {
            print0(ucase ? "SESSION " : "session ");
        }

        print0(ucase ? "VARIABLES" : "variables");

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowVariantsStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        if (x.isIgnore()) {
            print0(ucase ? "ALTER IGNORE TABLE " : "alter ignore table ");
        } else {
            print0(ucase ? "ALTER TABLE " : "alter table ");
        }
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

        if (x.isRemovePatiting()) {
            println();
            print0(ucase ? "REMOVE PARTITIONING" : "remove partitioning");
        }

        if (x.isUpgradePatiting()) {
            println();
            print0(ucase ? "UPGRADE PARTITIONING" : "upgrade partitioning");
        }

        if (x.getTableOptions().size() > 0) {
            println();
        }

        decrementIndent();

        int i = 0;
        for (Map.Entry<String, SQLObject> option : x.getTableOptions().entrySet()) {
            String key = option.getKey();
            if (i != 0) {
                print(' ');
            }
            print0(ucase ? key : key.toLowerCase());

            if ("TABLESPACE".equals(key)) {
                print(' ');
                option.getValue().accept(this);
                continue;
            } else if ("UNION".equals(key)) {
                print0(" = (");
                option.getValue().accept(this);
                print(')');
                continue;
            }

            print0(" = ");

            option.getValue().accept(this);
            i++;
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        print0(ucase ? "ADD COLUMN " : "add column ");

        if (x.getColumns().size() > 1) {
            print('(');
        }
        printAndAccept(x.getColumns(), ", ");
        if (x.getFirstColumn() != null) {
            print0(ucase ? " FIRST " : " first ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print0(ucase ? " AFTER " : " after ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print0(ucase ? " FIRST" : " first");
        }

        if (x.getColumns().size() > 1) {
            print(')');
        }
        return false;
    }

    @Override
    public boolean visit(MySqlRenameTableStatement.Item x) {
        x.getName().accept(this);
        print0(ucase ? " TO " : " to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement.Item x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement x) {
        print0(ucase ? "RENAME TABLE " : "rename table ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement x) {

    }

//    public boolean visit(SQLUnionQuery x) {
//        print('(');
//        x.getLeft().accept(this);
//        print(')');
//        println();
//        print0(ucase ? x.getOperator().name : x.getOperator().name_lcase);
//        println();
//
//        SQLSelectQuery right = x.getRight();
//        boolean needParen = ! (right instanceof SQLUnionQuery);
//
//        if (needParen) {
//            print('(');
//            right.accept(this);
//            print(')');
//        } else {
//            right.accept(this);
//        }
//
//        if (x.getOrderBy() != null) {
//            println();
//            x.getOrderBy().accept(this);
//        }
//
//        if (x.getLimit() != null) {
//            println();
//            x.getLimit().accept(this);
//        }
//
//        return false;
//    }

    @Override
    public boolean visit(MySqlUseIndexHint x) {
        print0(ucase ? "USE INDEX " : "use index ");
        if (x.getOption() != null) {
            print0(ucase ? "FOR " : "for ");
            print0(x.getOption().name);
            print(' ');
        }
        print('(');
        printAndAccept(x.getIndexList(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(MySqlUseIndexHint x) {

    }

    @Override
    public boolean visit(MySqlIgnoreIndexHint x) {
        print0(ucase ? "IGNORE INDEX " : "ignore index ");
        if (x.getOption() != null) {
            print0(ucase ? "FOR " : "for ");
            print0(ucase ? x.getOption().name : x.getOption().name_lcase);
            print(' ');
        }
        print('(');
        printAndAccept(x.getIndexList(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(MySqlIgnoreIndexHint x) {

    }

    public boolean visit(SQLExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        if (x.getAlias() != null) {
            print(' ');
            print0(x.getAlias());
        }

        for (int i = 0; i < x.getHintsSize(); ++i) {
            print(' ');
            x.getHints().get(i).accept(this);
        }

        if (x.getPartitionSize() > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            printlnAndAccept(x.getPartitions(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public boolean visit(MySqlLockTableStatement x) {
        print0(ucase ? "LOCK TABLES " : "lock tables ");
        x.getTableSource().accept(this);
        if (x.getLockType() != null) {
            print(' ');
            print0(x.getLockType().name);
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlLockTableStatement x) {

    }

    @Override
    public boolean visit(MySqlUnlockTablesStatement x) {
        print0(ucase ? "UNLOCK TABLES" : "unlock tables");
        return false;
    }

    @Override
    public void endVisit(MySqlUnlockTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlForceIndexHint x) {
        print0(ucase ? "FORCE INDEX " : "force index ");
        if (x.getOption() != null) {
            print0(ucase ? "FOR " : "for ");
            print0(x.getOption().name);
            print(' ');
        }
        print('(');
        printAndAccept(x.getIndexList(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(MySqlForceIndexHint x) {

    }

    @Override
    public boolean visit(MySqlAlterTableChangeColumn x) {
        print0(ucase ? "CHANGE COLUMN " : "change column ");
        x.getColumnName().accept(this);
        print(' ');
        x.getNewColumnDefinition().accept(this);
        if (x.getFirstColumn() != null) {
            print0(ucase ? " FIRST " : " first ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print0(ucase ? " AFTER " : " after ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print0(ucase ? " FIRST" : " first");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableChangeColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableModifyColumn x) {
        print0(ucase ? "MODIFY COLUMN " : "modify column ");
        x.getNewColumnDefinition().accept(this);
        if (x.getFirstColumn() != null) {
            print0(ucase ? " FIRST " : " first ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print0(ucase ? " AFTER " : " after ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print0(ucase ? " FIRST" : " first");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableModifyColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableCharacter x) {
        print0(ucase ? "CHARACTER SET = " : "character set = ");
        x.getCharacterSet().accept(this);

        if (x.getCollate() != null) {
            print0(ucase ? ", COLLATE = " : ", collate = ");
            x.getCollate().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableCharacter x) {

    }

    @Override
    public boolean visit(MySqlAlterTableOption x) {
        print0(x.getName());
        print0(" = ");
        print0(x.getValue().toString());
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableOption x) {

    }

    @Override
    public void endVisit(MySqlCreateTableStatement x) {

    }

    @Override
    public boolean visit(MySqlHelpStatement x) {
        print0(ucase ? "HELP " : "help ");
        x.getContent().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlHelpStatement x) {

    }

    @Override
    public boolean visit(MySqlCharExpr x) {
        if (parameterized && ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            print('?');
            incrementReplaceCunt();
            if (this instanceof ExportParameterVisitor || this.parameters != null) {
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        print0(x.toString());
        return false;
    }

    @Override
    public void endVisit(MySqlCharExpr x) {

    }

    @Override
    public boolean visit(MySqlUnique x) {
        if (x.isHasConstaint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print0(ucase ? "UNIQUE" : "unique");

        SQLName name = x.getName();
        if (name != null) {
            print(' ');
            name.accept(this);
        }

        if (x.getIndexType() != null) {
            print0(ucase ? " USING " : " using ");
            ;
            print0(x.getIndexType());
        }

        print0(" (");
        printAndAccept(x.getColumns(), ", ");
        print(')');

        return false;
    }

    @Override
    public boolean visit(MysqlForeignKey x) {
        if (x.isHasConstraint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print0(ucase ? "FOREIGN KEY" : "foreign key");

        if (x.getIndexName() != null) {
            print(' ');
            x.getIndexName().accept(this);
        }

        print0(" (");
        printAndAccept(x.getReferencingColumns(), ", ");
        print(')');

        print0(ucase ? " REFERENCES " : " references ");
        x.getReferencedTableName().accept(this);

        print0(" (");
        printAndAccept(x.getReferencedColumns(), ", ");
        print(')');

        MysqlForeignKey.Match match = x.getReferenceMatch();
        if (match != null) {
            print0(ucase ? " MATCH " : " match ");
            print0(ucase ? match.name : match.name_lcase);
        }

        if (x.getOnDelete() != null) {
            print0(ucase ? " ON DELETE " : " on delete ");
            print0(ucase ? x.getOnDelete().name : x.getOnDelete().name_lcase);
        }

        if (x.getOnDelete() != null) {
            print0(ucase ? " ON UPDATE " : " on update ");
            print0(ucase ? x.getOnDelete().name : x.getOnDelete().name_lcase);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUnique x) {

    }

    @Override
    public boolean visit(MySqlAlterTableDiscardTablespace x) {
        print0(ucase ? "DISCARD TABLESPACE" : "discard tablespace");
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableDiscardTablespace x) {

    }

    @Override
    public boolean visit(MySqlAlterTableImportTablespace x) {
        print0(ucase ? "IMPORT TABLESPACE" : "import tablespace");
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableImportTablespace x) {

    }

    @Override
    public boolean visit(SQLAssignItem x) {


        String tagetString = x.getTarget().toString();

        boolean mysqlSpecial = false;

        if (JdbcConstants.MYSQL.equals(dbType)) {
            mysqlSpecial = "NAMES".equalsIgnoreCase(tagetString)
                    || "CHARACTER SET".equalsIgnoreCase(tagetString)
                    || "CHARSET".equalsIgnoreCase(tagetString);
        }

        if (!mysqlSpecial) {
            x.getTarget().accept(this);
            print0(" = ");
        } else {
            print0(ucase ? tagetString.toUpperCase() : tagetString.toLowerCase());
            print(' ');
        }
        x.getValue().accept(this);
        return false;
    }

    @Override
    public boolean visit(TableSpaceOption x) {
        x.getName().accept(this);

        if (x.getStorage() != null) {
            print(' ');
            x.getStorage().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(TableSpaceOption x) {

    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {
        {
            SQLOrderBy value = (SQLOrderBy) aggregateExpr.getAttribute("ORDER BY");
            if (value != null) {
                print(' ');
                ((SQLObject) value).accept(this);
            }
        }
        {
            Object value = aggregateExpr.getAttribute("SEPARATOR");
            if (value != null) {
                print0(ucase ? " SEPARATOR " : " separator ");
                ((SQLObject) value).accept(this);
            }
        }
    }

    @Override
    public boolean visit(MySqlAnalyzeStatement x) {
        print0(ucase ? "ANALYZE " : "analyze ");
        if (x.isNoWriteToBinlog()) {
            print0(ucase ? "NO_WRITE_TO_BINLOG " : "no_write_to_binlog ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "TABLE " : "table ");

        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlAnalyzeStatement x) {

    }

    @Override
    public boolean visit(MySqlOptimizeStatement x) {
        print0(ucase ? "OPTIMIZE " : "optimize ");
        if (x.isNoWriteToBinlog()) {
            print0(ucase ? "NO_WRITE_TO_BINLOG " : "No_write_to_binlog ");
        }

        if (x.isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "TABLE " : "table ");

        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlOptimizeStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterUserStatement x) {
        print0(ucase ? "ALTER USER" : "alter user");
        for (SQLExpr user : x.getUsers()) {
            print(' ');
            user.accept(this);
            print0(ucase ? " PASSWORD EXPIRE" : " password expire");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlAlterUserStatement x) {

    }

    @Override
    public boolean visit(MySqlSetPasswordStatement x) {
        print0(ucase ? "SET PASSWORD " : "set password ");

        if (x.getUser() != null) {
            print0(ucase ? "FOR " : "for ");
            x.getUser().accept(this);
            print(' ');
        }

        print0("= ");

        if (x.getPassword() != null) {
            x.getPassword().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlSetPasswordStatement x) {

    }

    @Override
    public boolean visit(MySqlHintStatement x) {
        List<SQLCommentHint> hints = x.getHints();

        for (SQLCommentHint hint : hints) {
            hint.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlHintStatement x) {

    }

    @Override
    public boolean visit(MySqlOrderingExpr x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(' ');
            print0(ucase ? x.getType().name : x.getType().name_lcase);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlOrderingExpr x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }
        if (x.getParameters().size() != 0) {
            incrementIndent();
            if (x.getParent() instanceof SQLCreateProcedureStatement) {
                printIndent();
            }
            if (!(x.getParent() instanceof SQLCreateProcedureStatement)) {
                print0(ucase ? "DECLARE" : "declare");
                println();
            }

            for (int i = 0, size = x.getParameters().size(); i < size; ++i) {
                if (i != 0) {
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
                print(';');
            }

            decrementIndent();
            println();
        }
        print0(ucase ? "BEGIN" : "begin");
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatementList().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = x.getStatementList().get(i);
            stmt.accept(this);
        }
        decrementIndent();
        println();
        print0(ucase ? "END" : "end");
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print(' ');
            print0(x.getLabelName());
        }
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement x) {
        print0(ucase ? "IF " : "if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
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
        for (SQLIfStatement.ElseIf iterable_element : x.getElseIfList()) {
            iterable_element.accept(this);
        }

        if (x.getElseItem() != null) x.getElseItem().accept(this);

        print0(ucase ? "END IF" : "end if");
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        print0(ucase ? "ELSE IF " : "else if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
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
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        print0(ucase ? "ELSE " : "else ");
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
        return false;
    }

    @Override
    public boolean visit(MySqlCaseStatement x) {
        print0(ucase ? "CASE " : "case ");
        x.getCondition().accept(this);
        println();
        for (int i = 0; i < x.getWhenList().size(); i++) {
            x.getWhenList().get(i).accept(this);
        }
        if (x.getElseItem() != null) x.getElseItem().accept(this);
        print0(ucase ? "END CASE" : "end case");
        return false;
    }

    @Override
    public void endVisit(MySqlCaseStatement x) {

    }

    @Override
    public boolean visit(MySqlDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        printAndAccept(x.getVarList(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlDeclareStatement x) {

    }

    @Override
    public boolean visit(MySqlSelectIntoStatement x) {
        x.getSelect().accept(this);
        print0(ucase ? " INTO " : " into ");
        for (int i = 0; i < x.getVarList().size(); i++) {
            x.getVarList().get(i).accept(this);
            if (i != x.getVarList().size() - 1) print0(", ");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlSelectIntoStatement x) {

    }

    @Override
    public boolean visit(MySqlWhenStatement x) {
        print0(ucase ? "WHEN " : "when ");
        x.getCondition().accept(this);
        print0(" THEN");
        println();
        for (int i = 0; i < x.getStatements().size(); i++) {
            x.getStatements().get(i).accept(this);
            if (i != x.getStatements().size() - 1) {
                println();
            }
        }
        println();
        return false;
    }

    @Override
    public void endVisit(MySqlWhenStatement x) {

    }

    @Override
    public boolean visit(SQLLoopStatement x) {
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }

        print0(ucase ? "LOOP " : "loop ");
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
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(" ");
            print0(x.getLabelName());
        }
        return false;
    }

    @Override
    public boolean visit(MySqlLeaveStatement x) {
        print0(ucase ? "LEAVE " : "leave ");
        print0(x.getLabelName());
        return false;
    }

    @Override
    public void endVisit(MySqlLeaveStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(MySqlIterateStatement x) {
        print0(ucase ? "ITERATE " : "iterate ");
        print0(x.getLabelName());
        return false;
    }

    @Override
    public void endVisit(MySqlIterateStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(MySqlRepeatStatement x) {
        // TODO Auto-generated method stub
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }

        print0(ucase ? "REPEAT " : "repeat ");
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
        print0(ucase ? "UNTIL " : "until ");
        x.getCondition().accept(this);
        println();
        print0(ucase ? "END REPEAT" : "end repeat");
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print(' ');
            print0(x.getLabelName());
        }
        return false;
    }

    @Override
    public void endVisit(MySqlRepeatStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(MySqlCursorDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        print0(x.getCursorName());
        print0(ucase ? " CURSOR FOR" : " cursor for");
        incrementIndent();
        println();
        x.getSelect().accept(this);
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(MySqlCursorDeclareStatement x) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean visit(MySqlUpdateTableSource x) {
        MySqlUpdateStatement update = x.getUpdate();
        if (update != null) {
            update.accept0(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUpdateTableSource x) {

    }

    @Override
    public boolean visit(MySqlAlterTableAlterColumn x) {
        print0(ucase ? "ALTER COLUMN " : "alter column ");
        x.getColumn().accept(this);
        if (x.getDefaultExpr() != null) {
            print0(ucase ? " SET DEFAULT " : " set default ");
            x.getDefaultExpr().accept(this);
        } else if (x.isDropDefault()) {
            print0(ucase ? " DROP DEFAULT" : " drop default");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByKey x) {
        if (x.isLinear()) {
            print0(ucase ? "SUBPARTITION BY LINEAR KEY (" : "subpartition by linear key (");
        } else {
            print0(ucase ? "SUBPARTITION BY KEY (" : "subpartition by key (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(')');

        if (x.getSubPartitionsCount() != null) {
            print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
            x.getSubPartitionsCount().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlSubPartitionByKey x) {

    }

    @Override
    public boolean visit(MySqlSubPartitionByList x) {
        print0(ucase ? "SUBPARTITION BY LIST " : "subpartition by list ");
        if (x.getExpr() != null) {
            print('(');
            x.getExpr().accept(this);
            print0(") ");
        } else {
            if (x.getColumns().size() == 1 && Boolean.TRUE.equals(x.getAttribute("ads.subPartitionList"))) {
                print('(');
            } else {
                print0(ucase ? "COLUMNS (" : "columns (");
            }
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }

        if (x.getOptions().size() != 0) {
            println();
            print0(ucase ? "SUBPARTITION OPTIONS (" : "subpartition options (");
            printAndAccept(x.getOptions(), ", ");
            print(')');
        }

        return false;
    }

    @Override
    public void endVisit(MySqlSubPartitionByList x) {

    }


    @Override
    public boolean visit(MySqlDeclareHandlerStatement x) {
        String handleType = x.getHandleType().name();

        print0(ucase ? "DECLARE " : "declare ");
        print0(ucase ? handleType : handleType.toLowerCase());
        print0(ucase ? " HANDLER FOR " : " handler for ");
        for (int i = 0; i < x.getConditionValues().size(); i++) {
            ConditionValue cv = x.getConditionValues().get(i);
            if (cv.getType() == ConditionType.SQLSTATE) {
                print0(ucase ? " SQLSTATE " : " sqlstate ");
                print0(cv.getValue());
            } else if (cv.getType() == ConditionType.MYSQL_ERROR_CODE) {
                print0(cv.getValue());
            } else if (cv.getType() == ConditionType.SELF) {
                print0(cv.getValue());
            } else if (cv.getType() == ConditionType.SYSTEM) {
                print0(ucase ? cv.getValue().toUpperCase() : cv.getValue().toLowerCase());
            }

            if (i != x.getConditionValues().size() - 1) {
                print0(", ");
            }

        }
        incrementIndent();
        println();
        x.getSpStatement().accept(this);
        decrementIndent();
        return false;
    }

    @Override
    public void endVisit(MySqlDeclareHandlerStatement x) {

    }

    @Override
    public boolean visit(MySqlDeclareConditionStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        print0(x.getConditionName());
        print0(ucase ? " CONDITION FOR " : " condition for ");

        if (x.getConditionValue().getType() == ConditionType.SQLSTATE) {
            print0(ucase ? "SQLSTATE " : "sqlstate ");
            print0(x.getConditionValue().getValue());
        } else {
            print0(x.getConditionValue().getValue());
        }

        println();
        return false;
    }

    @Override
    public void endVisit(MySqlDeclareConditionStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {

        for (int i = 0; i < x.getColumns().size(); ++i) {
            if (i != 0) {
                print0(", ");
            }

            SQLName columnn = x.getColumns().get(i);

            print0(ucase ? "DROP COLUMN " : "drop column ");
            columnn.accept(this);

            if (x.isCascade()) {
                print0(ucase ? " CASCADE" : " cascade");
            }
        }
        return false;
    }

    /**
     * visit procedure create node
     */
    @Override
    public boolean visit(SQLCreateProcedureStatement x) {
        if (x.isOrReplace()) {
            print0(ucase ? "CREATE OR REPLACE PROCEDURE " : "create or replace procedure ");
        } else {
            print0(ucase ? "CREATE PROCEDURE " : "create procedure ");
        }
        x.getName().accept(this);

        int paramSize = x.getParameters().size();

        print0(" (");
        if (paramSize > 0) {
            incrementIndent();
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            decrementIndent();
            println();
        }
        print(')');

        if (x.isDeterministic()) {
            println();
            print(ucase ? "DETERMINISTIC" : "deterministic");
        }

        if (x.isContainsSql()) {
            println();
            print0(ucase ? "CONTAINS SQL" : "contains sql");
        }

        if (x.isNoSql()) {
            println();
            print(ucase ? "NO SQL" : "no sql");
        }

        if (x.isModifiesSqlData()) {
            println();
            print(ucase ? "MODIFIES SQL DATA" : "modifies sql data");
        }

        SQLName authid = x.getAuthid();
        if (authid != null) {
            println();
            print(ucase ? "SQL SECURITY " : "sql security ");
            authid.accept(this);
        }

        println();
        x.getBlock().setParent(x);
        x.getBlock().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        print0(ucase ? "CREATE FUNCTION " : "create function ");
        x.getName().accept(this);

        int paramSize = x.getParameters().size();

        if (paramSize > 0) {
            print0(" (");
            incrementIndent();
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            decrementIndent();
            println();
            print(')');
        }

        println();
        print(ucase ? "RETURNS " : "returns ");
        x.getReturnDataType().accept(this);

        if (x.isDeterministic()) {
            print(ucase ? " DETERMINISTIC" : " deterministic");
        }

        SQLStatement block = x.getBlock();

        println();

        block.accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        SQLCommentStatement.Type type = x.getType();

        SQLExpr on = x.getOn();
        if (type == SQLCommentStatement.Type.TABLE) {
            print0(ucase ? "ALTER TABLE " : "alter table ");
            on.accept(this);
            print0(ucase ? " COMMENT = " : " comment = ");
            x.getComment().accept(this);
        } else {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) on;

            SQLExpr table = propertyExpr.getOwner();
            String column = propertyExpr.getName();

            print0(ucase ? "ALTER TABLE " : "alter table ");
            table.accept(this);
            print0(ucase ? " MODIFY COLUMN " : " modify column ");
            print(column);
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlFlushStatement x) {
        print0(ucase ? "FLUSH " : "flush ");

        print0(ucase ? "TABLES" : "tables");

        List<SQLExprTableSource> tables = x.getTables();
        if (tables.size() > 0) {
            print(' ');
            printAndAccept(tables, ", ");
        }

        if (x.isWithReadLock()) {
            print0(ucase ?  " WITH READ LOCK" : " with read lock");
        }

        if (x.isForExport()) {
            print0(ucase ?  " FOR EXPORT" : " for export");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlFlushStatement x) {

    }
} //
