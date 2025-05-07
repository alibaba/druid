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
package com.alibaba.druid.wall.spi;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbInsertStatement;
import com.alibaba.druid.sql.dialect.gaussdb.visitor.GaussDbASTVisitor;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;

/**
 * @author Acewuye
 *
 * Notes: Original code of this class based on com.alibaba.druid.wall.spi.PGWallVisitor
 */
public class GaussDBWallVisitor extends WallVisitorBase implements WallVisitor, GaussDbASTVisitor {
    public GaussDBWallVisitor(WallProvider provider) {
        super(provider);
    }

    @Override
    public DbType getDbType() {
        return DbType.gaussdb;
    }

    @Override
    public boolean isDenyTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        name = WallVisitorUtils.form(name);
        if (name.startsWith("v$") || name.startsWith("v_$")) {
            return true;
        }
        return !this.provider.checkDenyTable(name);
    }

    @Override
    public boolean visit(GaussDbCreateTableStatement x) {
        WallVisitorUtils.check(this, x);
        return true;
    }

    @Override
    public boolean visit(PGSelectQueryBlock x) {
        WallVisitorUtils.checkSelect(this, x);
        return true;
    }

    @Override
    public boolean visit(PGDeleteStatement x) {
        WallVisitorUtils.checkReadOnly(this, x.getFrom());
        return visit((SQLDeleteStatement) x);
    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        return visit((SQLUpdateStatement) x);
    }

    @Override
    public boolean visit(GaussDbInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }
}
