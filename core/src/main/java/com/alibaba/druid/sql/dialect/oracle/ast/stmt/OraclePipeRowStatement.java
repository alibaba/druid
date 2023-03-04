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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OraclePipeRowStatement extends OracleStatementImpl {
    private final List<SQLExpr> parameters = new ArrayList<SQLExpr>();

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, parameters);
        }
        visitor.endVisit(this);
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    @Override
    public OraclePipeRowStatement clone() {
        OraclePipeRowStatement x = new OraclePipeRowStatement();

        x.setAfterSemi(this.afterSemi);

        x.setDbType(this.dbType);

        for (SQLExpr expr : parameters) {
            SQLExpr expr2 = expr.clone();
            expr2.setParent(x);
            x.parameters.add(expr2);
        }

        if (this.headHints != null) {
            List<SQLCommentHint> headHintsClone = new ArrayList<SQLCommentHint>(this.headHints.size());
            for (SQLCommentHint hint : headHints) {
                SQLCommentHint h2 = hint.clone();
                h2.setParent(x);
                headHintsClone.add(h2);
            }
            x.setHeadHints(headHintsClone);
        }

        return x;

    }
}
