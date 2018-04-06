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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLExternalRecordFormat extends SQLObjectImpl {
    private SQLExpr delimitedBy;
    private SQLExpr terminatedBy;

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, delimitedBy);
            acceptChild(visitor, terminatedBy);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getDelimitedBy() {
        return delimitedBy;
    }

    public void setDelimitedBy(SQLExpr delimitedBy) {
        if (delimitedBy != null) {
            delimitedBy.setParent(this);
        }
        this.delimitedBy = delimitedBy;
    }

    public SQLExpr getTerminatedBy() {
        return terminatedBy;
    }

    public void setTerminatedBy(SQLExpr terminatedBy) {
        if (terminatedBy != null) {
            terminatedBy.setParent(this);
        }
        this.terminatedBy = terminatedBy;
    }
}
