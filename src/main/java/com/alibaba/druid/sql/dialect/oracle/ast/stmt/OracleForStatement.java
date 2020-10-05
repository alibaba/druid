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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLForStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleForStatement extends SQLForStatement implements OracleStatement {


    private boolean            all;

    private SQLName           endLabel;

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) v);
            return;
        }

        super.accept0(v);
    }

    @Override
    public void accept0(OracleASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, index);
            acceptChild(v, range);
            acceptChild(v, statements);
        }
        v.endVisit(this);
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public SQLName getEndLabel() {
        return endLabel;
    }

    public void setEndLabel(SQLName endLabel) {
        if (endLabel != null) {
            endLabel.setParent(this);
        }
        this.endLabel = endLabel;
    }

    public OracleForStatement clone() {
        OracleForStatement x = new OracleForStatement();
        if (index != null) {
            x.setIndex(index.clone());
        }
        if (range != null) {
            x.setRange(range.clone());
        }
        for (SQLStatement stmt : statements) {
            SQLStatement stmt2 = stmt.clone();
            stmt2.setParent(x);
            x.statements.add(stmt2);
        }
        x.all = all;
        if (endLabel != null) {
            x.setEndLabel(endLabel.clone());
        }
        return x;
    }
}
