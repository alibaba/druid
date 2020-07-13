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
package com.alibaba.druid.sql.dialect.impala.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsert;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsertStatement;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaKuduPartition;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaMultiInsertStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaMetaStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaUpdateStatements;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.util.List;
import java.util.Map;

public class ImpalaOutputVisitor extends SQLASTOutputVisitor implements ImpalaASTVisitor {
    public ImpalaOutputVisitor(Appendable appender) {
        super(appender);
    }

    public ImpalaOutputVisitor(Appendable appender, String dbType) {
        super(appender, dbType);
    }

    public ImpalaOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }


    @Override
    public boolean visit(ImpalaCreateTableStatement x) {
        printCreateTable(x, true);

        return false;
    }

    protected void printCreateTable(ImpalaCreateTableStatement x, boolean printSelect) {
        print0(ucase ? "CREATE " : "create ");

        final SQLCreateTableStatement.Type tableType = x.getType();
        if (SQLCreateTableStatement.Type.EXTERNAL.equals(tableType)) {
            print0(ucase ? "EXTERNAL " : "external ");
        }
        print0(ucase ? "TABLE " : "table ");

        if (x.isIfNotExiists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }

        printTableSourceExpr(x.getName());

        printTableElements(x.getTableElementList());


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

        List<ImpalaKuduPartition> kuduPartitions = x.getKuduPartitions();
        if (kuduPartitions.size() > 0){
            println(ucase ? "PARTITION BY":"partition BY");
            int kuduPartitionSize = kuduPartitions.size();
            for (int num=0;num<kuduPartitionSize;num++){
                ImpalaKuduPartition kuduPartition = kuduPartitions.get(num);
                print0("  " + kuduPartition.getType().name() + ' ');
                int partitionColumnsize = kuduPartition.getPartitionColumns().size();
                if (partitionColumnsize > 0){
                    print0("(");
                    for (int i=0;i<partitionColumnsize;i++){
                        SQLColumnDefinition column = kuduPartition.getPartitionColumns().get(i);
                        column.accept(this);

                        if (i != partitionColumnsize - 1) {
                            print(',');
                        }
                    }
                    print0(") ");
                }
                if (kuduPartition.getNumber() > 0){
                    print(ucase ? "PARTITIONS ":"partitions ");
                    print(Integer.toString(kuduPartition.getNumber()));
                }

                List<String> partitionAssign = kuduPartition.getPartitionAssign();
                if (partitionAssign.size() > 0){
                    println("(");
                    int assignSize = partitionAssign.size();
                    for (int i=0;i<assignSize;i++){
                        print0("    " + partitionAssign.get(i));
                        if (i < assignSize - 1){
                            println(",");
                        }
                    }
                    println(")");
                }
                if (num < kuduPartitionSize - 1){
                    println(",");
                }
            }
        }

        List<SQLSelectOrderByItem> sortedBy = x.getSortedBy();
        if (sortedBy.size() > 0) {
            print0(ucase ? " SORT BY (" : " sort by (");
            printAndAccept(sortedBy, ", ");
            print(')');
        }


        SQLName storedAs = x.getStoredAs();
        if (storedAs != null) {
            println();
            print0(ucase ? "STORE AS " : "store as ");
            printExpr(storedAs);
        }

        SQLName location = x.getLocation();
        if (location != null){
            println();
            print0(ucase ? "LOCATION " : "location ");
            printExpr(location);
        }



        Map<String, SQLObject> tableOptions = x.getTableOptions();
        if (tableOptions.size() > 0) {
            println();
            print0(ucase ? " TBLPROPERTIES (" : " tblproperties (");
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
    public void endVisit(ImpalaCreateTableStatement x) {

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
    public void endVisit(ImpalaMultiInsertStatement x) {

    }

    @Override
    public boolean visit(ImpalaMultiInsertStatement x) {
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
            ImpalaInsert insert = x.getItems().get(i);
            if (i != 0) {
                println();
            }
            insert.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(ImpalaInsertStatement x) {

    }

    public boolean visit(ImpalaInsertStatement x) {
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
    public boolean visit(ImpalaInsert x) {
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
    public void endVisit(ImpalaInsert x) {

    }

    @Override
    public boolean visit(ImpalaMetaStatement x) {
        if (x.getMetaType() == Token.INVALIDATE){
            print0(ucase? "INVALIDATE METADATA ": "invalidate metadata ");
            if (x.getTableSource() != null){
                x.getTableSource().accept(this);
            }
        }else{
            print0(ucase? "REFRESH " : "refresh ");
            x.getTableSource().accept(this);
            int partitions = x.getPartitions().size();
            if (partitions > 0) {
                println();
                print0(ucase ? "PARTITION (" : " partition (");
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
        }
        return false;
    }

    @Override
    public void endVisit(ImpalaMetaStatement x) {

    }

    @Override
    public boolean visit(ImpalaUpdateStatements x) {
        print0(ucase ? "UPDATE " : "update ");

        printTableSource(x.getTableSource());

        print0(ucase ? "SET " : "set ");
        for (int i = 0, size = x.getItems().size(); i < size; ++i) {
            if (i != 0) {
                print0(", ");
            }
            SQLUpdateSetItem item = x.getItems().get(i);
            visit(item);
        }

        if (x.getJoin() != null){
            indentCount++;
            println();
            print0(ucase? "FROM ":"from ");
            printTableSource(x.getJoin().getLeft());
            print0(ucase? " JOIN ":" join ");
            printTableSource(x.getJoin().getRight());
            println();
            print(ucase?"ON ":"on ");
            printExpr(x.getJoin().getCondition());
            indentCount--;
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            indentCount++;
            print0(ucase ? "WHERE " : "where ");
            printExpr(where);
            indentCount--;
        }

        return false;
    }

    @Override
    public void endVisit(ImpalaUpdateStatements x) {

    }
}
