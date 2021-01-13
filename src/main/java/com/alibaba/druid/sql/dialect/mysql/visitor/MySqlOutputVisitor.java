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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.*;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.ConditionValue.ConditionType;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.FnvHash;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySqlOutputVisitor extends SQLASTOutputVisitor implements MySqlASTVisitor {

    {
        this.dbType = DbType.mysql;
        this.shardingSupport = true;
        this.quote = '`';
    }

    public MySqlOutputVisitor(Appendable appender) {
        super(appender);
    }

    public MySqlOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);

        try {
            configFromProperty();
        } catch (AccessControlException e) {
            // skip
        }
    }

    private static boolean shardingSupportChecked = false;

    public void configFromProperty() {
        if (this.parameterized && !shardingSupportChecked) {
            shardingSupportChecked = true;

            String property = System.getProperties().getProperty("fastsql.parameterized.shardingSupport");
            if ("true".equals(property)) {
                this.setShardingSupport(true);
            } else if ("false".equals(property)) {
                this.setShardingSupport(false);
            }
        }
    }

    public boolean isShardingSupport() {
        return this.parameterized
                && shardingSupport;
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

        String cachedSelectList = x.getCachedSelectList();

        if (cachedSelectList != null) {
            if (!isEnabled(VisitorFeature.OutputSkipSelectListCacheString)) {
                print0(cachedSelectList);
            }
        } else {
            print0(ucase ? "SELECT " : "select ");

            for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
                SQLCommentHint hint = x.getHints().get(i);
                hint.accept(this);
                print(' ');
            }

            final int distionOption = x.getDistionOption();
            if (SQLSetQuantifier.ALL == distionOption) {
                print0(ucase ? "ALL " : "all ");
            } else if (SQLSetQuantifier.DISTINCT == distionOption) {
                print0(ucase ? "DISTINCT " : "distinct ");
            } else if (SQLSetQuantifier.DISTINCTROW == distionOption) {
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

            SQLName forcePartition = x.getForcePartition();
            if (forcePartition != null) {
                println();
                print0(ucase ? "FORCE PARTITION " : "force partition ");
                printExpr(forcePartition, parameterized);
            }

            SQLExprTableSource into = x.getInto();
            if (into != null) {
                println();
                print0(ucase ? "INTO " : "into ");
                printTableSource(into);
            }
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");

            printTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);
        }

        printHierarchical(x);

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            println();
            visit(groupBy);
        }

        final List<SQLWindow> windows = x.getWindows();
        if (windows != null && windows.size() > 0) {
            println();
            print0(ucase ? "WINDOW " : "window ");
            printAndAccept(windows, ", ");
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            visit(orderBy);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            visit(limit);
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

            if (x.isSkipLocked()) {
                print0(ucase ? " SKIP LOCKED" : " skip locked");
            }
        }

        if (x.isForShare()) {
            println();
            print0(ucase ? "FOR SHARE" : "for share");
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
        boolean parameterized = this.parameterized;
        this.parameterized = false;

        x.getName().accept(this);

        SQLDataType dataType = x.getDataType();
        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        SQLExpr generatedAlawsAs = x.getGeneratedAlawsAs();
        if (generatedAlawsAs != null) {
            print0(ucase ? " GENERATED ALWAYS AS (" : " generated always as (");
            printExpr(generatedAlawsAs);
            print(')');
        }

        if (x.isVirtual()) {
            print0(ucase ? " VIRTUAL" : " virtual");
        }

        if (x.isVisible()) {
            print0(ucase ? " VISIBLE" : " visible");
        }

        final SQLExpr charsetExpr = x.getCharsetExpr();
        if (charsetExpr != null) {
            print0(ucase ? " CHARACTER SET " : " character set ");
            charsetExpr.accept(this);
        }

        final SQLExpr collateExpr = x.getCollateExpr();
        if (collateExpr != null) {
            print0(ucase ? " COLLATE " : " collate ");
            collateExpr.accept(this);
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            if(item instanceof SQLColumnReference) {
                continue;
            }
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

        final SQLExpr format = x.getFormat();
        if (format != null) {
            printUcase(" COLUMN_FORMAT ");
            format.accept(this);
        }

        SQLExpr onUpdate = x.getOnUpdate();
        if (onUpdate != null) {
            print0(ucase ? " ON UPDATE " : " on update ");

            onUpdate.accept(this);
        }

        if (x.getJsonIndexAttrsExpr() != null) {
            print0(ucase ? " JSONINDEXATTRS '" : " jsonindexattrs '");
            x.getJsonIndexAttrsExpr().accept(this);
            print0("' ");
        }

        if (x.isAutoIncrement()) {
            print0(ucase ? " AUTO_INCREMENT" : " auto_increment");
        }

        if (x.getDelimiterTokenizer() != null) {
            print0(ucase ? " DELIMITER_TOKENIZER " : " delimiter_tokenizer ");
            x.getDelimiterTokenizer().accept(this);
        }
        if (x.getNlpTokenizer() != null) {
            print0(ucase ? " NLP_TOKENIZER " : " nlp_tokenizer ");
            x.getNlpTokenizer().accept(this);
        }
        if (x.getValueType() != null) {
            print0(ucase ? " VALUE_TYPE " : " value_type ");
            x.getValueType().accept(this);
        }


        final AutoIncrementType sequenceType = x.getSequenceType();
        if (sequenceType != null) {
            print0(ucase ? " BY " : " by ");
            print0(ucase ? sequenceType.getKeyword() : sequenceType.getKeyword().toLowerCase());
        }

        final SQLExpr unitCount = x.getUnitCount();
        if (unitCount != null) {
            print0(ucase ? " UNIT COUNT " : " unit count ");
            printExpr(unitCount);
        }

        final SQLExpr unitIndex = x.getUnitIndex();
        if (unitIndex != null) {
            print0(ucase ? " INDEX " : " index ");
            printExpr(unitIndex);
        }

        if (x.getStep() != null) {
            print0(ucase ? " STEP " : " STEP ");
            printExpr(x.getStep());
        }

        SQLExpr delimiter = x.getDelimiter();
        if (delimiter != null) {
            print0(ucase ? " DELIMITER " : " delimiter ");
            delimiter.accept(this);
        }

        if (x.isDisableIndex() == true) {
            print0(ucase ? " DISABLEINDEX TRUE" : " disableindex true");
        }

        final SQLAnnIndex annIndex = x.getAnnIndex();
        if (annIndex != null) {
            print(' ');
            annIndex.accept(this);
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

        if (x.isStored()) {
            print0(ucase ? " STORED" : " stored");
        }

        if (x.getEncode() != null) {
            print0(ucase ? " ENCODE=" : " encode=");
            x.getEncode().accept(this);
        }

        if (x.getCompression() != null) {
            print0(ucase ? " COMPRESSION=" : " compression=");
            x.getCompression().accept(this);
        }

        List<SQLAssignItem> colProperties = x.getColPropertiesDirect();
        if (colProperties != null && colProperties.size() > 0) {
            print0(ucase ? " COLPROPERTIES (" : " colproperties (");
            printAndAccept(colProperties, ", ");
            print0(ucase ? ")" : ")");
        }

        for (SQLColumnConstraint item : x.getConstraints()) {
            if (item instanceof SQLColumnReference) {
                print(' ');
                item.accept(this);
            }
        }

        this.parameterized = parameterized;
        return false;
    }

    public boolean visit(SQLDataType x) {
        printDataType(x);

        if (x instanceof SQLDataTypeImpl) {
            SQLDataTypeImpl dataTypeImpl = (SQLDataTypeImpl) x;
            if (dataTypeImpl.isUnsigned()) {
                print0(ucase ? " UNSIGNED" : " unsigned");
            }

            if (dataTypeImpl.isZerofill()) {
                print0(ucase ? " ZEROFILL" : " zerofill");
            }

            SQLExpr indexBy = ((SQLDataTypeImpl) x).getIndexBy();
            if (indexBy != null) {
                print0(ucase ? " INDEX BY " : " index by ");
                indexBy.accept(this);
            }
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
    public boolean visit(MySqlTableIndex x) {
        String indexType = x.getIndexType();

        boolean indexTypePrinted = false;
        if ("FULLTEXT".equalsIgnoreCase(indexType)) {
            print0(ucase ? "FULLTEXT " : "fulltext ");
            indexTypePrinted = true;
        } else if ("SPATIAL".equalsIgnoreCase(indexType)) {
            print0(ucase ? "SPATIAL " : "spatial ");
            indexTypePrinted = true;
        } else if ("CLUSTERED".equalsIgnoreCase(indexType)) {
            print0(ucase ? "CLUSTERED " : "clustered ");
            indexTypePrinted = true;
        } else if ("CLUSTERING".equalsIgnoreCase(indexType)) {
            print0(ucase ? "CLUSTERING " : "clustering ");
            indexTypePrinted = true;
        }

        if (x.getIndexDefinition().isGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        } else if (x.getIndexDefinition().isLocal()) {
            print0(ucase ? "LOCAL " : "local ");
        }

        print0(ucase ? "INDEX" : "index");
        if (x.getName() != null) {
            print(' ');
            x.getName().accept(this);
        }

        if (indexType != null && !indexTypePrinted) {
            if ("ANN".equals(indexType)) {
                print0(" ");
                print0(indexType);
            }
        }

        if (Boolean.TRUE.equals(x.getAttribute("ads.index"))) {
            if (x.getIndexDefinition().isHashMapType()) {
                print0(ucase ? " HASHMAP" : " hashmap");
            } else if (x.getIndexDefinition().isHashType()) {
                print0(ucase ? " HASH" : " hash");
            }
        }

        String using = x.getIndexDefinition().hasOptions() ? x.getIndexDefinition().getOptions().getIndexType() : null;
        if (using != null) {
            print0(ucase ? " USING " : " using ");
            print0(using);
        }

        print('(');
        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        if (x.getAnalyzerName() != null) {
            print0(ucase ? " WITH ANALYZER " : " with analyzer ");
            x.getAnalyzerName().accept(this);
        } else {
            if (x.getIndexAnalyzerName() != null) {
                print0(ucase ? " WITH INDEX ANALYZER " : " with index analyzer ");
                x.getIndexAnalyzerName().accept(this);
            }

            if (x.getQueryAnalyzerName() != null) {
                print0(ucase ? " WITH QUERY ANALYZER " : " with query analyzer ");
                x.getQueryAnalyzerName().accept(this);
            }

            if (x.getWithDicName() != null) {
                printUcase(" WITH DICT ");
                x.getWithDicName().accept(this);
            }
        }

        final List<SQLName> covering = x.getCovering();
        if (null != covering && covering.size() > 0) {
            print0(ucase ? " COVERING " : " covering ");
            print('(');
            for (int i = 0, size = covering.size(); i < size; ++i) {
                if (i != 0) {
                    print0(", ");
                }
                covering.get(i).accept(this);
            }
            print(')');
        }

        final SQLExpr dbPartitionBy = x.getDbPartitionBy();
        if (dbPartitionBy != null) {
            print0(ucase ? " DBPARTITION BY " : " dbpartition by ");
            dbPartitionBy.accept(this);
        }

        final SQLExpr tablePartitionBy = x.getTablePartitionBy();
        if (tablePartitionBy != null) {
            print0(ucase ? " TBPARTITION BY " : " tbpartition by ");
            tablePartitionBy.accept(this);
        }

        final SQLExpr tablePartitions = x.getTablePartitions();
        if (tablePartitions != null) {
            print0(ucase ? " TBPARTITIONS " : " tbpartitions ");
            tablePartitions.accept(this);
        }

        /*
        final List<SQLAssignItem> options = x.getOptions();
        if (options.size() > 0) {
            for (SQLAssignItem option : options) {
                print(' ');
                option.accept(this);
            }
        }

        final SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            comment.accept(this);
        }
        */
        if (x.getIndexDefinition().hasOptions()) {
            x.getIndexDefinition().getOptions().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlKey x) {
        if (x.isHasConstraint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        final String indexType = x.getIndexType();
        boolean fullText = "FULLTEXT".equalsIgnoreCase(indexType);
        boolean clustering = "CLUSTERING".equalsIgnoreCase(indexType);
        boolean clustered = "CLUSTERED".equalsIgnoreCase(indexType);

        if (fullText) {
            print0(ucase ? "FULLTEXT " : "fulltext ");
        } else if (clustering) {
            print0(ucase ? "CLUSTERING " : "clustering ");
        } else if (clustered) {
            print0(ucase ? "CLUSTERED " : "CLUSTERED ");
        }

        print0(ucase ? "KEY" : "key");

        SQLName name = x.getName();
        if (name != null) {
            print(' ');
            name.accept(this);
        }

        if (indexType != null && !fullText && !clustering && !clustered) {
            print0(ucase ? " USING " : " using ");
            print0(indexType);
        }

        print0(" (");

        for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            x.getColumns().get(i).accept(this);
        }
        print(')');

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment);
        }

        return false;
    }

    public boolean visit(SQLCharExpr x, boolean parameterized) {
        if (this.appender == null) {
            return false;
        }

        try {
            if (parameterized) {
                this.appender.append('?');
                incrementReplaceCunt();
                if (this.parameters != null) {
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
                boolean regForPresto = false;
                if (isEnabled(VisitorFeature.OutputRegForPresto)
                        && x.getParent() instanceof SQLMethodInvokeExpr) {
                    SQLMethodInvokeExpr regCall = (SQLMethodInvokeExpr) x.getParent();
                    long nameHash = regCall.methodNameHashCode64();
                    regForPresto = (x == regCall.getArguments().get(1))
                            && (nameHash == FnvHash.Constants.REGEXP_SUBSTR
                                    || nameHash == FnvHash.Constants.REGEXP_COUNT
                                    || nameHash == FnvHash.Constants.REGEXP_EXTRACT
                                    || nameHash == FnvHash.Constants.REGEXP_EXTRACT_ALL
                                    || nameHash == FnvHash.Constants.REGEXP_LIKE
                                    || nameHash == FnvHash.Constants.REGEXP_REPLACE
                                    || nameHash == FnvHash.Constants.REGEXP_SPLIT)
                    ;
                }

                for (int i = 0; i < text.length(); ++i) {
                    char ch = text.charAt(i);
                    if (ch == '\'') {
                        appender.append('\'');
                        appender.append('\'');
                    } else if (ch == '\\') {
                        appender.append('\\');
                        if (regForPresto) {
                            continue;
                        }
                        if (i < text.length() - 1 && text.charAt(i + 1) == '_') {
                            continue;
                        }
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

        if (x.isGlobal()) {
            print0("@@global.");
        }else if(x.isSession()){
            print0("@@session.");
        }

        String varName = x.getName();
        for (int i = 0; i < varName.length(); ++i) {
            char ch = varName.charAt(i);
            if (ch == '\'') {
                if (varName.startsWith("@@") && i == 2) {
                    print(ch);
                } else if (varName.startsWith("@") && i == 1) {
                    print(ch);
                } else if (i != 0 && i != varName.length() - 1) {
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

    @Override
    public boolean visit(MySqlPrepareStatement x) {
        print0(ucase ? "PREPARE " : "prepare ");
        x.getName().accept(this);
        print0(ucase ? " FROM " : " from ");
        x.getFrom().accept(this);
        return false;
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
    public boolean visit(MySqlExecuteForAdsStatement x) {
        print0(ucase ? "EXECUTE " : "execute ");
        x.getAction().accept(this);
        print(" ");
        x.getRole().accept(this);
        print(" ");
        x.getTargetId().accept(this);
        print(" ");
        if (x.getStatus() != null) {
            x.getStatus().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlManageInstanceGroupStatement x) {
        x.getOperation().accept(this);

        print0(ucase ? " INSTANCE_GROUP " : " instance_group ");
        printAndAccept(x.getGroupNames(), ",");

        if (x.getReplication() != null) {
            print0(ucase ? " REPLICATION = " : " replication = ");
            x.getReplication().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlRaftMemberChangeStatement x) {

        print0(ucase ? "SYNC RAFT_MEMBER_CHANGE " : "sync raft_member_change ");

        if (x.isNoLeader()) {
            print0(ucase ? "NOLEADER " : "noleader ");
        }

        print0(ucase ? "SHARD=" : "shard=");
        x.getShard().accept(this);

        print0(ucase ? " HOST=" : " host=");
        x.getHost().accept(this);

        print0(ucase ? " STATUS=" : " status=");
        x.getStatus().accept(this);

        if (x.isForce()) {
            print0(ucase ? " FORCE" : " force");
        }

        return false;
    }

    @Override
    public boolean visit(MySqlRaftLeaderTransferStatement x) {
        print0(ucase ? "SYNC RAFT_LEADER_TRANSFER SHARD=" : "sync raft_leader_transfer shard=");
        x.getShard().accept(this);

        print0(ucase ? " FROM=" : " from=");
        x.getFrom().accept(this);

        print0(ucase ? " TO=" : " to=");
        x.getTo().accept(this);

        print0(ucase ? " TIMEOUT=" : " timeout=");
        x.getTimeout().accept(this);

        return false;
    }

    @Override
    public boolean visit(MySqlMigrateStatement x) {

        return false;
    }

    @Override
    public void endVisit(MySqlMigrateStatement x) {
        print0(ucase ? "MIGRATE DATABASE " : "migrate database ");
        x.getSchema().accept(this);

        print0(ucase ? " SHARDS=" : "shards= ");
        x.getShardNames().accept(this);
        print0(" ");
        if (x.getMigrateType().getNumber().intValue() == 0) {
            print0(ucase ? "GROUP " : "group ");
        } else if (x.getMigrateType().getNumber().intValue() == 1) {
            print0(ucase ? "HOST " : "host ");
        }

        print0(ucase ? "FROM " : "from ");
        x.getFromInsId().accept(this);

        if (x.getFromInsIp() != null) {
            print(":");
            x.getFromInsIp().accept(this);
            print(":");
            x.getFromInsPort().accept(this);
            print(":");
            x.getFromInsStatus().accept(this);
        }

        print0(ucase ? " TO " : " to ");
        x.getToInsId().accept(this);

        if (x.getToInsIp() != null) {
            print(":");
            x.getToInsIp().accept(this);
            print(":");
            x.getToInsPort().accept(this);
            print(":");
            x.getToInsStatus().accept(this);
        }

    }

    @Override
    public boolean visit(MySqlShowClusterNameStatement x) {
        print0(ucase ? "SHOW CLUSTER NAME" : "show cluster name");
        return false;
    }

    @Override
    public boolean visit(MySqlShowJobStatusStatement x) {
        print0(ucase ? "SHOW " : "show ");
        if (x.isSync()) {
            print0(ucase ? "SYNC_JOB " : "sync_job ");
        } else {
            print0(ucase ? "JOB " : "job ");
        }
        print0(ucase ? "STATUS " : "status ");

        if (x.getWhere() != null) {
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowMigrateTaskStatusStatement x) {
        print0(ucase ? "SHOW MIGRATE TASK STATUS" : "show migrate task status");
        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }
        return false;
    }

    public boolean visit(MysqlDeallocatePrepareStatement x) {
        print0(ucase ? "DEALLOCATE PREPARE " : "deallocate prepare ");
        x.getStatementName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

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

        if (x.isForceAllPartitions()) {
            print0(ucase ? "FORCE ALL PARTITIONS " : "force all partitions ");
        } else {
            SQLName partition = x.getForcePartition();
            if (partition != null) {
                print0(ucase ? "FORCE PARTITION " : "force partition ");
                printExpr(partition, parameterized);
                print(' ');
            }
        }

        SQLTableSource from = x.getFrom();
        if (from == null) {
            print0(ucase ? "FROM " : "from ");
            if(x.isFulltextDictionary()) {
                print0(ucase ? "FULLTEXT DICTIONARY " : "fulltext dictionary ");
            }
            x.getTableSource().accept(this);
        } else {
            x.getTableSource().accept(this);
            println();
            print0(ucase ? "FROM " : "from ");
            from.accept(this);
        }

        SQLTableSource using = x.getUsing();
        if (using != null) {
            println();
            print0(ucase ? "USING " : "using ");
            using.accept(this);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            this.indentCount++;
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);
            this.indentCount--;
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
    public boolean visit(MySqlInsertStatement x) {
        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            visit(with);
            println();
        }

        print0(ucase ? "INSERT " : "insert ");

        for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
            SQLCommentHint hint = x.getHints().get(i);
            hint.accept(this);
            print(' ');
        }

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

        boolean outputIntoKeyword = true;
        if (x.isOverwrite()) {
            print0(ucase ? "OVERWRITE " : "overwrite ");
        }

        if (outputIntoKeyword) {
            print0(ucase ? "INTO " : "into ");
        }

        if (x.isFulltextDictionary()) {
            print0(ucase ? "FULLTEXT DICTIONARY " : "fulltext dictionary ");
        }

        SQLExprTableSource tableSource = x.getTableSource();
        if (tableSource != null) {
            if (tableSource.getClass() == SQLExprTableSource.class) {
                visit(tableSource);
            } else {
                tableSource.accept(this);
            }
        }

        List<SQLAssignItem> partitions = x.getPartitions();
        if (partitions != null) {
            int partitionsSize = partitions.size();
            if (partitionsSize > 0) {
                print0(ucase ? " PARTITION (" : " partition (");
                for (int i = 0; i < partitionsSize; ++i) {
                    if (i != 0) {
                        print0(", ");
                    }

                    SQLAssignItem assign = partitions.get(i);
                    assign.getTarget().accept(this);

                    if (assign.getValue() != null) {
                        print('=');
                        assign.getValue().accept(this);
                    }
                }
                print(')');
            }

            if (x.isIfNotExists()) {
                print0(ucase ? " IF NOT EXISTS " : "if not exists ");
            }
        }


        String columnsString = x.getColumnsString();
        if (columnsString != null) {
            if (!isEnabled(VisitorFeature.OutputSkipInsertColumnsString)) {
                print0(columnsString);
            }
        } else {
            List<SQLExpr> columns = x.getColumns();
            if (columns.size() > 0) {
                this.indentCount++;
                print0(" (");
                for (int i = 0, size = columns.size(); i < size; ++i) {
                    if (i != 0) {
                        if (i % 5 == 0) {
                            println();
                        }
                        print0(", ");
                    }

                    SQLExpr column = columns.get(i);
                    if (column instanceof SQLIdentifierExpr) {
                        printName0(((SQLIdentifierExpr) column).getName());
                    } else {
                        printExpr(column, parameterized);
                    }
                }
                print(')');
                this.indentCount--;
            }
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

        List<SQLExpr> duplicateKeyUpdate = x.getDuplicateKeyUpdate();
        if (duplicateKeyUpdate.size() != 0) {
            println();
            print0(ucase ? "ON DUPLICATE KEY UPDATE " : "on duplicate key update ");
            for (int i = 0, size = duplicateKeyUpdate.size(); i < size; ++i) {
                if (i != 0) {
                    if (i % 5 == 0) {
                        println();
                    }
                    print0(", ");
                }
                duplicateKeyUpdate.get(i).accept(this);
            }
        }

        return false;
    }

    protected void printValuesList(List<SQLInsertStatement.ValuesClause> valuesList) {

        if (this.parameterized
                && valuesList.size() > 1
                && !this.parameterizedQuesUnMergeValuesList)
        {
            print0(ucase ? "VALUES " : "values ");
            this.indentCount++;

            boolean allConst = true;
            if (valuesList.size() > 1) {
                for (int index = 0; index < valuesList.size(); index++) {
                    List<SQLExpr> values = valuesList.get(index).getValues();
                    for (int i = 0; i < values.size(); i++) {
                        SQLExpr value = values.get(i);
                        if (value instanceof SQLLiteralExpr || value instanceof SQLVariantRefExpr) {
                            continue;
                        } else if (value instanceof SQLMethodInvokeExpr && ((SQLMethodInvokeExpr) value).getArguments().size() == 0) {
                            continue;
                        }
                        allConst = false;
                        break;
                    }
                    if (!allConst) {
                        break;
                    }
                }
            }

            if (!allConst) {
                for (int index = 0; index < valuesList.size(); index++) {
                    if (index != 0) {
                        print(',');
                        println();
                    }
                    visit(valuesList.get(index), this.parameters);
                }
            } else if (valuesList.size() > 1 && this.parameters != null) {
                SQLInsertStatement.ValuesClause first = valuesList.get(0);

                List<Object> valuesParameters = new ArrayList<Object>(first.getValues().size());
                visit(first, valuesParameters);
                this.parameters.add(valuesParameters);

                for (int index = 1; index < valuesList.size(); index++) {
                    List<SQLExpr> values = valuesList.get(index).getValues();
                    valuesParameters = new ArrayList<Object>(values.size());

                    for (int i = 0, size = values.size(); i < size; ++i) {
                        SQLExpr expr = values.get(i);
                        if (expr instanceof SQLIntegerExpr
                                || expr instanceof SQLBooleanExpr
                                || expr instanceof SQLNumberExpr
                                || expr instanceof SQLCharExpr
                                || expr instanceof SQLNCharExpr
                                || expr instanceof SQLTimestampExpr
                                || expr instanceof SQLDateExpr
                                || expr instanceof SQLTimeExpr) {
                            incrementReplaceCunt();
                            ExportParameterVisitorUtils.exportParameter(valuesParameters, expr);
                        } else if (expr instanceof SQLNullExpr) {
                            incrementReplaceCunt();
                            valuesParameters.add(null);
                        } else {
                            // skip
                        }
                    }

                    this.parameters.add(valuesParameters);
                }

                this.incrementReplaceCunt();
            } else {
                if (valuesList.size() > 1) {
                    this.incrementReplaceCunt();
                }
                visit(valuesList.get(0), this.parameters);
            }

            this.indentCount--;
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
            visit(item, parameters);
        }
        if (valuesList.size() > 1) {
            this.indentCount--;
        }
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

    public boolean visit(SQLReplaceStatement x) {

        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        print0(ucase ? "REPLACE " : "replace ");



        if (x.isLowPriority()) {
            print0(ucase ? "LOW_PRIORITY " : "low_priority ");
        }

        if (x.isDelayed()) {
            print0(ucase ? "DELAYED " : "delayed ");
        }

        if (x.getHints() != null && x.getHints().size() > 0) {
            printAndAccept(x.getHints(), " ");
            print0(" ");
        }

        print0(ucase ? "INTO " : "into ");

        printTableSourceExpr(x.getTableName());


        List<SQLExpr> columns = x.getColumns();
        if (columns.size() > 0) {
            print0(" (");
            for (int i = 0, size = columns.size(); i < size; ++i) {
                if (i != 0) {
                    print0(", ");
                }

                SQLExpr columnn = columns.get(i);
                printExpr(columnn, parameterized);
            }
            print(')');
        }

        List<SQLAssignItem> partitions = x.getPartitions();
        if (partitions != null) {
            int partitionsSize = partitions.size();
            if (partitionsSize > 0) {
                print0(ucase ? " PARTITION (" : " partition (");
                for (int i = 0; i < partitionsSize; ++i) {
                    if (i != 0) {
                        print0(", ");
                    }

                    SQLAssignItem assign = partitions.get(i);
                    assign.getTarget().accept(this);

                    if (assign.getValue() != null) {
                        print('=');
                        assign.getValue().accept(this);
                    }
                }
                print(')');
            }
        }

        List<SQLInsertStatement.ValuesClause> valuesClauseList = x.getValuesList();
        if (valuesClauseList.size() != 0) {
            println();
            print0(ucase ? "VALUES " : "values ");
            int size = valuesClauseList.size();
            if (size == 0) {
                print0("()");
            } else {
                for (int i = 0; i < size; ++i) {
                    if (i != 0) {
                        print0(", ");
                    }
                    visit(valuesClauseList.get(i));
                }
            }
        }

        SQLQueryExpr query = x.getQuery();
        if (query != null) {
            visit(query);
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

        SQLStartTransactionStatement.IsolationLevel isolationLevel = x.getIsolationLevel();
        if (isolationLevel != null) {
            print0(" ISOLATION LEVEL ");
            print(isolationLevel.getText());
        }

        if (x.isReadOnly()) {
            print0(ucase ?" READ ONLY" : " read only");
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
    public boolean visit(SQLShowTablesStatement x) {
        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        if (x.isFull()) {
            print0(ucase ? "SHOW FULL TABLES" : "show full tables");
        } else {
            print0(ucase ? "SHOW TABLES" : "show tables");
        }

        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
        }

        final SQLExpr like = x.getLike();
        if (like != null) {
            print0(ucase ? " LIKE " : " like ");
            printExpr(like);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowHMSMetaStatement x) {
        print0(ucase ? "SHOW HMSMETA " : "show hmsmeta ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowDatabaseStatusStatement x) {
        if (x.isFull()) {
            print0(ucase ? "SHOW FULL DATABASE STATUS" : "show full database status");
        } else {
            print0(ucase ? "SHOW DATABASE STATUS" : "show database status");
        }

        if (x.getName() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getName().accept(this);
        }


        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }


        return false;
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
            x.getWhere().accept(this);
        }

        return false;
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
    public boolean visit(CobarShowStatus x) {
        print0(ucase ? "SHOW COBAR_STATUS" : "show cobar_status");
        return false;
    }

    @Override
    public boolean visit(DrdsShowDDLJobs x) {
        print0(ucase ? "SHOW " : "show ");
        if (x.isFull()) {
            print0(ucase ? "FULL " : "full ");
        }
        print0(ucase ? "DDL" : "ddl");
        boolean first = true;
        for (Long id : x.getJobIds()) {
            if (first) {
                first = false;
                print0(" ");
            } else {
                print0(", ");
            }
            print(id);
        }
        return false;
    }

    @Override
    public boolean visit(DrdsCancelDDLJob x) {
        print0(ucase ? "CANCEL DDL" : "cancel ddl");
        boolean first = true;
        for (Long id : x.getJobIds()) {
            if (first) {
                first = false;
                print0(" ");
            } else {
                print0(", ");
            }
            print(id);
        }
        return false;
    }

    @Override
    public boolean visit(DrdsRecoverDDLJob x) {
        print0(ucase ? "RECOVER DDL" : "recover ddl");
        if (x.isAllJobs()) {
            print0(ucase ? " ALL" : " all");
        } else {
            boolean first = true;
            for (Long id : x.getJobIds()) {
                if (first) {
                    first = false;
                    print0(" ");
                } else {
                    print0(", ");
                }
                print(id);
            }
        }
        return false;
    }

    @Override
    public boolean visit(DrdsRollbackDDLJob x) {
        print0(ucase ? "ROLLBACK DDL" : "rollback ddl");
        boolean first = true;
        for (Long id : x.getJobIds()) {
            if (first) {
                first = false;
                print0(" ");
            } else {
                print0(", ");
            }
            print(id);
        }
        return false;
    }

    @Override
    public boolean visit(DrdsRemoveDDLJob x) {
        print0(ucase ? "REMOVE DDL" : "remove ddl");
        if (x.isAllCompleted()) {
            print0(ucase ? " ALL COMPLETED" : " all completed");
        } else if (x.isAllPending()) {
            print(ucase ? " ALL PENDING" : " all pending");
        } else {
            boolean first = true;
            for (Long id : x.getJobIds()) {
                if (first) {
                    first = false;
                    print0(" ");
                } else {
                    print0(", ");
                }
                print(id);
            }
        }
        return false;
    }

    @Override
    public boolean visit(DrdsInspectDDLJobCache x) {
        print0(ucase ? "INSPECT DDL CACHE" : "inspect ddl cache");
        return false;
    }

    @Override
    public boolean visit(DrdsClearDDLJobCache x) {
        print0(ucase ? "CLEAR DDL CACHE" : "clear ddl cache");
        if (x.isAllJobs()) {
            print0(ucase ? " ALL" : " all");
        } else {
            boolean first = true;
            for (Long id : x.getJobIds()) {
                if (first) {
                    first = false;
                    print0(" ");
                } else {
                    print0(", ");
                }
                print(id);
            }
        }
        return false;
    }

    @Override
    public boolean visit(DrdsChangeDDLJob x) {
        print0(ucase ? "CHANGE DDL " : "change ddl ");
        print(x.getJobId());
        if (x.isSkip()) {
            print0(ucase ? " SKIP" : " skip");
        } else if (x.isAdd()) {
            print0(ucase ? " ADD" : " add");
        }

        boolean first = true;
        for (String name : x.getGroupAndTableNameList()) {
            if (first) {
                first = false;
                print0(" ");
            } else {
                print0(", ");
            }
            print(name);
        }
        return false;
    }

    @Override
    public boolean visit(DrdsBaselineStatement x) {
        print0(ucase ? "BASELINE " : "baseline ");
        print0(ucase ? x.getOperation().toUpperCase() : x.getOperation().toLowerCase());

        boolean isFirst = true;
        for (Long id : x.getBaselineIds()) {
            if (isFirst) {
                print0(" ");
                isFirst = false;
            } else {
                print0(", ");
            }
            print(id);
        }

        SQLSelect select = x.getSelect();
        if (x.getSelect() != null) {
            print(ucase ? " SQL" : " sql");
            println();
            List<SQLCommentHint> headHints = x.getHeadHintsDirect();
            if (headHints != null) {
                for (SQLCommentHint hint : headHints) {
                    visit((SQLCommentHint) hint);
                    println();
                }
            }

            this.visit(select);
        }
        return false;
    }

    @Override
    public boolean visit(DrdsShowGlobalIndex x) {
        print0(ucase ? "SHOW GLOBAL INDEX" : "show global index");
        if (x.getTableName() != null) {
            print0(ucase ? " FROM " : " from ");
            printExpr(x.getTableName(), parameterized);
        }
        return false;
    }

    @Override
    public boolean visit(DrdsShowMetadataLock x) {
        print0(ucase ? "SHOW METADATA LOCK" : "show metadata lock");
        if (x.getSchemaName() != null) {
            print0(" ");
            printExpr(x.getSchemaName(), parameterized);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlBinlogStatement x) {
        print0(ucase ? "BINLOG " : "binlog ");
        x.getExpr().accept(this);
        return false;
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
    public boolean visit(MySqlCreateUserStatement x) {
        print0(ucase ? "CREATE USER " : "create user ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printAndAccept(x.getUsers(), ", ");
        return false;
    }

    @Override
    public boolean visit(UserSpecification x) {
        x.getUser().accept(this);

        if (x.getAuthPlugin() != null) {
            print0(ucase ? " IDENTIFIED WITH " : " identified with ");
            x.getAuthPlugin().accept(this);
            if (x.getPassword() != null) {
                if (x.isPluginAs()) {
                    print0(ucase ? " AS " : " as ");
                } else {
                    print0(ucase ? " BY " : " by ");
                }
                x.getPassword().accept(this);
            }
        } else if (x.getPassword() != null) {
            print0(ucase ? " IDENTIFIED BY " : " identified by ");
            if (x.isPasswordHash()) {
                print0(ucase ? "PASSWORD " : "password ");
            }
            x.getPassword().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlPartitionByKey x) {
        if (x.isLinear()) {
            print0(ucase ? "LINEAR KEY (" : "linear key (");
        } else {
            print0(ucase ? "KEY (" : "key (");
        }
        printAndAccept(x.getColumns(), ", ");
        print(')');

        printPartitionsCountAndSubPartitions(x);
        return false;
    }

    @Override public boolean visit(MySqlShowPlanCacheStatusStatement x) {
        print0(ucase ? "SHOW PLANCACHE STATUS" : "show plancache status");
        return false;
    }

    @Override public boolean visit(MySqlClearPlanCacheStatement x) {
        print0(ucase ? "CLEAR PLANCACHE" : "clear plancache");
        return false;
    }

    @Override public boolean visit(MySqlDisabledPlanCacheStatement x) {
        print0(ucase ? "DISABLED PLANCACHE" : "disabled plancache");
        return false;
    }

    @Override public boolean visit(MySqlExplainPlanCacheStatement x) {
        print0(ucase ? "EXPLAIN PLANCACHE" : "explain plancache");
        return false;
    }

    @Override public boolean visit(MySqlUpdatePlanCacheStatement x) {
        print0(ucase ? "UPDATE PLANCACHE " : "update plancache ");
        x.getFormSelect().accept(this);
        println();
        print0(ucase ? " TO " : " to ");
        println();
        x.getToSelect().accept(this);
        return false;
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
    public boolean visit(MySqlExplainStatement x) {
        List<SQLCommentHint> hints = x.getHeadHintsDirect();
        if (null != hints) {
            for (int i = 0; i < hints.size(); i++) {
                if (i != 0) {
                    print(' ');
                }
                hints.get(i).accept(this);
            }
            println();
        }

        String name = x.isDescribe() ? "desc" : "explain";
        print0(ucase ? name.toUpperCase() : name);

        hints = x.getHints();
        if (hints != null) {
            print(' ');
            for (int i = 0; i < hints.size(); i++) {
                if (i != 0) {
                    print(' ');
                }
                hints.get(i).accept(this);
            }
        }

        String type = x.getType();
        // tbl_name [col_name | wild]
        if (x.getTableName() != null) {
            print(' ');
            x.getTableName().accept(this);
            if (x.getColumnName() != null) {
                print(' ');
                x.getColumnName().accept(this);
            } else if (x.getWild() != null) {
                print(' ');
                x.getWild().accept(this);
            }
        } else {
            if (x.isExtended()) {
                print0(ucase ? " EXTENDED" : " extended");
            }

            if (x.isOptimizer()) {
                print0(ucase ? " OPTIMIZER" : " optimizer");
            }

            if (x.isDependency()) {
                print0(ucase ? " DEPENDENCY" : " dependency");
            }

            if (x.isAuthorization()) {
                print0(ucase ? " AUTHORIZATION" : " authorization");
            }

            // [explain_type]
            String format = x.getFormat();
            if (type != null || format != null) {
                final boolean parenthesis = x.isParenthesis();
                if (parenthesis) {
                    print0(" (");
                } else {
                    print(' ');
                }

                if (type != null) {
                    if (parenthesis) {
                        print0(ucase ? "TYPE " : "type ");
                    }
                    print0(type);
                }

                if (format != null) {
                    if (type != null) {
                        if (parenthesis) {
                            print0(", ");
                        } else {
                            print(' ');
                        }
                    }

                    print0(ucase ? "FORMAT " : "format ");
                    if (!parenthesis) {
                        print0("= ");
                    }
                    print0(format);
                }

                if (parenthesis) {
                    print(')');
                }
            }

            // {explainable_stmt | FOR CONNECTION connection_id}
            if (x.getConnectionId() != null) {
                print0(ucase ? " FOR CONNECTION " : " for connection ");
                x.getConnectionId().accept(this);
            } else {
                print(' ');
                x.getStatement().accept(this);
            }

            if (x.isDistributeInfo()) {
                print0(ucase ? " DISTRIBUTE INFO" : " distribute info");
            }
        }

        return false;
    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        List<SQLExpr> returning = x.getReturning();
        if (returning != null && returning.size() > 0) {
            print0(ucase ? "SELECT " : "select ");
            printAndAccept(returning, ", ");
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

        if (x.getHints() != null && x.getHints().size() > 0) {
            printAndAccept(x.getHints(), " ");
            print0(" ");
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

        SQLExpr targetAffectRow = x.getTargetAffectRow();
        if (targetAffectRow != null) {
            print0(ucase ? "TARGET_AFFECT_ROW " : "target_affect_row ");
            printExpr(targetAffectRow, parameterized);
            print(' ');
        }

        if (x.isForceAllPartitions()) {
            print0(ucase ? "FORCE ALL PARTITIONS " : "force all partitions ");
        } else {
            SQLName partition = x.getForcePartition();
            if (partition != null) {
                print0(ucase ? "FORCE PARTITION " : "force partition ");
                printExpr(partition, parameterized);
                print(' ');
            }
        }

        printTableSource(x.getTableSource());

        println();
        print0(ucase ? "SET " : "set ");
        List<SQLUpdateSetItem> items = x.getItems();
        for (int i = 0, size = items.size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLUpdateSetItem item = items.get(i);
            visit(item);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            indentCount++;
            print0(ucase ? "WHERE " : "where ");
            printExpr(where, parameterized);
            indentCount--;
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            visit(orderBy);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            visit(limit);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlSetTransactionStatement x) {
        print0(ucase ? "SET " : "set ");
        if (x.getGlobal() != null && x.getGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        } else if(x.getSession() != null && x.getSession()) {
            print0(ucase ? "SESSION " : "session ");
        }

        print0(ucase ? "TRANSACTION " : "transaction ");

        if (x.getIsolationLevel() != null) {
            print0(ucase ? "ISOLATION LEVEL " : "isolation level ");
            print0(x.getIsolationLevel());
        }

        final String accessModel = x.getAccessModel();
        if (accessModel != null) {
            print0(ucase ? "READ " : "read ");
            print0(accessModel);
        }

        final SQLExpr policy = x.getPolicy();
        if (policy != null) {
            print0(ucase ? "POLICY " : "policy ");
            policy.accept(this);
        }

        return false;
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
    public boolean visit(MySqlShowAuthorsStatement x) {
        print0(ucase ? "SHOW AUTHORS" : "show authors");
        return false;
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
    public boolean visit(MySqlShowContributorsStatement x) {
        print0(ucase ? "SHOW CONTRIBUTORS" : "show contributors");
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateDatabaseStatement x) {
        print0(ucase ? "SHOW CREATE DATABASE " : "show create database ");
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        x.getDatabase().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateEventStatement x) {
        print0(ucase ? "SHOW CREATE EVENT " : "show create event ");
        x.getEventName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateFunctionStatement x) {
        print0(ucase ? "SHOW CREATE FUNCTION " : "show create function ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateProcedureStatement x) {
        print0(ucase ? "SHOW CREATE PROCEDURE " : "show create procedure ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowCreateTriggerStatement x) {
        print0(ucase ? "SHOW CREATE TRIGGER " : "show create trigger ");
        x.getName().accept(this);
        return false;
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
    public boolean visit(MySqlShowFunctionCodeStatement x) {
        print0(ucase ? "SHOW FUNCTION CODE " : "show function code ");
        x.getName().accept(this);
        return false;
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
    public boolean visit(MySqlShowEnginesStatement x) {
        if (x.isStorage()) {
            print0(ucase ? "SHOW STORAGE ENGINES" : "show storage engines");
        } else {
            print0(ucase ? "SHOW ENGINES" : "show engines");
        }
        return false;
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
    public boolean visit(MySqlShowGrantsStatement x) {
        print0(ucase ? "SHOW GRANTS" : "show grants");
        SQLExpr user = x.getUser();
        if (user != null) {
            print0(ucase ? " FOR " : " for ");
            user.accept(this);
        }

        SQLExpr on = x.getOn();
        if (on != null) {
            print0(ucase ? " ON " : " on ");
            on.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlUserName x) {
        String userName = x.getUserName();

        if (userName.length() > 0 && userName.charAt(0) == '\'') {
            print0(userName);
        } else {
            print('\'');
            print0(userName);
            print('\'');
        }

        String host = x.getHost();
        if (host != null) {
            print('@');

            if (host.length() > 0 && host.charAt(0) == '\'') {
                print0(host);
            } else {
                print('\'');
                print0(host);
                print('\'');
            }
        }

        String identifiedBy = x.getIdentifiedBy();
        if (identifiedBy != null) {
            print0(ucase ? " IDENTIFIED BY '" : " identified by '");
            print0(identifiedBy);
            print('\'');
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowMasterStatusStatement x) {
        print0(ucase ? "SHOW MASTER STATUS" : "show master status");
        return false;
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
    public boolean visit(MySqlShowPluginsStatement x) {
        print0(ucase ? "SHOW PLUGINS" : "show plugins");
        return false;
    }

    @Override
    public boolean visit(MySqlShowPartitionsStatement x) {
        print0(ucase ? "SHOW DBPARTITIONS " : "show dbpartitions ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLShowPartitionsStmt x) {
        print0(ucase ? "SHOW PARTITIONS FROM " : "show partitions from ");
        x.getTableSource().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowPrivilegesStatement x) {
        print0(ucase ? "SHOW PRIVILEGES" : "show privileges");
        return false;
    }

    @Override
    public boolean visit(MySqlShowProcedureCodeStatement x) {
        print0(ucase ? "SHOW PROCEDURE CODE " : "show procedure code ");
        x.getName().accept(this);
        return false;
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
    public boolean visit(MySqlShowProcessListStatement x) {
        return visit((SQLShowProcessListStatement) x);
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
    public boolean visit(MySqlShowProfilesStatement x) {
        print0(ucase ? "SHOW PROFILES" : "show profiles");
        return false;
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
    public boolean visit(MySqlShowSlaveHostsStatement x) {
        print0(ucase ? "SHOW SLAVE HOSTS" : "show slave hosts");
        return false;
    }

    @Override
    public boolean visit(MySqlShowSlowStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isFull()) {
            print0(ucase ? "FULL " : "full ");
        }
        if (x.isPhysical()) {
            print0(ucase ? "PHYSICAL_SLOW" : "PHYSICAL_SLOW");
        } else {
            print0(ucase ? "SLOW" : "slow");
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowSequencesStatement x) {
        print0(ucase ? "SHOW SEQUENCES" : "show sequences");


        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowSlaveStatusStatement x) {
        print0(ucase ? "SHOW SLAVE STATUS" : "show slave status");
        return false;
    }

    @Override
    public boolean visit(MySqlShowTableStatusStatement x) {
        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        print0(ucase ? "SHOW TABLE STATUS" : "show table status");
        if (x.getDatabase() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getDatabase().accept(this);
            if (x.getTableGroup() != null) {
                print0(".");
                x.getTableGroup().accept(this);
            }
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
    public boolean visit(MysqlShowDbLockStatement x) {
        print0(ucase ? "SHOW DBLOCK" : "show dblock");
        return false;
    }

    @Override
    public boolean visit(MysqlShowHtcStatement x) {
        print0(ucase ? "SHOW HTC" : "show htc");
        return false;
    }

    @Override
    public boolean visit(MysqlShowStcStatement x) {
        if (x.isHis()) {
            print0(ucase ? "SHOW STC HIS" : "show stc his");
        } else {
            print0(ucase ? "SHOW STC" : "show stc");
        }
        return false;
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
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowRuleStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isFull()) {
            print0(ucase ? "FULL RULE" : "full rule");
        } else {
            print0(ucase ? "RULE" : "rule");
        }

        if (x.isVersion()) {
            print0(ucase ? " VERSION" : " version");
        }

        if (x.getName() != null) {
            print0(ucase ? " FROM " : " from ");
            x.getName().accept(this);
        }

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowRuleStatusStatement x) {
        print0(ucase ? "SHOW RULE" : "show rule");

        if (x.isFull()) {
            print0(ucase ? " FULL" : " full");
        } else if (x.isVersion()) {
            print0(ucase ? " VERSION" : " version");
        }

        print0(ucase ? " STATUS" : " status");

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowPhysicalProcesslistStatement x) {
        print0(ucase ? "SHOW" : "show");

        if (x.isFull()) {
            print0(ucase ? " FULL" : " full");
        }

        print0(ucase ? " PHYSICAL_PROCESSLIST" : " physical_processlist");

        return false;
    }

    @Override
    public boolean visit(MySqlRenameSequenceStatement x) {
        print0(ucase ? "RENAME SEQUENCE " : "rename sequence ");
        x.getName().accept(this);
        print0(ucase ? " TO " : " to ");
        x.getTo().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlCheckTableStatement x) {
        print0(ucase ? "CHECK TABLE " : "check table ");
        printAndAccept(x.getTables(), "，");
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextCharFilterStatement x) {
        print0(ucase? "CREATE FULLTEXT CHARFILTER " : "create fulltext charfilter ");
        x.getName().accept(this);
        println("(");
        print0("\"type\" = " + x.getTypeName());
        println(",");
        printAndAccept(x.getOptions(), ",");

        println();
        print0(")");

        return false;
    }

    @Override public boolean visit(MysqlShowFullTextStatement x) {
        print0(ucase ? "SHOW FULLTEXT " : "show fulltext ");
        if (x.getType() == FullTextType.DICTIONARY) {
            print0(ucase ? "DICTIONARIES" : "dictionaries");
        } else {
            print0(ucase ? x.getType().toString().toUpperCase() + "S" : x.getType().toString().toLowerCase() + "s");
        }
        return false;
    }

    @Override public boolean visit(MysqlShowCreateFullTextStatement x) {
        print0(ucase ? "SHOW CREATE FULLTEXT " : "show create fulltext ");
        print0(ucase ? x.getType().toString().toUpperCase() : x.getType().toString().toLowerCase());
        print0(" ");
        x.getName().accept(this);
        return false;
    }

    @Override public boolean visit(MysqlAlterFullTextStatement x) {
        print0(ucase ? "ALTER FULLTEXT " : "alter fulltext ");
        print0(ucase ? x.getType().toString().toUpperCase() : x.getType().toString().toLowerCase());
        print0( " ");
        x.getName().accept(this);
        print0(ucase ? " SET " : " set ");
        x.getItem().accept(this);
        return false;
    }

    @Override public boolean visit(SQLAlterTableDropClusteringKey x) {
        print0(ucase ? "DROP CLUSTERED KEY " : "drop clustered key ");
        x.getKeyName().accept(this);

        return false;
    }

    @Override public boolean visit(MysqlDropFullTextStatement x) {
        print0(ucase ? "DROP FULLTEXT " : "drop fulltext ");
        print0(ucase ? x.getType().toString().toUpperCase() : x.getType().toString().toLowerCase());
        print0(" ");
        x.getName().accept(this);
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextTokenizerStatement x) {

        print0(ucase ? "CREATE FULLTEXT TOKENIZER " : "create fulltext tokenizer ");
        x.getName().accept(this);
        println("(");
        print0("\"type\" = " + x.getTypeName());
        if (x.getUserDefinedDict() != null) {
            println(",");
            print("\"user_defined_dict\" = " + x.getUserDefinedDict());
        }
        if(!x.getOptions().isEmpty()) {
            println(",");
            printAndAccept(x.getOptions(), ",");
        }
        println();
        print0(")");

        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextTokenFilterStatement x) {
        print0(ucase ? "CREATE FULLTEXT TOKENFILTER " : "create fulltext tokenfilter ");
        x.getName().accept(this);
        println("(");
        println("\"type\" = " + x.getTypeName() + ",");
        printAndAccept(x.getOptions(), ",");

        println();
        print0(")");
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextAnalyzerStatement x) {
        print0(ucase ? "CREATE FULLTEXT ANALYZER " : "create fulltext analyzer ");
        x.getName().accept(this);
        println("(");
        print0(ucase ? "\"TOKENIZER\" = " : "\"tokenizer\" = " );
        print0(x.getTokenizer());
        println(",");

        if (!x.getCharfilters().isEmpty()) {
            print0(ucase ? "\"CHARFILTER\" = [" : "\"charfilter\" = [");
            for (int i = 0; i < x.getCharfilters().size(); i++) {
                if (i != 0) {
                    print0(", ");
                }

                print0("\"" + x.getCharfilters().get(i) + "\"");
            }
            println("],");
        }
        if (!x.getTokenizers().isEmpty()) {
            print0(ucase ? "\"TOKENFILTER\" = [" : "\"tokenfilter\" = [");
            for (int i = 0; i < x.getTokenizers().size(); i++) {
                if (i != 0) {
                    print0(", ");
                }

                print0("\"" + x.getTokenizers().get(i) + "\"");
            }
            print0("]");
        }
        println();
        print0(")");
        return false;
    }

    @Override public boolean visit(MysqlCreateFullTextDictionaryStatement x) {
        print0(ucase ? "CREATE FULLTEXT DICTIONARY " : "create fulltext dictionary ");
        x.getName().accept(this);
        println("(");
        x.getColumn().accept(this);
        println();
        print0(") ");

        if (x.getComment() != null) {
            print0(ucase ? "COMMENT " : "comment ");
            print0(x.getComment());
        }
        return false;
    }

    @Override public boolean visit(MySqlAlterTableAlterFullTextIndex x) {
        print0(ucase ? " ALTER INDEX " : " alter index ");
        x.getIndexName().accept(this);
        print0(ucase ? " FULLTEXT " : " fulltext ");
        if(x.getAnalyzerType() != null) {
            String analyzerType = x.getAnalyzerType().toString();
            print0(ucase ? analyzerType.toUpperCase() : analyzerType.toLowerCase());
        }
        print0(ucase ? " ANALYZER = " : " analyzer = ");
        x.getAnalyzerName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlExtPartition x) {
        print0(ucase ? "EXTPARTITION (" : "extpartition (");
        incrementIndent();
        println();
        for (int i = 0; i < x.getItems().size(); i++) {
            if (i != 0) {
                println(", ");
            }
            MySqlExtPartition.Item item = x.getItems().get(i);
            item.accept(this);
        }
        decrementIndent();
        println();
        print(')');
        return false;
    }

    public boolean visit(MySqlExtPartition.Item x) {
        SQLName dbPartition = x.getDbPartition();
        if (dbPartition != null) {
            print0(ucase ? "DBPARTITION " : "dbpartition ");
            dbPartition.accept(this);

            SQLExpr dbPartitionBy = x.getDbPartitionBy();
            if (dbPartitionBy != null) {
                print0(ucase ? " BY " : " by ");
            }
            dbPartitionBy.accept(this);
        }

        SQLName tbPartition = x.getTbPartition();
        if (tbPartition != null) {
            if (dbPartition != null) {
                print(' ');
            }

            print0(ucase ? "TBPARTITION " : "tbpartition ");
            tbPartition.accept(this);

            SQLExpr tbPartitionBy = x.getTbPartitionBy();
            if (tbPartitionBy != null) {
                print0(ucase ? " BY " : " by ");
            }
            tbPartitionBy.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlShowTraceStatement x) {
        print0(ucase ? "SHOW TRACE" : "show trace");


        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowBroadcastsStatement x) {
        print0(ucase ? "SHOW BROADCASTS" : "show broadcasts");

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowDatasourcesStatement x) {
        print0(ucase ? "SHOW DATASOURCES" : "show datasources");


        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowNodeStatement x) {
        print0(ucase ? "SHOW NODE" : "show node");


        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowHelpStatement x) {
        print0(ucase ? "SHOW HELP" : "show help");


        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlFlashbackStatement x) {
        print0(ucase ? "FLASHBACK TABLE " : "flashback table ");
        x.getName().accept(this);
        print0(ucase ? " TO BEFORE DROP" : " to before drop");

        final SQLName renameTo = x.getRenameTo();
        if (renameTo != null) {
            print0(ucase ? " RENAME TO " : " rename to ");
            renameTo.accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlShowConfigStatement x) {
        print0(ucase ? "SHOW CONFIG " : "show config ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowPlanCacheStatement x) {
        print0(ucase ? "SHOW PLANCACHE PLAN" : "show plancache plan");
        println();
        x.getSelect().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlShowDdlStatusStatement x) {
        print0(ucase ? "SHOW DDL STATUS" : "show ddl status");

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }


        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowDsStatement x) {
        print0(ucase ? "SHOW DS" : "show ds");

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlShowTopologyStatement x) {
        print0(ucase ? "SHOW " : "show ");

        if (x.isFull()) {
            print0(ucase ? "FULL " : "full ");
        }

        print0(ucase ? "TOPOLOGY FROM " : "topology from ");

        print0(x.getName().getSimpleName());

        if (x.getWhere() != null) {
            print0(ucase ? " WHERE " : " where ");
            x.getWhere().accept(this);
        }

        if (x.getOrderBy() != null) {
            print0(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getLimit() != null) {
            print0(" ");
            x.getLimit().accept(this);
        }

        return false;
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
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        final List<SQLCommentHint> headHints = x.getHeadHintsDirect();
        if (headHints != null) {
            for (SQLCommentHint hint : headHints) {
                hint.accept(this);
                println();
            }
        }

        print0(ucase ? "ALTER " : "alter ");
        if (x.isOnline()) {
            print0(ucase ? "ONLINE  " : "online ");
        } else if (x.isOffline()) {
            print0(ucase ? "OFFLINE  " : "offline ");
        }
        if (x.isIgnore()) {
            print0(ucase ? "IGNORE " : "ignore ");
        }
        print0(ucase ? "TABLE " : "table ");

        printTableSourceExpr(x.getName());
        this.indentCount++;
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
            if (x.getItems().size() > 0) {
                print(',');
            }
            println();
        }

        this.indentCount--;

        int i = 0;
        for (SQLAssignItem item : x.getTableOptions()) {
            SQLExpr key = item.getTarget();
            if (i != 0) {
                print(' ');
            }
            print0(ucase ? key.toString().toUpperCase() : key.toString().toLowerCase());

            if ("TABLESPACE".equals(key)) {
                print(' ');
                item.getValue().accept(this);
                continue;
            } else if ("UNION".equals(key)) {
                print0(" = (");
                item.getValue().accept(this);
                print(')');
                continue;
            }

            print0(" = ");

            item.getValue().accept(this);
            i++;
        }

        SQLPartitionBy partitionBy = x.getPartition();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
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
    public boolean visit(MySqlRenameTableStatement x) {
        print0(ucase ? "RENAME TABLE " : "rename table ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

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

    public boolean visit(SQLExprTableSource x) {
        printTableSourceExpr(x.getExpr());

        final SQLTableSampling sampling = x.getSampling();
        if (sampling != null) {
            print(' ');
            sampling.accept(this);
        }

        String alias = x.getAlias();
        List<SQLName> columns = x.getColumnsDirect();
        if (alias != null) {
            print(' ');
            if (columns != null && columns.size() > 0) {
                print0(ucase ? " AS " : " as ");
            }
            print0(alias);
        }

        if (columns != null && columns.size() > 0) {
            print(" (");
            printAndAccept(columns, ", ");
            print(')');
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
        print0(ucase ? "LOCK TABLES" : "lock tables");
        List<MySqlLockTableStatement.Item> items = x.getItems();
        if(items.size() > 0) {
            print(' ');
            printAndAccept(items, ", ");
        }
        return false;
    }

    @Override
    public boolean visit(MySqlLockTableStatement.Item x) {
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
    public boolean visit(MySqlUnlockTablesStatement x) {
        print0(ucase ? "UNLOCK TABLES" : "unlock tables");
        return false;
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
    public boolean visit(MySqlAlterTableOption x) {
        print0(x.getName());
        print0(" = ");
        print0(x.getValue().toString());
        return false;
    }

    @Override
    public boolean visit(MySqlAlterDatabaseSetOption x) {
        print0(ucase ? "SET " : "set ");
        printAndAccept(x.getOptions(), ", ");

        SQLName on = x.getOn();
        if (on != null) {
            print0(ucase ? " ON " : " on ");
            on.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlAlterDatabaseKillJob x) {
        print0(ucase ? "KILL " : "kill ");
        x.getJobType().accept(this);
        print0(" ");
        x.getJobId().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlHelpStatement x) {
        print0(ucase ? "HELP " : "help ");
        x.getContent().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlCharExpr x) {
        if (this.parameterized) {
            print('?');
            incrementReplaceCunt();
            if (this.parameters != null) {
                ExportParameterVisitorUtils.exportParameter(this.parameters, x);
            }
            return false;
        }

        String charset = x.getCharset();
        String collate = x.getCollate();
        String text = x.getText();

        if (charset != null) {
            print(charset);

            long charsetHashCode = FnvHash.hashCode64(charset);
            if (charsetHashCode == FnvHash.Constants._UCS2 || charsetHashCode == FnvHash.Constants._UTF16) {
                print(" x'");
            } else {
                print(" '");
            }
            print(text);
            print('\'');
        } else {
            print('\'');
            print(text);
            print('\'');
        }

        if (collate != null) {
            print(" COLLATE ");
            print(collate);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlUnique x) {
        visit(x.getIndexDefinition());
        /*
        if (x.isHasConstraint()) {
            print0(ucase ? "CONSTRAINT " : "constraint ");
            if (x.getName() != null) {
                x.getName().accept(this);
                print(' ');
            }
        }

        if (x.isGlobal()) {
            print0(ucase ? "GLOBAL " : "global ");
        }

        print0(ucase ? "UNIQUE" : "unique");

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
        printAndAccept(x.getColumns(), ", ");
        print(')');

        List<SQLName> covering = x.getCovering();
        if (covering.size() > 0) {
            print0(ucase ? " COVERING (" : " covering (");
            printAndAccept(covering, ", ");
            print(')');
        }

        final SQLPartitionBy dbPartitionBy = x.getDbPartitionBy();
        if (dbPartitionBy != null) {
            print0(ucase ? " DBPARTITION BY " : " dbpartition by ");
            dbPartitionBy.accept(this);
        }

        final SQLExpr tablePartitionBy = x.getTablePartitionBy();
        if (tablePartitionBy != null) {
            print0(ucase ? " TBPARTITION BY " : " tbpartition by ");
            tablePartitionBy.accept(this);
        }

        final SQLExpr tablePartitions = x.getTablePartitions();
        if (tablePartitions != null) {
            print0(ucase ? " TBPARTITIONS " : " tbpartitions ");
            tablePartitions.accept(this);
        }

        SQLExpr keyBlockSize = x.getKeyBlockSize();
        if (keyBlockSize != null) {
            print0(ucase ? " KEY_BLOCK_SIZE = " : " key_block_size = ");
            keyBlockSize.accept(this);
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            comment.accept(this);
        }
        */

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

        SQLForeignKeyImpl.Match match = x.getReferenceMatch();
        if (match != null) {
            print0(ucase ? " MATCH " : " match ");
            print0(ucase ? match.name : match.name_lcase);
        }

        if (x.getOnDelete() != null) {
            print0(ucase ? " ON DELETE " : " on delete ");
            print0(ucase ? x.getOnDelete().name : x.getOnDelete().name_lcase);
        }

        if (x.getOnUpdate() != null) {
            print0(ucase ? " ON UPDATE " : " on update ");
            print0(ucase ? x.getOnUpdate().name : x.getOnUpdate().name_lcase);
        }
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableDiscardTablespace x) {
        print0(ucase ? "DISCARD TABLESPACE" : "discard tablespace");
        return false;
    }

    @Override
    public boolean visit(MySqlCreateExternalCatalogStatement x) {
        print0(ucase ? "CREATE EXTERNAL CATALOG " : "create external catalog ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        x.getName().accept(this);

        print0(ucase ? " PROPERTIES (" : " properties (");

        for (Map.Entry<SQLName, SQLName> entry : x.getProperties().entrySet()) {
            println();
            entry.getKey().accept(this);
            print0("=");
            entry.getValue().accept(this);
        }
        print0(")");

        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableImportTablespace x) {
        print0(ucase ? "IMPORT TABLESPACE" : "import tablespace");
        return false;
    }

    @Override
    public boolean visit(SQLAssignItem x) {


        String tagetString = x.getTarget().toString();

        boolean mysqlSpecial = false;

        if (DbType.mysql == dbType) {
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
            print(ucase ? " STORAGE " : " storage ");
            x.getStorage().accept(this);
        }
        return false;
    }

    protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {
        {
            SQLOrderBy value = aggregateExpr.getOrderBy();
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


        if (!x.getAdbColumns().isEmpty()) {
            print0(ucase ? "COLUMN " : "column ");
        } else if (!x.getAdbColumnsGroup().isEmpty()) {
            print0(ucase ? "COLUMNS GROUP " : "columns group ");
        } else if (x.getAdbSchema() != null) {
            print0(ucase ? "DATABASE " : "database ");
            x.getAdbSchema().accept(this);
        } else if (!x.getTableSources().isEmpty()) {
            print0(ucase ? "TABLE " : "table ");
        }

        printAndAccept(x.getTableSources(), ", ");

        if (!x.getAdbColumns().isEmpty()) {
            print0("(");
            printAndAccept(x.getAdbColumns(), ",");
            print0(")");
            if (x.getAdbWhere() != null) {
                println();
                print0(ucase ? " WHERE " : " WHERE ");
                printExpr(x.getAdbWhere());
            }
        } else if (!x.getAdbColumnsGroup().isEmpty()) {
            print0("(");
            printAndAccept(x.getAdbColumnsGroup(), ",");
            print0(")");
            if (x.getAdbWhere() != null) {
                println();
                print0(ucase ? " WHERE " : " WHERE ");
                printExpr(x.getAdbWhere());
            }
        } else if (!x.getTableSources().isEmpty()) {
            if (x.getAdbWhere() != null) {
                println();
                print0(ucase ? " WHERE " : " WHERE ");
                printExpr(x.getAdbWhere());
            }
        }

        SQLPartitionRef partition = x.getPartition();
        if (partition != null) {
            print(' ');
            partition.accept(this);
        }

        if (x.isComputeStatistics()) {
            print0(ucase ? " COMPUTE STATISTICS" : " compute statistics");
        }

        if (x.isForColums()) {
            print0(ucase ? " FOR COLUMNS" : " for columns");
        }

        if (x.isCacheMetadata()) {
            print0(ucase ? " CACHE METADATA" : " cache metadata");
        }

        if (x.isNoscan()) {
            print0(ucase ? " NOSCAN" : " noscan");
        }

        return false;
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
    public boolean visit(MySqlAlterUserStatement x) {
        print0(ucase ? "ALTER USER" : "alter user");

        if (x.isIfExists()) {
            print0(ucase ? " IF EXISTS" : " if exists");
        }

        for (int i = 0; i < x.getAlterUsers().size(); i++) {
            if (i != 0) {
                print(',');
            }

            MySqlAlterUserStatement.AlterUser alterUser = x.getAlterUsers().get(i);
            print(' ');
            alterUser.getUser().accept(this);

            if (alterUser.getAuthOption() != null) {
                print(" IDENTIFIED BY ");
                SQLCharExpr authString = alterUser.getAuthOption().getAuthString();
                authString.accept(this);
            }
        }

        MySqlAlterUserStatement.PasswordOption passwordOption = x.getPasswordOption();
        if (passwordOption != null) {
            switch (passwordOption.getExpire()) {
                case PASSWORD_EXPIRE:
                    print0(ucase ? " PASSWORD EXPIRE" : " password expire");
                    break;
                case PASSWORD_EXPIRE_DEFAULT:
                    print0(ucase ? " PASSWORD EXPIRE DEFAULT" : " password expire default");
                    break;
                case PASSWORD_EXPIRE_NEVER:
                    print0(ucase ? " PASSWORD EXPIRE NEVER" : " password expire never");
                    break;
                case PASSWORD_EXPIRE_INTERVAL:
                    print0(ucase ? " PASSWORD EXPIRE INTERVAL " : " password expire interval ");
                    passwordOption.getIntervalDays().accept(this);
                    print0(ucase ? " DAY" : " day");
                    break;
                default:
                    throw new RuntimeException("invalid password option:" + passwordOption);
            }
        }
        return false;
    }

    @Override
    public boolean visit(SQLSetStatement x) {
        boolean printSet = x.getAttribute("parser.set") == Boolean.TRUE || DbType.oracle != dbType;
        if (printSet) {
            print0(ucase ? "SET " : "set ");
        }
        SQLSetStatement.Option option = x.getOption();
        if (option != null) {
            print(option.name());
            print(' ');
        }

        if (option == SQLSetStatement.Option.PASSWORD) {
            print0("FOR ");
        }

        printAndAccept(x.getItems(), ", ");

        if (x.getHints() != null && x.getHints().size() > 0) {
            print(' ');
            printAndAccept(x.getHints(), " ");
        }

        return false;
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
    public boolean visit(MySqlOrderingExpr x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(' ');
            print0(ucase ? x.getType().name : x.getType().name_lcase);
        }

        return false;
    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        SQLObject parent = x.getParent();
        String labelName = x.getLabelName();

        if (labelName != null && !labelName.equals("")) {
            print0(labelName);
            print0(": ");
        }

        List<SQLParameter> parameters = x.getParameters();
        if (parameters.size() != 0) {
            this.indentCount++;
            if (parent instanceof SQLCreateProcedureStatement) {
                printIndent();
            }
            if (!(parent instanceof SQLCreateProcedureStatement)) {
                print0(ucase ? "DECLARE" : "declare");
                println();
            }

            for (int i = 0, size = parameters.size(); i < size; ++i) {
                if (i != 0) {
                    println();
                }
                SQLParameter param = parameters.get(i);
                visit(param);
                print(';');
            }

            this.indentCount--;
            println();
        }

        print0(ucase ? "BEGIN" : "begin");
        if (!x.isEndOfCommit()) {
            this.indentCount++;
        } else {
            print(';');
        }
        println();
        List<SQLStatement> statementList = x.getStatementList();
        for (int i = 0, size = statementList.size(); i < size; ++i) {
            if (i != 0) {
                println();
            }
            SQLStatement stmt = statementList.get(i);
            stmt.accept(this);
        }

        if (!x.isEndOfCommit()) {
            this.indentCount--;
            println();
            print0(ucase ? "END" : "end");
            if (labelName != null && !labelName.equals("")) {
                print(' ');
                print0(labelName);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLIfStatement x) {
        print0(ucase ? "IF " : "if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
        this.indentCount++;
        println();
        List<SQLStatement> statements = x.getStatements();
        for (int i = 0, size = statements.size(); i < size; ++i) {
            SQLStatement item = statements.get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        for (SQLIfStatement.ElseIf iterable_element : x.getElseIfList()) {
            iterable_element.accept(this);
        }

        if (x.getElseItem() != null) {
            x.getElseItem().accept(this);
        }

        print0(ucase ? "END IF" : "end if");
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        print0(ucase ? "ELSE IF " : "else if ");
        x.getCondition().accept(this);
        print0(ucase ? " THEN" : " then");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        return false;
    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        print0(ucase ? "ELSE " : "else ");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
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
    public boolean visit(MySqlDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        printAndAccept(x.getVarList(), ", ");
        return false;
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
    public boolean visit(SQLLoopStatement x) {
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }

        print0(ucase ? "LOOP " : "loop ");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
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
    public boolean visit(MySqlIterateStatement x) {
        print0(ucase ? "ITERATE " : "iterate ");
        print0(x.getLabelName());
        return false;
    }

    @Override
    public boolean visit(MySqlRepeatStatement x) {
        // TODO Auto-generated method stub
        if (x.getLabelName() != null && !x.getLabelName().equals("")) {
            print0(x.getLabelName());
            print0(": ");
        }

        print0(ucase ? "REPEAT " : "repeat ");
        this.indentCount++;
        println();
        for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
            SQLStatement item = x.getStatements().get(i);
            item.accept(this);
            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
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
    public boolean visit(MySqlCursorDeclareStatement x) {
        print0(ucase ? "DECLARE " : "declare ");
        printExpr(x.getCursorName(), parameterized);
        print0(ucase ? " CURSOR FOR" : " cursor for");
        this.indentCount++;
        println();
        x.getSelect().accept(this);
        this.indentCount--;
        return false;
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
    public boolean visit(MySqlAlterTableForce x) {
        print0(ucase ? "FORCE" : "force");
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableLock x) {
        print0(ucase ? "LOCK = " : "lock = ");
        printExpr(x.getLockType());
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableOrderBy x) {
        print0(ucase ? "ORDER BY " : "order by ");
        printAndAccept(x.getColumns(), ", ");
        return false;
    }

    @Override
    public boolean visit(MySqlAlterTableValidation x) {
        if (x.isWithValidation()) {
            print0(ucase ? "WITH VALIDATION" : "with validation");
        } else {
            print0(ucase ? "WITHOUT VALIDATION" : "without validation");
        }
        return false;
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
    public boolean visit(MySqlSubPartitionByValue x) {
        print0(ucase ? "SUBPARTITION BY VALUE (" : "subpartition by value (");
        printAndAccept(x.getColumns(), ", ");
        print(')');

        if (x.getLifecycle() != null) {
            print0(ucase ? " LIFECYCLE " : " lifecycle ");
            x.getLifecycle().accept(this);
        }

        if (x.getSubPartitionsCount() != null) {
            if ((Boolean) x.getAttribute("adb.partitons")) {
                print0(ucase ? " PARTITIONS " : " partitions ");
            } else {
                print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
            }
            x.getSubPartitionsCount().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTablePartitionCount x) {
        print0(ucase ? "PARTITIONS " : "partitons ");
        x.getCount().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableBlockSize x) {
        print0(ucase ? "BLOCK_SIZE " : "block_size ");
        x.getSize().accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableCompression x) {
        print0(ucase ? "COMPRESSION = " : "compression = ");
        x.getName().accept(this);
        return false;
    }

    @Override
    public boolean visit(MySqlSubPartitionByList x) {
        print0(ucase ? "SUBPARTITION BY LIST " : "subpartition by list ");
        if (x.getKeys().size() > 0) {
            if (Boolean.TRUE.equals(x.getAttribute("ads.subPartitionList"))) {
                print0(ucase ? "KEY (" : "key (");
            } else {
                print('(');
            }

            printAndAccept(x.getKeys(), ",");
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
        this.indentCount++;
        println();
        x.getSpStatement().accept(this);
        this.indentCount--;
        return false;
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
            this.indentCount++;
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            this.indentCount--;
            println();
        }
        print(')');

        if (x.getComment() != null) {
            println();
            print(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }
        if (x.isDeterministic()) {
            println();
            print(ucase ? "DETERMINISTIC" : "deterministic");
        }

        if (x.isContainsSql()) {
            println();
            print0(ucase ? "CONTAINS SQL" : "contains sql");
        }

        if (x.isLanguageSql()) {
            println();
            print0(ucase ? "LANGUAGE SQL" : "language sql");
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
            this.indentCount++;
            println();

            for (int i = 0; i < paramSize; ++i) {
                if (i != 0) {
                    print0(", ");
                    println();
                }
                SQLParameter param = x.getParameters().get(i);
                param.accept(this);
            }

            this.indentCount--;
            println();
            print(')');
        }

        println();
        print(ucase ? "RETURNS " : "returns ");
        x.getReturnDataType().accept(this);

        String comment = x.getComment();
        if (comment != null) {
            print(ucase ? " COMMENT " : " comment ");
            print(ucase ? comment.toUpperCase() : comment.toLowerCase());
        }

        if (x.isDeterministic()) {
            print(ucase ? " DETERMINISTIC" : " deterministic");
        }

        String language = x.getLanguage();
        if (language != null) {
            print(ucase ? " LANGUAGE " : " language ");
            print(ucase ? language.toUpperCase() : language.toLowerCase());
        }

        SQLStatement block = x.getBlock();

        println();

        block.accept(this);
        return false;
    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        SQLCommentStatement.Type type = x.getType();

        SQLExprTableSource on = x.getOn();
        if (type == SQLCommentStatement.Type.TABLE) {
            print0(ucase ? "ALTER TABLE " : "alter table ");
            on.accept(this);
            print0(ucase ? " COMMENT = " : " comment = ");
            x.getComment().accept(this);
        } else {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) on.getExpr();

            SQLExpr table = propertyExpr.getOwner();
            String column = propertyExpr.getName();

            print0(ucase ? "ALTER TABLE " : "alter table ");
            printTableSourceExpr(table);
            print0(ucase ? " MODIFY COLUMN " : " modify column ");
            print(column);
            print0(ucase ? " COMMENT " : " comment ");
            x.getComment().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlFlushStatement x) {
        print0(ucase ? "FLUSH" : "flush");

        if (x.isNoWriteToBinlog()) {
            print0(ucase ? " NO_WRITE_TO_BINLOG" : " no_write_to_binlog");
        } else if (x.isLocal()) {
            print0(ucase ? " LOCAL" : " local");
        }

        if(x.isBinaryLogs()) {
            print0(ucase ? " BINARY LOGS" : " binary logs");
        }
        if (x.isDesKeyFile()) {
            print0(ucase ? " DES_KEY_FILE" : " des_key_file");
        }
        if (x.isEngineLogs()) {
            print0(ucase ? " ENGINE LOGS" : " engine logs");
        }
        if (x.isErrorLogs()) {
            print0(ucase ? " ERROR LOGS" : " error logs");
        }
        if (x.isGeneralLogs()) {
            print0(ucase ? " GENERAL LOGS" : " general logs");
        }
        if (x.isHots()) {
            print0(ucase ? " HOSTS" : " hosts");
        }
        if (x.isLogs()) {
            print0(ucase ? " LOGS" : " logs");
        }
        if (x.isPrivileges()) {
            print0(ucase ? " PRIVILEGES" : " privileges");
        }
        if (x.isOptimizerCosts()) {
            print0(ucase ? " OPTIMIZER_COSTS" : " optimizer_costs");
        }
        if (x.isQueryCache()) {
            print0(ucase ? " QUERY CACHE" : " query cache");
        }
        if (x.isRelayLogs()) {
            print0(ucase ? " RELAY LOGS" : " relay logs");
            SQLExpr channel = x.getRelayLogsForChannel();
            if (channel != null) {
                print(' ');
                channel.accept(this);
            }
        }
        if (x.isSlowLogs()) {
            print0(ucase ? " SLOW LOGS" : " slow logs");
        }
        if (x.isStatus()) {
            print0(ucase ? " STATUS" : " status");
        }
        if (x.isUserResources()) {
            print0(ucase ? " USER_RESOURCES" : " user_resources");
        }

        if(x.isTableOption()){
            print0(ucase ? " TABLES" : " tables");

            List<SQLExprTableSource> tables = x.getTables();
            if (tables != null && tables.size() > 0) {
                print(' ');
                printAndAccept(tables, ", ");
            }

            if (x.isWithReadLock()) {
                print0(ucase ? " WITH READ LOCK" : " with read lock");
            }

            if (x.isForExport()) {
                print0(ucase ? " FOR EXPORT" : " for export");
            }

            if (x.getVersion() != null) {
                print0(ucase ? " VERSION = " : " version = ");
                x.getVersion().accept(this);
            }
        }
        return false;
    }

    @Override
    public boolean visit(MySqlEventSchedule x) {
        int cnt = 0;
        if (x.getAt() != null) {
            print0(ucase ? "AT " : "at ");
            printExpr(x.getAt(), parameterized);

            cnt++;
        }

        if (x.getEvery() != null) {
            print0(ucase ? "EVERY " : "every ");
            SQLIntervalExpr interval = (SQLIntervalExpr) x.getEvery();
            printExpr(interval.getValue(), parameterized);
            print(' ');
            print(interval.getUnit().name());

            cnt++;
        }

        if (x.getStarts() != null) {
            if (cnt > 0) {
                print(' ');
            }

            print0(ucase ? "STARTS " : "starts ");
            printExpr(x.getStarts(), parameterized);

            cnt++;
        }

        if (x.getEnds() != null) {
            if (cnt > 0) {
                print(' ');
            }
            print0(ucase ? "ENDS " : "ends ");
            printExpr(x.getEnds(), parameterized);

            cnt++;
        }

        return false;
    }

    @Override
    public boolean visit(MySqlCreateEventStatement x) {
        print0(ucase ? "CREATE " : "create ");

        SQLName definer = x.getDefiner();
        if (definer != null) {
            print0(ucase ? "DEFINER = " : "definer = ");
        }

        print0(ucase ? "EVENT " : "evnet ");

        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printExpr(x.getName(), parameterized);

        MySqlEventSchedule schedule = x.getSchedule();
        print0(ucase ? " ON SCHEDULE " : " on schedule ");
        schedule.accept(this);

        Boolean enable = x.getEnable();
        if (enable != null) {
            if (enable) {
                print0(ucase ? " ENABLE" : " enable");
            } else {
                print0(ucase ? " DISABLE" : " disable");
                if (x.isDisableOnSlave()) {
                    print0(ucase ? " ON SLAVE" : " on slave");
                }
            }
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? "COMMENT " : "comment ");
            comment.accept(this);
        }

        println();
        SQLStatement body = x.getEventBody();
        if (!(body instanceof SQLExprStatement)) {
            print0(ucase ? "DO" : "do");
            println();
        }
        body.accept(this);

        return false;
    }

    @Override
    public boolean visit(MySqlCreateAddLogFileGroupStatement x) {
        print0(ucase ? "CREATE LOGFILE GROUP " : "create logfile group ");
        x.getName().accept(this);
        print(' ');
        print0(ucase ? "ADD UNDOFILE " : "add undofile ");
        printExpr(x.getAddUndoFile(), false);

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE " : " initial_size ");
            printExpr(initialSize, false);
        }

        SQLExpr undoBufferSize = x.getUndoBufferSize();
        if (undoBufferSize != null) {
            print0(ucase ? " UNDO_BUFFER_SIZE " : " undo_buffer_size ");
            printExpr(undoBufferSize, false);
        }

        SQLExpr redoBufferSize = x.getRedoBufferSize();
        if (redoBufferSize != null) {
            print0(ucase ? " REDO_BUFFER_SIZE " : " redo_buffer_size ");
            printExpr(redoBufferSize, false);
        }

        SQLExpr nodeGroup = x.getNodeGroup();
        if (nodeGroup != null) {
            print0(ucase ? " NODEGROUP " : " nodegroup ");
            printExpr(nodeGroup, false);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment, parameterized);
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine, parameterized);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlCreateServerStatement x) {
        print0(ucase ? "CREATE SERVER " : "create server ");
        x.getName().accept(this);
        print0(ucase ? " FOREIGN DATA WRAPPER " : " foreign data wrapper ");
        printExpr(x.getForeignDataWrapper(), parameterized);

        print(" OPTIONS(");
        int cnt = 0;
        SQLExpr host = x.getHost();
        if (host != null) {
            print0(ucase ? "HOST " : "host ");
            printExpr(host, parameterized);
            cnt++;
        }

        SQLExpr database = x.getDatabase();
        if (database != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "DATABASE " : "database ");
            printExpr(database, parameterized);
        }

        SQLExpr user = x.getUser();
        if (user != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "USER " : "user ");
            printExpr(user);
        }

        SQLExpr password = x.getPassword();
        if (password != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "PASSWORD " : "password ");
            printExpr(password);
        }

        SQLExpr socket = x.getSocket();
        if (socket != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "SOCKET " : "socket ");
            printExpr(socket);
        }

        SQLExpr owner = x.getOwner();
        if (owner != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "OWNER " : "owner ");
            printExpr(owner);
        }

        SQLExpr port = x.getPort();
        if (port != null) {
            if (cnt++ > 0) {
                print(", ");
            }
            print0(ucase ? "PORT " : "port ");
            printExpr(port);
        }
        print(')');

        return false;
    }

    @Override
    public boolean visit(MySqlCreateTableSpaceStatement x) {
        print0(ucase ? "CREATE TABLESPACE " : "create tablespace ");
        x.getName().accept(this);

        SQLExpr addDataFile = x.getAddDataFile();
        if (addDataFile != null) {
            print0(ucase ? " ADD DATAFILE " : " add datafile ");
            addDataFile.accept(this);
        }

        SQLExpr fileBlockSize = x.getFileBlockSize();
        if (fileBlockSize != null) {
            print0(ucase ? " FILE_BLOCK_SIZE = " : " file_block_size = ");
            fileBlockSize.accept(this);
        }

        SQLExpr logFileGroup = x.getLogFileGroup();
        if (logFileGroup != null) {
            print0(ucase ? " USE LOGFILE GROUP " : " use logfile group ");
            logFileGroup.accept(this);
        }

        SQLExpr extentSize = x.getExtentSize();
        if (extentSize != null) {
            print0(ucase ? " EXTENT_SIZE = " : " extent_size = ");
            extentSize.accept(this);
        }

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE = " : " initial_size = ");
            initialSize.accept(this);
        }

        SQLExpr autoExtentSize = x.getAutoExtentSize();
        if (autoExtentSize != null) {
            print0(ucase ? " AUTOEXTEND_SIZE = " : " autoextend_size = ");
            autoExtentSize.accept(this);
        }

        SQLExpr maxSize = x.getMaxSize();
        if (autoExtentSize != null) {
            print0(ucase ? " MAX_SIZE = " : " max_size = ");
            maxSize.accept(this);
        }

        SQLExpr nodeGroup = x.getNodeGroup();
        if (nodeGroup != null) {
            print0(ucase ? " NODEGROUP = " : " nodegroup = ");
            nodeGroup.accept(this);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? " COMMENT " : " comment ");
            printExpr(comment);
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlAlterEventStatement x) {
        print0(ucase ? "ALTER " : "alter ");

        SQLName definer = x.getDefiner();
        if (definer != null) {
            print0(ucase ? "DEFINER = " : "definer = ");
        }

        print0(ucase ? "EVENT " : "evnet ");
        printExpr(x.getName());

        MySqlEventSchedule schedule = x.getSchedule();
        if (schedule != null) {
            print0(ucase ? " ON SCHEDULE " : " on schedule ");
            schedule.accept(this);
        }

        Boolean enable = x.getEnable();
        if (enable != null) {
            if (enable) {
                print0(ucase ? " ENABLE" : " enable");
            } else {
                print0(ucase ? " DISABLE" : " disable");
                if (x.isDisableOnSlave()) {
                    print0(ucase ? " ON SLAVE" : " on slave");
                }
            }
        }

        SQLExpr comment = x.getComment();
        if (comment != null) {
            print0(ucase ? "COMMENT " : "comment ");
            comment.accept(this);
        }

        SQLStatement body = x.getEventBody();
        if (body != null) {
            println();
            if (!(body instanceof SQLExprStatement)) {
                print0(ucase ? "DO" : "do");
                println();
            }
            body.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlAlterLogFileGroupStatement x) {
        print0(ucase ? "ALTER LOGFILE GROUP " : "alter logfile group ");
        x.getName().accept(this);
        print(' ');
        print0(ucase ? "ADD UNDOFILE " : "add undofile ");
        printExpr(x.getAddUndoFile());

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE " : " initial_size ");
            printExpr(initialSize);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlAlterServerStatement x) {
        print0(ucase ? "ATLER SERVER " : "alter server ");
        x.getName().accept(this);

        print(" OPTIONS(");
        SQLExpr user = x.getUser();
        if (user != null) {
            print0(ucase ? "USER " : "user ");
            printExpr(user);
        }

        print(')');

        return false;
    }

    @Override
    public boolean visit(MySqlAlterTablespaceStatement x) {
        print0(ucase ? "CREATE TABLESPACE " : "create tablespace ");
        x.getName().accept(this);

        SQLExpr addDataFile = x.getAddDataFile();
        if (addDataFile != null) {
            print0(ucase ? " ADD DATAFILE " : " add datafile ");
            addDataFile.accept(this);
        }

        SQLExpr initialSize = x.getInitialSize();
        if (initialSize != null) {
            print0(ucase ? " INITIAL_SIZE = " : " initial_size = ");
            initialSize.accept(this);
        }

        if (x.isWait()) {
            print0(ucase ? " WAIT" : " wait");
        }

        SQLExpr engine = x.getEngine();
        if (engine != null) {
            print0(ucase ? " ENGINE " : " engine ");
            printExpr(engine);
        }

        return false;
    }

    @Override
    public boolean visit(MySqlChecksumTableStatement x) {
        print0(ucase ? "CHECKSUM TABLE " : "checksum table ");
        final List<SQLExprTableSource> tables = x.getTables();
        for (int i = 0; i < tables.size(); i++) {
            if (i != 0) {
                print0(", ");
            }
            tables.get(i)
                    .accept(this);
        }
        return false;
    }

    protected void printQuery(SQLSelectQuery x) {
        Class<?> clazz = x.getClass();
        if (clazz == MySqlSelectQueryBlock.class) {
            visit((MySqlSelectQueryBlock) x);
        } else if (clazz == SQLSelectQueryBlock.class) {
            visit((SQLSelectQueryBlock) x);
        } else if (clazz == SQLUnionQuery.class) {
            visit((SQLUnionQuery) x);
        } else {
            x.accept(this);
        }
    }

    public void printInsertColumns(List<SQLExpr> columns) {
        final int size = columns.size();
        if (size > 0) {
            if (size > 5) {
                this.indentCount++;
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
                if (column instanceof SQLIdentifierExpr) {
                    visit((SQLIdentifierExpr) column);
                } else {
                    printExpr(column, parameterized);
                }

                String dataType = (String) column.getAttribute("dataType");
                if (dataType != null) {
                    print(' ');
                    print(dataType);
                }
            }
            print(')');
            if (size > 5) {
                this.indentCount--;
            }
        }
    }

    @Override
    public boolean visit(SQLValuesTableSource x) {
        print('(');
        incrementIndent();
        println();
        print0(ucase ? "VALUES " : "values ");
        List<SQLListExpr> values = x.getValues();
        for (int i = 0; i < values.size(); ++i) {
            if (i != 0) {
                print(", ");
                println();
            }
            SQLListExpr list = values.get(i);
            visit(list);
        }
        decrementIndent();
        println();
        print0(")");
        if(x.getAlias() != null) {
            print0(" AS ");
            print0(x.getAlias());

            if (x.getColumns().size() > 0) {
                print0(" (");
                printAndAccept(x.getColumns(), ", ");
                print(')');
            }
        }

        return false;
    }

    public boolean visit(SQLExternalRecordFormat x) {
        return hiveVisit(x);
    }

    public boolean visit(MySqlJSONTableExpr x) {
        print0(ucase ? "JSON_TABLE(" : "json_table(");
        x.getExpr().accept(this);
        print(' ');
        x.getPath().accept(this);
        incrementIndent();
        println();
        print0(ucase ? "COLUMNS (" : "columns (");
        incrementIndent();
        println();
        printlnAndAccept(x.getColumns(), ", ");
        decrementIndent();
        println();
        print(')');
        decrementIndent();
        println();
        print(')');

        return false;
    }

    public boolean visit(MySqlJSONTableExpr.Column x) {
        x.getName().accept(this);

        if (x.isOrdinality()) {
            print0(ucase ? " FOR ORDINALITY" : " for ordinality");
        }

        SQLDataType dataType = x.getDataType();
        if (dataType != null) {
            print(' ');
            dataType.accept(this);
        }

        if (x.isExists()) {
            print0(ucase ? " EXISTS" : " exists");
        }

        SQLExpr path = x.getPath();
        if (path != null) {
            print0(ucase ? " PATH " : " path ");
            path.accept(this);
        }

        List<MySqlJSONTableExpr.Column> nestedColumns = x.getNestedColumns();
        if (nestedColumns.size() > 0) {
            print0(ucase ? " COLUMNS (" : " columns (");
            printAndAccept(nestedColumns, ", ");
            print(')');
        }

        SQLExpr onEmpty = x.getOnEmpty();
        if (onEmpty != null) {
            print(' ');
            if (!(onEmpty instanceof SQLNullExpr || onEmpty instanceof SQLIdentifierExpr)) {
                print0(ucase ? "DEFAULT " : "default ");
            }
            onEmpty.accept(this);
            print0(ucase ? " ON EMPTY" : " on empty");
        }

        SQLExpr onError = x.getOnError();
        if (onError != null) {
            print(' ');
            if (!(onEmpty instanceof SQLNullExpr || onEmpty instanceof SQLIdentifierExpr)) {
                print0(ucase ? "DEFAULT " : "default ");
            }
            onError.accept(this);
            print0(ucase ? " ON ERROR" : " on error");
        }
        return false;
    }

//    public boolean visit(SQLSelectItem x) {
//        SQLExpr expr = x.getExpr();
//
//        if (expr instanceof SQLIdentifierExpr) {
//            print0(((SQLIdentifierExpr) expr).getName());
//        } else if (expr instanceof SQLPropertyExpr) {
//            visit((SQLPropertyExpr) expr);
//        } else {
//            printExpr(expr);
//        }
//
//        String alias = x.getAlias();
//        if (alias != null && alias.length() > 0) {
//            print0(ucase ? " AS " : " as ");
//
//            boolean hasSpecial = false;
//            for (int i = 1; i < alias.length() - 1; ++i) {
//                char ch = alias.charAt(i);
//                if (ch == ' ' || ch == '\"' || ch == '\n') {
//                    hasSpecial = true;
//                }
//            }
//            char c0 = alias.charAt(0);
//            if (!hasSpecial) {
//                print0(alias);
//            } else {
//                print('"');
//
//                for (int i = 0; i < alias.length(); ++i) {
//                    char ch = alias.charAt(i);
//                    if (ch == '\"') {
//                        print('\\');
//                        print(ch);
//                    } else if (ch == '\n') {
//                        print0("\\n");
//                    } else {
//                        print(ch);
//                    }
//                }
//
//                print('"');
//            }
//        }
//        return false;
//    }

    public boolean visit(MysqlAlterTableAlterCheck x) {
        print0(ucase ? "ALTER CONSTRAINT " : "alter constraint ");

        SQLName name = x.getName();
        if (name != null) {
            name.accept(this);
            print(' ');
        }

        Boolean enforced = x.getEnforced();
        if (enforced != null) {
            if (enforced) {
                print0(ucase ? " ENFORCED" : " enforced");
            } else {
                print0(ucase ? " NOT ENFORCED" : " not enforced");
            }
        }
        return false;
    }

    public void endVisit(MysqlAlterTableAlterCheck x) {

    }
} //
