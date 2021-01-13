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
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class SQLASTParameterizedVisitor extends SQLASTVisitorAdapter {

    protected DbType dbType;

    protected List<Object> parameters;
    private int replaceCount = 0;

    public SQLASTParameterizedVisitor(DbType dbType){
        this.dbType = dbType;
    }

    public int getReplaceCount() {
        return this.replaceCount;
    }

    public void incrementReplaceCunt() {
        replaceCount++;
    }

    public DbType getDbType() {
        return dbType;
    }

    public SQLASTParameterizedVisitor(DbType dbType, List<Object> parameters) {
        this.dbType = dbType;
        this.parameters = parameters;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        parameterizeAndExportPara(x);
        return false;
    }

    public void parameterizeAndExportPara(SQLExpr x) {
        SQLVariantRefExpr variantRefExpr = new SQLVariantRefExpr("?");
        variantRefExpr.setIndex(this.replaceCount);

        SQLUtils.replaceInParent(x, variantRefExpr);
        incrementReplaceCunt();
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
    }

    public void parameterize(SQLExpr x) {
        SQLVariantRefExpr variantRefExpr = new SQLVariantRefExpr("?");
        variantRefExpr.setIndex(this.replaceCount);

        SQLUtils.replaceInParent(x, variantRefExpr);
        incrementReplaceCunt();
    }

    @Override
    public boolean visit(SQLCharExpr x) {
        parameterizeAndExportPara(x);
        return false;
    }

    @Override
    public boolean visit(SQLIntegerExpr x) {
        SQLObject parent = x.getParent();
        if (parent instanceof SQLSelectGroupByClause || parent instanceof SQLSelectOrderByItem) {
            return false;
        }
        parameterizeAndExportPara(x);
        return false;
    }


    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        List<SQLExpr> arguments = x.getArguments();
        if (x.methodNameHashCode64() == FnvHash.Constants.TRIM
                && arguments.size() == 1
                && arguments.get(0) instanceof SQLCharExpr && x.getTrimOption() == null && x.getFrom() == null) {
            parameterizeAndExportPara(x);

            if (this.parameters != null) {
                SQLCharExpr charExpr = (SQLCharExpr) arguments.get(0);
                this.parameters.add(charExpr.getText().trim());
            }

            replaceCount++;
            return false;
        }
        return true;
    }

    @Override
    public boolean visit(SQLNCharExpr x) {
        parameterizeAndExportPara(x);
        return false;
    }

    @Override
    public boolean visit(SQLNullExpr x) {
        SQLObject parent = x.getParent();
        if (parent instanceof SQLInsertStatement || parent instanceof ValuesClause || parent instanceof SQLInListExpr ||
                (parent instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) parent).getOperator() == SQLBinaryOperator.Equality)) {
            parameterize(x);

            if (this.parameters != null) {
                if (parent instanceof SQLBinaryOpExpr) {
                    ExportParameterVisitorUtils.exportParameter(parameters, x);
                } else {
                    this.parameters.add(null);
                }
            }
            return false;
        }

        return false;
    }

    @Override
    public boolean visit(SQLNumberExpr x) {
        parameterizeAndExportPara(x);
        return false;
    }


    @Override
    public boolean visit(SQLHexExpr x) {
        parameterizeAndExportPara(x);
        return false;
    }
}
