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
package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLColumnConstraint;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlIntervalExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlSelectGroupByExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAddColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableCharacter;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBlockStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCommitStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateIndexStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDescribeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByHash;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByList;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByRange;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitioningDef;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitioningDef.InValues;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitioningDef.LessThanValues;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRollbackStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectGroupBy;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetPasswordStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStartTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;
import java.util.Map;

public class MySqlOutputVisitor extends SQLASTOutputVisitor implements MySqlASTVisitor {

    public MySqlOutputVisitor(Appendable appender){
        super(appender);
    }

    @Override
    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof MySqlSelectQueryBlock) {
            return visit((MySqlSelectQueryBlock) select);
        }

        return super.visit(select);
    }

    public boolean visit(MySqlSelectQueryBlock x) {
        if (x.getOrderBy() != null) {
            x.getOrderBy().setParent(x);
        }

        print("SELECT ");

        for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
            SQLCommentHint hint = x.getHints().get(i);
            hint.accept(this);
            print(' ');
        }

        if (SQLSetQuantifier.ALL == x.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.DISTINCTROW == x.getDistionOption()) {
            print("DISTINCTROW ");
        }

        if (x.isHignPriority()) {
            print("HIGH_PRIORITY ");
        }

        if (x.isStraightJoin()) {
            print("STRAIGHT_JOIN ");
        }

        if (x.isSmallResult()) {
            print("SQL_SMALL_RESULT ");
        }

        if (x.isBigResult()) {
            print("SQL_BIG_RESULT ");
        }

        if (x.isBufferResult()) {
            print("SQL_BUFFER_RESULT ");
        }

        if (x.getCache() != null) {
            if (x.getCache().booleanValue()) {
                print("SQL_CACHE ");
            } else {
                print("SQL_NO_CACHE ");
            }
        }

        if (x.isCalcFoundRows()) {
            print("SQL_CALC_FOUND_ROWS ");
        }

        printSelectList(x.getSelectList());

        if (x.getInto() != null) {
            println();
            print("INTO ");
            x.getInto().accept(this);
        }

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

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }

        if (x.getProcedureName() != null) {
            print(" PROCEDURE ");
            x.getProcedureName().accept(this);
            if (x.getProcedureArgumentList().size() > 0) {
                print("(");
                printAndAccept(x.getProcedureArgumentList(), ", ");
                print(")");
            }
        }

        if (x.isForUpdate()) {
            println();
            print("FOR UPDATE");
        }

        if (x.isLockInShareMode()) {
            println();
            print("LOCK IN SHARE MODE");
        }

        return false;
    }

    public boolean visit(SQLColumnDefinition x) {
        MySqlSQLColumnDefinition mysqlColumn = null;

        if (x instanceof MySqlSQLColumnDefinition) {
            mysqlColumn = (MySqlSQLColumnDefinition) x;
        }

        x.getName().accept(this);
        print(' ');
        x.getDataType().accept(this);

        for (SQLColumnConstraint item : x.getConstraints()) {
            print(' ');
            item.accept(this);
        }

        if (x.getDefaultExpr() != null) {
            if (x.getDefaultExpr() instanceof SQLNullExpr) {
                print(" NULL");
            } else {
                print(" DEFAULT ");
                x.getDefaultExpr().accept(this);
            }
        }

        if (mysqlColumn != null && mysqlColumn.getStorage() != null) {
            print(" STORAGE ");
            mysqlColumn.getStorage().accept(this);
        }

        if (mysqlColumn != null && mysqlColumn.getOnUpdate() != null) {
            print(" ON UPDATE ");

            mysqlColumn.getOnUpdate().accept(this);
        }

        if (mysqlColumn != null && mysqlColumn.isAutoIncrement()) {
            print(" AUTO_INCREMENT");
        }

        if (x.getComment() != null) {
            print(" COMMENT ");
            x.getComment().accept(this);
        }

        return false;
    }

    public boolean visit(MySqlSelectQueryBlock.Limit x) {
        print("LIMIT ");
        if (x.getOffset() != null) {
            x.getOffset().accept(this);
            print(", ");
        }
        x.getRowCount().accept(this);

        return false;
    }

    public boolean visit(SQLDataType x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            printAndAccept(x.getArguments(), ", ");
            print(")");
        }

        if (Boolean.TRUE == x.getAttribute("UNSIGNED")) {
            print(" UNSIGNED");
        }
        
        if (Boolean.TRUE == x.getAttribute("ZEROFILL")) {
            print(" ZEROFILL");
        }


        if (x instanceof SQLCharacterDataType) {
            SQLCharacterDataType charType = (SQLCharacterDataType) x;
            if (charType.getCharSetName() != null) {
                print(" CHARACTER SET ");
                print(charType.getCharSetName());

                if (charType.getCollate() != null) {
                    print(" COLLATE ");
                    print(charType.getCollate());
                }
            }
        }
        return false;
    }

    public boolean visit(SQLCharacterDataType x) {
        print(x.getName());
        if (x.getArguments().size() > 0) {
            print("(");
            printAndAccept(x.getArguments(), ", ");
            print(")");
        }

        if (x.isHasBinary()) {
            print(" BINARY ");
        }
        
        if (x.getCharSetName() != null) {
            print(" CHARACTER SET ");
            print(x.getCharSetName());
            if (x.getCollate() != null) {
                print(" COLLATE ");
                print(x.getCollate());
            }
        }else if (x.getCollate() != null) {
            print(" COLLATE ");
            print(x.getCollate());
        }
        
        return false;
    }

    @Override
    public void endVisit(Limit x) {

    }

    @Override
    public void endVisit(MySqlTableIndex x) {

    }

    @Override
    public boolean visit(MySqlTableIndex x) {
        print("INDEX");
        if (x.getName() != null) {
            print(" ");
            x.getName().accept(this);
        }

        if (x.getIndexType() != null) {
            print(" USING ");
            print(x.getIndexType());
        }

        print("(");
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(")");
        return false;
    }

    public boolean visit(MySqlCreateTableStatement x) {

        print("CREATE ");

        for (SQLCommentHint hint : x.getHints()) {
            hint.accept(this);
            print(' ');
        }

        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
            print("TEMPORARY TABLE ");
        } else {
            print("TABLE ");
        }

        if (x.isIfNotExiists()) {
            print("IF NOT EXISTS ");
        }

        x.getName().accept(this);

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        int size = x.getTableElementList().size();
        if (size > 0) {
            print(" (");
            incrementIndent();
            println();
            for (int i = 0; i < size; ++i) {
                if (i != 0) {
                    print(", ");
                    println();
                }
                x.getTableElementList().get(i).accept(this);
            }
            decrementIndent();
            println();
            print(")");
        }

        for (Map.Entry<String, SQLObject> option : x.getTableOptions().entrySet()) {
            String key = option.getKey();

            print(' ');
            print(key);

            if ("TABLESPACE".equals(key)) {
                print(' ');
                option.getValue().accept(this);
                continue;
            } else if ("UNION".equals(key)) {
                print(" = (");
                option.getValue().accept(this);
                print(')');
                continue;
            }

            print(" = ");

            option.getValue().accept(this);
        }

        if (x.getPartitioning() != null) {
            print(' ');
            x.getPartitioning().accept(this);
        }

        if (x.getQuery() != null) {
            incrementIndent();
            println();
            x.getQuery().accept(this);
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
            print("CONSTRAINT ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print("KEY");

        if (x.getIndexName() != null) {
            print(' ');
            x.getIndexName().accept(this);
        }

        if (x.getIndexType() != null) {
            print(" USING ");
            print(x.getIndexType());
        }

        print(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(")");

        return false;
    }

    @Override
    public boolean visit(MySqlPrimaryKey x) {
        if (x.getName() != null) {
            print("CONSTRAINT ");
            x.getName().accept(this);
            print(' ');
        }

        print("PRIMARY KEY");

        if (x.getIndexType() != null) {
            print(" USING ");
            print(x.getIndexType());
        }

        print(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(")");

        return false;
    }

    public boolean visit(SQLCharExpr x) {
        print('\'');

        String text = x.getText();
        text = text.replaceAll("'", "''");
        text = text.replace("\\", "\\\\");

        print(text);

        print('\'');
        return false;
    }

    public boolean visit(SQLVariantRefExpr x) {
        {
            int parametersSize = this.getParametersSize();
            int index = x.getIndex();

            if (index >= 0 && index < parametersSize) {
                Object param = this.getParameters().get(index);
                printParameter(param);
                return false;
            }
        }

        String varName = x.getName();
        if (x.isGlobal()) {
            print("@@global.");
        } else {
            if ((!varName.startsWith("@")) // /
                && (!varName.equals("?")) //
                && (!varName.startsWith("#")) //
                && (!varName.startsWith("$")) //
                && (!varName.startsWith(":"))) {
                print("@@");
            }
        }

        for (int i = 0; i < x.getName().length(); ++i) {
            char ch = x.getName().charAt(i);
            if (ch == '\'') {
                if (x.getName().startsWith("@@") && i == 2) {
                    print(ch);
                } else if (x.getName().startsWith("@") && i == 1) {
                    print(ch);
                } else if (i != 0 && i != x.getName().length() - 1) {
                    print("\\'");
                } else {
                    print(ch);
                }
            } else {
                print(ch);
            }
        }

        String collate = (String) x.getAttribute("COLLATE");
        if (collate != null) {
            print(" COLLATE ");
            print(collate);
        }

        return false;
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        if ("SUBSTRING".equalsIgnoreCase(x.getMethodName())) {
            if (x.getOwner() != null) {
                x.getOwner().accept(this);
                print(".");
            }
            print(x.getMethodName());
            print("(");
            printAndAccept(x.getParameters(), ", ");
            SQLExpr from = (SQLExpr) x.getAttribute("FROM");
            if (from != null) {
                print(" FROM ");
                from.accept(this);
            }

            SQLExpr forExpr = (SQLExpr) x.getAttribute("FOR");
            if (forExpr != null) {
                print(" FOR ");
                forExpr.accept(this);
            }
            print(")");

            return false;
        }

        if ("TRIM".equalsIgnoreCase(x.getMethodName())) {
            if (x.getOwner() != null) {
                x.getOwner().accept(this);
                print(".");
            }
            print(x.getMethodName());
            print("(");

            String trimType = (String) x.getAttribute("TRIM_TYPE");
            if (trimType != null) {
                print(trimType);
                print(' ');
            }

            printAndAccept(x.getParameters(), ", ");

            SQLExpr from = (SQLExpr) x.getAttribute("FROM");
            if (from != null) {
                print(" FROM ");
                from.accept(this);
            }

            print(")");

            return false;
        }

        if (("CONVERT".equalsIgnoreCase(x.getMethodName()))||"CHAR".equalsIgnoreCase(x.getMethodName())) {
            if (x.getOwner() != null) {
                x.getOwner().accept(this);
                print(".");
            }
            print(x.getMethodName());
            print("(");
            printAndAccept(x.getParameters(), ", ");

            String charset = (String) x.getAttribute("USING");
            if (charset != null) {
                print(" USING ");
                print(charset);
            }
            print(")");
            return false;
        }

        return super.visit(x);
    }

    @Override
    public void endVisit(MySqlIntervalExpr x) {

    }

    @Override
    public boolean visit(MySqlIntervalExpr x) {
        print("INTERVAL ");
        x.getValue().accept(this);
        print(' ');
        print(x.getUnit().name());
        return false;
    }

    @Override
    public boolean visit(MySqlExtractExpr x) {
        print("EXTRACT(");
        print(x.getUnit().name());
        print(" FROM ");
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
        print("MATCH (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        print(" AGAINST (");
        x.getAgainst().accept(this);
        if (x.getSearchModifier() != null) {
            print(' ');
            print(x.getSearchModifier().name);
        }
        print(')');

        return false;
    }

    @Override
    public void endVisit(MySqlPrepareStatement x) {
    }

    @Override
    public boolean visit(MySqlPrepareStatement x) {
        print("PREPARE ");
        x.getName().accept(this);
        print(" FROM ");
        x.getFrom().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlExecuteStatement x) {

    }

    @Override
    public boolean visit(MySqlExecuteStatement x) {
        print("EXECUTE ");
        x.getStatementName().accept(this);
        if (x.getParameters().size() > 0) {
            print(" USING ");
            printAndAccept(x.getParameters(), ", ");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlDeleteStatement x) {

    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        print("DELETE ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isQuick()) {
            print("QUICK ");
        }

        if (x.isIgnore()) {
            print("IGNORE ");
        }

        if (x.getFrom() == null) {
            print("FROM ");
            x.getTableSource().accept(this);
        } else {
            x.getTableSource().accept(this);
            println();
            print("FROM ");
            x.getFrom().accept(this);
        }

        if (x.getUsing() != null) {
            println();
            print("USING ");
            x.getUsing().accept(this);
        }

        if (x.getWhere() != null) {
            println();
            incrementIndent();
            print("WHERE ");
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
        print("INSERT ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isDelayed()) {
            print("DELAYED ");
        }

        if (x.isHighPriority()) {
            print("HIGH_PRIORITY ");
        }

        if (x.isIgnore()) {
            print("IGNORE ");
        }

        print("INTO ");

        x.getTableSource().accept(this);

        if (x.getColumns().size() > 0) {
            incrementIndent();
            print(" (");
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

        if (x.getValuesList().size() != 0) {
            println();
            printValuesList(x);
        }

        if (x.getQuery() != null) {
            println();
            x.getQuery().accept(this);
        }

        if (x.getDuplicateKeyUpdate().size() != 0) {
            println();
            print("ON DUPLICATE KEY UPDATE ");
            for (int i = 0, size = x.getDuplicateKeyUpdate().size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print(", ");
                }
                x.getDuplicateKeyUpdate().get(i).accept(this);
            }
        }

        return false;
    }

    protected void printValuesList(MySqlInsertStatement x) {
        print("VALUES ");
        if (x.getValuesList().size() > 1) {
            incrementIndent();
        }
        for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
            if (i != 0) {
                print(",");
                println();
            }
            x.getValuesList().get(i).accept(this);
        }
        if (x.getValuesList().size() > 1) {
            decrementIndent();
        }
    }

    @Override
    public void endVisit(MySqlLoadDataInFileStatement x) {

    }

    @Override
    public boolean visit(MySqlLoadDataInFileStatement x) {
        print("LOAD DATA ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isConcurrent()) {
            print("CONCURRENT ");
        }

        if (x.isLocal()) {
            print("LOCAL ");
        }

        print("INFILE ");

        x.getFileName().accept(this);

        if (x.isReplicate()) {
            print(" REPLACE ");
        }

        if (x.isIgnore()) {
            print(" IGNORE ");
        }

        print(" INTO TABLE ");
        x.getTableName().accept(this);

        if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
            print(" COLUMNS");
            if (x.getColumnsTerminatedBy() != null) {
                print(" TERMINATED BY ");
                x.getColumnsTerminatedBy().accept(this);
            }

            if (x.getColumnsEnclosedBy() != null) {
                if (x.isColumnsEnclosedOptionally()) {
                    print(" OPTIONALLY");
                }
                print(" ENCLOSED BY ");
                x.getColumnsEnclosedBy().accept(this);
            }

            if (x.getColumnsEscaped() != null) {
                print(" ESCAPED BY ");
                x.getColumnsEscaped().accept(this);
            }
        }

        if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
            print(" LINES");
            if (x.getLinesStartingBy() != null) {
                print(" STARTING BY ");
                x.getLinesStartingBy().accept(this);
            }

            if (x.getLinesTerminatedBy() != null) {
                print(" TERMINATED BY ");
                x.getLinesTerminatedBy().accept(this);
            }
        }
        
        if(x.getIgnoreLinesNumber() != null) {
            print(" IGNORE ");
            x.getIgnoreLinesNumber().accept(this);
            print(" LINES");
        }

        if (x.getColumns().size() != 0) {
            print(" (");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }
        
        if (x.getSetList().size() != 0) {
            print(" SET ");
            printAndAccept(x.getSetList(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlReplaceStatement x) {

    }

    @Override
    public boolean visit(MySqlReplaceStatement x) {
        print("REPLACE ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isDelayed()) {
            print("DELAYED ");
        }

        print("INTO ");

        x.getTableName().accept(this);

        if (x.getColumns().size() > 0) {
            print(" (");
            for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
                if (i != 0) {
                    print(", ");
                }
                x.getColumns().get(i).accept(this);
            }
            print(")");
        }

        if (x.getValuesList().size() != 0) {
            println();
            print("VALUES ");
            int size = x.getValuesList().size();
            if (size == 0) {
                print("()");
            } else {
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print(", ");
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
    public void endVisit(MySqlSelectGroupBy x) {

    }

    @Override
    public boolean visit(MySqlSelectGroupBy x) {
        super.visit(x);

        if (x.isRollUp()) {
            print(" WITH ROLLUP");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlStartTransactionStatement x) {

    }

    @Override
    public boolean visit(MySqlStartTransactionStatement x) {
        print("START TRANSACTION");
        if (x.isConsistentSnapshot()) {
            print(" WITH CONSISTENT SNAPSHOT");
        }
        
        if (x.getHints() != null && x.getHints().size() > 0) {
            print(" ");
            printAndAccept(x.getHints(), " ");
        }

        if (x.isBegin()) {
            print(" BEGIN");
        }

        if (x.isWork()) {
            print(" WORK");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlCommitStatement x) {

    }

    @Override
    public boolean visit(MySqlCommitStatement x) {
        print("COMMIT");

        if (x.isWork()) {
            print(" WORK");
        }

        if (x.getChain() != null) {
            if (x.getChain().booleanValue()) {
                print(" AND CHAIN");
            } else {
                print(" AND NO CHAIN");
            }
        }

        if (x.getRelease() != null) {
            if (x.getRelease().booleanValue()) {
                print(" AND RELEASE");
            } else {
                print(" AND NO RELEASE");
            }
        }

        return false;
    }

    @Override
    public void endVisit(MySqlRollbackStatement x) {

    }

    @Override
    public boolean visit(MySqlRollbackStatement x) {
        print("ROLLBACK");

        if (x.getChain() != null) {
            if (x.getChain().booleanValue()) {
                print(" AND CHAIN");
            } else {
                print(" AND NO CHAIN");
            }
        }

        if (x.getRelease() != null) {
            if (x.getRelease().booleanValue()) {
                print(" AND RELEASE");
            } else {
                print(" AND NO RELEASE");
            }
        }

        if (x.getTo() != null) {
            print(" TO ");
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
            print("SHOW FULL COLUMNS");
        } else {
            print("SHOW COLUMNS");
        }

        if (x.getTable() != null) {
            print(" FROM ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTablesStatement x) {
        if (x.isFull()) {
            print("SHOW FULL TABLES");
        } else {
            print("SHOW TABLES");
        }

        if (x.getDatabase() != null) {
            print(" FROM ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
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
        print("SHOW DATABASES");

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
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
            print("SHOW COUNT(*) WARNINGS");
        } else {
            print("SHOW WARNINGS");
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
        print("SHOW ");

        if (x.isGlobal()) {
            print("GLOBAL ");
        }

        if (x.isSession()) {
            print("SESSION ");
        }

        print("STATUS");

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
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
        print("LOAD XML ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isConcurrent()) {
            print("CONCURRENT ");
        }

        if (x.isLocal()) {
            print("LOCAL ");
        }

        print("INFILE ");

        x.getFileName().accept(this);

        if (x.isReplicate()) {
            print(" REPLACE ");
        }

        if (x.isIgnore()) {
            print(" IGNORE ");
        }

        print(" INTO TABLE ");
        x.getTableName().accept(this);

        if (x.getCharset() != null) {
            print(" CHARSET ");
            print(x.getCharset());
        }

        if (x.getRowsIdentifiedBy() != null) {
            print(" ROWS IDENTIFIED BY ");
            x.getRowsIdentifiedBy().accept(this);
        }

        if (x.getSetList().size() != 0) {
            print(" SET ");
            printAndAccept(x.getSetList(), ", ");
        }

        return false;
    }

    @Override
    public void endVisit(CobarShowStatus x) {

    }

    @Override
    public boolean visit(CobarShowStatus x) {
        print("SHOW COBAR_STATUS");
        return false;
    }

    @Override
    public void endVisit(MySqlKillStatement x) {

    }

    @Override
    public boolean visit(MySqlKillStatement x) {
        if (MySqlKillStatement.Type.CONNECTION.equals(x.getType())) {
            print("KILL CONNECTION ");
        } else if (MySqlKillStatement.Type.QUERY.equals(x.getType())) {
            print("KILL QUERY ");
        }
        x.getThreadId().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlBinlogStatement x) {

    }

    @Override
    public boolean visit(MySqlBinlogStatement x) {
        print("BINLOG ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlResetStatement x) {

    }

    @Override
    public boolean visit(MySqlResetStatement x) {
        print("RESET ");
        for (int i = 0; i < x.getOptions().size(); ++i) {
            if (i != 0) {
                print(", ");
            }
            print(x.getOptions().get(i));
        }
        return false;
    }

    @Override
    public void endVisit(MySqlCreateUserStatement x) {

    }

    @Override
    public boolean visit(MySqlCreateUserStatement x) {
        print("CREATE USER ");
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
            print(" IDENTIFIED BY ");
            if (x.isPasswordHash()) {
                print("PASSWORD ");
            }
            x.getPassword().accept(this);
        }

        if (x.getAuthPlugin() != null) {
            print(" IDENTIFIED WITH ");
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
            print("PARTITION BY LINEAR KEY (");
        } else {
            print("PARTITION BY KEY (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(")");

        if (x.getPartitionCount() != null) {
            print(" PARTITIONS ");
            x.getPartitionCount().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlPartitionByRange x) {

    }

    @Override
    public boolean visit(MySqlPartitionByRange x) {
        print("PARTITION BY RANGE");
        if (x.getExpr() != null) {
            print(" (");
            x.getExpr().accept(this);
            print(")");
        } else {
            print(" COLUMNS (");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }

        if (x.getPartitionCount() != null) {
            print(" PARTITIONS ");
            x.getPartitionCount().accept(this);
        }

        List<MySqlPartitioningDef> partitions = x.getPartitions();
        int partitionsSize = partitions.size();
        if (partitionsSize > 0) {
            print("(");
            incrementIndent();
            for (int i = 0; i < partitionsSize; ++i) {
                println();
                partitions.get(i).accept(this);
                if (i != partitionsSize - 1) {
                    print(", ");
                }
            }
            decrementIndent();
            println();
            print(")");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlPartitionByList x) {

    }

    @Override
    public boolean visit(MySqlPartitionByList x) {
        print("PARTITION BY LIST ");
        if (x.getExpr() != null) {
            print("(");
            x.getExpr().accept(this);
            print(") ");
        } else {
            print("COLUMNS (");
            printAndAccept(x.getColumns(), ", ");
            print(") ");
        }

        if (x.getPartitionCount() != null) {
            print(" PARTITIONS ");
            x.getPartitionCount().accept(this);
        }

        List<MySqlPartitioningDef> partitions = x.getPartitions();
        int partitionsSize = partitions.size();
        if (partitionsSize > 0) {
            print("(");
            incrementIndent();
            for (int i = 0; i < partitionsSize; ++i) {
                println();
                partitions.get(i).accept(this);
                if (i != partitionsSize - 1) {
                    print(", ");
                }
            }
            decrementIndent();
            println();
            print(")");
        }
        return false;
    }

    //

    @Override
    public void endVisit(MySqlPartitionByHash x) {

    }

    @Override
    public boolean visit(MySqlPartitionByHash x) {
        if (x.isLinear()) {
            print("PARTITION BY LINEAR HASH (");
        } else {
            print("PARTITION BY HASH (");
        }
        //
        x.getExpr().accept(this);
        print(")");

        if (x.getPartitionCount() != null) {
            print(" PARTITIONS ");
            x.getPartitionCount().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlSelectQueryBlock x) {

    }

    @Override
    public boolean visit(MySqlOutFileExpr x) {
        print("OUTFILE ");
        x.getFile().accept(this);

        if (x.getCharset() != null) {
            print(" CHARACTER SET ");
            print(x.getCharset());
        }

        if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
            print(" COLUMNS");
            if (x.getColumnsTerminatedBy() != null) {
                print(" TERMINATED BY ");
                x.getColumnsTerminatedBy().accept(this);
            }

            if (x.getColumnsEnclosedBy() != null) {
                if (x.isColumnsEnclosedOptionally()) {
                    print(" OPTIONALLY");
                }
                print(" ENCLOSED BY ");
                x.getColumnsEnclosedBy().accept(this);
            }

            if (x.getColumnsEscaped() != null) {
                print(" ESCAPED BY ");
                x.getColumnsEscaped().accept(this);
            }
        }

        if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
            print(" LINES");
            if (x.getLinesStartingBy() != null) {
                print(" STARTING BY ");
                x.getLinesStartingBy().accept(this);
            }

            if (x.getLinesTerminatedBy() != null) {
                print(" TERMINATED BY ");
                x.getLinesTerminatedBy().accept(this);
            }
        }

        return false;
    }

    @Override
    public void endVisit(MySqlOutFileExpr x) {

    }

    @Override
    public boolean visit(MySqlDescribeStatement x) {
        print("DESC ");
        x.getObject().accept(this);
        if (x.getColName() != null) {
            print(" ");
            x.getColName().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlDescribeStatement x) {

    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        print("UPDATE ");

        if (x.isLowPriority()) {
            print("LOW_PRIORITY ");
        }

        if (x.isIgnore()) {
            print("IGNORE ");
        }

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
            print("SET TRANSACTION ");
        } else if (x.getGlobal().booleanValue()) {
            print("SET GLOBAL TRANSACTION ");
        } else {
            print("SET SESSION TRANSACTION ");
        }

        if (x.getIsolationLevel() != null) {
            print("ISOLATION LEVEL ");
            print(x.getIsolationLevel());
        }

        if (x.getAccessModel() != null) {
            print("READ ");
            print(x.getAccessModel());
        }

        return false;
    }

    @Override
    public void endVisit(MySqlSetTransactionStatement x) {
        
    }

    @Override
    public boolean visit(MySqlSetNamesStatement x) {
        print("SET NAMES ");
        if (x.isDefault()) {
            print("DEFAULT");
        } else {
            print(x.getCharSet());
            if (x.getCollate() != null) {
                print(" COLLATE ");
                print(x.getCollate());
            }
        }
        return false;
    }

    @Override
    public void endVisit(MySqlSetNamesStatement x) {

    }

    @Override
    public boolean visit(MySqlSetCharSetStatement x) {
        print("SET CHARACTER SET ");
        if (x.isDefault()) {
            print("DEFAULT");
        } else {
            print(x.getCharSet());
            if (x.getCollate() != null) {
                print(" COLLATE ");
                print(x.getCollate());
            }
        }
        return false;
    }

    @Override
    public void endVisit(MySqlSetCharSetStatement x) {

    }

    @Override
    public void endVisit(MySqlShowAuthorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowAuthorsStatement x) {
        print("SHOW AUTHORS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowBinaryLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinaryLogsStatement x) {
        print("SHOW BINARY LOGS");
        return false;
    }

    @Override
    public boolean visit(MySqlShowMasterLogsStatement x) {
        print("SHOW MASTER LOGS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowMasterLogsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCollationStatement x) {
        print("SHOW COLLATION");
        if (x.getPattern() != null) {
            print(" LIKE ");
            x.getPattern().accept(this);
        }
        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowCollationStatement x) {

    }

    @Override
    public boolean visit(MySqlShowBinLogEventsStatement x) {
        print("SHOW BINLOG EVENTS");
        if (x.getIn() != null) {
            print(" IN ");
            x.getIn().accept(this);
        }
        if (x.getFrom() != null) {
            print(" FROM ");
            x.getFrom().accept(this);
        }
        if (x.getLimit() != null) {
            print(" ");
            x.getLimit().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowBinLogEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCharacterSetStatement x) {
        print("SHOW CHARACTER SET");
        if (x.getPattern() != null) {
            print(" LIKE ");
            x.getPattern().accept(this);
        }
        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowCharacterSetStatement x) {

    }

    @Override
    public boolean visit(MySqlShowContributorsStatement x) {
        print("SHOW CONTRIBUTORS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowContributorsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateDatabaseStatement x) {
        print("SHOW CREATE DATABASE ");
        x.getDatabase().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateEventStatement x) {
        print("SHOW CREATE EVENT ");
        x.getEventName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateEventStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateFunctionStatement x) {
        print("SHOW CREATE FUNCTION ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateProcedureStatement x) {
        print("SHOW CREATE PROCEDURE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTableStatement x) {
        print("SHOW CREATE TABLE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateTableStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateTriggerStatement x) {
        print("SHOW CREATE TRIGGER ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(MySqlShowCreateViewStatement x) {
        print("SHOW CREATE VIEW ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowCreateViewStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEngineStatement x) {
        print("SHOW ENGINE ");
        x.getName().accept(this);
        print(' ');
        print(x.getOption().name());
        return false;
    }

    @Override
    public void endVisit(MySqlShowEngineStatement x) {

    }

    @Override
    public boolean visit(MySqlShowEventsStatement x) {
        print("SHOW EVENTS");
        if (x.getSchema() != null) {
            print(" FROM ");
            x.getSchema().accept(this);
        }

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowEventsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionCodeStatement x) {
        print("SHOW FUNCTION CODE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowFunctionCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowFunctionStatusStatement x) {
        print("SHOW FUNCTION STATUS");
        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
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
            print("SHOW STORAGE ENGINES");
        } else {
            print("SHOW ENGINES");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowEnginesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowErrorsStatement x) {
        if (x.isCount()) {
            print("SHOW COUNT(*) ERRORS");
        } else {
            print("SHOW ERRORS");
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
        print("SHOW GRANTS");
        if (x.getUser() != null) {
            print(" FOR ");
            x.getUser().accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowGrantsStatement x) {

    }

    @Override
    public boolean visit(MySqlUserName x) {
        print(x.getUserName());
        if (x.getHost() != null) {
            print('@');
            print(x.getHost());
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUserName x) {

    }

    @Override
    public boolean visit(MySqlShowIndexesStatement x) {
        print("SHOW INDEX");

        if (x.getTable() != null) {
            print(" FROM ");
            if (x.getDatabase() != null) {
                x.getDatabase().accept(this);
                print('.');
            }
            x.getTable().accept(this);
        }
        
        if (x.getHints() != null && x.getHints().size() > 0) {
            print(" ");
            printAndAccept(x.getHints(), " ");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowIndexesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowKeysStatement x) {
        print("SHOW KEYS");

        if (x.getTable() != null) {
            print(" FROM ");
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
        print("SHOW MASTER STATUS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowMasterStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowOpenTablesStatement x) {
        print("SHOW OPEN TABLES");

        if (x.getDatabase() != null) {
            print(" FROM ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowOpenTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPluginsStatement x) {
        print("SHOW PLUGINS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowPluginsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowPrivilegesStatement x) {
        print("SHOW PRIVILEGES");
        return false;
    }

    @Override
    public void endVisit(MySqlShowPrivilegesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureCodeStatement x) {
        print("SHOW PROCEDURE CODE ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcedureCodeStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProcedureStatusStatement x) {
        print("SHOW PROCEDURE STATUS");
        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
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
            print("SHOW FULL PROCESSLIST");
        } else {
            print("SHOW PROCESSLIST");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlShowProcessListStatement x) {

    }

    @Override
    public boolean visit(MySqlShowProfileStatement x) {
        print("SHOW PROFILE");
        for (int i = 0; i < x.getTypes().size(); ++i) {
            if (i == 0) {
                print(' ');
            } else {
                print(", ");
            }
            print(x.getTypes().get(i).name);
        }

        if (x.getForQuery() != null) {
            print(" FOR QUERY ");
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
        print("SHOW PROFILES");
        return false;
    }

    @Override
    public void endVisit(MySqlShowProfilesStatement x) {

    }

    @Override
    public boolean visit(MySqlShowRelayLogEventsStatement x) {
        print("SHOW RELAYLOG EVENTS");

        if (x.getLogName() != null) {
            print(" IN ");
            x.getLogName().accept(this);
        }

        if (x.getFrom() != null) {
            print(" FROM ");
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
        print("SHOW SLAVE HOSTS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowSlaveHostsStatement x) {

    }

    @Override
    public boolean visit(MySqlShowSlaveStatusStatement x) {
        print("SHOW SLAVE STATUS");
        return false;
    }

    @Override
    public void endVisit(MySqlShowSlaveStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTableStatusStatement x) {
        print("SHOW TABLE STATUS");
        if (x.getDatabase() != null) {
            print(" FROM ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowTableStatusStatement x) {

    }

    @Override
    public boolean visit(MySqlShowTriggersStatement x) {
        print("SHOW TRIGGERS");

        if (x.getDatabase() != null) {
            print(" FROM ");
            x.getDatabase().accept(this);
        }

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
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
        print("SHOW ");

        if (x.isGlobal()) {
            print("GLOBAL ");
        }

        if (x.isSession()) {
            print("SESSION ");
        }

        print("VARIABLES");

        if (x.getLike() != null) {
            print(" LIKE ");
            x.getLike().accept(this);
        }

        if (x.getWhere() != null) {
            print(" WHERE ");
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlShowVariantsStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterTableStatement x) {
        if (x.isIgnore()) {
            print("ALTER IGNORE TABLE ");
        } else {
            print("ALTER TABLE ");
        }
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
    public void endVisit(MySqlAlterTableStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterTableAddColumn x) {
        print("ADD COLUMN ");
        
        if (x.getColumns().size() > 1) {
            print("(");
        }
        printAndAccept(x.getColumns(), ", ");
        if (x.getFirstColumn() != null) {
            print(" FIRST ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print(" AFTER ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print(" FIRST");
        }
        
        if (x.getColumns().size() > 1) {
            print(")");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(MySqlCreateIndexStatement x) {
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

        if (x.getUsing() != null) {
            print(" USING ");
            print(x.getUsing());
        }
        return false;
    }

    @Override
    public void endVisit(MySqlCreateIndexStatement x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement.Item x) {
        x.getName().accept(this);
        print(" TO ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement.Item x) {

    }

    @Override
    public boolean visit(MySqlRenameTableStatement x) {
        print("RENAME TABLE ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlRenameTableStatement x) {

    }

    @Override
    public boolean visit(MySqlUnionQuery x) {
        {
            boolean needParen = false;
            if (x.getLeft() instanceof MySqlSelectQueryBlock) {
                MySqlSelectQueryBlock right = (MySqlSelectQueryBlock) x.getLeft();
                if (right.getOrderBy() != null || right.getLimit() != null) {
                    needParen = true;
                }
            }
            if (needParen) {
                print('(');
                x.getLeft().accept(this);
                print(')');
            } else {
                x.getLeft().accept(this);
            }
        }
        println();
        print(x.getOperator().name);
        println();

        boolean needParen = false;

        if (x.getOrderBy() != null || x.getLimit() != null) {
            needParen = true;
        } else if (x.getRight() instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock right = (MySqlSelectQueryBlock) x.getRight();
            if (right.getOrderBy() != null || right.getLimit() != null) {
                needParen = true;
            }
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

        if (x.getLimit() != null) {
            println();
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlUnionQuery x) {

    }

    @Override
    public boolean visit(MySqlUseIndexHint x) {
        print("USE INDEX ");
        if (x.getOption() != null) {
            print("FOR ");
            print(x.getOption().name);
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
        print("IGNORE INDEX ");
        if (x.getOption() != null) {
            print("FOR ");
            print(x.getOption().name);
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
        x.getExpr().accept(this);

        if (x.getAlias() != null) {
            print(' ');
            print(x.getAlias());
        }

        for (int i = 0; i < x.getHintsSize(); ++i) {
            print(' ');
            x.getHints().get(i).accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlLockTableStatement x) {
        print("LOCK TABLES ");
        x.getTableSource().accept(this);
        if (x.getLockType() != null) {
            print(' ');
            print(x.getLockType().name);
        }
        
        if (x.getHints() != null && x.getHints().size() > 0) {
            print(" ");
            printAndAccept(x.getHints(), " ");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlLockTableStatement x) {

    }

    @Override
    public boolean visit(MySqlUnlockTablesStatement x) {
        print("UNLOCK TABLES");
        return false;
    }

    @Override
    public void endVisit(MySqlUnlockTablesStatement x) {

    }

    @Override
    public boolean visit(MySqlForceIndexHint x) {
        print("FORCE INDEX ");
        if (x.getOption() != null) {
            print("FOR ");
            print(x.getOption().name);
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
        print("CHANGE COLUMN ");
        x.getColumnName().accept(this);
        print(' ');
        x.getNewColumnDefinition().accept(this);
        if (x.getFirstColumn() != null) {
            print(" FIRST ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print(" AFTER ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print(" FIRST");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableChangeColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableModifyColumn x) {
        print("MODIFY COLUMN ");
        x.getNewColumnDefinition().accept(this);
        if (x.getFirstColumn() != null) {
            print(" FIRST ");
            x.getFirstColumn().accept(this);
        } else if (x.getAfterColumn() != null) {
            print(" AFTER ");
            x.getAfterColumn().accept(this);
        } else if (x.isFirst()) {
            print(" FIRST");
        }

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableModifyColumn x) {

    }

    @Override
    public boolean visit(MySqlAlterTableCharacter x) {
        print("CHARACTER SET = ");
        x.getCharacterSet().accept(this);

        if (x.getCollate() != null) {
            print(", COLLATE = ");
            x.getCollate().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableCharacter x) {

    }

    @Override
    public boolean visit(MySqlAlterTableOption x) {
        print(x.getName());
        print(" = ");
        print(x.getValue().toString());
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
        print("HELP ");
        x.getContent().accept(this);
        return false;
    }

    @Override
    public void endVisit(MySqlHelpStatement x) {

    }

    @Override
    public boolean visit(MySqlCharExpr x) {
        print(x.toString());
        return false;
    }

    @Override
    public void endVisit(MySqlCharExpr x) {

    }

    @Override
    public boolean visit(MySqlUnique x) {
        if (x.isHasConstaint()) {
            print("CONSTRAINT ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print("UNIQUE");

        if (x.getIndexName() != null) {
            print(' ');
            x.getIndexName().accept(this);
        }

        if (x.getIndexType() != null) {
            print(" USING ");
            print(x.getIndexType());
        }

        print(" (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        return false;
    }

    @Override
    public boolean visit(MysqlForeignKey x) {
        if (x.isHasConstraint()) {
            print("CONSTRAINT ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        print("FOREIGN KEY");

        if (x.getIndexName() != null) {
            print(' ');
            x.getIndexName().accept(this);
        }

        print(" (");
        printAndAccept(x.getReferencingColumns(), ", ");
        print(")");

        print(" REFERENCES ");
        x.getReferencedTableName().accept(this);

        print(" (");
        printAndAccept(x.getReferencedColumns(), ", ");
        print(")");
        
        if(x.getReferenceMatch() != null) {
            print(" MATCH ");
            print(x.getReferenceMatch().name());
        }
        
        if(x.getReferenceOn()!= null) {
            print(" ON ");
            print(x.getReferenceOn().name());
            print(" ");
            if(x.getReferenceOption() != null) {
                print(x.getReferenceOption().getText());
            }
        }
        return false;
    }

    @Override
    public void endVisit(MySqlUnique x) {

    }

    @Override
    public boolean visit(MySqlAlterTableDiscardTablespace x) {
        print("DISCARD TABLESPACE");
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableDiscardTablespace x) {

    }

    @Override
    public boolean visit(MySqlAlterTableImportTablespace x) {
        print("IMPORT TABLESPACE");
        return false;
    }

    @Override
    public void endVisit(MySqlAlterTableImportTablespace x) {

    }

    @Override
    public boolean visit(SQLAssignItem x) {
        x.getTarget().accept(this);
        if (!"NAMES".equalsIgnoreCase(x.getTarget().toString())) {
            print(" = ");
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

    @Override
    public boolean visit(MySqlPartitioningDef x) {
        print("PARTITION ");
        x.getName().accept(this);
        if (x.getValues() != null) {
            print(' ');
            x.getValues().accept(this);
        }

        if (x.getDataDirectory() != null) {
            incrementIndent();
            println();
            print("DATA DIRECTORY ");
            x.getDataDirectory().accept(this);
            decrementIndent();
        }
        if (x.getIndexDirectory() != null) {
            incrementIndent();
            println();
            print("INDEX DIRECTORY ");
            x.getIndexDirectory().accept(this);
            decrementIndent();
        }

        return false;
    }

    @Override
    public void endVisit(MySqlPartitioningDef x) {

    }

    @Override
    public boolean visit(LessThanValues x) {
        print("VALUES LESS THAN (");
        printAndAccept(x.getItems(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(LessThanValues x) {

    }

    @Override
    public boolean visit(InValues x) {
        print("VALUES IN (");
        printAndAccept(x.getItems(), ", ");
        print(')');
        return false;
    }

    @Override
    public void endVisit(InValues x) {

    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {
        {
            SQLOrderBy value = (SQLOrderBy) aggregateExpr.getAttribute("ORDER BY");
            if (value != null) {
                print(" ");
                ((SQLObject) value).accept(this);
            }
        }
        {
            Object value = aggregateExpr.getAttribute("SEPARATOR");
            if (value != null) {
                print(" SEPARATOR ");
                ((SQLObject) value).accept(this);
            }
        }
    }

    @Override
    public boolean visit(MySqlAnalyzeStatement x) {
        print("ANALYZE ");
        if (x.isNoWriteToBinlog()) {
            print("NO_WRITE_TO_BINLOG ");
        }

        if (x.isLocal()) {
            print("LOCAL ");
        }

        print("TABLE ");

        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlAnalyzeStatement x) {

    }

    @Override
    public boolean visit(MySqlOptimizeStatement x) {
        print("OPTIMIZE ");
        if (x.isNoWriteToBinlog()) {
            print("NO_WRITE_TO_BINLOG ");
        }

        if (x.isLocal()) {
            print("LOCAL ");
        }

        print("TABLE ");

        printAndAccept(x.getTableSources(), ", ");
        return false;
    }

    @Override
    public void endVisit(MySqlOptimizeStatement x) {

    }

    @Override
    public boolean visit(MySqlAlterUserStatement x) {
        print("ALTER USER");
        for (SQLExpr user : x.getUsers()) {
            print(' ');
            user.accept(this);
            print(" PASSWORD EXPIRE");
        }
        return false;
    }

    @Override
    public void endVisit(MySqlAlterUserStatement x) {

    }

    @Override
    public boolean visit(MySqlSetPasswordStatement x) {
        print("SET PASSWORD ");

        if (x.getUser() != null) {
            print("FOR ");
            x.getUser().accept(this);
            print(' ');
        }

        print("= ");

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
    public boolean visit(MySqlSelectGroupByExpr x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(" ");
            print(x.getType().name().toUpperCase());
        }

        return false;
    }

    @Override
    public void endVisit(MySqlSelectGroupByExpr x) {

    }

    @Override
    public boolean visit(MySqlBlockStatement x) {
        print("BEGIN");
        incrementIndent();
        println();
        for (int i = 0, size = x.getStatementList().size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = x.getStatementList().get(i);
            stmt.setParent(x);
            stmt.accept(this);
            print(";");
        }
        decrementIndent();
        println();
        print("END");
        return false;
    }

    @Override
    public void endVisit(MySqlBlockStatement x) {
        
    }

} //
