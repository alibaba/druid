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
package com.alibaba.druid.sql.dialect.oracle.ast.visitor;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLObjectCreateExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleDataTypeInterval;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArrayAccessExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleGroupComparisonCondition;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleGroupingSetsExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OraclePriorIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTableCollectionExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.AddColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.AddConstraint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.DeallocateClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.DropColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.EnableClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.EnableTrigger;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.ModifyCollectionRetrieval;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.ModifyColumnClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.ModifyConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.NoParallelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.ParallelClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.RenameColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement.RenameConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCheck;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintNull;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraintState;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleRefDataType;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleReferencesConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectForUpdate;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectHierachicalQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableColumn;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleTableTypeDef;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListMultiColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListSingleColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetValueClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class OracleOutputVisitor extends SQLASTOutputVisitor implements OracleASTVisitor {

    public OracleOutputVisitor(Appendable appender){
        super(appender);
    }

    public void postVisit(SQLObject astNode) {
        if (astNode instanceof SQLStatement) {
            if (astNode instanceof OraclePLSQLCommitStatement) {
                return;
            }

            println(";");
        }
    }

    private void printHints(List<OracleHint> hints) {
        if (hints.size() > 0) throw new UnsupportedOperationException();
    }

    public boolean visit(OracleAggregateExpr expr) {
        expr.getMethodName().accept(this);
        print("(");
        printAndAccept(expr.getArguments(), ", ");
        print(")");

        if (expr.getOver() != null) {
            print(" OVER (");
            expr.getOver().accept(this);
            print(")");
        }
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
        print("*");
        return false;
    }

    public boolean visit(OracleAnalytic x) {
        if (x.getPartitionBy().size() > 0) {
            print("PARTITION BY (");
            printAndAccept(x.getPartitionBy(), ", ");
            print(")");
        }

        if (x.getOrderBy() != null) {
            print(" ");
            x.getOrderBy().accept(this);
        }

        if (x.getWindowing() != null) {
            print(" ");
            x.getWindowing().accept(this);
        }

        return false;
    }

    public boolean visit(OracleAnalyticWindowing x) {
        print(x.getType().name().toUpperCase());
        print(" ");
        x.getExpr().accept(this);
        return false;
    }

    public boolean visit(OracleArrayAccessExpr x) {
        x.getOwnner().accept(this);
        print("[");

        printAndAccept(x.getArguments(), ", ");

        print("]");

        return false;
    }

    public boolean visit(OracleCheck node) {
        if (node.getName() != null) {
            print("CONSTAINT ");
            node.getName().accept(this);
            print(" ");
        }
        print("CHECK (");

        node.getCondition().accept(this);

        print(")");

        if (node.getState() != null) {
            print(" ");
            node.getState().accept(this);
        }

        return false;
    }

    public boolean visit(OracleConstraintNull node) {
        if (node.getName() != null) {
            print("CONSTAINT ");
            node.getName().accept(this);
            print(" ");
        }

        if (node.isNullable()) print("NULL");
        else {
            print("NOT NULL");
        }

        if (node.getState() != null) {
            print(" ");
            node.getState().accept(this);
        }

        return false;
    }

    public boolean visit(OracleCreateViewStatement x) {
        print("CREATE ");
        if (x.isReplace()) {
            print("OR REPLACE ");
        }

        if (Boolean.TRUE.equals(x.getForce())) {
            print("FORCE ");
        } else if (Boolean.FALSE.equals(x.getForce())) {
            print("NO FORCE ");
        }

        print("VIEW ");

        x.getName().accept(this);

        println();
        print("AS");
        println();
        x.getSubQuery().accept(this);

        return false;
    }

    public boolean visit(OracleDataTypeInterval x) {
        print("INTERVAL ");
        print(x.getType().name());
        if (x.getPrecision() != null) {
            print(" (");
            x.getPrecision().accept(this);
            print(")");
        }
        print(" TO ");
        print(x.getToType().name());
        if (x.getFractionalSecondsPrecision() != null) {
            print(" (");
            x.getFractionalSecondsPrecision().accept(this);
            print(")");
        }
        return false;
    }

    public boolean visit(OracleDateExpr x) {
        print("DATE '");

        print(x.getYear());
        print("-");
        print(x.getMonth());
        print("-");
        print(x.getDayOfMonth());

        if ((x.getHour() != 0) || (x.getMinute() != 0) || (x.getSecond() != 0)) {
            print(" ");
            print(x.getHour());
            print(":");
            print(x.getMinute());
            if (x.getSecond() != 0) {
                print(":");
                print(x.getSecond());
            }
        }

        print("'");
        return false;
    }

    public boolean visit(OracleDbLinkExpr x) {
        x.getExpr().accept(this);
        print("@");
        print(x.getDbLink());
        return false;
    }

    public boolean visit(OracleDeleteStatement x) {
        print("DELETE ");
        printHints(x.getHints());

        print("FROM ");
        if (x.isOnly()) {
            print("ONLY (");
            x.getTableName().accept(this);
            print(")");
        } else {
            x.getTableName().accept(this);
        }

        printAlias(x.getAlias());

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    public boolean visit(OracleExtractExpr x) {
        print("EXTRACT(");
        print(x.getUnit().name());
        print(" FROM ");
        x.getFrom().accept(this);
        print(")");
        return false;
    }

    public boolean visit(OracleForeignKey node) {
        if (node.getName() != null) {
            print("CONSTAINT ");
            node.getName().accept(this);
            print(" ");
        }
        print("FOREIGN KEY (");

        printAndAccept(node.getColumns(), ", ");

        print(") REFERENCES ");

        node.getRefObject().accept(this);
        print("(");
        printAndAccept(node.getColumns(), ", ");
        print(")");

        if (node.getState() != null) {
            print(" ");
            node.getState().accept(this);
        }

        return false;
    }

    public boolean visit(OracleGroupComparisonCondition x) {
        if (x.getExprList().size() > 1) {
            print("(");
        }
        printAndAccept(x.getExprList(), ", ");
        if (x.getExprList().size() > 1) {
            print(")");
        }

        print(" ");
        print(x.getOperator().name);
        print(" ");
        print(x.getComparator().name());

        print(" ");

        print("(");
        printAndAccept(x.getTargetExprList(), ", ");
        print(")");

        return false;
    }

    public boolean visit(OracleGroupingSetsExpr x) {
        print("GROUPING SETS (");
        printAndAccept(x.getItems(), ", ");
        print(")");
        return false;
    }

    public boolean visit(OracleHint x) {
        print(x.getName());
        return false;
    }

    public boolean visit(OracleIntervalExpr x) {
        print("INTERVAL '");
        print(x.getValue());
        print("' ");
        print(x.getType().name());

        if ((x.getPrecision() != null) || (x.getFactionalSecondsPrecision() != null)) {
            print("(");
            print(x.getPrecision().intValue());
            if (x.getFactionalSecondsPrecision() != null) {
                print(", ");
                print(x.getFactionalSecondsPrecision().intValue());
            }
            print(")");
        }

        if (x.getToType() != null) {
            print(" TO ");
            print(x.getType().name());
            if (x.getToFactionalSecondsPrecision() != null) {
                print("(");
                print(x.getFactionalSecondsPrecision().intValue());
                print(")");
            }
        }

        return false;
    }

    public boolean visit(OracleOrderBy x) {
        if (x.getItems().size() > 0) {
            print("ORDER ");
            if (x.isSibings()) {
                print("SIBLINGS ");
            }
            print("BY ");

            printAndAccept(x.getItems(), ", ");
        }
        return false;
    }

    public boolean visit(OracleOuterExpr x) {
        x.getExpr().accept(this);
        print("(+)");
        return false;
    }

    public boolean visit(OraclePLSQLCommitStatement astNode) {
        print("/");
        return false;
    }

    public boolean visit(OraclePrimaryKey node) {
        if (node.getName() != null) {
            print("CONSTAINT ");
            node.getName().accept(this);
            print(" ");
        }
        print("PRIMARY KEY");
        if (node.getColumns().size() > 0) {
            print(" (");
            printAndAccept(node.getColumns(), ", ");
            print(")");
        }
        return false;
    }

    public boolean visit(OraclePriorIdentifierExpr x) {
        print("PRIOR ");
        x.getValue().accept(this);
        return false;
    }

    public boolean visit(OracleRefDataType x) {
        print("REF ");
        x.getRefObject().accept(this);
        return false;
    }

    public boolean visit(OracleReferencesConstaint node) {
        if (node.getName() != null) {
            print("CONSTAINT ");
            node.getName().accept(this);
            print(" ");
        }
        print("REFERENCES ");

        node.getRefObject().accept(this);

        if (node.getRefColumns().size() > 0) {
            print("(");
            printAndAccept(node.getRefColumns(), ", ");
            print(")");
        }

        if (node.getState() != null) {
            print(" ");
            node.getState().accept(this);
        }

        return false;
    }

    public boolean visit(SQLSelect x) {
        if (x instanceof OracleSelect) {
            return visit((OracleSelect) x);
        }

        return super.visit(x);
    }

    public boolean visit(OracleSelect x) {
        x.getQuery().accept(this);

        if (x.getRestriction() != null) {
            print(" ");
            x.getRestriction().accept(this);
        }

        if (x.getForUpdate() != null) {
            println();
            x.getForUpdate().accept(this);
        }

        if (x.getOrderBy() != null) {
            println();
            x.getOrderBy().accept(this);
        }

        return false;
    }

    public boolean visit(OracleSelectForUpdate x) {
        print("FOR UPDATE");
        if (x.getOf().size() > 0) {
            print("(");
            printAndAccept(x.getOf(), ", ");
            print(")");
        }
        return false;
    }

    public boolean visit(OracleSelectHierachicalQueryClause x) {
        print("START WITH ");
        x.getStartWith().accept(this);

        println();
        print("CONNECT BY ");

        if (x.isPrior()) {
            print("PRIOR ");
        }

        if (x.isNoCycle()) {
            print("NOCYCLE ");
        }

        x.getConnectBy().accept(this);

        return false;
    }

    public boolean visit(OracleSelectJoin x) {
        x.getLeft().accept(this);
        print(" ");
        print(OracleSelectJoin.JoinType.toString(x.getJoinType()));
        print(" ");
        x.getRight().accept(this);

        if (x.getCondition() != null) {
            print(" ON ");
            x.getCondition().accept(this);
            print(" ");
        }

        if (x.getUsing().size() > 0) {
            print(" USING (");
            printAndAccept(x.getUsing(), ", ");
            print(")");
        }
        return false;
    }

    public boolean visit(OracleSelectOrderByItem x) {
        x.getExpr().accept(this);
        if (x.getType() != null) {
            print(" ");
            print(x.getType().name().toUpperCase());
        }

        if (x.getNullsOrderType() != null) {
            print(" ");
            print(x.getNullsOrderType().toFormalString());
        }

        return false;
    }

    public boolean visit(OracleSelectPivot x) {
        print("PIVOT");
        if (x.isXml()) {
            print(" XML");
        }
        print(" (");
        printAndAccept(x.getItems(), ", ");

        if (x.getPivotFor().size() > 0) {
            print(" FOR ");
            if (x.getPivotFor().size() == 1) {
                ((SQLExpr) x.getPivotFor().get(0)).accept(this);
            } else {
                print("(");
                printAndAccept(x.getPivotFor(), ", ");
                print(")");
            }
        }

        if (x.getPivotIn().size() > 0) {
            print(" IN (");
            printAndAccept(x.getPivotIn(), ", ");
            print(")");
        }

        print(")");

        return false;
    }

    public boolean visit(OracleSelectPivot.Item x) {
        x.getExpr().accept(this);
        if ((x.getAlias() != null) && (x.getAlias().length() > 0)) {
            print(" AS ");
            print(x.getAlias());
        }
        return false;
    }

    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof OracleSelectQueryBlock) {
            return visit((OracleSelectQueryBlock) select);
        }

        return super.visit(select);
    }

    public boolean visit(OracleSelectQueryBlock select) {
        print("SELECT ");

        if (SQLSetQuantifier.ALL == select.getDistionOption()) {
            print("ALL ");
        } else if (SQLSetQuantifier.DISTINCT == select.getDistionOption()) {
            print("DISTINCT ");
        } else if (SQLSetQuantifier.UNIQUE == select.getDistionOption()) {
            print("UNIQUE ");
        }

        if (select.getHints().size() > 0) {
            print("/*+");
            printAndAccept(select.getHints(), ", ");
            print("*/ ");
        }

        printAndAccept(select.getSelectList(), ", ");

        println();
        print("FROM ");
        if (select.getFrom() == null) {
            print("DUAL");
        } else {
            select.getFrom().accept(this);
        }

        if (select.getWhere() != null) {
            println();
            print("WHERE ");
            select.getWhere().accept(this);
        }

        if (select.getHierachicalQueryClause() != null) {
            println();
            select.getHierachicalQueryClause().accept(this);
        }

        if (select.getGroupBy() != null) {
            print(" ");
            select.getGroupBy().accept(this);
        }

        return false;
    }

    public boolean visit(OracleSelectRestriction.CheckOption x) {
        print("CHECK OPTION");
        if (x.getConstraint() != null) {
            print(" ");
            x.getConstraint().accept(this);
        }
        return false;
    }

    public boolean visit(OracleSelectRestriction.ReadOnly x) {
        print("READ ONLY");
        return false;
    }

    public boolean visit(OracleSelectSubqueryTableSource x) {
        print("(");
        incrementIndent();
        x.getSelect().accept(this);
        decrementIndent();
        print(")");

        if (x.getPivot() != null) {
            incrementIndent();

            println();
            x.getPivot().accept(this);

            decrementIndent();
        }

        if ((x.getAlias() != null) && (x.getAlias().length() != 0)) {
            print(" ");
            print(x.getAlias());
        }

        return false;
    }

    public boolean visit(OracleSelectTableReference x) {
        if (x.isOnly()) {
            print("ONLY (");
            x.getExpr().accept(this);
            print(")");
        } else {
            x.getExpr().accept(this);
        }

        if (x.getPivot() != null) {
            incrementIndent();

            println();
            x.getPivot().accept(this);

            decrementIndent();
        }

        printAlias(x.getAlias());

        return false;
    }

    public boolean visit(OracleSelectUnPivot x) {
        print("UNPIVOT");
        if (x.getNullsIncludeType() != null) {
            print(" ");
            print(OracleSelectUnPivot.NullsIncludeType.toString(x.getNullsIncludeType()));
        }

        print(" (");
        if (x.getItems().size() == 1) {
            ((SQLExpr) x.getItems().get(0)).accept(this);
        } else {
            print(" (");
            printAndAccept(x.getItems(), ", ");
            print(")");
        }

        if (x.getPivotFor().size() > 0) {
            print(" FOR ");
            if (x.getPivotFor().size() == 1) {
                ((SQLExpr) x.getPivotFor().get(0)).accept(this);
            } else {
                print("(");
                printAndAccept(x.getPivotFor(), ", ");
                print(")");
            }
        }

        if (x.getPivotIn().size() > 0) {
            print(" IN (");
            printAndAccept(x.getPivotIn(), ", ");
            print(")");
        }

        print(")");
        return false;
    }

    public boolean visit(OracleTableCollectionExpr x) {
        print("TALBE (");
        x.getExpr().accept(this);
        print(")");
        if (x.isOuter()) {
            print("(+)");
        }
        return false;
    }

    public boolean visit(OracleTableColumn x) {
        print(x.getName());

        if (x.getDataType() != null) {
            print(" ");
            x.getDataType().accept(this);
        }

        if (x.isSort()) {
            print(" SORT");
        }

        if (x.isGeneratedAlways()) {
            print(" GENERATED ALWAYS");
        }

        if (x.getDefaultValue() != null) {
            print(" DEFAULT ");
            x.getDefaultValue().accept(this);
        }

        if (x.getAs() != null) {
            print(" AS ");
            x.getAs().accept(this);
        }

        if (x.isVirtual()) {
            print(" VIRTUAL");
        }

        int i = 0;
        for (int size = x.getConstaints().size(); i < size; ++i) {
            print(" ");
            ((OracleConstraint) x.getConstaints().get(i)).accept(this);
        }

        return false;
    }

    public boolean visit(OracleTableExpr x) {
        x.getTable().accept(this);

        if (x.getPartition() != null) {
            print(" PARTITION (");
            x.getPartition().accept(this);
            print(")");
        } else {
            if (x.getPartitionFor().size() > 0) {
                print(" PARTITION FOR (");
                for (int i = 0, size = x.getPartitionFor().size(); i < size; ++i) {
                    ((SQLName) x.getPartitionFor().get(i)).accept(this);
                }
                print(")");
            } else if (x.getSubPartition() != null) {
                print(" SUBPARTITION (");
                x.getSubPartition().accept(this);
                print(")");
            } else if (x.getSubPartitionFor().size() > 0) {
                print(" SUBPARTITION FOR (");
                for (int i = 0, size = x.getSubPartitionFor().size(); i < size; ++i) {
                    ((SQLName) x.getSubPartitionFor().get(i)).accept(this);
                }
                print(")");
            }
        }
        return false;
    }

    public boolean visit(OracleTableTypeDef x) {
        print("AS TABLE OF ");
        x.getName().accept(this);

        if (x.isType()) {
            print("%TYPE");
        }

        if (x.isNotNull()) {
            print(" NOT NULL");
        }

        return false;
    }

    public boolean visit(OracleTimestampExpr x) {
        print("TIMESTAMP '");

        print(x.getYear());
        print('-');
        print(x.getMonth());
        print("-");
        print(x.getDayOfMonth());

        if ((x.getHour() != 0) || (x.getMinute() != 0) || (x.getSecond() != 0) || (x.getMilliSecond() != 0) || (x.getTimeZone() != null)) {
            print(' ');
            print(x.getHour());
            print(":");
            print(x.getHour());
            print(":");
            print(x.getSecond());

            if (x.getMilliSecond() != 0) {
                print(".");
                print(x.getMilliSecond());
            }

            if (x.getTimeZone() != null) {
                print(x.getTimeZone());
            }
        }

        print("'");

        return false;
    }

    public boolean visit(OracleUpdateSetListClause x) {
        print("SET ");
        printAndAccept(x.getItems(), ", ");
        return false;
    }

    public boolean visit(OracleUpdateSetListMultiColumnItem x) {
        print("(");
        printAndAccept(x.getColumns(), ", ");
        print(") = (");
        x.getSubQuery().accept(this);
        print(")");
        return false;
    }

    public boolean visit(OracleUpdateSetListSingleColumnItem x) {
        x.getColumn().accept(this);
        print(" = ");
        x.getValue().accept(this);
        return false;
    }

    public boolean visit(OracleUpdateSetValueClause x) {
        throw new UnsupportedOperationException();
    }

    public boolean visit(OracleUpdateStatement x) {
        print("UPDATE ");
        printHints(x.getHints());

        if (x.isOnly()) {
            print("ONLY (");
            x.getTable().accept(this);
            print(")");
        } else {
            x.getTable().accept(this);
        }

        printAlias(x.getAlias());

        println();

        x.getSetClause().accept(this);

        if (x.getWhere() != null) {
            println();
            print("WHERE ");
            x.getWhere().accept(this);
        }

        return false;
    }

    public boolean visit(OracleAlterTableStatement.AddColumnClause x) {
        print("ADD (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        return false;
    }

    public boolean visit(OracleAlterTableStatement.ModifyCollectionRetrieval x) {
        print("MODIFY NESTED TABLE ");
        x.getCollectionItem().accept(this);
        print(" RETURN AS ");
        print(x.getReturnAs().name());
        return false;
    }

    public boolean visit(OracleAlterTableStatement.NoParallelClause x) {
        print("NOPARALLEL");
        return false;
    }

    public boolean visit(OracleAlterTableStatement.ParallelClause x) {
        print("PARALLEL");
        if (x.getValue() != null) {
            print(" ");
            x.getValue().accept(this);
        }
        return false;
    }

    public boolean visit(OracleAlterTableStatement.EnableClause x) {
        if (x.isEnable()) print("ENABLE");
        else {
            print("DISABLE");
        }

        if (x.getValidate() != null) {
            if (x.getValidate().booleanValue()) print(" VALIDATE");
            else {
                print(" NOVALIDATE");
            }
        }

        print(" ");
        print(x.getType().name());

        if (x.getType().equals(OracleAlterTableStatement.ConstraintType.UNIQUE)) {
            print("(");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        } else if (x.getType().equals(OracleAlterTableStatement.ConstraintType.CONSTAINT)) {
            print(" ");
            x.getConstraintName().accept(this);
        }

        if (x.getUsingIndex() != null) {
            print(" ");
            x.getUsingIndex().accept(this);
        }

        if (x.getExceptions() != null) {
            print(" EXCEPTIONS INTO ");
            x.getExceptions().accept(this);
        }

        if (x.isCascade()) {
            print(" CASCADE");
        }

        if (x.getIndexType() != null) {
            print(" ");
            print(x.getIndexType().toFormalString());
        }
        return false;
    }

    public boolean visit(OracleAlterTableStatement x) {
        print("ALTER TABLE ");
        x.getTable().accept(this);
        if (x.getNode() != null) {
            incrementIndent();
            println();
            x.getNode().accept(this);
            decrementIndent();
        }

        if (x.getEnableClauses().size() > 0) {
            incrementIndent();
            int i = 0;
            for (int size = x.getEnableClauses().size(); i < size; ++i) {
                println();
                ((OracleAlterTableStatement.EnableClause) x.getEnableClauses().get(i)).accept(this);
            }
            decrementIndent();
        }
        return false;
    }

    public boolean visit(OracleAlterTableStatement.ModifyConstaint x) {
        print("MODIFY ");
        print(x.getType().toFormalString());

        if (x.getType().equals(OracleAlterTableStatement.ConstraintType.UNIQUE)) {
            print("(");
            printAndAccept(x.getColumns(), ", ");
            print(")");
        } else if (x.getType().equals(OracleAlterTableStatement.ConstraintType.CONSTAINT)) {
            print(" ");
            x.getConstraintName().accept(this);
        }

        if (x.getState() != null) {
            print(" ");
            x.getState().accept(this);
        }
        return false;
    }

    public boolean visit(OracleAlterTableStatement.AddConstraint x) {
        print("ADD ");
        printAndAccept(x.getConstraints(), " ");
        return false;
    }

    public boolean visit(OracleConstraintState x) {
        printlnAndAccept(x.getStates(), " ");
        return false;
    }

    public boolean visit(OracleAlterTableStatement.RenameConstaint x) {
        print("RENAME CONSTRAINT ");
        x.getOldName().accept(this);
        print(" TO ");
        x.getNewName().accept(this);
        return false;
    }

    public boolean visit(OracleAlterTableStatement.EnableTrigger x) {
        if (x.isEnable()) {
            print("ENABLE ALL TRIGGERS");
        } else {
            print("DISABLE ALL TRIGGERS");
        }
        return false;
    }

    public boolean visit(OracleAlterTableStatement.DeallocateClause x) {
        print("DEALLOCATE UNUSED");
        if (x.getKeep() != null) {
            print(" KEEP ");
            x.getKeep().accept(this);
        }
        return false;
    }

    public boolean visit(OracleAlterTableStatement.RenameColumn x) {
        print("RENAME COLUMN ");
        x.getOldName().accept(this);
        print(" TO ");
        x.getNewName().accept(this);
        return false;
    }

    public boolean visit(OracleAlterTableStatement.DropColumn x) {
        print("DROP (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        if (x.isCascade()) {
            print(" CASCADE CONSTRAINTS");
        }

        if (x.isInvlidate()) {
            print(" INVALIDATE");
        }

        if (x.getCheckPoint() != null) {
            print(" CHECKPOINT ");
            x.getCheckPoint().accept(this);
        }

        return false;
    }

    public boolean visit(OracleAlterTableStatement.ModifyColumnClause x) {
        print("MODIFY (");
        printAndAccept(x.getColumns(), ", ");
        print(")");

        return false;
    }

    // ///////////////////

    @Override
    public void endVisit(OracleAggregateExpr astNode) {

    }

    @Override
    public void endVisit(ModifyColumnClause astNode) {

    }

    @Override
    public void endVisit(DropColumn astNode) {

    }

    @Override
    public void endVisit(RenameColumn astNode) {

    }

    @Override
    public void endVisit(DeallocateClause astNode) {

    }

    @Override
    public void endVisit(EnableTrigger astNode) {

    }

    @Override
    public void endVisit(RenameConstaint astNode) {

    }

    @Override
    public void endVisit(AddConstraint astNode) {

    }

    @Override
    public void endVisit(ModifyConstaint astNode) {

    }

    @Override
    public void endVisit(OracleConstraintState astNode) {

    }

    @Override
    public void endVisit(OracleAlterTableStatement astNode) {

    }

    @Override
    public void endVisit(EnableClause astNode) {

    }

    @Override
    public void endVisit(ParallelClause astNode) {

    }

    @Override
    public void endVisit(NoParallelClause astNode) {

    }

    @Override
    public void endVisit(ModifyCollectionRetrieval astNode) {

    }

    @Override
    public void endVisit(AddColumnClause astNode) {

    }

    @Override
    public void endVisit(OracleForeignKey astNode) {

    }

    @Override
    public void endVisit(OraclePLSQLCommitStatement astNode) {

    }

    @Override
    public void endVisit(OracleReferencesConstaint astNode) {

    }

    @Override
    public void endVisit(OracleConstraintNull astNode) {

    }

    @Override
    public void endVisit(OracleCheck astNode) {

    }

    @Override
    public void endVisit(OraclePrimaryKey astNode) {

    }

    @Override
    public void endVisit(OracleAnalytic x) {

    }

    @Override
    public void endVisit(OracleAnalyticWindowing x) {

    }

    @Override
    public void endVisit(OracleArrayAccessExpr x) {

    }

    @Override
    public void endVisit(OracleCreateViewStatement x) {

    }

    @Override
    public void endVisit(SQLDataType x) {

    }

    @Override
    public void endVisit(OracleDataTypeInterval x) {

    }

    @Override
    public void endVisit(OracleRefDataType x) {

    }

    @Override
    public void endVisit(OracleDateExpr x) {

    }

    @Override
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleDeleteStatement x) {

    }

    @Override
    public void endVisit(OracleExtractExpr x) {

    }

    @Override
    public void endVisit(OracleGroupComparisonCondition x) {

    }

    @Override
    public void endVisit(OracleGroupingSetsExpr x) {

    }

    @Override
    public void endVisit(OracleHint x) {

    }

    @Override
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(SQLMethodInvokeExpr x) {

    }

    @Override
    public void endVisit(SQLObjectCreateExpr x) {

    }

    @Override
    public void endVisit(OracleOrderBy x) {

    }

    @Override
    public void endVisit(OracleOuterExpr x) {

    }

    @Override
    public void endVisit(OraclePriorIdentifierExpr x) {

    }

    @Override
    public void endVisit(OracleSelectForUpdate x) {

    }

    @Override
    public void endVisit(OracleSelectHierachicalQueryClause x) {

    }

    @Override
    public void endVisit(OracleSelectJoin x) {

    }

    @Override
    public void endVisit(OracleSelectOrderByItem x) {

    }

    @Override
    public void endVisit(OracleSelectPivot x) {

    }

    @Override
    public void endVisit(Item x) {

    }

    @Override
    public void endVisit(CheckOption x) {

    }

    @Override
    public void endVisit(ReadOnly x) {

    }

    @Override
    public void endVisit(OracleSelectSubqueryTableSource x) {

    }

    @Override
    public void endVisit(OracleSelectUnPivot x) {

    }

    @Override
    public void endVisit(OracleTableCollectionExpr x) {

    }

    @Override
    public void endVisit(OracleTableColumn x) {

    }

    @Override
    public void endVisit(OracleTableExpr x) {

    }

    @Override
    public void endVisit(OracleTableTypeDef x) {

    }

    @Override
    public void endVisit(OracleTimestampExpr x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListClause x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListMultiColumnItem x) {

    }

    @Override
    public void endVisit(OracleUpdateSetListSingleColumnItem x) {

    }

    @Override
    public void endVisit(OracleUpdateSetValueClause x) {

    }

    @Override
    public void endVisit(OracleUpdateStatement x) {

    }

}
