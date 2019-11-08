/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

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

        SQLExpr comment = x.getComment();
        if (comment != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            comment.accept(this);
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

        List<SQLSelectOrderByItem> clusteredBy = x.getClusteredBy();
        if (clusteredBy.size() > 0) {
            println();
            print0(ucase ? "CLUSTERED BY (" : "clustered by (");
            printAndAccept(clusteredBy, ",");
            print(')');
        }

        SQLExternalRecordFormat format = x.getRowFormat();
        if (format != null) {
            println();
            print0(ucase ? "ROW FORMAT DELIMITED " : "row format delimited ");
            visit(format);
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

        Map<String, SQLObject> tableOptions = x.getTableOptions();
        if (tableOptions.size() > 0) {
            println();
            print0(ucase ? "TBLPROPERTIES (" : "tblproperties (");
            int i = 0;
            for (Map.Entry<String, SQLObject> option : tableOptions.entrySet()) {
                print0(option.getKey());
                print0(" = ");
                option.getValue().accept(this);
                ++i;
            }
            print(')');
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

    public boolean visit(SQLExternalRecordFormat x) {
        if (x.getDelimitedBy() != null) {
            println();
            print0(ucase ? "LINES TERMINATED BY " : "lines terminated by ");
            x.getDelimitedBy().accept(this);
        }

        if (x.getTerminatedBy() != null) {
            println();
            print0(ucase ? "FIELDS TERMINATED BY " : "fields terminated by ");
            x.getTerminatedBy().accept(this);
        }

        return false;
    }


    @Override
    public void endVisit(HiveMultiInsertStatement x) {

    }

    @Override
    public boolean visit(HiveMultiInsertStatement x) {
        SQLTableSource from = x.getFrom();
        if (x.getFrom() != null) {
            if (from instanceof SQLSubqueryTableSource) {
                SQLSelect select = ((SQLSubqueryTableSource) from).getSelect();
                print0(ucase ? "FROM (" : "from (");
                this.indentCount++;
                println();
                select.accept(this);
                this.indentCount--;
                println();
                print0(") ");
                print0(x.getFrom().getAlias());
            } else {
                print0(ucase ? "FROM " : "from ");
                from.accept(this);
            }
            println();
        }

        for (int i = 0; i < x.getItems().size(); ++i) {
            HiveInsert insert = x.getItems().get(i);
            if (i != 0) {
                println();
            }
            insert.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(HiveInsertStatement x) {

    }

    public boolean visit(HiveInsertStatement x) {
        if (x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }
        if (x.isOverwrite()) {
            print0(ucase ? "INSERT OVERWRITE TABLE " : "insert overwrite table ");
        } else {
            print0(ucase ? "INSERT INTO TABLE " : "insert into table ");
        }
        x.getTableSource().accept(this);

        int partitions = x.getPartitions().size();
        if (partitions > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            for (int i = 0; i < partitions; ++i) {
                if (i != 0) {
                    print0(", ");
                }

                SQLAssignItem assign = x.getPartitions().get(i);
                assign.getTarget().accept(this);

                if (assign.getValue() != null) {
                    print('=');
                    assign.getValue().accept(this);
                }
            }
            print(')');
        }
        println();

        SQLSelect select = x.getQuery();
        List<SQLInsertStatement.ValuesClause> valuesList = x.getValuesList();
        if (select != null) {
            select.accept(this);
        } else if (!valuesList.isEmpty()) {
            print0(ucase ? "VALUES " : "values ");
            printAndAccept(valuesList, ", ");
        }


        return false;
    }

    @Override
    public boolean visit(HiveInsert x) {
        if (x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }
        if (x.isOverwrite()) {
            print0(ucase ? "INSERT OVERWRITE TABLE " : "insert overwrite table ");
        } else {
            print0(ucase ? "INSERT INTO TABLE " : "insert into table ");
        }
        x.getTableSource().accept(this);

        int partitions = x.getPartitions().size();
        if (partitions > 0) {
            print0(ucase ? " PARTITION (" : " partition (");
            for (int i = 0; i < partitions; ++i) {
                if (i != 0) {
                    print0(", ");
                }

                SQLAssignItem assign = x.getPartitions().get(i);
                assign.getTarget().accept(this);

                if (assign.getValue() != null) {
                    print('=');
                    assign.getValue().accept(this);
                }
            }
            print(')');
        }
        println();

        SQLSelect select = x.getQuery();
        List<SQLInsertStatement.ValuesClause> valuesList = x.getValuesList();
        if (select != null) {
            select.accept(this);
        } else if (!valuesList.isEmpty()) {
            print0(ucase ? "VALUES " : "values ");
            printAndAccept(valuesList, ", ");
        }


        return false;
    }

    @Override
    public void endVisit(HiveInsert x) {

    }
}
