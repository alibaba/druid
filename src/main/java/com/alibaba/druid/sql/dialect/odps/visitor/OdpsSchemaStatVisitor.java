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
package com.alibaba.druid.sql.dialect.odps.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.odps.ast.*;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class OdpsSchemaStatVisitor extends HiveSchemaStatVisitor implements OdpsASTVisitor {
    public OdpsSchemaStatVisitor() {
        super(DbType.odps);
    }

    public OdpsSchemaStatVisitor(SchemaRepository repository) {
        super(repository);
    }

    @Override
    public boolean visit(OdpsSetLabelStatement x) {
        if (x.getTable() != null) {
            x.getTable().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(OdpsAddUserStatement x) {
        return false;
    }

    @Override
    public boolean visit(OdpsRemoveUserStatement x) {
        return false;
    }

    @Override
    public boolean visit(OdpsAlterTableSetChangeLogs x) {
        return false;
    }

    @Override
    public boolean visit(OdpsAddTableStatement x) {
        SQLExprTableSource table = x.getTable();
        TableStat stat = getTableStatWithUnwrap(table.getExpr());
        if (stat != null) {
            stat.incrementAddCount();
        }

        resolvePartitions(table, x.getPartitions());

        return false;
    }

    @Override
    public boolean visit(OdpsUnloadStatement x) {
        SQLExprTableSource table = (SQLExprTableSource) x.getFrom();
        TableStat stat = getTableStatWithUnwrap(table.getExpr());
        if (stat != null) {
            stat.incrementSelectCount();
        }

        resolvePartitions(table, x.getPartitions());

        return false;
    }

    @Override
    public boolean visit(OdpsCountStatement x) {
        SQLExprTableSource table = x.getTable();
        TableStat stat = getTableStatWithUnwrap(table.getExpr());
        if (stat != null) {
            stat.incrementSelectCount();
        }

        resolvePartitions(table, x.getPartitions());

        return false;
    }

    @Override
    public boolean visit(OdpsExstoreStatement x) {
        SQLExprTableSource table = x.getTable();
        TableStat stat = getTableStatWithUnwrap(table.getExpr());
        if (stat != null) {
            stat.incrementSelectCount();
        }

        resolvePartitions(table, x.getPartitions());

        return false;
    }

    private void resolvePartitions(SQLExprTableSource table, List<SQLAssignItem> parttions) {
        for (SQLAssignItem partition : parttions) {
            SQLExpr target = partition.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr columnName = (SQLIdentifierExpr) target;
                columnName.setResolvedTableSource(table);
                columnName.accept(this);
            }
        }
    }

    public boolean visit(OdpsSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }
}
