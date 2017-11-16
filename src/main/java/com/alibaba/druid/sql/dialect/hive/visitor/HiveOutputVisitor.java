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
package com.alibaba.druid.sql.dialect.hive.visitor;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;

public class HiveOutputVisitor extends SQLASTOutputVisitor implements HiveASTVisitor {
    public HiveOutputVisitor(Appendable appender) {
        super(appender);
    }

    public HiveOutputVisitor(Appendable appender, String dbType) {
        super(appender, dbType);
    }

    public HiveOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }


    @Override
    public boolean visit(HiveCreateTableStatement x) {
        printCreateTable(x, true);

        return false;
    }

    protected void printCreateTable(HiveCreateTableStatement x, boolean printSelect) {
        print0(ucase ? "CREATE " : "create ");

        final SQLCreateTableStatement.Type tableType = x.getType();
        if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(tableType)) {
            print0(ucase ? "GLOBAL TEMPORARY " : "global temporary ");
        } else if (SQLCreateTableStatement.Type.LOCAL_TEMPORARY.equals(tableType)) {
            print0(ucase ? "LOCAL TEMPORARY " : "local temporary ");
        }
        print0(ucase ? "TABLE " : "table ");

        if (x.isIfNotExiists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(x.getName());

        printTableElements(x.getTableElementList());

        SQLExprTableSource inherits = x.getInherits();
        if (inherits != null) {
            print0(ucase ? " INHERITS (" : " inherits (");
            inherits.accept(this);
            print(')');
        }

        int partitionSize = x.getPartitionColumns().size();
        if (partitionSize > 0) {
            println();
            print0(ucase ? "PARTITIONED BY (" : "partitioned by (");
            this.indentCount++;
            println();
            for (int i = 0; i < partitionSize; ++i) {
                SQLColumnDefinition column = x.getPartitionColumns().get(i);
                column.accept(this);

                if (i != partitionSize - 1) {
                    print(',');
                }
                if (this.isPrettyFormat() && column.hasAfterComment()) {
                    print(' ');
                    printlnComment(column.getAfterCommentsDirect());
                }

                if (i != partitionSize - 1) {
                    println();
                }
            }
            this.indentCount--;
            println();
            print(')');
        }

        List<SQLName> clusteredBy = x.getClusteredBy();
        if (clusteredBy.size() > 0) {
            println();
            print0(ucase ? "CLUSTERED BY (" : "clustered by (");
            printAndAccept(clusteredBy, ",");
            print(')');
        }

        List<SQLSelectOrderByItem> sortedBy = x.getSortedBy();
        if (sortedBy.size() > 0) {
            println();
            print0(ucase ? "SORTED BY (" : "sorted by (");
            printAndAccept(sortedBy, ", ");
            print(')');
        }

        int buckets = x.getBuckets();
        if (buckets > 0) {
            println();
            print0(ucase ? "INTO " : "into ");
            print(buckets);
            print0(ucase ? " BUCKETS" : " buckets");
        }

        SQLName storedAs = x.getStoredAs();
        if (storedAs != null) {
            println();
            print0(ucase ? "STORE AS " : "store as ");
            printExpr(storedAs);
        }

        SQLSelect select = x.getSelect();
        if (printSelect && select != null) {
            println();
            print0(ucase ? "AS" : "as");

            println();
            visit(select);
        }
    }

    @Override
    public void endVisit(HiveCreateTableStatement x) {

    }
}
