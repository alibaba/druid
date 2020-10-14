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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInputOutputFormat;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveLoadDataStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveMsckRepairStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class HiveSchemaStatVisitor extends SchemaStatVisitor implements HiveASTVisitor {

    public HiveSchemaStatVisitor() {
        super(DbType.hive);
    }

    public HiveSchemaStatVisitor(DbType dbType) {
        super(dbType);
    }

    public HiveSchemaStatVisitor(SchemaRepository repository) {
        super (repository);
    }

    @Override
    public boolean visit(HiveInsert x) {
        setMode(x, TableStat.Mode.Insert);

        SQLExprTableSource tableSource = x.getTableSource();
        SQLExpr tableName = tableSource.getExpr();

        if (tableName instanceof SQLName) {
            TableStat stat = getTableStat((SQLName) tableName);
            stat.incrementInsertCount();

        }

        for (SQLAssignItem partition : x.getPartitions()) {
            partition.accept(this);
        }

        accept(x.getQuery());

        return false;
    }


    @Override
    public boolean visit(HiveMultiInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }
        return true;
    }

    @Override
    public boolean visit(HiveInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        SQLWithSubqueryClause with = x.getWith();
        if (with != null) {
            with.accept(this);
        }

        setMode(x, TableStat.Mode.Insert);

        SQLExprTableSource tableSource = x.getTableSource();
        SQLExpr tableName = tableSource.getExpr();

        if (tableName instanceof SQLName) {
            TableStat stat = getTableStat((SQLName) tableName);
            stat.incrementInsertCount();

            List<SQLExpr> columns = x.getColumns();
            for (SQLExpr column : columns) {
                if (column instanceof SQLIdentifierExpr) {
                    addColumn((SQLName) tableName, ((SQLIdentifierExpr) column).normalizedName());
                }
            }
        }

        for (SQLAssignItem partition : x.getPartitions()) {
            partition.accept(this);
        }

        accept(x.getQuery());

        return false;
    }

    @Override
    public boolean visit(HiveCreateFunctionStatement x) {
        return false;
    }

    @Override
    public boolean visit(HiveLoadDataStatement x) {
        TableStat tableStat = getTableStat(x.getInto());
        if (tableStat != null) {
            tableStat.incrementInsertCount();
        }
        return false;
    }

    @Override public boolean visit(HiveMsckRepairStatement x) {
        return false;
    }

}
