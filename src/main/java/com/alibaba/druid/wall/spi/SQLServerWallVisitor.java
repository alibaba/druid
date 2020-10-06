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
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.wall.*;
import com.alibaba.druid.wall.spi.WallVisitorUtils.WallTopStatementContext;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;

public class SQLServerWallVisitor extends WallVisitorBase implements WallVisitor, SQLServerASTVisitor {

    public SQLServerWallVisitor(WallProvider provider){
        super (provider);
    }

    @Override
    public DbType getDbType() {
        return DbType.sqlserver;
    }

    @Override
    public boolean isDenyTable(String name) {
        if (!config.isTableCheck()) {
            return false;
        }

        return !this.provider.checkDenyTable(name);
    }

    public boolean visit(SQLIdentifierExpr x) {
        // String name = x.getName();
        // name = WallVisitorUtils.form(name);
        // if (config.isVariantCheck() && config.getDenyVariants().contains(name)) {
        // getViolations().add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : " + name,
        // toSQL(x)));
        // }
        return true;
    }


    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        if (x.getParent() instanceof SQLExprTableSource) {
            WallVisitorUtils.checkFunctionInTableSource(this, x);
        }

        WallVisitorUtils.checkFunction(this, x);

        return true;
    }

    @Override
    public boolean visit(SQLServerExecStatement x) {
        return false;
    }

    public boolean visit(SQLVariantRefExpr x) {
        String varName = x.getName();
        if (varName == null) {
            return false;
        }

        if (config.isVariantCheck() && varName.startsWith("@@")) {

            final WallTopStatementContext topStatementContext = WallVisitorUtils.getWallTopStatementContext();
            if (topStatementContext != null
                && (topStatementContext.fromSysSchema() || topStatementContext.fromSysTable())) {
                return false;
            }

            boolean allow = true;
            if (isDeny(varName) && (WallVisitorUtils.isWhereOrHaving(x) || WallVisitorUtils.checkSqlExpr(x))) {
                allow = false;
            }

            if (!allow) {
                violations.add(new IllegalSQLObjectViolation(ErrorCode.VARIANT_DENY, "variable not allow : "
                                                                                     + x.getName(), toSQL(x)));
            }
        }

        return false;
    }

    public boolean isDeny(String varName) {
        if (varName.startsWith("@@")) {
            varName = varName.substring(2);
        }

        return config.getDenyVariants().contains(varName);
    }

    @Override
    public boolean visit(SQLServerObjectReferenceExpr x) {
        return false;
    }
}
