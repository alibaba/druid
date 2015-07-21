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
package com.alibaba.druid.sql.dialect.odps.visitor;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsAddStatisticStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsAnalyzeTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsert;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsReadStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsRemoveStatisticStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSetLabelStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowPartitionsStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowStatisticStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsStatisticClause;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OdpsOutputVisitor extends SQLASTOutputVisitor implements OdpsASTVisitor {

    public OdpsOutputVisitor(Appendable appender){
        super(appender);
    }

    public boolean visit(OdpsCreateTableStatement x) {
        if (x.isIfNotExiists()) {
            print("CREATE TABLE IF NOT EXISTS ");
        } else {
            print("CREATE TABLE ");
        }

        x.getName().accept(this);

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
                SQLTableElement element = x.getTableElementList().get(i);
                element.accept(this);
                
                if (i != size - 1) {
                    print(",");
                }
                if (this.isPrettyFormat() && element.hasAfterComment()) {
                    print(' ');
                    printComment(element.getAfterCommentsDirect(), "\n");
                }
                
                if (i != size - 1) {
                    println();
                }
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

        int partitionSize = x.getPartitionColumns().size();
        if (partitionSize > 0) {
            println();
            print("PARTITIONED BY (");
            incrementIndent();
            println();
            for (int i = 0; i < partitionSize; ++i) {
                SQLColumnDefinition column = x.getPartitionColumns().get(i);
                column.accept(this);
                
                if (i != partitionSize - 1) {
                    print(',');
                }
                if (this.isPrettyFormat() && column.hasAfterComment()) {
                    print(' ');
                    printComment(column.getAfterCommentsDirect(), "\n");
                }
                
                if (i != partitionSize - 1) {
                    println();
                }
            }
            decrementIndent();
            println();
            print(")");
        }

        if (x.getLifecycle() != null) {
            println();
            print("LIFECYCLE ");
            x.getLifecycle().accept(this);
        }

        if (x.getSelect() != null) {
            println();
            x.getSelect().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OdpsCreateTableStatement x) {
        super.endVisit((SQLCreateTableStatement) x);
    }

    public SQLStatement parseInsert() {
        OdpsInsertStatement stmt = new OdpsInsertStatement();

        return stmt;
    }

    @Override
    public void endVisit(OdpsInsertStatement x) {

    }

    @Override
    public boolean visit(OdpsInsertStatement x) {
        if (x.getFrom() != null) {
            print("FROM (");
            incrementIndent();
            println();
            x.getFrom().getSelect().accept(this);
            decrementIndent();
            println();
            print(") ");
            print(x.getFrom().getAlias());
            println();
        }

        for (int i = 0; i < x.getItems().size(); ++i) {
            OdpsInsert insert = x.getItems().get(i);
            if (i != 0) {
                println();
            }
            insert.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OdpsInsert x) {

    }

    @Override
    public boolean visit(OdpsInsert x) {
        if (x.hasBeforeComment()) {
            printComment(x.getBeforeCommentsDirect(), "\n");
            println();
        }
        if (x.isOverwrite()) {
            print("INSERT OVERWRITE TABLE ");
        } else {
            print("INSERT INTO TABLE ");
        }
        x.getTableSource().accept(this);

        int partitions = x.getPartitions().size();
        if (partitions > 0) {
            print(" PARTITION (");
            for (int i = 0; i < partitions; ++i) {
                if (i != 0) {
                    print(", ");
                }

                SQLAssignItem assign = x.getPartitions().get(i);
                assign.getTarget().accept(this);

                if (assign.getValue() != null) {
                    print("=");
                    assign.getValue().accept(this);
                }
            }
            print(")");
        }
        println();
        x.getQuery().accept(this);

        return false;
    }

    public boolean visit(SQLCaseExpr x) {
        incrementIndent();
        print("CASE ");
        if (x.getValueExpr() != null) {
            x.getValueExpr().accept(this);
            println();
        }

        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            println();
            x.getItems().get(i).accept(this);
        }

        if (x.getElseExpr() != null) {
            println();
            print("ELSE ");
            x.getElseExpr().accept(this);
        }

        decrementIndent();
        println();
        print("END");
        
        return false;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        int itemSize = x.getItems().size();
        if (itemSize > 0) {
            print("GROUP BY ");
            incrementIndent();
            for (int i = 0; i < itemSize; ++i) {
                if (i != 0) {
                    println(", ");
                }
                x.getItems().get(i).accept(this);
            }
            decrementIndent();
        }

        if (x.getHaving() != null) {
            println();
            print("HAVING ");
            x.getHaving().accept(this);
        }
        return false;
    }

    protected void printSelectList(List<SQLSelectItem> selectList) {
        incrementIndent();
        for (int i = 0, size = selectList.size(); i < size; ++i) {
            SQLSelectItem selectItem = selectList.get(i);

            if (i != 0) {
                print(", ");

                printComment(selectList.get(i - 1).getAfterCommentsDirect(), "\n");

                println();
            }

            selectItem.accept(this);

            if (i == selectList.size() - 1 && selectItem.hasAfterComment()) {
                print(' ');
                printComment(selectItem.getAfterCommentsDirect(), "\n");
            }
        }
        decrementIndent();
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        print("(");
        incrementIndent();
        println();
        x.getSelect().accept(this);
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
    public boolean visit(SQLJoinTableSource x) {
        x.getLeft().accept(this);

        if (x.getJoinType() == JoinType.COMMA) {
            print(",");
        } else {
            println();
            print(JoinType.toString(x.getJoinType()));
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

        return false;
    }

    @Override
    public void endVisit(OdpsUDTFSQLSelectItem x) {

    }

    @Override
    public boolean visit(OdpsUDTFSQLSelectItem x) {
        x.getExpr().accept(this);

        println();
        print("AS (");
        
        int aliasSize = x.getAliasList().size();
        if (aliasSize > 5) {
            incrementIndent();
            println();
        }

        for (int i = 0; i < aliasSize; ++i) {
            if (i != 0) {
                if (aliasSize > 5) {
                    println(",");
                } else {
                    print(", ");    
                }
            }
            print(x.getAliasList().get(i));
        }
        
        if (aliasSize > 5) {
            decrementIndent();
            println();
        }
        print(")");

        return false;
    }

    @Override
    public void endVisit(OdpsShowPartitionsStmt x) {

    }

    @Override
    public boolean visit(OdpsShowPartitionsStmt x) {
        print("SHOW PARTITIONS ");
        x.getTableSource().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsShowStatisticStmt x) {

    }

    @Override
    public boolean visit(OdpsShowStatisticStmt x) {
        print("SHOW STATISTIC ");
        x.getTableSource().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsSetLabelStatement x) {

    }

    @Override
    public boolean visit(OdpsSetLabelStatement x) {
        print("SET LABEL ");
        print(x.getLabel());
        print(" TO ");

        if (x.getUser() != null) {
            print("USER ");
            x.getUser().accept(this);
        } else if (x.getTable() != null) {
            print("TABLE ");
            x.getTable().accept(this);
            if (x.getColumns().size() > 0) {
                print("(");
                printAndAccept(x.getColumns(), ", ");
                print(")");
            }
        }

        return false;
    }

    @Override
    public void endVisit(OdpsSelectQueryBlock x) {

    }

    @Override
    public boolean visit(OdpsSelectQueryBlock x) {
        if (isPrettyFormat() && x.hasBeforeComment()) {
            printComment(x.getBeforeCommentsDirect(), "\n");
            println();
        }

        print("SELECT ");

        List<SQLHint> hints = x.getHintsDirect();
        if (hints != null) {
            printAndAccept(hints, " ");
            print(' ');
        }

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
            if (x.getWhere().hasBeforeComment() && isPrettyFormat()) {
                printlnComments(x.getWhere().getBeforeCommentsDirect());
            }
            
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
            if (x.getWhere().hasAfterComment() && isPrettyFormat()) {
                print(' ');
                printComment(x.getWhere().getAfterCommentsDirect(), "\n");
            }
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
            print("LIMIT ");
            x.getLimit().accept(this);
        }

        return false;
    }

    public boolean visit(SQLOrderBy x) {
        int itemSize = x.getItems().size();
        if (itemSize > 0) {
            print("ORDER BY ");
            incrementIndent();
            for (int i = 0; i < itemSize; ++i) {
                if (i != 0) {
                    println(", ");
                }
                x.getItems().get(i).accept(this);
            }
            decrementIndent();
        }

        return false;
    }

    @Override
    public void endVisit(OdpsAnalyzeTableStatement x) {

    }

    @Override
    public boolean visit(OdpsAnalyzeTableStatement x) {
        print("ANALYZE TABLE ");
        x.getTable().accept(this);

        if (x.getPartition().size() > 0) {
            print(" PARTITION (");
            printAndAccept(x.getPartition(), ", ");
            print(")");
        }

        print(" COMPUTE STATISTICS");

        return false;
    }

    @Override
    public void endVisit(OdpsAddStatisticStatement x) {

    }

    @Override
    public boolean visit(OdpsAddStatisticStatement x) {
        print("ADD STATISTIC ");
        x.getTable().accept(this);
        print(" ");
        x.getStatisticClause().accept(this);

        return false;
    }

    @Override
    public void endVisit(OdpsRemoveStatisticStatement x) {

    }

    @Override
    public boolean visit(OdpsRemoveStatisticStatement x) {
        print("REMOVE STATISTIC ");
        x.getTable().accept(this);
        print(" ");
        x.getStatisticClause().accept(this);

        return false;
    }

    @Override
    public void endVisit(OdpsStatisticClause.TableCount x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.TableCount x) {
        print("TABLE_COUNT");
        return false;
    }

    @Override
    public void endVisit(OdpsStatisticClause.ExpressionCondition x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.ExpressionCondition x) {
        print("EXPRESSION_CONDITION ");
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsStatisticClause.NullValue x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.NullValue x) {
        print("NULL_VALUE ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsStatisticClause.ColumnSum x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.ColumnSum x) {
        print("COLUMN_SUM ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsStatisticClause.ColumnMax x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.ColumnMax x) {
        print("COLUMN_MAX ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsStatisticClause.ColumnMin x) {

    }

    @Override
    public boolean visit(OdpsStatisticClause.ColumnMin x) {
        print("COLUMN_MIN ");
        x.getColumn().accept(this);
        return false;
    }

    @Override
    public void endVisit(OdpsReadStatement x) {

    }

    @Override
    public boolean visit(OdpsReadStatement x) {
        print("READ ");
        x.getTable().accept(this);
        
        if (x.getColumns().size() > 0) {
            print(" (");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        }

        if (x.getPartition().size() > 0) {
            print(" PARTITION (");
            printAndAccept(x.getPartition(), ", ");
            print(")");
        }
        
        if (x.getRowCount() != null) {
            print(' ');
            x.getRowCount().accept(this);
        }

        return false;
    }
    
    public boolean visit(SQLMethodInvokeExpr x) {
        if (x.getOwner() != null) {
            x.getOwner().accept(this);
            print(":");
        }
        print(x.getMethodName());
        print("(");
        printAndAccept(x.getParameters(), ", ");
        print(")");
        return false;
    }
}
