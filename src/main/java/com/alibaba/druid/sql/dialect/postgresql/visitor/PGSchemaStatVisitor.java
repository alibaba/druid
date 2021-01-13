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
package com.alibaba.druid.sql.dialect.postgresql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.druid.util.PGUtils;

public class PGSchemaStatVisitor extends SchemaStatVisitor implements PGASTVisitor {
    public PGSchemaStatVisitor() {
        super(DbType.postgresql);
    }

    public PGSchemaStatVisitor(SchemaRepository repository) {
        super (repository);
    }

    @Override
    public DbType getDbType() {
        return DbType.postgresql;
    }

    @Override
    public boolean visit(PGDeleteStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        SQLTableSource using = x.getUsing();
        if (using != null) {
            using.accept(this);
        }

        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Delete);

        TableStat stat = getTableStat(x.getTableName());
        stat.incrementDeleteCount();

        accept(x.getWhere());

        return false;
    }

    @Override
    public boolean visit(PGInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Insert);


        SQLName tableName = x.getTableName();
        {
            TableStat stat = getTableStat(tableName);
            stat.incrementInsertCount();
        }

        accept(x.getColumns());
        accept(x.getQuery());

        return false;
    }

    @Override
    public void endVisit(PGSelectStatement x) {

    }

    @Override
    public boolean visit(PGSelectStatement x) {
        return visit((SQLSelectStatement) x);
    }

    public boolean isPseudoColumn(long hash) {
        return PGUtils.isPseudoColumn(hash);
    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        TableStat stat = getTableStat(x.getTableName());
        stat.incrementUpdateCount();

        accept(x.getFrom());

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    @Override
    public boolean visit(PGTypeCastExpr x) {
        x.getExpr().accept(this);
        return false;
    }

    @Override
    public boolean visit(PGShowStatement x) {
        return false;
    }

    @Override
    public boolean visit(PGStartTransactionStatement x) {
        return false;
    }

    @Override
    public boolean visit(PGConnectToStatement x) {
        return false;
    }

    @Override
    public boolean visit(PGCreateSchemaStatement x) {
        return false;
    }

    @Override
    public boolean visit(PGDropSchemaStatement x) {
        return false;
    }

    @Override
    public boolean visit(PGAlterSchemaStatement x) {
        return false;
    }

}
