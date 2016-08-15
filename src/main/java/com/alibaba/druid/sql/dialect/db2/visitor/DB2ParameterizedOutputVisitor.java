/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;

public class DB2ParameterizedOutputVisitor extends DB2OutputVisitor implements ParameterizedVisitor {

    private int replaceCount;

    public DB2ParameterizedOutputVisitor(){
        this(new StringBuilder());
    }

    public DB2ParameterizedOutputVisitor(Appendable appender){
        super(appender);
    }

    public int getReplaceCount() {
        return this.replaceCount;
    }

    public void incrementReplaceCunt() {
        replaceCount++;
    }

    public boolean visit(SQLInListExpr x) {
        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x = ParameterizedOutputVisitorUtils.merge(this, x);

        return super.visit(x);
    }

    public boolean visit(SQLIntegerExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLNumberExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLCharExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLNCharExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLNullExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

}
